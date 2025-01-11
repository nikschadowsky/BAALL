package de.nikschadowsky.baall.compiler;


import org.jetbrains.annotations.NotNull;

/**
 * @since 11.01.2025
 */
public interface CompileInformation {

    void add(@NotNull String key, @NotNull Object value);

}
