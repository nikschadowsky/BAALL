package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.expression;

import de.nikschadowsky.baall.compiler.syntax.tree.ast.NodeType;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.AbstractNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.access.ElementAccessNode;
import de.nikschadowsky.baall.compiler.syntax.tree.traversal.ASTVisitor;
import de.nikschadowsky.baall.compiler.syntax.tree.util.NodeDiagnosticCollector;
import org.jetbrains.annotations.NotNull;

/**
 * @since 29.07.2024
 */
public class UnaryExpressionNodeImpl extends AbstractNode implements UnaryExpressionNode {

    private ElementAccessNode identifierAccess;
    private boolean isPrefix;
    private OperatorNode operator;

    public UnaryExpressionNodeImpl(NodeDiagnosticCollector diagnostics) {
        super(diagnostics);
    }

    @Override
    public ElementAccessNode getElementAccess() {
        return identifierAccess;
    }

    public void setIdentifierAccess(ElementAccessNode identifier) {
        this.identifierAccess = identifier;
    }

    @Override
    public OperatorNode getOperator() {
        return operator;
    }

    public void setOperator(OperatorNode operator) {
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
    public <D, R> R accept(ASTVisitor<D, R> visitor, D data) {
        return visitor.visitUnaryExpression(this, data);
    }
}
