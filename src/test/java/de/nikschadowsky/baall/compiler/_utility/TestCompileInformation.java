package de.nikschadowsky.baall.compiler._utility;


import de.nikschadowsky.baall.compiler.CompileInformation;
import de.nikschadowsky.baall.compiler.output.error.Diagnostic;
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

    @Override
    public void addError(@NotNull String message) {
        System.out.printf("[ERROR] %s%n", message);
    }

    @Override
    public void addError(@NotNull String message, @NotNull Throwable cause) {
        System.out.printf("[ERROR] %s%nCause: %s%n", message, cause);
    }

    @Override
    public void addError(@NotNull Diagnostic diagnostic) {
        System.out.printf("[ERROR] %s%n", diagnostic);
    }
}
