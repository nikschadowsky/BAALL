package de.nikschadowsky.baall.compiler;


import org.jetbrains.annotations.Nullable;

/**
 * @since 11.01.2025
 */
public abstract class Step<INPUT, OUTPUT> {

    private final StepOptions options;

    private final boolean isOptionsSet;

    public Step(@Nullable StepOptions options) {
        this.options = options;
        this.isOptionsSet = options != null;
    }

    public abstract OUTPUT executeStep(INPUT input, CompileInformation compileInformation);

    public StepOptions getOptions() {
        return options;
    }

    public boolean isOptionsSet() {
        return isOptionsSet;
    }
}
