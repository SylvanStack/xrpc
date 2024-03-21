package com.yuanstack.xrpc.core.api;

import java.util.List;

/**
 * @author Sylvan
 * @date 2024/03/21  23:20
 */
public interface RegistryCenter {
    // 注册中心
    void start();

    void stop();


    // provider侧
    void register(String service, String instance);

    void unregister(String service, String instance);

    // consumer侧
    List<String> fetchAll(String service);

    void subscribe();

    class StaticRegistryCenter implements RegistryCenter {

        List<String> providers;

        public StaticRegistryCenter(List<String> providers) {
            this.providers = providers;
        }

        @Override
        public void start() {

        }

        @Override
        public void stop() {

        }

        @Override
        public void register(String service, String instance) {

        }

        @Override
        public void unregister(String service, String instance) {

        }

        @Override
        public List<String> fetchAll(String service) {
            return providers;
        }

        @Override
        public void subscribe() {

        }
    }
}
