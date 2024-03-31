package com.yuanstack.xrpc.core.filter;

import com.yuanstack.xrpc.core.api.Filter;
import com.yuanstack.xrpc.core.api.RpcRequest;
import com.yuanstack.xrpc.core.api.RpcResponse;
import com.yuanstack.xrpc.core.util.MethodUtils;
import com.yuanstack.xrpc.core.util.MockUtils;
import lombok.SneakyThrows;

import java.lang.reflect.Method;


/**
 * @author Sylvan
 * @date 2024/03/24  16:12
 */
public class MockFilter implements Filter {
    @SneakyThrows
    @Override
    public Object preFilter(RpcRequest request) {
        Class<?> service = Class.forName(request.getService());
        Method method = findMethod(service, request.getMethodSign());
        if (method == null) {
            return null;
        }

        Class<?> clazz = method.getReturnType();
        return MockUtils.mock(clazz);
    }

    public static Method findMethod(Class<?> service, String methodSign) {
        Method[] methods = service.getMethods();
        for (Method method : methods) {
            if (MethodUtils.isObjectMethod(method)) {
                continue;
            }
            if (MethodUtils.generateMethodSign(method).equals(methodSign)) {
                return method;
            }
        }
        return null;
    }

    @Override
    public Object postFilter(RpcRequest request, RpcResponse<?> response, Object result) {
        return null;
    }
}
