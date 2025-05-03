package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.typing;


import de.nikschadowsky.baall.compiler.syntax.tree.ast.NodeType;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.AbstractNode;
import de.nikschadowsky.baall.compiler.syntax.tree.traversal.ASTVisitor;
import de.nikschadowsky.baall.compiler.syntax.tree.util.NodeDiagnosticCollector;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @since 09.03.2025
 */
public final class PrimitiveTypeNodeImpl extends AbstractNode implements PrimitiveTypeNode {

    private Kind kind;

    public PrimitiveTypeNodeImpl(NodeDiagnosticCollector diagnostics) {
        super(diagnostics);
    }

    @Override
    public Kind getKind() {
        return kind;
    }

    public void setKind(Kind kind) {
        this.kind = kind;
    }

    @Override
    public boolean isNoneSafe() {
        return true;
    }

    @Override
    public @NotNull NodeType getNodeType() {
        return NodeType.PRIMITIVE_TYPE;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof PrimitiveTypeNodeImpl that)) return false;
        return getKind() == that.getKind() && getNodeType().equals(that.getNodeType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getKind(), getNodeType());
    }

    @Override
    public @NotNull String getDisplayDescriptor() {
        return "%s".formatted(getKind().getDisplayDescriptor());
    }

    @Override
    public <D, R> R accept(ASTVisitor<D, R> visitor, D data) {
        return visitor.visitPrimitiveType(this, data);
    }
}
