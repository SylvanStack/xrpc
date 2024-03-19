package com.yuanstack.xrpc.demo.consumer;

import com.yuanstack.xrpc.core.annotation.XConsumer;
import com.yuanstack.xrpc.core.consumer.ConsumerConfig;
import com.yuanstack.xrpc.demo.api.dto.Order;
import com.yuanstack.xrpc.demo.api.dto.User;
import com.yuanstack.xrpc.demo.api.service.OrderService;
import com.yuanstack.xrpc.demo.api.service.UserService;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@SpringBootApplication
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
            System.out.println("consumerRunner");
            User user = userService.findById(1);
            System.out.println(user);

            // 重载Case
            User user2 = userService.findById(2, "Stack");
            System.out.println(user2);

            Order order = orderService.findById(2);
            System.out.println(order);

            // 异常case
            //Order order404 = orderService.findById(404);
            //System.out.println(order404);

            // 返回基本类型Case
            int userId = userService.getId(2);
            System.out.println(userId);

            System.out.println(orderService.toString());
        };
    }

}
