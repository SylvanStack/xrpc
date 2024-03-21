package com.yuanstack.xrpc.demo.consumer.controller;

import com.yuanstack.xrpc.core.annotation.XConsumer;
import com.yuanstack.xrpc.demo.api.dto.User;
import com.yuanstack.xrpc.demo.api.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Sylvan
 * @date 2024/03/10  13:45
 */
@RestController
public class UserController {

    @XConsumer
    private UserService userService;

    @PostMapping("/")
    public User findById(@RequestParam("userId") Integer userId) {
        return userService.findById(userId);
    }
}
