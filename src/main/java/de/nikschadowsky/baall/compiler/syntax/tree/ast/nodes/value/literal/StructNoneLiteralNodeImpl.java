package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.literal;


import de.nikschadowsky.baall.compiler.syntax.tree.ast.NodeType;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.AbstractNode;
import de.nikschadowsky.baall.compiler.syntax.tree.traversal.ASTVisitor;
import de.nikschadowsky.baall.compiler.syntax.tree.util.NodeDiagnosticCollector;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @since 03.03.2025
 */
public final class StructNoneLiteralNodeImpl extends AbstractNode implements StructNoneLiteralNode {

    public StructNoneLiteralNodeImpl(NodeDiagnosticCollector diagnostics) {
        super(diagnostics);
    }

    @Override
    public @NotNull NodeType getNodeType() {
        return NodeType.NONE;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof StructNoneLiteralNodeImpl that)) return false;
        return getNodeType().equals(that.getNodeType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getNodeType());
    }

    @Override
    public @NotNull String getDisplayDescriptor() {
        return "none";
    }

    @Override
    public <D, R> R accept(ASTVisitor<D, R> visitor, D data) {
        return visitor.visitStructNoneLiteral(this, data);
    }
}
