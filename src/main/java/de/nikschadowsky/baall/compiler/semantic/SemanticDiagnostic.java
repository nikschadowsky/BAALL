package de.nikschadowsky.baall.compiler.semantic;

import de.nikschadowsky.baall.compiler.output.error.Diagnostic;

public class SemanticDiagnostic extends Diagnostic {

    public SemanticDiagnostic(String message) {}

    @Override
    public String getFormatted() {
        return "";
    }
}
