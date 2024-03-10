package com.yuanstack.xrpc.core.api;

import lombok.Data;

/**
 * @author Sylvan
 * @date 2024/03/09  22:44
 */
@Data
public class RpcRequest {
    private String service; // 服务：com.yuanstack.xrpc.demo.api.service.UserService
    private String method; // 方法：findById
    private Object[] args; // 参数：1000L
}
