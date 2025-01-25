package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.controlstatement;

import de.nikschadowsky.baall.compiler.semantic.traversal.ASTVisitor;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.NodeType;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.AbstractNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.expression.ExpressionNode;
import de.nikschadowsky.baall.compiler.syntax.tree.util.NodeDiagnosticCollector;
import org.jetbrains.annotations.NotNull;

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
    public void accept(ASTVisitor visitor) {
        visitor.visitReturnStatement(this);
    }
}
