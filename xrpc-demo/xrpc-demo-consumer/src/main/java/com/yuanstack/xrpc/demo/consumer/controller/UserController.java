package com.yuanstack.xrpc.demo.consumer.controller;

import com.yuanstack.xrpc.core.annotation.XConsumer;
import com.yuanstack.xrpc.core.api.Router;
import com.yuanstack.xrpc.core.cluster.GrayRouter;
import com.yuanstack.xrpc.core.meta.InstanceMeta;
import com.yuanstack.xrpc.demo.api.dto.User;
import com.yuanstack.xrpc.demo.api.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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

    @Resource
    private Router<InstanceMeta> router;

    @RequestMapping("/gray/")
    public String gray(@RequestParam("ratio") int ratio) {
        ((GrayRouter) router).setGrayRatio(ratio);
        return "OK-new gray ratio is " + ratio;
    }

    @PostMapping("/")
    public User findById(@RequestParam("userId") Integer userId) {
        return userService.findById(userId);
    }

    @PostMapping("/timeout")
    public User findTimeOut(@RequestParam("timeout") Integer timeout) {
        return userService.findTimeOut(timeout);
    }
}
