package com.yuanstack.xrpc.core.consumer;

import com.yuanstack.xrpc.core.api.RpcRequest;
import com.yuanstack.xrpc.core.api.RpcResponse;

/**
 * Interface for http invoke.
 *
 * @author Sylvan
 * @date 2024/03/23  18:28
 */
public interface HttpInvoker {

    RpcResponse<?> post(RpcRequest rpcRequest, String url);
}
