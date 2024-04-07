package com.yuanstack.xrpc.core.api;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Sylvan
 * @date 2024/03/09  22:44
 */
@Data
public class RpcRequest {
    /**
     * 服务：com.yuanstack.xrpc.demo.api.service.UserService
     */
    private String service;

    /**
     * 方法签名：findById
     */
    private String methodSign;

    /**
     * 参数：1000L
     */
    private Object[] args;

    /**
     * 跨调用方需要传递的参数
     */
    private Map<String, String> params = new HashMap<>();
}
