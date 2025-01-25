package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.program;

import de.nikschadowsky.baall.compiler.semantic.traversal.ASTVisitor;
import de.nikschadowsky.baall.compiler.symbol.Token;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.NodeType;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.AbstractNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.IdentifierAccessNode;
import de.nikschadowsky.baall.compiler.syntax.tree.util.NodeDiagnostic;
import de.nikschadowsky.baall.compiler.syntax.tree.util.NodeDiagnosticCollector;
import de.nikschadowsky.baall.compiler.syntax.tree.util.NodeUtility;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * @since 21.04.2024
 */
public class ExportsNodeImpl extends AbstractNode implements ExportsNode {

    private List<IdentifierAccessNode> exports;

    private Token namespace;

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
    public Optional<Token> getNamespace() {
        return Optional.ofNullable(namespace);
    }

    public void setNamespace(@NotNull Token namespace) {
        this.namespace = namespace;
    }

    @Override
    public @NotNull NodeType getNodeType() {
        return NodeType.EXPORT;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visitExports(this);
    }
}
