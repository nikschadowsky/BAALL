package de.nikschadowsky.baall.compiler;


import de.nikschadowsky.baall.compiler.output.error.CompileException;
import de.nikschadowsky.baall.compiler.output.error.Diagnostic;
import de.nikschadowsky.baall.compiler.output.error.DiagnosticCollector;
import org.jetbrains.annotations.Nullable;

/**
 * @since 11.01.2025
 */
public abstract class Step<INPUT, OUTPUT, DIAG extends Diagnostic> {

    private final DiagnosticCollector<DIAG> diagnosticCollector = new DiagnosticCollector<>();
    private final StepOptions options;

    private final boolean isOptionsSet;

    public Step(@Nullable StepOptions options) {
        this.options = options;
        this.isOptionsSet = options != null;
    }

    public abstract OUTPUT executeStep(INPUT input, CompileInformation compileInformation) throws CompileException;

    public StepOptions getOptions() {
        return options;
    }

    public boolean isOptionsSet() {
        return isOptionsSet;
    }

    public DiagnosticCollector<DIAG> getDiagnosticCollector() {
        return diagnosticCollector;
    }
}
