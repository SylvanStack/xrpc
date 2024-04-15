package com.yuanstack.xrpc.core.api;

import com.yuanstack.xrpc.core.config.ConsumerProperties;
import com.yuanstack.xrpc.core.meta.InstanceMeta;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Sylvan
 * @date 2024/03/21  23:06
 */
@Data
public class RpcContext {
    private List<Filter> filters;

    private Router<InstanceMeta> router;

    private Loadbalancer<InstanceMeta> loadbalancer;

    private Map<String, String> parameters = new HashMap<>();

    private ConsumerProperties consumerProperties;

    public String param(String key) {
        return parameters.get(key);
    }

    public static ThreadLocal<Map<String,String>> ContextParameters = ThreadLocal.withInitial(HashMap::new);

    public static void setContextParameter(String key, String value) {
        ContextParameters.get().put(key, value);
    }

    public static String getContextParameter(String key) {
        return ContextParameters.get().get(key);
    }

    public static void removeContextParameter(String key) {
        ContextParameters.get().remove(key);
    }
}
