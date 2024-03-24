package com.yuanstack.xrpc.core.api;

/**
 * @author Sylvan
 * @date 2024/03/20  23:15
 */
public interface Filter {

    Object preFilter(RpcRequest request);

    Object postFilter(RpcRequest request, RpcResponse<?> response, Object result);

    //Filter next(); // 更简易的实现方式 Filter[]

    Filter Default = new Filter() {
        @Override
        public Object preFilter(RpcRequest request) {
            return null;
        }

        @Override
        public Object postFilter(RpcRequest request, RpcResponse<?> response, Object result) {
            return result;
        }
    };
}
