package com.yuanstack.xrpc.demo.provider;

import com.yuanstack.xrpc.core.api.RpcRequest;
import com.yuanstack.xrpc.core.api.RpcResponse;
import com.yuanstack.xrpc.core.provider.ProviderConfig;
import com.yuanstack.xrpc.core.provider.ProviderInvoker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({ProviderConfig.class})
@Slf4j
public class XrpcDemoProviderApplication {

    public static void main(String[] args) {
        SpringApplication.run(XrpcDemoProviderApplication.class, args);
    }

    @Autowired
    private ProviderInvoker providerInvoker;

    @Bean
    ApplicationRunner providerRunner() {
        return x -> {
            // test 1 parameter method
            RpcRequest request = new RpcRequest();
            request.setService("com.yuanstack.xrpc.demo.api.service.UserService");
            request.setMethodSign("findById@1_java.lang.Integer");
            request.setArgs(new Object[]{100});

            RpcResponse<Object> rpcResponse = providerInvoker.invoke(request);
            log.info("return:" + rpcResponse.getData());

            // test 2 parameters method
            RpcRequest request2 = new RpcRequest();
            request2.setService("com.yuanstack.xrpc.demo.api.service.UserService");
            request2.setMethodSign("findById@2_java.lang.Integer_java.lang.String");
            request2.setArgs(new Object[]{101, "Stack"});

            RpcResponse<Object> rpcResponse2 = providerInvoker.invoke(request2);
            log.info("return:" + rpcResponse2.getData());
        };
    }
}
