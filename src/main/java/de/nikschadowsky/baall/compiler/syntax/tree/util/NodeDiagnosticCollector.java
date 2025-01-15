package de.nikschadowsky.baall.compiler.syntax.tree.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @since 14.04.2024
 */
public class NodeDiagnosticCollector {

    private final List<NodeDiagnostic> diagnostics = new ArrayList<NodeDiagnostic>();

    public NodeDiagnosticCollector() {
    }

    public void report(NodeDiagnostic diagnostic) {
        diagnostics.add(diagnostic);
    }

    public String createReport() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<NodeDiagnostic> getDiagnostics() {
        return Collections.unmodifiableList(diagnostics);
    }
}
