package com.yuanstack.xrpc.core.filter;

import com.yuanstack.xrpc.core.api.Filter;
import com.yuanstack.xrpc.core.api.RpcContext;
import com.yuanstack.xrpc.core.api.RpcRequest;
import com.yuanstack.xrpc.core.api.RpcResponse;

import java.util.Map;

/**
 * 处理上下文参数
 *
 * @author Sylvan
 * @date 2024/04/07  21:23
 */
public class ParameterFilter implements Filter {
    @Override
    public Object preFilter(RpcRequest request) {
        Map<String, String> params = RpcContext.ContextParameters.get();
        if (!params.isEmpty()) {
            request.getParams().putAll(params);
        }
        return null;
    }

    @Override
    public Object postFilter(RpcRequest request, RpcResponse<?> response, Object result) {
        // RpcContext.ContextParameters.get().clear();
        return null;
    }
}
