package com.yuanstack.xrpc.core.consumer.http;

import com.alibaba.fastjson.JSON;
import com.yuanstack.xrpc.core.api.RpcRequest;
import com.yuanstack.xrpc.core.api.RpcResponse;
import com.yuanstack.xrpc.core.consumer.HttpInvoker;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author Sylvan
 * @date 2024/03/23  18:29
 */
@Slf4j
public class OkHttpInvoker implements HttpInvoker {
    final static MediaType JSON_TYPE = MediaType.get("application/json; charset=utf-8");
    private final OkHttpClient client;

    public OkHttpInvoker(Integer timeout) {
        this.client = new OkHttpClient()
                .newBuilder()
                .connectionPool(new ConnectionPool(16, 60, TimeUnit.SECONDS))
                .readTimeout(timeout, TimeUnit.MICROSECONDS)
                .writeTimeout(timeout, TimeUnit.MICROSECONDS)
                .connectTimeout(timeout, TimeUnit.MICROSECONDS).build();
    }

    @Override
    public RpcResponse<?> post(RpcRequest rpcRequest, String url) {
        String reqJson = JSON.toJSONString(rpcRequest);
        log.info("url is {}, reqJson is {}", url, reqJson);

        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(reqJson, JSON_TYPE))
                .build();

        try {
            String respJson = Objects.requireNonNull(client.newCall(request).execute().body()).string();
            log.info("respJson is {}", respJson);
            RpcResponse<Object> rpcResponse = JSON.parseObject(respJson, RpcResponse.class);
            log.info("rpcResponse is {}", rpcResponse);
            return rpcResponse;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
