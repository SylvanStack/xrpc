package com.yuanstack.xrpc.core.filter;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.lang.reflect.Field;

/**
 * @author Sylvan
 * @date 2024/03/24  16:24
 */
public class MockUtils {
    public static Object mock(Class<?> type) {
        if (type.equals(Integer.class) || type.equals(Integer.TYPE)) {
            return 1;
        } else if (type.equals(Long.class) || type.equals(Long.TYPE)) {
            return 10000L;
        } else if (type.equals(Float.class) || type.equals(Float.TYPE)) {
            return 5.50F;
        }

        if (Number.class.isAssignableFrom(type)) {
            return 1;
        }
        if (type.equals(String.class)) {
            return "this is a mock string.";
        }

        return mockPojo(type);
    }

    @SneakyThrows
    private static Object mockPojo(Class<?> type) {
        Object result = type.getDeclaredConstructor().newInstance();
        Field[] fields = type.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            Class<?> fieldType = field.getType();
            Object fieldValue = mock(fieldType);
            field.set(result, fieldValue);
        }

        return result;
    }

    public static void main(String[] args) {
        System.out.println(mock(UserDTO.class));
    }

    @Getter
    @Setter
    public static class UserDTO {
        private int a;
        private String b;

        @Override
        public String toString() {
            return a + "," + b;
        }
    }
}
