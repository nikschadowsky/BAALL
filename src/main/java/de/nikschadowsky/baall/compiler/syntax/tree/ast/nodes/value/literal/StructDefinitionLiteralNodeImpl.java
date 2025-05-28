package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.literal;

import de.nikschadowsky.baall.compiler.semantic.SemanticDiagnostic;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.NodeType;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.AbstractNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.access.IdentifierNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.FieldNode;
import de.nikschadowsky.baall.compiler.syntax.tree.traversal.ASTVisitor;
import de.nikschadowsky.baall.compiler.syntax.tree.util.NodeDiagnostic;
import de.nikschadowsky.baall.compiler.syntax.tree.util.NodeDiagnosticCollector;
import de.nikschadowsky.baall.compiler.util.CollectionUtility;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @since 29.07.2024
 */
public final class StructDefinitionLiteralNodeImpl extends AbstractNode implements StructDefinitionLiteralNode {

    private List<FieldNode> fields = Collections.emptyList();

    public StructDefinitionLiteralNodeImpl(NodeDiagnosticCollector diagnostics) {
        super(diagnostics);
    }

    @Override
    public @NotNull @UnmodifiableView List<FieldNode> getFields() {
        return Collections.unmodifiableList(fields);
    }

    public void setFields(@NotNull List<FieldNode> fields) {
        this.fields = new ArrayList<>(fields);
        checkIntegrity();
    }

    private void checkIntegrity() {
        List<IdentifierNode> duplicates = CollectionUtility.duplicates(fields, FieldNode::getIdentifier);
        duplicates.forEach(identifierNode -> getDiagnosticCollector().report(new NodeDiagnostic(
                "There is already a field with this identifier.",
                identifierNode.getIdentifier(),
                NodeDiagnostic.ReportingLevel.ERROR)
        ));
    }

    @Override
    public @NotNull NodeType getNodeType() {
        return NodeType.STRUCT_DEFINITION;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof StructDefinitionLiteralNodeImpl that)) return false;
        return Objects.equals(getFields(), that.getFields()) && getNodeType().equals(that.getNodeType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFields(), getNodeType());
    }

    @Override
    public @NotNull String getDisplayDescriptor() {
        return getFields().stream().map(FieldNode::getDisplayDescriptor).collect(Collectors.joining(",", "(", ")"));
    }

    @Override
    public <D, R> R accept(ASTVisitor<D, R> visitor, D data) {
        return visitor.visitStructDefinitionLiteral(this, data);
    }
}
