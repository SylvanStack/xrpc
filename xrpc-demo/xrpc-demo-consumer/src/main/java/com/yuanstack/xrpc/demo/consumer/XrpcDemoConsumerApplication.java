package com.yuanstack.xrpc.demo.consumer;

import com.yuanstack.xrpc.core.annotation.XConsumer;
import com.yuanstack.xrpc.core.consumer.ConsumerConfig;
import com.yuanstack.xrpc.demo.api.dto.Order;
import com.yuanstack.xrpc.demo.api.dto.User;
import com.yuanstack.xrpc.demo.api.service.OrderService;
import com.yuanstack.xrpc.demo.api.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.util.Arrays;

@SpringBootApplication
@Slf4j
@Import({ConsumerConfig.class})
public class XrpcDemoConsumerApplication {

    @XConsumer
    private UserService userService;
    @XConsumer
    private OrderService orderService;

    public static void main(String[] args) {
        SpringApplication.run(XrpcDemoConsumerApplication.class, args);
    }

    @Bean
    public ApplicationRunner consumerRunner() {
        return x -> {
            log.info("=========consumerRunner==========");

            long start = System.currentTimeMillis();
            User user = userService.findTimeOut(500);
            log.info("time out case response user is {}, use time is {} ms",
                    user, System.currentTimeMillis() - start);

            //   allTestCase();
        };
    }

    private void allTestCase() {
        User user = userService.findById(1);
        log.info(String.valueOf(user));

        // 类型转换
        log.info(String.valueOf(userService.getUserId(1L)));
        log.info(String.valueOf(userService.getUserId(new User(102, "stack"))));
        log.info(String.valueOf(userService.getUserId(10F)));
        log.info(Arrays.toString(userService.getUserIds(new int[]{1, 2})));

        // 重载Case
        User userService2 = userService.findById(2, "Stack");
        log.info(String.valueOf(userService2));

        // 重载Case
        log.info(userService.getName(2));
        log.info(userService.getName("Stack"));

        Order order = orderService.findById(2);
        log.info(String.valueOf(order));

        // 异常case
        Order order404 = orderService.findById(404);
        log.info(String.valueOf(order404));

        // 返回基本类型Case
        int userId = userService.getId(2);
        log.info(String.valueOf(userId));

        // Object 方法调用
        log.info(orderService.toString());
    }

}
