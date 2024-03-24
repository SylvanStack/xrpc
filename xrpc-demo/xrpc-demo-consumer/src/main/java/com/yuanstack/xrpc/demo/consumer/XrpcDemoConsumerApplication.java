package com.yuanstack.xrpc.demo.consumer;

import com.yuanstack.xrpc.core.annotation.XConsumer;
import com.yuanstack.xrpc.core.consumer.ConsumerConfig;
import com.yuanstack.xrpc.demo.api.dto.User;
import com.yuanstack.xrpc.demo.api.service.OrderService;
import com.yuanstack.xrpc.demo.api.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

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

            User user = userService.findById(1);
            log.info(String.valueOf(user));

            // 类型转换
            //log.info(userService.getUserId(1L));
            //log.info(userService.getUserId(new User(102, "stack")));
            //log.info(userService.getUserId(10F));
            //log.info(Arrays.toString(userService.getUserIds(new int[]{1, 2})));

            // 重载Case
            //User user2 = userService.findById(2, "Stack");
            //log.info(user2);

            // 重载Case
            //log.info(userService.getName(2));
            //log.info(userService.getName("Stack"));
            //
            //Order order = orderService.findById(2);
            //log.info(order);

            // 异常case
            //Order order404 = orderService.findById(404);
            //log.info(order404);

            // 返回基本类型Case
            //int userId = userService.getId(2);
            //log.info(userId);

            // Object 方法调用
            //log.info(orderService.toString());
        };
    }

}
