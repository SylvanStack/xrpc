package com.yuanstack.xrpc.demo.provider;

import com.yuanstack.xrpc.core.test.TestZookeeperServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
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
        System.out.println("Unit tests");
    }

    @AfterAll
    static void destroy() {
        testZookeeperServer.stop();
    }

}
