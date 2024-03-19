package com.yuanstack.xrpc.core.meta;

import lombok.Data;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * 服务提供者Meta信息
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
