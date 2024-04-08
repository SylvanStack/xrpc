package com.yuanstack.xrpc.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * config app properties.
 *
 * @author Sylvan
 * @date 2024/04/08  22:02
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "xrpc.app")
public class AppConfigProperties {
    // for app instance
    private String id = "app1";

    private String namespace = "public";

    private String env = "dev";
}
