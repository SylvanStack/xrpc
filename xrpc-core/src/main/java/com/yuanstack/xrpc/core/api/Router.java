package com.yuanstack.xrpc.core.api;

import com.yuanstack.xrpc.core.meta.InstanceMeta;

import java.util.List;

/**
 * @author Sylvan
 * @date 2024/03/20  23:15
 */
public interface Router<T> {

    List<T> route(List<T> providers);

    Router<InstanceMeta> Default = p -> p;
}
