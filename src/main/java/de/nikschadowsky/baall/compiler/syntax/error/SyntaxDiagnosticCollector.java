package de.nikschadowsky.baall.compiler.syntax.error;

import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * File created on 07.04.2024
 */
public class SyntaxDiagnosticCollector {

    private final List<SyntaxDiagnostic> diagnostics = new ArrayList<SyntaxDiagnostic>();

    public void report(SyntaxDiagnostic diagnostic) {
        diagnostics.add(diagnostic);
    }

    public @Unmodifiable List<SyntaxDiagnostic> getDiagnostics() {
        return Collections.unmodifiableList(diagnostics);
    }


    // TODO implement
    public void throwReport() {
        StringBuilder builder = new StringBuilder("Syntax errors found:");
        for (SyntaxDiagnostic diagnostic : diagnostics) {

            builder.append('\n');
            builder.append("Syntax error in line %s:%s! Expected %s but got %s!");
        }
        throw new SyntaxException(builder.toString());
    }

    public boolean isEmpty() {
        return diagnostics.isEmpty();
    }
}
