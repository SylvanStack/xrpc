package com.yuanstack.xrpc.core.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yuanstack.xrpc.core.api.RpcRequest;
import com.yuanstack.xrpc.core.api.RpcResponse;
import okhttp3.*;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author Sylvan
 * @date 2024/03/11  22:27
 */
public class XInvocationHandler implements InvocationHandler {
    final static MediaType JSON_TYPE = MediaType.get("application/json; charset=utf-8");
    Class<?> service;

    public XInvocationHandler(Class<?> service) {
        this.service = service;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcRequest request = new RpcRequest();
        request.setService(service.getCanonicalName());
        request.setMethod(method.getName());
        request.setArgs(args);

        RpcResponse rpcResponse = post(request);
        if (rpcResponse.getStatus()) {
            JSONObject jsonResult = (JSONObject) rpcResponse.getData();
            return jsonResult.toJavaObject(method.getReturnType());
        }
        return null;
    }

    OkHttpClient client = new OkHttpClient().newBuilder()
            .connectionPool(new ConnectionPool(16, 60, TimeUnit.SECONDS))
            .readTimeout(1, TimeUnit.SECONDS)
            .writeTimeout(1, TimeUnit.SECONDS)
            .connectTimeout(1, TimeUnit.SECONDS)
            .build();

    private RpcResponse post(RpcRequest rpcRequest) {
        String reqJson = JSON.toJSONString(rpcRequest);

        Request request = new Request.Builder()
                .url("http://localhost:8080/")
                .post(RequestBody.create(reqJson, JSON_TYPE))
                .build();

        try {
            String respJson = client.newCall(request).execute().body().string();
            RpcResponse rpcResponse = JSON.parseObject(respJson, RpcResponse.class);
            return rpcResponse;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
