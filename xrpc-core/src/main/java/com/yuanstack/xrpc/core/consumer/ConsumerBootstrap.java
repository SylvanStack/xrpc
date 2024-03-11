package com.yuanstack.xrpc.core.consumer;

import com.yuanstack.xrpc.core.annotation.XConsumer;
import com.yuanstack.xrpc.core.annotation.XProvider;
import com.yuanstack.xrpc.core.api.RpcRequest;
import com.yuanstack.xrpc.core.api.RpcResponse;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;

/**
 * @author Sylvan
 * @date 2024/03/10  13:20
 */
@Data
public class ConsumerBootstrap implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    private Map<String, Object> stub = new HashMap<>();

    public void start() {
        String[] beanDefinitionNames = applicationContext.getBeanDefinitionNames();
        for (String beanDefinitionName : beanDefinitionNames) {
            Object bean = applicationContext.getBean(beanDefinitionName);
            List<Field> fields = findAnnotatedField(bean.getClass());

            fields.forEach(field -> {
                try {
                    System.out.println("consumer name ====>" + field.getName());
                    Class<?> service = field.getType();
                    String serviceName = service.getCanonicalName();

                    Object consumer = stub.get(serviceName);
                    if (consumer == null) {
                        consumer = createConsumer(service);
                    }

                    field.setAccessible(true);
                    field.set(bean, consumer);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    private Object createConsumer(Class<?> service) {
        return Proxy.newProxyInstance(service.getClassLoader(), new Class[]{service}, new XInvocationHandler(service));
    }

    private List<Field> findAnnotatedField(Class<?> aClass) {
        List<Field> result = new ArrayList<>();
        while (aClass != null) {
            Field[] fields = aClass.getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(XConsumer.class)) {
                    result.add(field);
                }
            }

            aClass = aClass.getSuperclass();
        }
        return result;
    }
}
