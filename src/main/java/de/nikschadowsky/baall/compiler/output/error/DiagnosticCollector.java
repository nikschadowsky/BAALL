package de.nikschadowsky.baall.compiler.output.error;

import java.util.ArrayList;
import java.util.List;

public class DiagnosticCollector<T extends Diagnostic> {

    private final List<T> error = new ArrayList<>();
    private final List<T> warning = new ArrayList<>();
    private final List<T> information = new ArrayList<>();

    public void addError(T diagnostic) {
        error.add(diagnostic);
    }

    public void addWarning(T diagnostic) {
        warning.add(diagnostic);
    }

    public void addInformation(T diagnostic) {
        information.add(diagnostic);
    }

}
