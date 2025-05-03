package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.program;

import de.nikschadowsky.baall.compiler.syntax.tree.ast.NodeType;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.AbstractNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.access.ElementAccessNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.access.IdentifierNode;
import de.nikschadowsky.baall.compiler.syntax.tree.traversal.ASTVisitor;
import de.nikschadowsky.baall.compiler.syntax.tree.util.NodeDiagnostic;
import de.nikschadowsky.baall.compiler.syntax.tree.util.NodeDiagnosticCollector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @since 21.04.2024
 */
public final class ExportsNodeImpl extends AbstractNode implements ExportsNode {

    private List<ElementAccessNode> exports = Collections.emptyList();
    private IdentifierNode namespace;

    public ExportsNodeImpl(NodeDiagnosticCollector diagnostics) {
        super(diagnostics);
    }

    @Override
    public @NotNull @UnmodifiableView List<ElementAccessNode> getExportedElements() {
        return Collections.unmodifiableList(exports);
    }

    public void setExports(@NotNull List<ElementAccessNode> exports) {
        this.exports = new ArrayList<>(exports);

        if (exports.isEmpty()) {
            getDiagnosticCollector().report(new NodeDiagnostic(
                    "No exports defined",
                    null,
                    NodeDiagnostic.ReportingLevel.INFO
            ));
            return;
        }

        /*exports.stream()
               .filter(e -> Collections.frequency(exports, e) > 1)
               .forEach(e -> getDiagnosticCollector().report(new NodeDiagnostic(
                       "Duplicate export",
                       e.getIdentifier().getIdentifier(),
                       NodeDiagnostic.ReportingLevel.WARN
               ))); */
    }

    @Override
    public Optional<IdentifierNode> getNamespace() {
        return Optional.ofNullable(namespace);
    }

    public void setNamespace(@NotNull IdentifierNode namespace) {
        this.namespace = namespace;
    }

    @Override
    public @NotNull NodeType getNodeType() {
        return NodeType.EXPORT;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof ExportsNodeImpl that)) return false;
        return Objects.equals(getExportedElements(), that.getExportedElements())
                       && Objects.equals(getNamespace(), that.getNamespace())
                       && getNodeType().equals(that.getNodeType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getExportedElements(), getNamespace(), getNodeType());
    }

    @Override
    public @NotNull String getDisplayDescriptor() {
        return "export {%s}%s;".formatted(
                getExportedElements().stream()
                                     .map(ElementAccessNode::getDisplayDescriptor)
                                     .collect(Collectors.joining(",")),
                getNamespace().map(IdentifierNode::getDisplayDescriptor).map(" as %s"::formatted).orElse("")
        );
    }

    @Override
    public <D, R> R accept(ASTVisitor<D, R> visitor, D data) {
        return visitor.visitExports(this, data);
    }
}
