package com.yuanstack.xrpc.core.filter;

import com.yuanstack.xrpc.core.api.Filter;
import com.yuanstack.xrpc.core.api.RpcRequest;
import com.yuanstack.xrpc.core.api.RpcResponse;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Sylvan
 * @date 2024/03/24  15:40
 */
@Component
public class CacheFilter implements Filter {

    Map<String, Object> cache = new ConcurrentHashMap<>();

    @Override
    public Object preFilter(RpcRequest request) {
        return cache.get(request.toString());
    }

    @Override
    public Object postFilter(RpcRequest request, RpcResponse<?> response, Object result) {
        cache.putIfAbsent(request.toString(), result);
        return result;
    }
}
