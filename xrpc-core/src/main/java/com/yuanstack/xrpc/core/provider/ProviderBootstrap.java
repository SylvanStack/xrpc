package com.yuanstack.xrpc.core.provider;

import com.yuanstack.xrpc.core.annotation.XProvider;
import com.yuanstack.xrpc.core.api.RpcRequest;
import com.yuanstack.xrpc.core.api.RpcResponse;
import com.yuanstack.xrpc.core.meta.ProviderMeta;
import com.yuanstack.xrpc.core.util.MethodUtils;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author Sylvan
 * @date 2024/03/10  13:20
 */
@Data
public class ProviderBootstrap implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    private MultiValueMap<String, ProviderMeta> skeleton = new LinkedMultiValueMap<>();

    @PostConstruct
    public void buildProviders() {
        Map<String, Object> providers = applicationContext.getBeansWithAnnotation(XProvider.class);
        providers.values().forEach(this::genInterface);
    }

    private void genInterface(Object x) {
        Class<?> anInterface = x.getClass().getInterfaces()[0];
        Method[] methods = anInterface.getMethods();
        for (Method method : methods) {
            if (MethodUtils.isObjectMethod(method)) {
                continue;
            }

            createProvider(anInterface, x, method);
        }
    }

    private void createProvider(Class<?> anInterface, Object x, Method method) {
        ProviderMeta meta = new ProviderMeta();
        meta.setMethod(method);
        meta.setMethodSign(MethodUtils.generateMethodSign(method));
        meta.setServiceImpl(x);
        System.out.println("create provider :" + meta);
        skeleton.add(anInterface.getCanonicalName(), meta);
    }

    public RpcResponse invoke(RpcRequest request) {
        RpcResponse rpcResponse = new RpcResponse();
        List<ProviderMeta> providerMetas = skeleton.get(request.getService());
        try {
            ProviderMeta providerMeta = findProviderMeta(providerMetas, request.getMethodSign());
            Method method = providerMeta.getMethod();
            Object result = method.invoke(providerMeta.getServiceImpl(), request.getArgs());
            rpcResponse.setData(result);
            rpcResponse.setStatus(true);
        } catch (InvocationTargetException e) {
            rpcResponse.setEx(new RuntimeException(e.getTargetException().getMessage()));
        } catch (IllegalAccessException e) {
            rpcResponse.setEx(new RuntimeException(e.getMessage()));
        }
        return rpcResponse;
    }

    private ProviderMeta findProviderMeta(List<ProviderMeta> providerMetas, String methodSign) {
        Optional<ProviderMeta> first = providerMetas.stream()
                .filter(providerMeta -> providerMeta.getMethodSign().equals(methodSign))
                .findFirst();
        return first.orElse(null);
    }
}
