package com.yuanstack.xrpc.core.annotation;

import com.yuanstack.xrpc.core.config.ConsumerConfig;
import com.yuanstack.xrpc.core.config.ProviderConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 组合一个入口
 *
 * @author Sylvan
 * @date 2024/04/08  22:01
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Import({ProviderConfig.class, ConsumerConfig.class})
public @interface EnableXrpc {

}
