package com.yuanstack.xrpc.core.util;

import java.lang.reflect.Method;

/**
 * 方法工具类
 *
 * @author Sylvan
 * @date 2024/03/19  22:10
 */
public class MethodUtils {

    /**
     * 是否为Object基类方法
     *
     * @param methodName 方法名
     * @return true 是，false 否
     */
    public static boolean isDefaultMethod(String methodName) {
        if ("toSting".equals(methodName) ||
                "hashCode".equals(methodName) ||
                "notifyAll".equals(methodName) ||
                "equals".equals(methodName) ||
                "wait".equals(methodName) ||
                "getClass".equals(methodName) ||
                "notify".equals(methodName)) {
            return true;
        }
        return false;
    }

    public static boolean isDefaultMethod(Method method) {
        return method.getDeclaringClass().equals(Object.class);
    }

    /**
     * 根据方法生成方法签名
     * 签名规则：方法名_参数个数@参数类型@参数类型
     *
     * @param method
     * @return
     */
    public static String generateMethodSign(Method method) {
        StringBuilder stringBuilder = new StringBuilder(method.getName());
        stringBuilder.append("@").append(method.getParameterCount());
        for (Class<?> parameterType : method.getParameterTypes()) {
            stringBuilder.append("_").append(parameterType.getCanonicalName());
        }
        return stringBuilder.toString();
    }

    public static void main(String[] args) {
        for (Method declaredMethod : MethodUtils.class.getDeclaredMethods()) {
            System.out.println(generateMethodSign(declaredMethod));
        }
    }
}
