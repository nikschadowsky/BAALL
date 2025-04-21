package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.literal;


import de.nikschadowsky.baall.compiler.symbol.Token;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.NodeType;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.AbstractNode;
import de.nikschadowsky.baall.compiler.syntax.tree.traversal.ASTVisitor;
import de.nikschadowsky.baall.compiler.syntax.tree.util.NodeDiagnosticCollector;
import org.jetbrains.annotations.NotNull;

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
    public <D, R> R accept(ASTVisitor<D, R> visitor, D data) {
        return visitor.visitBooleanLiteral(this, data);
    }
}
