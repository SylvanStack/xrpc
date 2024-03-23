package com.yuanstack.xrpc.core.registry;

import com.yuanstack.xrpc.core.api.RegistryCenter;
import com.yuanstack.xrpc.core.meta.InstanceMeta;
import lombok.SneakyThrows;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Sylvan
 * @date 2024/03/22  0:05
 */
public class ZkRegistryCenter implements RegistryCenter {
    private CuratorFramework client = null;

    @Override
    public void start() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        client = CuratorFrameworkFactory.builder()
                .connectString("localhost:2181")
                .namespace("xrpc")
                .retryPolicy(retryPolicy)
                .build();
        client.start();
        System.out.println("====> Zk client started.");
    }

    @Override
    public void stop() {
        System.out.println("====> Zk client stopped.");
        client.close();
    }

    @Override
    public void register(String service, InstanceMeta instance) {
        String servicePath = "/" + service;
        try {

            // 创建服务的持久化节点
            if (client.checkExists().forPath(servicePath) == null) {
                client.create().withMode(CreateMode.PERSISTENT).forPath(servicePath, "service".getBytes());
            }

            // 创建实例的临时节点
            String instancePath = servicePath + "/" + instance.toPath();
            if (client.checkExists().forPath(instancePath) == null) {
                client.create().withMode(CreateMode.EPHEMERAL).forPath(instancePath, "provider".getBytes());
            }
            System.out.println("====> register to Zk：" + instancePath);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void unregister(String service, InstanceMeta instance) {
        String servicePath = "/" + service;
        try {

            // 创建服务的持久化节点
            if (client.checkExists().forPath(servicePath) == null) {
                return;
            }

            // 创建实例的临时节点
            String instancePath = servicePath + "/" + instance.toPath();
            System.out.println("====> unregister to Zk：" + instancePath);
            client.delete().quietly().forPath(instancePath);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<InstanceMeta> fetchAll(String service) {
        String servicePath = "/" + service;
        try {
            List<String> nodes = client.getChildren().forPath(servicePath);
            System.out.println("===> fetchAll form zk:" + servicePath);
            nodes.forEach(System.out::println);

            return mapInstances(nodes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static List<InstanceMeta> mapInstances(List<String> nodes) {
        return nodes.stream().map(x -> {
            String[] split = x.split("_");
            return InstanceMeta.http(split[0], Integer.valueOf(split[1]));
        }).collect(Collectors.toList());
    }

    @SneakyThrows
    @Override
    public void subscribe(String service, ChangeListener changeListener) {
        final TreeCache cache = TreeCache.newBuilder(client, "/" + service)
                .setCacheData(true).setMaxDepth(2).build();
        cache.getListenable().addListener(
                (curator, event) -> {
                    // 有任何节点变动都会执行
                    System.out.println("zk subscribe event" + event);

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
