package com.minepalm.servergui.impl.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Container {

    private static Map<Class<?>, Object> map = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public static <T> T get(Class<T> clazz) {
        return (T) map.get(clazz);
    }

    public static <T> void put(Class<T> clazz, T instance) {
        map.put(clazz, instance);
    }

}
