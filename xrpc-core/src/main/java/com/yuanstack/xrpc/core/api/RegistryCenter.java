package com.yuanstack.xrpc.core.api;

import com.yuanstack.xrpc.core.meta.InstanceMeta;
import com.yuanstack.xrpc.core.registry.ChangeListener;

import java.util.List;

/**
 * @author Sylvan
 * @date 2024/03/21  23:20
 */
public interface RegistryCenter {
    // 注册中心 Provider & Consumer 侧都需要
    void start();

    void stop();


    // provider侧
    void register(String service, InstanceMeta instance);

    void unregister(String service, InstanceMeta instance);

    // consumer侧
    List<InstanceMeta> fetchAll(String service);

    void subscribe(String service, ChangeListener changeListener);

    void heartbeat();

    class StaticRegistryCenter implements RegistryCenter {

        List<InstanceMeta> providers;

        public StaticRegistryCenter(List<InstanceMeta> providers) {
            this.providers = providers;
        }

        @Override
        public void start() {

        }

        @Override
        public void stop() {

        }

        @Override
        public void register(String service, InstanceMeta instance) {

        }

        @Override
        public void unregister(String service, InstanceMeta instance) {

        }

        @Override
        public List<InstanceMeta> fetchAll(String service) {
            return providers;
        }

        @Override
        public void subscribe(String service, ChangeListener changeListener) {

        }

        @Override
        public void heartbeat() {

        }
    }
}
