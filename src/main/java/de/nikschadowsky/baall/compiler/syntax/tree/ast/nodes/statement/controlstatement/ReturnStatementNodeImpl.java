package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.controlstatement;

import de.nikschadowsky.baall.compiler.syntax.tree.ast.NodeType;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.AbstractNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.expression.ExpressionNode;
import de.nikschadowsky.baall.compiler.syntax.tree.traversal.ASTVisitor;
import de.nikschadowsky.baall.compiler.syntax.tree.util.NodeDiagnosticCollector;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * @since 30.07.2024
 */
public class ReturnStatementNodeImpl extends AbstractNode implements ReturnStatementNode {

    private ExpressionNode returnExpression;

    public ReturnStatementNodeImpl(NodeDiagnosticCollector diagnostics) {
        super(diagnostics);
    }

    @Override
    public ExpressionNode getReturnExpression() {
        return returnExpression;
    }

    public void setReturnExpression(ExpressionNode returnExpression) {
        this.returnExpression = returnExpression;
    }

    @Override
    public @NotNull NodeType getNodeType() {
        return NodeType.RETURN;
    }

    @Override
    public <D, R> Optional<R> accept(ASTVisitor<D, R> visitor, D data) {
        return visitor.visitReturnStatement(this, data);
    }
}
