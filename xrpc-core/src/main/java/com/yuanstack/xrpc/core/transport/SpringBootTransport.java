package com.yuanstack.xrpc.core.transport;

import com.yuanstack.xrpc.core.api.RpcRequest;
import com.yuanstack.xrpc.core.api.RpcResponse;
import com.yuanstack.xrpc.core.provider.ProviderInvoker;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Transport for spring boot endpoint.
 *
 * @author Sylvan
 * @date 2024/04/08  22:10
 */
@RestController
public class SpringBootTransport {
    final ProviderInvoker providerInvoker;

    public SpringBootTransport(ProviderInvoker providerInvoker) {
        this.providerInvoker = providerInvoker;
    }

    @RequestMapping("/xrpc")
    public RpcResponse<Object> invoke(@RequestBody RpcRequest request) {
        return providerInvoker.invoke(request);
    }
}
