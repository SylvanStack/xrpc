package com.yuanstack.xrpc.core.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * response data for RPC call.
 *
 * @author Sylvan
 * @date 2024/03/09  22:43
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RpcResponse<T> {
    private boolean status;
    private T data;
    private RpcException ex;
}
