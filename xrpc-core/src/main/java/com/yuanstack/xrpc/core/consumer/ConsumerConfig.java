package com.yuanstack.xrpc.core.consumer;

import com.yuanstack.xrpc.core.api.Loadbalancer;
import com.yuanstack.xrpc.core.api.Router;
import com.yuanstack.xrpc.core.cluster.RandomLoadbalancer;
import com.yuanstack.xrpc.core.cluster.RoundRibonLoadbalancer;
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
public class ConsumerConfig {

    @Bean
    ConsumerBootstrap consumerBootstrap() {
        return new ConsumerBootstrap();
    }

    @Bean
    @Order(Integer.MIN_VALUE)
    public ApplicationRunner consumerBootstrapRunner(@Autowired ConsumerBootstrap consumerBootstrap) {
        return x -> {
            System.out.println("consumerBootstrapRunner");
            consumerBootstrap.start();
        };
    }

    @Bean
    public Loadbalancer loadbalancer() {
        //return Loadbalancer.Default;
        //return new RandomLoadbalancer();
        return new RoundRibonLoadbalancer();
    }

    @Bean
    public Router router() {
        return Router.Default;
    }

}
