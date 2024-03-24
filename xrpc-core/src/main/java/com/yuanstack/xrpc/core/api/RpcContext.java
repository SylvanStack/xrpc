package com.yuanstack.xrpc.core.api;

import com.yuanstack.xrpc.core.meta.InstanceMeta;
import lombok.Data;

import java.util.List;

/**
 * @author Sylvan
 * @date 2024/03/21  23:06
 */
@Data
public class RpcContext {
    List<Filter> filters;

    Router<InstanceMeta> router;

    Loadbalancer<InstanceMeta> loadbalancer;
}
