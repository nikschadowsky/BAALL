package de.nikschadowsky.baall.compiler._utility;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

/**
 * File created on 13.01.2024
 */
public class ClassUtility {

    /**
     * @param clazz          class of object to be created
     * @param parameterTypes classes of the constructors parameters
     * @param parameter      values for the instantiation parameters
     * @param <T>            the Type of Object to be created
     * @return a new instance of this type T
     * @throws NoSuchMethodException     see {@link Class#getDeclaredConstructor}
     * @throws InvocationTargetException see {@link Constructor#newInstance}
     * @throws InstantiationException    see {@link Constructor#newInstance}
     * @throws IllegalAccessException    see {@link Constructor#newInstance}
     */
    public static <T> T getInstance(@NotNull Class<T> clazz, Class<?>[] parameterTypes, Object... parameter)
            throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {

        Constructor<T> constructor = clazz.getDeclaredConstructor(parameterTypes);
        constructor.setAccessible(true);
        return constructor.newInstance(parameter);

    }

    /**
     * @param targetClass class of the return type of the desired field
     * @param object      object of which to get the field
     * @param fieldName   the name of the desired field
     * @param <T>         return type
     * @return the value of the field of this object
     * @throws NoSuchFieldException   if the specified field does not exist
     * @throws IllegalAccessException see {@link Field#get(Object)}
     */
    public static <T> T getFieldValue(@NotNull Class<T> targetClass, @NotNull Object object, @NotNull String fieldName) throws NoSuchFieldException, IllegalAccessException {

        Class<?> clazz = object.getClass();

        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);

        return targetClass.cast(field.get(object));

    }
}
