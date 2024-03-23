package com.yuanstack.xrpc.core.consumer;

import com.yuanstack.xrpc.core.annotation.XConsumer;
import com.yuanstack.xrpc.core.api.Loadbalancer;
import com.yuanstack.xrpc.core.api.RegistryCenter;
import com.yuanstack.xrpc.core.api.Router;
import com.yuanstack.xrpc.core.api.RpcContext;
import com.yuanstack.xrpc.core.util.MethodUtils;
import lombok.Data;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Sylvan
 * @date 2024/03/10  13:20
 */
@Data
public class ConsumerBootstrap implements ApplicationContextAware, EnvironmentAware {

    private ApplicationContext applicationContext;
    private Environment environment;

    private Map<String, Object> stub = new HashMap<>();

    public void start() {
        Router router = applicationContext.getBean(Router.class);
        Loadbalancer loadbalancer = applicationContext.getBean(Loadbalancer.class);

        RpcContext rpcContext = new RpcContext();
        rpcContext.setRouter(router);
        rpcContext.setLoadbalancer(loadbalancer);
        RegistryCenter registryCenter = applicationContext.getBean(RegistryCenter.class);

        String[] beanDefinitionNames = applicationContext.getBeanDefinitionNames();
        for (String beanDefinitionName : beanDefinitionNames) {
            Object bean = applicationContext.getBean(beanDefinitionName);
            List<Field> fields = MethodUtils.findAnnotatedField(bean.getClass(), XConsumer.class);

            fields.forEach(field -> {
                try {
                    System.out.println("consumer name ====>" + field.getName());
                    Class<?> service = field.getType();
                    String serviceName = service.getCanonicalName();

                    Object consumer = stub.get(serviceName);
                    if (consumer == null) {
                        consumer = createConsumerFromRegistry(service, rpcContext, registryCenter);
                    }

                    field.setAccessible(true);
                    field.set(bean, consumer);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    private Object createConsumerFromRegistry(Class<?> service, RpcContext context, RegistryCenter rc) {
        String serviceName = service.getCanonicalName();
        List<String> providers = mapUrls(rc.fetchAll(serviceName));

        rc.subscribe(serviceName, event -> {
            providers.clear();
            providers.addAll(mapUrls(event.getData()));
        });
        return createConsumer(service, context, providers);
    }

    private List<String> mapUrls(List<String> nodes) {
        if (nodes == null || nodes.isEmpty()) {
            return Collections.emptyList();
        }

        return nodes.stream()
                .map(i -> "http://" + i.replace("_", ":"))
                .collect(Collectors.toList());
    }

    private Object createConsumer(Class<?> service, RpcContext rpcContext, List<String> providers) {
        return Proxy.newProxyInstance(service.getClassLoader(),
                new Class[]{service},
                new XInvocationHandler(service, rpcContext, providers));
    }
}
