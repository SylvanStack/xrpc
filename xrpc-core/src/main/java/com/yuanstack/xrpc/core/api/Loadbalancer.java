package com.yuanstack.xrpc.core.api;

import java.util.List;

/**
 * @author Sylvan
 * @date 2024/03/20  23:15
 */
public interface Loadbalancer {

    String choose(List<String> providers);

    Loadbalancer Default = p -> (p == null || p.isEmpty()) ? null : p.get(1);
}
