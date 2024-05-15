package com.yuanstack.xrpc.core.api;

import com.yuanstack.xrpc.core.meta.InstanceMeta;
import com.yuanstack.xrpc.core.meta.ServiceMeta;
import com.yuanstack.xrpc.core.registry.ChangeListener;

import java.util.List;

/**
 * @author Sylvan
 * @date 2024/03/21  23:20
 */
public interface RegistryCenter {
    // ================== 注册中心 Provider & Consumer 侧都需要  ====================
    void start();

    void stop();

    // =============================== provider侧  ===============================
    void register(ServiceMeta service, InstanceMeta instance);

    void unregister(ServiceMeta service, InstanceMeta instance);

    // =============================== consumer侧  ===============================
    List<InstanceMeta> fetchAll(ServiceMeta service);

    void subscribe(ServiceMeta service, ChangeListener changeListener);

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
        public void register(ServiceMeta service, InstanceMeta instance) {

        }

        @Override
        public void unregister(ServiceMeta service, InstanceMeta instance) {

        }

        @Override
        public List<InstanceMeta> fetchAll(ServiceMeta service) {
            return providers;
        }

        @Override
        public void subscribe(ServiceMeta service, ChangeListener changeListener) {

        }
    }
}
