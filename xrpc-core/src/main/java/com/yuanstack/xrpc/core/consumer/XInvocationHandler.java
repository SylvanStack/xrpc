package com.yuanstack.xrpc.core.consumer;

import com.yuanstack.xrpc.core.api.*;
import com.yuanstack.xrpc.core.consumer.http.OkHttpInvoker;
import com.yuanstack.xrpc.core.governance.SlidingTimeWindow;
import com.yuanstack.xrpc.core.meta.InstanceMeta;
import com.yuanstack.xrpc.core.util.MethodUtils;
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

import static com.yuanstack.xrpc.core.util.TypeUtils.castMethodResult;

/**
 * @author Sylvan
 * @date 2024/03/11  22:27
 */
@Slf4j
public class XInvocationHandler implements InvocationHandler {
    Class<?> service;
    RpcContext rpcContext;
    private final List<InstanceMeta> providers;
    private final List<InstanceMeta> isolateProviders = new ArrayList<>();
    private final List<InstanceMeta> halfOpenProviders = new ArrayList<>();
    private final Map<String, SlidingTimeWindow> windows = new HashMap<>();
    HttpInvoker httpInvoker;
    ScheduledExecutorService executorService;

    public XInvocationHandler(Class<?> service, RpcContext rpcContext, List<InstanceMeta> providers) {
        this.service = service;
        this.rpcContext = rpcContext;
        this.providers = providers;
        Integer timeout = Integer.valueOf(rpcContext.getParameters().getOrDefault("app.timeout", "1000"));
        this.httpInvoker = new OkHttpInvoker(timeout);
        this.executorService = Executors.newScheduledThreadPool(1);
        int halfOpenInitialDelay = Integer.parseInt(rpcContext.getParameters()
                .getOrDefault("app.halfOpenInitialDelay", "10000"));
        int halfOpenDelay = Integer.parseInt(rpcContext.getParameters()
                .getOrDefault("app.halfOpenDelay", "60000"));
        this.executorService.scheduleWithFixedDelay(this::halfOpen, halfOpenInitialDelay, halfOpenDelay, TimeUnit.SECONDS);
    }

    private void halfOpen() {
        log.debug(" half open isolateProviders is [{}]", isolateProviders);
        halfOpenProviders.clear();
        halfOpenProviders.addAll(isolateProviders);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        if (MethodUtils.isObjectMethod(method)) {
            return null;
        }

        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setService(service.getCanonicalName());
        rpcRequest.setMethodSign(MethodUtils.generateMethodSign(method));
        rpcRequest.setArgs(args);

        int retries = Integer.parseInt(
                rpcContext.getParameters().getOrDefault("app.retries", "1"));
        int faultLimit = Integer.parseInt(rpcContext.getParameters()
                .getOrDefault("app.faultLimit", "10"));

        while (retries-- > 0) {
            log.debug("invoke service provider retries count is [{}]", retries);
            try {
                for (Filter filter : this.rpcContext.getFilters()) {
                    Object preResult = filter.preFilter(rpcRequest);
                    if (preResult != null) {
                        log.debug(filter.getClass().getName() + "preFilter is [{}]", preResult);
                        return preResult;
                    }
                }

                InstanceMeta instance;
                synchronized (halfOpenProviders) {
                    if (halfOpenProviders.isEmpty()) {
                        List<InstanceMeta> nodes = rpcContext.getRouter().route(providers);
                        instance = rpcContext.getLoadbalancer().choose(nodes);
                        log.debug("loadbalancer choose url is [{}]", instance);
                    } else {
                        instance = halfOpenProviders.remove(0);
                        log.debug("check alive instance is [{}]", instance);
                    }
                }

                Object result;
                RpcResponse<?> rpcResponse;
                String url = instance.toUrl();
                try {
                    rpcResponse = httpInvoker.post(rpcRequest, url);
                    result = castReturnResult(method, rpcResponse);
                } catch (Exception ex) {
                    // 故障的规则统计与隔离
                    // 每一次异常，记录一次，统计30s的异常数
                    synchronized (windows) {
                        SlidingTimeWindow window = windows.computeIfAbsent(url, k -> new SlidingTimeWindow());
                        window.record(System.currentTimeMillis());
                        log.debug("instance {} in window with {}", url, window.getSum());
                        if (window.getSum() >= faultLimit) {
                            isolate(instance);
                        }
                    }

                    throw ex;
                }

                synchronized (providers) {
                    if (!providers.contains(instance)) {
                        isolateProviders.remove(instance);
                        providers.add(instance);
                        log.debug("instance {} is recovered , isolateProviders is [{}], provider is [{}]",
                                instance, isolateProviders, providers);
                    }
                }

                for (Filter filter : this.rpcContext.getFilters()) {
                    Object filterResult = filter.postFilter(rpcRequest, rpcResponse, result);
                    log.debug(filter.getClass().getName() + "filterResult is [{}]", filterResult);
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

    private Object castReturnResult(Method method, RpcResponse<?> rpcResponse) {
        if (rpcResponse.isStatus()) {
            return castMethodResult(method, rpcResponse.getData());
        } else {
            Exception exception = rpcResponse.getEx();
            if (exception instanceof RpcException ex) {
                throw ex;
            } else {
                throw new RpcException(exception, RpcException.UnknownEx);
            }
        }
    }

    private void isolate(InstanceMeta instance) {
        log.debug("isolate instance is [{}]", instance);
        providers.remove(instance);
        log.debug("providers is [{}]", providers);
        isolateProviders.add(instance);
        log.debug("isolateProviders is [{}]", providers);
    }
}
