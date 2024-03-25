package com.yuanstack.xrpc.demo.consumer;

import com.yuanstack.xrpc.core.test.TestZookeeperServer;
import com.yuanstack.xrpc.demo.provider.XrpcDemoProviderApplication;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(locations="classpath:application.yml")
class XrpcDemoConsumerApplicationTests {

    static ApplicationContext context;
    static TestZookeeperServer testZookeeperServer;


    @BeforeAll
    static void init() {
        testZookeeperServer = new TestZookeeperServer();
        testZookeeperServer.start();
        context = SpringApplication.run(XrpcDemoProviderApplication.class,
                "--server.port=8084", "--logging.level.root=debug", "--xrpc.zkServers=localhost:2182");
    }

    @Test
    void contextLoads() {
        System.out.println("Unit tests");
    }

    @AfterAll
    static void destroy() {
        testZookeeperServer.stop();
        SpringApplication.exit(context, () -> 1);
    }
}
