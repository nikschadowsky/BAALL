package de.nikschadowsky.compiler.util;

import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * File created on 25.01.2024
 */
public class LambdaUtility {

    public static <T> @NotNull Supplier<T> createSupplier(final T object) {
        return () -> object;
    }

}
