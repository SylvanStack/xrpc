package com.yuanstack.xrpc.core.consumer;

import com.yuanstack.xrpc.core.api.Filter;
import com.yuanstack.xrpc.core.api.Loadbalancer;
import com.yuanstack.xrpc.core.api.RegistryCenter;
import com.yuanstack.xrpc.core.api.Router;
import com.yuanstack.xrpc.core.cluster.RoundRibonLoadbalancer;
import com.yuanstack.xrpc.core.filter.CacheFilter;
import com.yuanstack.xrpc.core.meta.InstanceMeta;
import com.yuanstack.xrpc.core.registry.zk.ZkRegistryCenter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * @author Sylvan
 * @date 2024/03/10  13:22
 */
@Configuration
@Slf4j
public class ConsumerConfig {

    @Value("${xrpc.providers}")
    String servers;

    @Bean
    ConsumerBootstrap consumerBootstrap() {
        return new ConsumerBootstrap();
    }

    @Bean
    @Order(Integer.MIN_VALUE)
    public ApplicationRunner consumerBootstrapRunner(@Autowired ConsumerBootstrap consumerBootstrap) {
        return x -> {
            log.info("consumerBootstrapRunner");
            consumerBootstrap.start();
        };
    }

    @Bean
    public Loadbalancer<InstanceMeta> loadbalancer() {
        //return Loadbalancer.Default;
        //return new RandomLoadbalancer();
        return new RoundRibonLoadbalancer<>();
    }

    @Bean
    public Router<InstanceMeta> router() {
        return Router.Default;
    }

    @Bean
    public Filter filter() {
        return new CacheFilter();
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public RegistryCenter consumerRegistryCenter() {
        return new ZkRegistryCenter();
    }

}
