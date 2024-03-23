package com.yuanstack.xrpc.core.provider;

import com.yuanstack.xrpc.core.annotation.XProvider;
import com.yuanstack.xrpc.core.api.RegistryCenter;
import com.yuanstack.xrpc.core.meta.ProviderMeta;
import com.yuanstack.xrpc.core.util.MethodUtils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Data;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Map;

/**
 * 服务提供者启动类
 *
 * @author Sylvan
 * @date 2024/03/10  13:20
 */
@Data
public class ProviderBootstrap implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    private MultiValueMap<String, ProviderMeta> skeleton = new LinkedMultiValueMap<>();

    private String instance;

    @Value("${server.port}")
    private String port;

    @PostConstruct
    public void init() {
        Map<String, Object> providers = applicationContext.getBeansWithAnnotation(XProvider.class);
        providers.values().forEach(this::genInterface);

    }

    @SneakyThrows
    public void start() {
        String ip = InetAddress.getLocalHost().getHostAddress();
        instance = ip + "_" + port;
        skeleton.keySet().forEach(this::registerService);
    }

    @PreDestroy
    public void stop() {
        skeleton.keySet().forEach(this::unregisterService);
    }

    private void registerService(String service) {
        RegistryCenter rc = applicationContext.getBean(RegistryCenter.class);
        rc.register(service, instance);
    }

    private void unregisterService(String service) {
        RegistryCenter rc = applicationContext.getBean(RegistryCenter.class);
        rc.register(service, instance);
    }

    private void genInterface(Object x) {
        Arrays.stream(x.getClass().getInterfaces()).forEach(anInterface -> {
            Method[] methods = anInterface.getMethods();
            for (Method method : methods) {
                if (MethodUtils.isObjectMethod(method)) {
                    continue;
                }

                createProvider(anInterface, x, method);
            }
        });
    }

    private void createProvider(Class<?> anInterface, Object x, Method method) {
        ProviderMeta meta = new ProviderMeta();
        meta.setMethod(method);
        meta.setMethodSign(MethodUtils.generateMethodSign(method));
        meta.setServiceImpl(x);
        System.out.println("create provider :" + meta);
        skeleton.add(anInterface.getCanonicalName(), meta);
    }
}
