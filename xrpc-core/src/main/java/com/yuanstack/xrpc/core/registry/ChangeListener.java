package com.yuanstack.xrpc.core.registry;

/**
 * @author Sylvan
 * @date 2024/03/23  17:19
 */
public interface ChangeListener {
    void fire(Event event);
}
