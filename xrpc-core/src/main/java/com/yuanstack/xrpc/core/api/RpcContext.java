package com.yuanstack.xrpc.core.api;

import com.yuanstack.xrpc.core.meta.InstanceMeta;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Sylvan
 * @date 2024/03/21  23:06
 */
@Data
public class RpcContext {
    private List<Filter> filters;

    private Router<InstanceMeta> router;

    private Loadbalancer<InstanceMeta> loadbalancer;

    private Map<String, String> parameters = new HashMap<>();
}
