package com.yuanstack.xrpc.core.registry;

import com.yuanstack.xrpc.core.api.RegistryCenter;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.util.List;

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
    public void register(String service, String instance) {
        String servicePath = "/" + service;
        try {

            // 创建服务的持久化节点
            if (client.checkExists().forPath(servicePath) == null) {
                client.create().withMode(CreateMode.PERSISTENT).forPath(servicePath, "service".getBytes());
            }

            // 创建实例的临时节点
            String instancePath = servicePath + "/" + instance;
            if (client.checkExists().forPath(instancePath) == null) {
                client.create().withMode(CreateMode.EPHEMERAL).forPath(instancePath, "provider".getBytes());
            }
            System.out.println("====> register to Zk：" + instancePath);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void unregister(String service, String instance) {
        String servicePath = "/" + service;
        try {

            // 创建服务的持久化节点
            if (client.checkExists().forPath(servicePath) == null) {
                return;
            }

            // 创建实例的临时节点
            String instancePath = servicePath + "/" + instance;
            System.out.println("====> unregister to Zk：" + instancePath);
            client.delete().quietly().forPath(instancePath);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<String> fetchAll(String service) {
        return null;
    }

    @Override
    public void subscribe() {

    }
}
