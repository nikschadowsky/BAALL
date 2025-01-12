package de.nikschadowsky.baall.compiler;


import org.jetbrains.annotations.NotNull;

/**
 * Interface for collecting information during the compilation process. Fatal errors can be raised by throwing a
 * subclass of {@link de.nikschadowsky.baall.compiler.output.error.CompileException}.
 *
 * @since 11.01.2025
 */
public interface CompileInformation {

    /**
     * Adds information in form of a key value pair to this compilation process.
     *
     * @param key key of the information
     * @param value information body
     */
    void addInformation(@NotNull String key, @NotNull Object value);

    /**
     * Adds a warning message to this compilation process.
     *
     * @param message message
     */
    void addWarning(@NotNull String message);

}
