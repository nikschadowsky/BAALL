package de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.program;

import de.nikschadowsky.baall.compiler.syntaxtree.ast.NodeType;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.AbstractNode;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.value.IdentifierAccessNode;
import de.nikschadowsky.baall.compiler.syntaxtree.util.NodeDiagnostic;
import de.nikschadowsky.baall.compiler.syntaxtree.util.NodeDiagnosticCollector;
import de.nikschadowsky.baall.compiler.syntaxtree.util.NodeUtility;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @since 21.04.2024
 */
public class ExportsNodeImpl extends AbstractNode implements ExportsNode {

    private List<IdentifierAccessNode> exports;

    public ExportsNodeImpl(NodeDiagnosticCollector diagnostics) {
        super(diagnostics);
    }

    @Override
    public @UnmodifiableView List<IdentifierAccessNode> getExportedElements() {
        return NodeUtility.toUnmodifiableList(exports);
    }

    public void setExports(List<IdentifierAccessNode> exports) {
        this.exports = new LinkedList<>(exports);

        if (exports.isEmpty()) {
            getDiagnosticCollector().report(new NodeDiagnostic(
                    "No exports defined",
                    null,
                    NodeDiagnostic.ReportingLevel.INFO
            ));
            return;
        }

        exports.stream()
               .filter(e -> Collections.frequency(exports, e) > 1)
               .forEach(e -> getDiagnosticCollector().report(new NodeDiagnostic(
                       "Duplicate export",
                       e.getIdentifier(),
                       NodeDiagnostic.ReportingLevel.WARN
               )));
    }

    @Override
    public @NotNull NodeType getNodeType() {
        return NodeType.EXPORT;
    }
}
