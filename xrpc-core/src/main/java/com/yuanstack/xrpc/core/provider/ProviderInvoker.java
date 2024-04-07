package com.yuanstack.xrpc.core.provider;

import com.yuanstack.xrpc.core.api.RpcContext;
import com.yuanstack.xrpc.core.api.RpcException;
import com.yuanstack.xrpc.core.api.RpcRequest;
import com.yuanstack.xrpc.core.api.RpcResponse;
import com.yuanstack.xrpc.core.meta.ProviderMeta;
import com.yuanstack.xrpc.core.util.TypeUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

/**
 * invoke the service methods in provider.
 *
 * @author Sylvan
 * @date 2024/03/10  13:20
 */
@Data
@Component
@Slf4j
public class ProviderInvoker {


    private MultiValueMap<String, ProviderMeta> skeleton;

    public ProviderInvoker(ProviderBootstrap providerBootstrap) {
        this.skeleton = providerBootstrap.getSkeleton();
    }


    public RpcResponse<Object> invoke(RpcRequest request) {
        log.debug("ProviderInvoker.invoke() request is [{}]", request);
        if (!request.getParams().isEmpty()) {
            request.getParams().forEach(RpcContext::setContextParameter);
        }

        RpcResponse<Object> rpcResponse = new RpcResponse<>();
        List<ProviderMeta> providerMetas = skeleton.get(request.getService());
        try {
            ProviderMeta providerMeta = findProviderMeta(providerMetas, request.getMethodSign());
            Method method = providerMeta.getMethod();
            Object[] args = processArgs(request.getArgs(), method.getParameterTypes(), method.getGenericParameterTypes());
            Object result = method.invoke(providerMeta.getServiceImpl(), args);
            rpcResponse.setData(result);
            rpcResponse.setStatus(true);
        } catch (InvocationTargetException e) {
            rpcResponse.setEx(new RpcException(e.getTargetException().getMessage()));
        } catch (IllegalAccessException e) {
            rpcResponse.setEx(new RpcException(e.getMessage()));
        } finally {
            // 防止内存泄露和上下文污染
            RpcContext.ContextParameters.get().clear();
        }
        log.debug("ProviderInvoker.invoke() rpcResponse is [{}]", rpcResponse);
        return rpcResponse;
    }

    private Object[] processArgs(Object[] args, Class<?>[] parameterTypes, Type[] genericParameterTypes) {
        if (args == null || args.length == 0) {
            return args;
        }

        Object[] actual = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            actual[i] = TypeUtils.castGeneric(args[i], parameterTypes[i], genericParameterTypes[i]);
        }
        return actual;
    }

    private ProviderMeta findProviderMeta(List<ProviderMeta> providerMetas, String methodSign) {
        Optional<ProviderMeta> first = providerMetas.stream()
                .filter(providerMeta -> providerMeta.getMethodSign().equals(methodSign))
                .findFirst();
        return first.orElse(null);
    }
}
