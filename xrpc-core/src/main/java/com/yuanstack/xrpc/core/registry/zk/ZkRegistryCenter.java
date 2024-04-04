package com.yuanstack.xrpc.core.registry.zk;

import com.alibaba.fastjson.JSON;
import com.yuanstack.xrpc.core.api.RegistryCenter;
import com.yuanstack.xrpc.core.meta.InstanceMeta;
import com.yuanstack.xrpc.core.meta.ServiceMeta;
import com.yuanstack.xrpc.core.registry.ChangeListener;
import com.yuanstack.xrpc.core.registry.Event;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Sylvan
 * @date 2024/03/22  0:05
 */
@Slf4j
public class ZkRegistryCenter implements RegistryCenter {
    private CuratorFramework client = null;
    @Value("${xrpc.zkRoot}")
    private String zkRoot;
    @Value("${xrpc.zkServers}")
    private String zkServers;

    @Override
    public void start() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        client = CuratorFrameworkFactory.builder()
                .connectString(zkServers)
                .namespace(zkRoot)
                .retryPolicy(retryPolicy)
                .build();
        client.start();
        log.info(" Zk client started to servers [" + zkServers + "/" + zkRoot + "] .");
    }

    @Override
    public void stop() {
        log.info(" Zk client stopped.");
        client.close();
    }

    @Override
    public void register(ServiceMeta service, InstanceMeta instance) {
        String servicePath = "/" + service.toPath();
        try {

            // 创建服务的持久化节点
            if (client.checkExists().forPath(servicePath) == null) {
                client.create().withMode(CreateMode.PERSISTENT).forPath(servicePath, service.toMetas().getBytes());
            }

            // 创建实例的临时节点
            String instancePath = servicePath + "/" + instance.toPath();
            if (client.checkExists().forPath(instancePath) == null) {
                client.create().withMode(CreateMode.EPHEMERAL).forPath(instancePath, instance.toMetas().getBytes());
            }
            log.info(" register to Zk：" + instancePath);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void unregister(ServiceMeta service, InstanceMeta instance) {
        String servicePath = "/" + service.toPath();
        try {

            // 创建服务的持久化节点
            if (client.checkExists().forPath(servicePath) == null) {
                return;
            }

            // 创建实例的临时节点
            String instancePath = servicePath + "/" + instance.toPath();
            log.info(" unregister to Zk：" + instancePath);
            client.delete().quietly().forPath(instancePath);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<InstanceMeta> fetchAll(ServiceMeta service) {
        String servicePath = "/" + service.toPath();
        try {
            List<String> nodes = client.getChildren().forPath(servicePath);
            log.info("===> fetchAll form zk:" + servicePath);
            nodes.forEach(System.out::println);

            return mapInstances(nodes, servicePath);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private List<InstanceMeta> mapInstances(List<String> nodes, String servicePath) {
        return nodes.stream().map(node -> {
            String[] split = node.split("_");
            InstanceMeta instanceMeta = InstanceMeta.http(split[0], Integer.valueOf(split[1]));

            String nodePath = servicePath + "/" + node;
            byte[] bytes;
            try {
                bytes = client.getData().forPath(nodePath);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            HashMap params = JSON.parseObject(new String(bytes), HashMap.class);
            params.forEach((k, v) -> log.debug("instance meta params {} : {}", k, v));
            instanceMeta.setParameters(params);
            return instanceMeta;
        }).collect(Collectors.toList());
    }

    @SneakyThrows
    @Override
    public void subscribe(ServiceMeta service, ChangeListener changeListener) {
        final TreeCache cache = TreeCache.newBuilder(client, "/" + service.toPath())
                .setCacheData(true).setMaxDepth(2).build();
        cache.getListenable().addListener(
                (curator, event) -> {
                    // 有任何节点变动都会执行
                    log.info("zk subscribe event" + event);

                    List<InstanceMeta> nodes = fetchAll(service);
                    changeListener.fire(new Event(nodes));
                }
        );

        cache.start();
    }

    @Override
    public void heartbeat() {

    }
}
