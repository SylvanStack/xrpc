package com.yuanstack.xrpc.core.util;

import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 方法工具类
 *
 * @author Sylvan
 * @date 2024/03/19  22:10
 */
@Slf4j
public class MethodUtils {

    /**
     * 是否为Object基类方法
     *
     * @param methodName 方法名
     * @return true 是，false 否
     */
    public static boolean isObjectMethod(String methodName) {
        return "toSting".equals(methodName) ||
                "hashCode".equals(methodName) ||
                "notifyAll".equals(methodName) ||
                "equals".equals(methodName) ||
                "wait".equals(methodName) ||
                "getClass".equals(methodName) ||
                "notify".equals(methodName);
    }

    public static boolean isObjectMethod(Method method) {
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

    public static List<Field> findAnnotatedField(Class<?> aClass, Class<? extends Annotation> annotationClass) {
        List<Field> result = new ArrayList<>();
        while (aClass != null) {
            Field[] fields = aClass.getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(annotationClass)) {
                    result.add(field);
                }
            }

            aClass = aClass.getSuperclass();
        }
        return result;
    }

    public static void main(String[] args) {
        for (Method declaredMethod : MethodUtils.class.getDeclaredMethods()) {
            log.info(generateMethodSign(declaredMethod));
        }
    }
}
