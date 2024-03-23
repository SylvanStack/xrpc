package com.yuanstack.xrpc.core.provider;

import com.yuanstack.xrpc.core.api.RegistryCenter;
import com.yuanstack.xrpc.core.registry.ZkRegistryCenter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * @author Sylvan
 * @date 2024/03/10  13:22
 */
@Configuration
public class ProviderConfig {

    @Bean
    ProviderBootstrap providerBootstrap() {
        return new ProviderBootstrap();
    }

    @Bean
    @Order(Integer.MIN_VALUE)
    public ApplicationRunner providerBootstrapRunner(@Autowired ProviderBootstrap providerBootstrap) {
        return x -> {
            providerBootstrap.start();
            System.out.println("providerBootstrap starting .....");
        };
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public RegistryCenter providerRegistryCenter() {
        return new ZkRegistryCenter();
    }

    @Bean
    public ProviderInvoker providerInvoker(@Autowired ProviderBootstrap providerBootstrap) {
        return new ProviderInvoker(providerBootstrap);
    }
}
