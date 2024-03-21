package com.yuanstack.xrpc.core.api;

import lombok.Data;

import java.util.List;

/**
 * @author Sylvan
 * @date 2024/03/21  23:06
 */
@Data
public class RpcContext {
    List<Filter> filters; // todo

    Router router;

    Loadbalancer loadbalancer;
}
