package com.yuanstack.xrpc.core.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yuanstack.xrpc.core.api.RpcResponse;

import java.lang.reflect.Array;
import java.lang.reflect.Method;

/**
 * @author Sylvan
 * @date 2024/03/23  18:21
 */
public class ResponseUtils {
    public static Object castResponse(Method method, RpcResponse rpcResponse) {
        if (rpcResponse.getData() instanceof JSONObject jsonResult) {
            return jsonResult.toJavaObject(method.getReturnType());
        } else if (rpcResponse.getData() instanceof JSONArray array) {
            Object[] arrays = array.toArray();
            Class<?> componentType = method.getReturnType().getComponentType();
            Object resultArray = Array.newInstance(componentType, arrays.length);
            for (int i = 0; i < arrays.length; i++) {
                Array.set(resultArray, i, arrays[i]);
            }
            return resultArray;
        } else {
            return TypeUtils.cast(rpcResponse.getData(), method.getReturnType());
        }
    }
}
