package com.yuanstack.xrpc.core.registry.xregistry;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.yuanstack.xrpc.core.api.RegistryCenter;
import com.yuanstack.xrpc.core.consumer.HttpInvoker;
import com.yuanstack.xrpc.core.meta.InstanceMeta;
import com.yuanstack.xrpc.core.meta.ServiceMeta;
import com.yuanstack.xrpc.core.registry.ChangeListener;
import com.yuanstack.xrpc.core.registry.Event;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * implementation of XRegistryCenter.
 *
 * @author Sylvan
 * @date 2024/05/15  0:31
 */
@Slf4j
public class XRegistryCenter implements RegistryCenter {

    private static final String REGISTER_PATH = "/register";
    private static final String UNREGISTER_PATH = "/unregister";
    private static final String FIND_ALL_PATH = "/findAll";
    private static final String VERSION_PATH = "/version";
    private static final String RENEWS_PATH = "/renews";

    @Value("${xregistry.servers}")
    String servers;

    private final Map<String, Long> VERSIONS = new HashMap<>();

    MultiValueMap<InstanceMeta, ServiceMeta> RENEWS = new LinkedMultiValueMap<>();
    XHeathChecker healthChecker = new XHeathChecker();

    @Override
    public void start() {
        log.info(" ====>>>> [XRegistry] : start with server: {}", servers);
        healthChecker.start();
        providerCheck();
    }

    @Override
    public void stop() {
        log.info(" ====>>>> [XRegistry] : stop with server: {}", servers);
        healthChecker.stop();
    }

    @Override
    public void register(ServiceMeta service, InstanceMeta instance) {
        log.info(" ====>>>> [XRegistry] : register instance {} to {}", instance.toUrl(), service.toPath());
        InstanceMeta inst = HttpInvoker.httpPost(JSON.toJSONString(instance), regPath(service), InstanceMeta.class);
        log.info(" ====>>>> [XRegistry] : registered {}", inst);
        RENEWS.add(instance, service);
    }

    @Override
    public void unregister(ServiceMeta service, InstanceMeta instance) {
        log.info(" ====>>>> [XRegistry] : unregister instance {} to {}", instance.toUrl(), service.toPath());
        InstanceMeta inst = HttpInvoker.httpPost(JSON.toJSONString(instance), unRegPath(service), InstanceMeta.class);
        log.info(" ====>>>> [XRegistry] : unregistered {}", inst);
        RENEWS.remove(instance, service);
    }

    @Override
    public List<InstanceMeta> fetchAll(ServiceMeta service) {
        log.info(" ====>>>> [XRegistry] : find all instances for {}", service.toPath());
        List<InstanceMeta> instances = HttpInvoker.httpGet(findAllPath(service), new TypeReference<List<InstanceMeta>>() {
        });
        log.info(" ====>>>> [XRegistry] : findAll = {}", instances);
        return instances;
    }

    @Override
    public void subscribe(ServiceMeta service, ChangeListener listener) {
        healthChecker.consumerCheck(() -> {
            Long version = VERSIONS.getOrDefault(service.toPath(), -1L);
            Long newVersion = HttpInvoker.httpGet(versionPath(service), Long.class);
            log.info(" ====>>>> [XRegistry] : version = {}, newVersion = {}", version, newVersion);
            if (newVersion > version) {
                List<InstanceMeta> instances = fetchAll(service);
                listener.fire(new Event(instances));
                VERSIONS.put(service.toPath(), newVersion);
            }
        });
    }

    private String regPath(ServiceMeta service) {
        return path(REGISTER_PATH, service);
    }

    private String unRegPath(ServiceMeta service) {
        return path(UNREGISTER_PATH, service);
    }

    private String findAllPath(ServiceMeta service) {
        return path(FIND_ALL_PATH, service);
    }

    private String versionPath(ServiceMeta service) {
        return path(VERSION_PATH, service);
    }

    private String path(String context, ServiceMeta service) {
        return servers + context + "?service=" + service.toPath();
    }

    private String renewsPath(List<ServiceMeta> serviceList) {
        return path(RENEWS_PATH, serviceList);
    }

    private String path(String context, List<ServiceMeta> serviceList) {
        StringBuffer sb = new StringBuffer();
        for (ServiceMeta service : serviceList) {
            sb.append(service.toPath()).append(",");
        }
        String services = sb.toString();
        if (services.endsWith(",")) services = services.substring(0, services.length() - 1);
        log.info(" ====>>>> [XRegistry] : renew instance for {}", services);
        return servers + context + "?services=" + services;
    }

    private void providerCheck() {
        healthChecker.providerCheck(() -> {
            RENEWS.keySet().stream().forEach(
                    instance -> {
                        Long timestamp = HttpInvoker.httpPost(JSON.toJSONString(instance),
                                renewsPath(RENEWS.get(instance)), Long.class);
                        log.info(" ====>>>> [XRegistry] : renew instance {} at {}", instance, timestamp);
                    }
            );
        });
    }
}
