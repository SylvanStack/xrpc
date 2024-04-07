package com.yuanstack.xrpc.core.consumer;

import com.yuanstack.xrpc.core.annotation.XConsumer;
import com.yuanstack.xrpc.core.api.*;
import com.yuanstack.xrpc.core.meta.InstanceMeta;
import com.yuanstack.xrpc.core.meta.ServiceMeta;
import com.yuanstack.xrpc.core.util.MethodUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 消费端启动类
 *
 * @author Sylvan
 * @date 2024/03/10  13:20
 */
@Data
@Slf4j
public class ConsumerBootstrap implements ApplicationContextAware, EnvironmentAware {

    private ApplicationContext applicationContext;
    private Environment environment;

    private Map<String, Object> stub = new HashMap<>();
    @Value("${app.id}")
    private String app;
    @Value("${app.namespace}")
    private String namespace;
    @Value("${app.env}")
    private String env;
    @Value("${app.retries}")
    private Integer retries;
    @Value("${app.timeout}")
    private Integer timeout;
    @Value("${app.grayRatio}")
    private Integer grayRatio;

    @Value("${app.faultLimit}")
    private int faultLimit;

    @Value("${app.halfOpenInitialDelay}")
    private int halfOpenInitialDelay;

    @Value("${app.halfOpenDelay}")
    private int halfOpenDelay;

    public void start() {
        RegistryCenter registryCenter = applicationContext.getBean(RegistryCenter.class);
        RpcContext rpcContext = createContext();

        String[] beanDefinitionNames = applicationContext.getBeanDefinitionNames();
        for (String beanDefinitionName : beanDefinitionNames) {
            Object bean = applicationContext.getBean(beanDefinitionName);
            List<Field> fields = MethodUtils.findAnnotatedField(bean.getClass(), XConsumer.class);

            fields.forEach(field -> {
                Class<?> service = field.getType();
                String serviceName = service.getCanonicalName();
                log.info("consumer name is [{}]", field.getName());
                try {
                    Object consumer = stub.get(serviceName);
                    if (consumer == null) {
                        consumer = createConsumerFromRegistry(service, rpcContext, registryCenter);
                        stub.put(serviceName, consumer);
                    }

                    field.setAccessible(true);
                    field.set(bean, consumer);
                } catch (Exception ex) {
                    // ignore and print it
                    log.warn("Field [{}.{}] create consumer failed.", serviceName, field.getName());
                    log.error("Ignore and print it as: ", ex);
                }
            });
        }
    }

    @NotNull
    private RpcContext createContext() {
        RpcContext rpcContext = new RpcContext();

        Router<InstanceMeta> router = applicationContext.getBean(Router.class);
        rpcContext.setRouter(router);

        Loadbalancer<InstanceMeta> loadbalancer = applicationContext.getBean(Loadbalancer.class);
        rpcContext.setLoadbalancer(loadbalancer);

        List<Filter> filters = new ArrayList<>(applicationContext.getBeansOfType(Filter.class).values());
        rpcContext.setFilters(filters);

        rpcContext.getParameters().put("app.retries", String.valueOf(retries));
        rpcContext.getParameters().put("app.timeout", String.valueOf(timeout));
        rpcContext.getParameters().put("app.grayRatio", String.valueOf(grayRatio));
        rpcContext.getParameters().put("app.faultLimit", String.valueOf(faultLimit));
        rpcContext.getParameters().put("app.halfOpenInitialDelay", String.valueOf(halfOpenInitialDelay));
        rpcContext.getParameters().put("app.halfOpenDelay", String.valueOf(halfOpenDelay));
        return rpcContext;
    }

    private Object createConsumerFromRegistry(Class<?> service, RpcContext context, RegistryCenter rc) {
        String serviceName = service.getCanonicalName();
        ServiceMeta serviceMeta = ServiceMeta.builder()
                .app(app)
                .namespace(namespace)
                .env(env)
                .name(serviceName).build();
        List<InstanceMeta> providers = rc.fetchAll(serviceMeta);

        rc.subscribe(serviceMeta, event -> {
            providers.clear();
            providers.addAll(event.getData());
        });
        return createConsumer(service, context, providers);
    }


    private Object createConsumer(Class<?> service, RpcContext rpcContext, List<InstanceMeta> providers) {
        return Proxy.newProxyInstance(service.getClassLoader(),
                new Class[]{service},
                new XInvocationHandler(service, rpcContext, providers));
    }
}
