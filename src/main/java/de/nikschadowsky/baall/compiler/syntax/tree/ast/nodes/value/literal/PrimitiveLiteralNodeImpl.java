package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.literal;

import de.nikschadowsky.baall.compiler.symbol.Token;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.NodeType;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.AbstractNode;
import de.nikschadowsky.baall.compiler.syntax.tree.traversal.ASTVisitor;
import de.nikschadowsky.baall.compiler.syntax.tree.util.NodeDiagnosticCollector;
import org.jetbrains.annotations.NotNull;

/**
 * @since 29.07.2024
 */
public class PrimitiveLiteralNodeImpl extends AbstractNode implements PrimitiveLiteralNode {

    private Token primitiveValue;
    private PrimitiveType primitiveType;

    public PrimitiveLiteralNodeImpl(NodeDiagnosticCollector diagnostics) {
        super(diagnostics);
    }

    @Override
    public Token getPrimitiveValue() {
        return primitiveValue;
    }

    public void setPrimitiveValue(Token primitiveValue) {
        this.primitiveValue = primitiveValue;
    }

    @Override
    public PrimitiveType getPrimitiveType() {
        return primitiveType;
    }

    public void setPrimitiveType(PrimitiveType primitiveType) {
        this.primitiveType = primitiveType;
    }

    @Override
    public @NotNull NodeType getNodeType() {
        return NodeType.PRIMITIVE;
    }

    @Override
    public <D, R> R accept(ASTVisitor<D, R> visitor, D data) {
        return visitor.visitPrimitiveLiteral(this, data);
    }
}
