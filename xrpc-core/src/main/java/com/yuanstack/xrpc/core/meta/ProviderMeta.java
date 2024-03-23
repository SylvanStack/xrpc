package com.yuanstack.xrpc.core.meta;

import lombok.Data;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * 描述服务提供者映射关系
 *
 * @author Sylvan
 * @date 2024/03/19  22:47
 */
@Data
public class ProviderMeta implements Serializable {

    Method method;
    String methodSign;
    Object serviceImpl;
}
