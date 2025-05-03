package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.literal;


import de.nikschadowsky.baall.compiler.symbol.Token;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.NodeType;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.AbstractNode;
import de.nikschadowsky.baall.compiler.syntax.tree.traversal.ASTVisitor;
import de.nikschadowsky.baall.compiler.syntax.tree.util.NodeDiagnosticCollector;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @since 28.03.2025
 */
public final class BooleanLiteralNodeImpl extends AbstractNode implements BooleanLiteralNode {

    private Token value;

    public BooleanLiteralNodeImpl(NodeDiagnosticCollector diagnostics) {
        super(diagnostics);
    }

    @Override
    public Token getValue() {
        return value;
    }

    public void setValue(Token value) {
        this.value = value;
    }

    @Override
    public @NotNull NodeType getNodeType() {
        return NodeType.BOOLEAN_LITERAL;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof BooleanLiteralNodeImpl that)) return false;
        return Objects.equals(getValue(), that.getValue()) && getNodeType().equals(that.getNodeType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getValue(), getNodeType());
    }

    @Override
    public @NotNull String getDisplayDescriptor() {
        return value.value();
    }

    @Override
    public <D, R> R accept(ASTVisitor<D, R> visitor, D data) {
        return visitor.visitBooleanLiteral(this, data);
    }
}
