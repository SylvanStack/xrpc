package com.yuanstack.xrpc.core.consumer;

import com.yuanstack.xrpc.core.annotation.XConsumer;
import com.yuanstack.xrpc.core.api.RegistryCenter;
import com.yuanstack.xrpc.core.api.RpcContext;
import com.yuanstack.xrpc.core.meta.InstanceMeta;
import com.yuanstack.xrpc.core.meta.ServiceMeta;
import com.yuanstack.xrpc.core.util.MethodUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
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

    public void start() {
        RegistryCenter registryCenter = applicationContext.getBean(RegistryCenter.class);
        RpcContext rpcContext = applicationContext.getBean(RpcContext.class);

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

    private Object createConsumerFromRegistry(Class<?> service, RpcContext context, RegistryCenter rc) {
        ServiceMeta serviceMeta = ServiceMeta.builder()
                .app(context.param("app.id"))
                .namespace(context.param("app.namespace"))
                .env(context.param("app.env"))
                .name(service.getCanonicalName()).build();
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
