package com.yuanstack.xrpc.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * config consumer properties.
 *
 * @author Sylvan
 * @date 2024/04/08  22:03
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "xrpc.consumer")
public class ConsumerConfigProperties {
    // for ha and governance
    private int retries = 1;

    private int timeout = 1000;

    private int faultLimit = 10;

    private int halfOpenInitialDelay = 10_000;

    private int halfOpenDelay = 60_000;

    private int grayRatio = 0;
}
