package com.yuanstack.xrpc.core.config;

import com.yuanstack.xrpc.core.api.*;
import com.yuanstack.xrpc.core.cluster.GrayRouter;
import com.yuanstack.xrpc.core.cluster.RoundRibonLoadbalancer;
import com.yuanstack.xrpc.core.consumer.ConsumerBootstrap;
import com.yuanstack.xrpc.core.filter.ParameterFilter;
import com.yuanstack.xrpc.core.meta.InstanceMeta;
import com.yuanstack.xrpc.core.registry.zk.ZkRegistryCenter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;

import java.util.List;

/**
 * @author Sylvan
 * @date 2024/03/10  13:22
 */
@Slf4j
@Configuration
@Import({AppConfigProperties.class, ConsumerConfigProperties.class})
public class ConsumerConfig {

    final AppConfigProperties appConfigProperties;

    final ConsumerConfigProperties consumerConfigProperties;

    public ConsumerConfig(AppConfigProperties appConfigProperties, ConsumerConfigProperties consumerConfigProperties) {
        this.appConfigProperties = appConfigProperties;
        this.consumerConfigProperties = consumerConfigProperties;
    }

    @Bean
    ConsumerBootstrap consumerBootstrap() {
        return new ConsumerBootstrap();
    }

    @Bean
    @Order(Integer.MIN_VALUE + 1)
    public ApplicationRunner consumerBootstrapRunner(@Autowired ConsumerBootstrap consumerBootstrap) {
        return x -> {
            log.info("consumerBootstrapRunner");
            consumerBootstrap.start();
        };
    }

    @Bean
    public Loadbalancer<InstanceMeta> loadbalancer() {
        return new RoundRibonLoadbalancer<>();
    }

    @Bean
    public Router<InstanceMeta> router() {
        log.debug("GrayRouter grayRatio is [{}]", consumerConfigProperties.getGrayRatio());
        return new GrayRouter(consumerConfigProperties.getGrayRatio());
    }

    @Bean
    public Filter defaultFilter() {
        return new ParameterFilter();
    }

    @Bean
    public Filter mockFilter() {
        return Filter.Default;
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public RegistryCenter consumerRegistryCenter() {
        return new ZkRegistryCenter();
    }

    @Bean
    public RpcContext createContext(@Autowired Router<InstanceMeta> router,
                                    @Autowired Loadbalancer<InstanceMeta> loadbalancer,
                                    @Autowired List<Filter> filters) {
        RpcContext rpcContext = new RpcContext();
        rpcContext.setRouter(router);
        rpcContext.setLoadbalancer(loadbalancer);
        rpcContext.setFilters(filters);
        rpcContext.getParameters().put("app.id", appConfigProperties.getId());
        rpcContext.getParameters().put("app.namespace", appConfigProperties.getNamespace());
        rpcContext.getParameters().put("app.env", appConfigProperties.getEnv());

        rpcContext.getParameters().put("consumer.retries", String.valueOf(consumerConfigProperties.getRetries()));
        rpcContext.getParameters().put("consumer.timeout", String.valueOf(consumerConfigProperties.getTimeout()));
        rpcContext.getParameters().put("consumer.grayRatio", String.valueOf(consumerConfigProperties.getGrayRatio()));
        rpcContext.getParameters().put("consumer.faultLimit", String.valueOf(consumerConfigProperties.getFaultLimit()));
        rpcContext.getParameters().put("consumer.halfOpenInitialDelay", String.valueOf(consumerConfigProperties.getHalfOpenInitialDelay()));
        rpcContext.getParameters().put("consumer.halfOpenDelay", String.valueOf(consumerConfigProperties.getHalfOpenDelay()));
        return rpcContext;
    }
}
