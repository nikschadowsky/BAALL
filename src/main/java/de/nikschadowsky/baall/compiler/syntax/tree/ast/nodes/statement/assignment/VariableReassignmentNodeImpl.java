package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.assignment;

import de.nikschadowsky.baall.compiler.syntax.tree.ast.NodeType;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.AbstractNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.access.ElementAccessNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.expression.ExpressionNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.expression.OperatorNode;
import de.nikschadowsky.baall.compiler.syntax.tree.traversal.ASTVisitor;
import de.nikschadowsky.baall.compiler.syntax.tree.util.NodeDiagnosticCollector;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @since 30.07.2024
 */
public final class VariableReassignmentNodeImpl extends AbstractNode implements VariableReassignmentNode {

    private ElementAccessNode identifier;
    private OperatorNode operator;
    private ExpressionNode value;

    public VariableReassignmentNodeImpl(NodeDiagnosticCollector diagnostics) {
        super(diagnostics);
    }

    @Override
    public ElementAccessNode getElementAccess() {
        return identifier;
    }

    public void setIdentifier(ElementAccessNode identifier) {
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
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof VariableReassignmentNodeImpl that)) return false;
        return Objects.equals(getElementAccess(), that.getElementAccess())
                       && Objects.equals(getOperator(), that.getOperator())
                       && Objects.equals(getValueExpression(), that.getValueExpression())
                       && getNodeType().equals(that.getNodeType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getElementAccess(), getOperator(), getValueExpression(), getNodeType());
    }

    @Override
    public @NotNull String getDisplayDescriptor() {
        return "%s %s %s".formatted(
                getElementAccess().getDisplayDescriptor(),
                getOperator().getDisplayDescriptor(),
                getValueExpression().getDisplayDescriptor()
        );
    }

    @Override
    public <D, R> R accept(ASTVisitor<D, R> visitor, D data) {
        return visitor.visitVariableReassignment(this, data);
    }
}
