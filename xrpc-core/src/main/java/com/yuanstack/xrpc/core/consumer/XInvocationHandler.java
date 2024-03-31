package com.yuanstack.xrpc.core.consumer;

import com.yuanstack.xrpc.core.api.*;
import com.yuanstack.xrpc.core.consumer.http.OkHttpInvoker;
import com.yuanstack.xrpc.core.meta.InstanceMeta;
import com.yuanstack.xrpc.core.util.MethodUtils;
import com.yuanstack.xrpc.core.util.ResponseUtils;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 * @author Sylvan
 * @date 2024/03/11  22:27
 */
@Slf4j
public class XInvocationHandler implements InvocationHandler {
    Class<?> service;
    RpcContext rpcContext;
    List<InstanceMeta> providers;

    HttpInvoker httpInvoker;

    public XInvocationHandler(Class<?> service, RpcContext rpcContext, List<InstanceMeta> providers) {
        this.service = service;
        this.rpcContext = rpcContext;
        this.providers = providers;
        Integer timeout = Integer.valueOf(rpcContext.getParameters().getOrDefault("app.timeout", "1000"));
        this.httpInvoker = new OkHttpInvoker(timeout);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
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
            log.debug(" ==> retries {}", retries);
            try {
                for (Filter filter : this.rpcContext.getFilters()) {
                    Object preResult = filter.preFilter(request);
                    if (preResult != null) {
                        log.debug(filter.getClass().getName() + "===> preFilter:" + preResult);
                        return preResult;
                    }
                }

                List<InstanceMeta> nodes = rpcContext.getRouter().route(providers);
                InstanceMeta instance = rpcContext.getLoadbalancer().choose(nodes);

                log.debug("loadbalancer.choose(urls) ==> " + instance);
                RpcResponse<?> rpcResponse = httpInvoker.post(request, instance.toUrl());
                if (rpcResponse.getStatus() == null || !rpcResponse.getStatus()) {
                    Exception exception = rpcResponse.getEx();
                    if (exception instanceof RpcException ex) {
                        throw ex;
                    } else {
                        throw new RpcException(rpcResponse.getEx(), RpcException.UnknownEx);
                    }
                }
                Object result = ResponseUtils.castResponse(method, rpcResponse);

                for (Filter filter : this.rpcContext.getFilters()) {
                    Object filterResult = filter.postFilter(request, rpcResponse, result);
                    log.debug(filter.getClass().getName() + "===> filterResult:" + filterResult);
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
}
