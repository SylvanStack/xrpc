package com.yuanstack.xrpc.demo.provider.controller;

import com.yuanstack.xrpc.core.api.RpcRequest;
import com.yuanstack.xrpc.core.api.RpcResponse;
import com.yuanstack.xrpc.core.provider.ProviderInvoker;
import com.yuanstack.xrpc.demo.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Sylvan
 * @date 2024/03/10  13:45
 */
@RestController
public class UserController {

    @Autowired
    private ProviderInvoker providerInvoker;
    @Autowired
    private UserService userService;

    //  使用HTTP + JSON 来实现序列化和通信

    @PostMapping("/")
    public RpcResponse<Object> invoke(@RequestBody RpcRequest request) {
        return providerInvoker.invoke(request);
    }

    @PostMapping("/ports")
    public String setPorts(@RequestParam("ports") String ports) {
        return userService.setPorts(ports);
    }
}
