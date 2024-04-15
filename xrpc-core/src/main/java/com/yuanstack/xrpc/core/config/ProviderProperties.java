package com.yuanstack.xrpc.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Sylvan
 * @date 2024/04/08  22:08
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "xrpc.provider")
public class ProviderProperties {
    // for provider
    Map<String, String> metas = new HashMap<>();
}
