package com.yuanstack.xrpc.core.api;

import java.util.List;

/**
 * @author Sylvan
 * @date 2024/03/20  23:15
 */
public interface Router {

    List<String> route(List<String> providers);

    Router Default = p -> p;
}
