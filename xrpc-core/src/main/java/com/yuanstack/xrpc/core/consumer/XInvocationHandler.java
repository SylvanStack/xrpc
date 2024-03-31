package com.yuanstack.xrpc.core.consumer;

import com.yuanstack.xrpc.core.api.Filter;
import com.yuanstack.xrpc.core.api.RpcContext;
import com.yuanstack.xrpc.core.api.RpcRequest;
import com.yuanstack.xrpc.core.api.RpcResponse;
import com.yuanstack.xrpc.core.consumer.http.OkHttpInvoker;
import com.yuanstack.xrpc.core.governance.SlidingTimeWindow;
import com.yuanstack.xrpc.core.meta.InstanceMeta;
import com.yuanstack.xrpc.core.util.MethodUtils;
import com.yuanstack.xrpc.core.util.ResponseUtils;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Sylvan
 * @date 2024/03/11  22:27
 */
@Slf4j
public class XInvocationHandler implements InvocationHandler {
    Class<?> service;
    RpcContext rpcContext;
    final List<InstanceMeta> providers;
    List<InstanceMeta> isolateProviders = new ArrayList<>();
    final List<InstanceMeta> halfOpenProviders = new ArrayList<>();
    Map<String, SlidingTimeWindow> windowMap = new HashMap<>();
    HttpInvoker httpInvoker;
    ScheduledExecutorService executorService;

    public XInvocationHandler(Class<?> service, RpcContext rpcContext, List<InstanceMeta> providers) {
        this.service = service;
        this.rpcContext = rpcContext;
        this.providers = providers;
        Integer timeout = Integer.valueOf(rpcContext.getParameters().getOrDefault("app.timeout", "1000"));
        this.httpInvoker = new OkHttpInvoker(timeout);
        this.executorService = Executors.newScheduledThreadPool(1);
        this.executorService.scheduleWithFixedDelay(this::halfOpen, 10, 10, TimeUnit.SECONDS);
    }

    private void halfOpen() {
        log.debug(" half open isolateProviders: {}", isolateProviders);
        halfOpenProviders.clear();
        halfOpenProviders.addAll(isolateProviders);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        if (MethodUtils.isObjectMethod(method)) {
            return null;
        }

        RpcRequest request = new RpcRequest();
        request.setService(service.getCanonicalName());
        request.setMethodSign(MethodUtils.generateMethodSign(method));
        request.setArgs(args);

        int retries = Integer.parseInt(
                rpcContext.getParameters().getOrDefault("app.retries", "1"));

        while (retries-- > 0) {
            log.debug("invoke service provider retries count is {}", retries);
            try {
                for (Filter filter : this.rpcContext.getFilters()) {
                    Object preResult = filter.preFilter(request);
                    if (preResult != null) {
                        log.debug(filter.getClass().getName() + "preFilter is {}", preResult);
                        return preResult;
                    }
                }

                InstanceMeta instance;
                synchronized (halfOpenProviders) {
                    if (halfOpenProviders.isEmpty()) {
                        List<InstanceMeta> nodes = rpcContext.getRouter().route(providers);
                        instance = rpcContext.getLoadbalancer().choose(nodes);
                        log.debug("loadbalancer choose url is {}", instance);
                    } else {
                        instance = halfOpenProviders.remove(0);
                        log.debug("check alive instance is {}", instance);
                    }
                }

                Object result;
                RpcResponse<?> rpcResponse;
                String url = instance.toUrl();
                try {
                    rpcResponse = httpInvoker.post(request, url);
                    result = ResponseUtils.castResponse(method, rpcResponse);
                } catch (Exception exception) {
                    // 故障的规则统计与隔离
                    // 每一次异常，记录一次，统计30s的异常数
                    SlidingTimeWindow window = windowMap.computeIfAbsent(url, k -> new SlidingTimeWindow());

                    window.record(System.currentTimeMillis());
                    log.debug("instance {} in window with {}", url, window.getSum());

                    // 发生了10次，就做故障隔离
                    if (window.getSum() >= 10) {
                        isolate(instance);
                    }

                    throw exception;
                }

                synchronized (providers) {
                    if (!providers.contains(instance)) {
                        isolateProviders.remove(instance);
                        providers.add(instance);
                        log.debug("instance {} is recovered , isolateProviders is {}, provider is {}",
                                instance, isolateProviders, providers);
                    }
                }

                for (Filter filter : this.rpcContext.getFilters()) {
                    Object filterResult = filter.postFilter(request, rpcResponse, result);
                    log.debug(filter.getClass().getName() + "filterResult is {}", filterResult);
                    if (filterResult != null) {
                        return filterResult;
                    }
                }

                return result;
            } catch (Exception ex) {
                if (!(ex.getCause() instanceof SocketTimeoutException)) {
                    throw ex;
                }
            }
        }
        return null;
    }

    private void isolate(InstanceMeta instance) {
        log.debug("isolate instance is {}", instance);
        providers.remove(instance);
        log.debug("providers is {}", providers);
        isolateProviders.add(instance);
        log.debug("isolateProviders is {}", providers);
    }
}
