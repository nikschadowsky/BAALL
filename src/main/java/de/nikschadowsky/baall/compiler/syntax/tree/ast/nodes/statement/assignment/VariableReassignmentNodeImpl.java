package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.assignment;

import de.nikschadowsky.baall.compiler.syntax.tree.ast.NodeType;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.AbstractNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.expression.ExpressionNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.expression.OperatorNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.IdentifierAccessNode;
import de.nikschadowsky.baall.compiler.syntax.tree.traversal.ASTVisitor;
import de.nikschadowsky.baall.compiler.syntax.tree.util.NodeDiagnosticCollector;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * @since 30.07.2024
 */
public class VariableReassignmentNodeImpl extends AbstractNode implements VariableReassignmentNode {

    private IdentifierAccessNode identifier;
    private OperatorNode operator;
    private ExpressionNode value;

    public VariableReassignmentNodeImpl(NodeDiagnosticCollector diagnostics) {
        super(diagnostics);
    }

    @Override
    public IdentifierAccessNode getIdentifierAccess() {
        return identifier;
    }

    public void setIdentifier(IdentifierAccessNode identifier) {
        this.identifier = identifier;
    }

    @Override
    public OperatorNode getOperator() {
        return operator;
    }

    public void setOperator(OperatorNode operator) {
        this.operator = operator;
    }

    @Override
    public ExpressionNode getValueExpression() {
        return value;
    }

    public void setValueExpression(ExpressionNode value) {
        this.value = value;
    }

    @Override
    public @NotNull NodeType getNodeType() {
        return NodeType.VARIABLE_REASSIGNMENT;
    }

    @Override
    public <D, R> Optional<R> accept(ASTVisitor<D, R> visitor, D data) {
        return visitor.visitVariableReassignment(this, data);
    }
}
