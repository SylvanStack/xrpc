package com.yuanstack.xrpc.core.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yuanstack.xrpc.core.api.Loadbalancer;
import com.yuanstack.xrpc.core.api.Router;
import com.yuanstack.xrpc.core.api.RpcRequest;
import com.yuanstack.xrpc.core.api.RpcResponse;
import com.yuanstack.xrpc.core.util.MethodUtils;
import com.yuanstack.xrpc.core.util.TypeUtils;
import okhttp3.*;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author Sylvan
 * @date 2024/03/11  22:27
 */
public class XInvocationHandler implements InvocationHandler {
    final static MediaType JSON_TYPE = MediaType.get("application/json; charset=utf-8");
    Class<?> service;
    Router router;
    Loadbalancer loadbalancer;
    String[] providers;

    public XInvocationHandler(Class<?> service, Router router, Loadbalancer loadbalancer, String[] providers) {
        this.service = service;
        this.router = router;
        this.loadbalancer = loadbalancer;
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

        List<String> urls = router.route(List.of(providers));
        String url = loadbalancer.choose(urls);

        System.out.println("loadbalancer.choose(urls) ==> " + url);

        RpcResponse rpcResponse = post(request, url);
        if (rpcResponse.getStatus()) {
            if (rpcResponse.getData() instanceof JSONObject) {
                JSONObject jsonResult = (JSONObject) rpcResponse.getData();
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
        } else {
            throw rpcResponse.getEx();
        }
    }

    OkHttpClient client = new OkHttpClient()
            .newBuilder()
            .connectionPool(new ConnectionPool(16, 60, TimeUnit.SECONDS))
            .readTimeout(1, TimeUnit.SECONDS)
            .writeTimeout(1, TimeUnit.SECONDS)
            .connectTimeout(1, TimeUnit.SECONDS).build();

    private RpcResponse post(RpcRequest rpcRequest, String url) {
        String reqJson = JSON.toJSONString(rpcRequest);
        System.out.println("reqJson :" + reqJson);
        System.out.println("url :" + url);

        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(reqJson, JSON_TYPE))
                .build();

        try {
            String respJson = Objects.requireNonNull(client.newCall(request).execute().body()).string();
            System.out.println("respJson :" + respJson);
            RpcResponse rpcResponse = JSON.parseObject(respJson, RpcResponse.class);
            System.out.println(rpcResponse);
            return rpcResponse;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
