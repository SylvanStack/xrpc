package com.yuanstack.xrpc.demo.provider;

import com.yuanstack.xrpc.core.test.TestZookeeperServer;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@Slf4j
@TestPropertySource(locations = "classpath:application.yml")
class XrpcDemoProviderApplicationTests {

    static TestZookeeperServer testZookeeperServer;


    @BeforeAll
    static void init() {
        testZookeeperServer = new TestZookeeperServer();
        testZookeeperServer.start();
    }

    @Test
    void contextLoads() {
        log.info("Unit tests");
    }

    @AfterAll
    static void destroy() {
        testZookeeperServer.stop();
    }

}
