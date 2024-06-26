package com.yuanstack.xrpc.core.provider;

import com.yuanstack.xrpc.core.annotation.XProvider;
import com.yuanstack.xrpc.core.api.RegistryCenter;
import com.yuanstack.xrpc.core.config.AppProperties;
import com.yuanstack.xrpc.core.config.ProviderProperties;
import com.yuanstack.xrpc.core.meta.InstanceMeta;
import com.yuanstack.xrpc.core.meta.ProviderMeta;
import com.yuanstack.xrpc.core.meta.ServiceMeta;
import com.yuanstack.xrpc.core.util.MethodUtils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class ProviderBootstrap implements ApplicationContextAware {

    private ApplicationContext applicationContext;
    private String port;
    private AppProperties appProperties;
    private ProviderProperties providerProperties;
    private MultiValueMap<String, ProviderMeta> skeleton = new LinkedMultiValueMap<>();
    private InstanceMeta instance;
    private RegistryCenter rc;

    public ProviderBootstrap(String port, AppProperties appProperties,
                             ProviderProperties providerProperties) {
        this.port = port;
        this.appProperties = appProperties;
        this.providerProperties = providerProperties;
    }

    @PostConstruct
    public void init() {
        rc = applicationContext.getBean(RegistryCenter.class);
        Map<String, Object> providers = applicationContext.getBeansWithAnnotation(XProvider.class);
        providers.values().forEach(this::genInterface);
    }

    @SneakyThrows
    public void start() {
        String host = InetAddress.getLocalHost().getHostAddress();
        this.instance = InstanceMeta.http(host, Integer.valueOf(port));
        this.instance.getParameters().putAll(providerProperties.getMetas());
        providerProperties.getMetas().forEach((k, v) -> log.debug("instance Parameters key:value is [{}:{}]", k, v));
        this.skeleton.keySet().forEach(this::registerService);
    }

    @PreDestroy
    public void stop() {
        skeleton.keySet().forEach(this::unregisterService);
    }

    private void registerService(String service) {
        ServiceMeta serviceMeta = ServiceMeta.builder()
                .app(appProperties.getId())
                .namespace(appProperties.getNamespace())
                .env(appProperties.getEnv())
                .name(service).build();
        rc.register(serviceMeta, instance);
    }

    private void unregisterService(String service) {
        ServiceMeta serviceMeta = ServiceMeta.builder()
                .app(appProperties.getId())
                .namespace(appProperties.getNamespace())
                .env(appProperties.getEnv())
                .name(service).build();
        rc.register(serviceMeta, instance);
    }

    private void genInterface(Object impl) {
        Arrays.stream(impl.getClass().getInterfaces()).forEach(service -> {
            Method[] methods = service.getMethods();
            for (Method method : methods) {
                if (MethodUtils.isObjectMethod(method)) {
                    continue;
                }

                createProvider(service, impl, method);
            }
        });
    }

    private void createProvider(Class<?> service, Object impl, Method method) {
        ProviderMeta meta = new ProviderMeta();
        meta.setMethod(method);
        meta.setMethodSign(MethodUtils.generateMethodSign(method));
        meta.setServiceImpl(impl);
        log.info("create provider :" + meta);
        skeleton.add(service.getCanonicalName(), meta);
    }
}
