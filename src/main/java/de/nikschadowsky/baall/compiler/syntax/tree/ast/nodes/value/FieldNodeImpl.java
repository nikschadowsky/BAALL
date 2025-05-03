package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value;


import de.nikschadowsky.baall.compiler.syntax.tree.ast.NodeType;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.AbstractNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.access.IdentifierNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.typing.TypeNode;
import de.nikschadowsky.baall.compiler.syntax.tree.traversal.ASTVisitor;
import de.nikschadowsky.baall.compiler.syntax.tree.util.NodeDiagnosticCollector;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @since 27.01.2025
 */
public final class FieldNodeImpl extends AbstractNode implements FieldNode {

    private TypeNode type;
    private IdentifierNode name;

    public FieldNodeImpl(NodeDiagnosticCollector diagnostics) {
        super(diagnostics);
    }

    @Override
    public TypeNode getType() {
        return type;
    }

    public void setType(TypeNode type) {
        this.type = type;
    }

    @Override
    public IdentifierNode getIdentifier() {
        return name;
    }

    public void setIdentifier(IdentifierNode name) {
        this.name = name;
    }

    @Override
    public @NotNull NodeType getNodeType() {
        return NodeType.FIELD;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof FieldNodeImpl that)) return false;
        return Objects.equals(getType(), that.getType())
                       && Objects.equals(getIdentifier(), that.getIdentifier())
                       && getNodeType().equals(that.getNodeType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getType(), getIdentifier(), getNodeType());
    }

    @Override
    public @NotNull String getDisplayDescriptor() {
        return "%s: %s".formatted(getType().getDisplayDescriptor(), getIdentifier().getDisplayDescriptor());
    }

    @Override
    public <D, R> R accept(ASTVisitor<D, R> visitor, D data) {
        return visitor.visitField(this, data);
    }
}
