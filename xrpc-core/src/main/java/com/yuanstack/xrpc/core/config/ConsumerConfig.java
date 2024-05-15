package com.yuanstack.xrpc.core.config;

import com.yuanstack.xrpc.core.api.*;
import com.yuanstack.xrpc.core.cluster.GrayRouter;
import com.yuanstack.xrpc.core.cluster.RoundRibonLoadbalancer;
import com.yuanstack.xrpc.core.consumer.ConsumerBootstrap;
import com.yuanstack.xrpc.core.filter.ContextParameterFilter;
import com.yuanstack.xrpc.core.meta.InstanceMeta;
import com.yuanstack.xrpc.core.registry.xregistry.XRegistryCenter;
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
@Import({AppProperties.class, ConsumerProperties.class})
public class ConsumerConfig {

    final AppProperties appProperties;

    final ConsumerProperties consumerProperties;

    //@Bean
    //@ConditionalOnMissingBean
    //ApolloChangedListener consumer_apolloChangedListener() {
    //    return new ApolloChangedListener();
    //}

    public ConsumerConfig(AppProperties appProperties, ConsumerProperties consumerProperties) {
        this.appProperties = appProperties;
        this.consumerProperties = consumerProperties;
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
        log.debug("GrayRouter grayRatio is [{}]", consumerProperties.getGrayRatio());
        return new GrayRouter(consumerProperties.getGrayRatio());
    }

    @Bean
    public Filter defaultFilter() {
        return new ContextParameterFilter();
    }

    @Bean
    public Filter mockFilter() {
        return Filter.Default;
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public RegistryCenter consumerRegistryCenter() {
        return new XRegistryCenter();
    }

    @Bean
    public RpcContext createContext(@Autowired Router<InstanceMeta> router,
                                    @Autowired Loadbalancer<InstanceMeta> loadbalancer,
                                    @Autowired List<Filter> filters) {
        RpcContext rpcContext = new RpcContext();
        rpcContext.setRouter(router);
        rpcContext.setLoadbalancer(loadbalancer);
        rpcContext.setFilters(filters);
        rpcContext.getParameters().put("app.id", appProperties.getId());
        rpcContext.getParameters().put("app.namespace", appProperties.getNamespace());
        rpcContext.getParameters().put("app.env", appProperties.getEnv());
        rpcContext.setConsumerProperties(consumerProperties);
        return rpcContext;
    }
}
