package com.yuanstack.xrpc.core.consumer;

import com.yuanstack.xrpc.core.api.Filter;
import com.yuanstack.xrpc.core.api.RpcContext;
import com.yuanstack.xrpc.core.api.RpcRequest;
import com.yuanstack.xrpc.core.api.RpcResponse;
import com.yuanstack.xrpc.core.consumer.http.OkHttpInvoker;
import com.yuanstack.xrpc.core.meta.InstanceMeta;
import com.yuanstack.xrpc.core.util.MethodUtils;
import com.yuanstack.xrpc.core.util.ResponseUtils;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @author Sylvan
 * @date 2024/03/11  22:27
 */
@Slf4j
public class XInvocationHandler implements InvocationHandler {
    Class<?> service;
    RpcContext rpcContext;
    List<InstanceMeta> providers;

    HttpInvoker httpInvoker = new OkHttpInvoker();

    public XInvocationHandler(Class<?> service, RpcContext rpcContext, List<InstanceMeta> providers) {
        this.service = service;
        this.rpcContext = rpcContext;
        this.providers = providers;
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

        for (Filter filter : this.rpcContext.getFilters()) {
            Object preResult = filter.preFilter(request);
            if (preResult != null) {
                log.debug(filter.getClass().getName() + "===> preFilter:" + preResult);
                return preResult;
            }
        }

        List<InstanceMeta> nodes = rpcContext.getRouter().route(providers);
        InstanceMeta instance = rpcContext.getLoadbalancer().choose(nodes);

        log.info("loadbalancer.choose(urls) ==> " + instance);
        RpcResponse<?> rpcResponse = httpInvoker.post(request, instance.toUrl());
        if (!rpcResponse.getStatus()) {
            throw rpcResponse.getEx();
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
    }
}
