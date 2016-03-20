package com.mroza;

import org.junit.Assert;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

public class ReflectionWrapper {

    public static Object invokePrivateMethod(Object object, String methodName, List<Object> args) {
        try {
            List<Class<?>> argsTypes = args.stream().map(Object::getClass).collect(Collectors.toList());
            Method method = object.getClass().getDeclaredMethod(
                    methodName, argsTypes.toArray(new Class<?>[argsTypes.size()]));
            method.setAccessible(true);
            return method.invoke(object, args.toArray(new Object[args.size()]));
        } catch (Exception exception) {
            Assert.fail("Exception was raised: " + exception.getMessage());
        }
        return null;
    }

    public static void setPrivateField(Object object, String fieldName, Object value) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(object, value);
        } catch (Exception exception) {
            Assert.fail("Exception was raised: " + exception.getMessage());
        }
    }

    public static Object getPrivateField(Object object, String fieldName, Object value) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(object);
        } catch (Exception exception) {
            Assert.fail("Exception was raised: " + exception.getMessage());
        }
        return null;
    }
}
