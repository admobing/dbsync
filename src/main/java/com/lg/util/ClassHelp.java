package com.lg.util;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClassHelp {

    private static Map<String, Field> fieldMap = new ConcurrentHashMap<>();

    public static Field findField(String fieldName, Class cls) {
        return fieldMap.computeIfAbsent(fieldName + "[" + cls.getPackage().getName() + "." + cls.getName() + "]", name -> {
            Field field = null;
            try {
                field = cls.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
            field.setAccessible(true);
            return field;
        });
    }

}
