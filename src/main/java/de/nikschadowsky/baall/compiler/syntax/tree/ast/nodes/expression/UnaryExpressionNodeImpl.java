package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.expression;

import de.nikschadowsky.baall.compiler.symbol.Token;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.NodeType;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.AbstractNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.IdentifierAccessNode;
import de.nikschadowsky.baall.compiler.syntax.tree.traversal.ASTVisitor;
import de.nikschadowsky.baall.compiler.syntax.tree.util.NodeDiagnosticCollector;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * @since 29.07.2024
 */
public class UnaryExpressionNodeImpl extends AbstractNode implements UnaryExpressionNode {

    private IdentifierAccessNode identifierAccess;

    private boolean isPrefix;

    private Token operator;

    public UnaryExpressionNodeImpl(NodeDiagnosticCollector diagnostics) {
        super(diagnostics);
    }

    @Override
    public IdentifierAccessNode getIdentifierAccess() {
        return identifierAccess;
    }

    public void setIdentifierAccess(IdentifierAccessNode identifier) {
        this.identifierAccess = identifier;
    }

    @Override
    public Token getOperator() {
        return operator;
    }

    public void setOperator(Token operator) {
        this.operator = operator;
    }

    @Override
    public boolean isPrefix() {
        return isPrefix;
    }

    public void setIsPrefix(boolean prefix) {
        isPrefix = prefix;
    }

    @Override
    public @NotNull NodeType getNodeType() {
        return NodeType.UNARY_EXPRESSION;
    }

    @Override
    public <D, R> Optional<R> accept(ASTVisitor<D, R> visitor, D data) {
        return visitor.visitUnaryExpression(this, data);
    }
}
