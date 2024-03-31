package com.yuanstack.xrpc.core.test;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.test.InstanceSpec;
import org.apache.curator.test.TestingCluster;
import org.apache.curator.utils.CloseableUtils;

/**
 * @author Sylvan
 * @date 2024/03/25  22:50
 */
@Slf4j
public class TestZookeeperServer {
    TestingCluster cluster;

    @SneakyThrows
    public void start() {
        InstanceSpec instanceSpec = new InstanceSpec(
                null, 2182, -1, -1,
                true, -1, -1, 1);
        cluster = new TestingCluster(instanceSpec);
        log.debug("TestingZooKeeperServer starting ...");
        cluster.start();
        cluster.getServers().forEach(s -> System.out.println(s.getInstanceSpec()));
        log.debug("TestingZooKeeperServer started.");
    }

    @SneakyThrows
    public void stop() {
        log.debug("TestingZooKeeperServer stopping ...");
        cluster.stop();
        CloseableUtils.closeQuietly(cluster);
        log.debug("TestingZooKeeperServer stopped.");
    }
}
