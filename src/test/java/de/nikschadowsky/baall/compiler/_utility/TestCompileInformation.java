package de.nikschadowsky.baall.compiler._utility;


import de.nikschadowsky.baall.compiler.CompileInformation;
import org.jetbrains.annotations.NotNull;

/**
 * @since 12.01.2025
 */
public class TestCompileInformation implements CompileInformation {


    @Override
    public void addInformation(@NotNull String key, @NotNull Object value) {
        System.out.printf("[INFORMATION] %s: %s%n", key, value);
    }

    @Override
    public void addWarning(@NotNull String message) {
        System.out.printf("[WARNING] %s%n", message);
    }
}
