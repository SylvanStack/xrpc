package com.yuanstack.xrpc.demo.provider;

import com.yuanstack.xrpc.core.api.RpcRequest;
import com.yuanstack.xrpc.core.api.RpcResponse;
import com.yuanstack.xrpc.core.provider.ProviderBootstrap;
import com.yuanstack.xrpc.core.provider.ProviderConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({ProviderConfig.class})
public class XrpcDemoProviderApplication {

    public static void main(String[] args) {
        SpringApplication.run(XrpcDemoProviderApplication.class, args);
    }

    @Autowired
    private ProviderBootstrap providerBootstrap;

    @Bean
    ApplicationRunner providerRunner() {
        return x -> {
            RpcRequest request = new RpcRequest();
            request.setService("com.yuanstack.xrpc.demo.api.service.UserService");
            request.setMethod("findById");
            request.setArgs(new Object[]{100});

            RpcResponse rpcResponse = providerBootstrap.invokeRequest(request);
            System.out.println("return:" + rpcResponse.getData());
        };
    }
}
