package de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.statement.assignment;

import de.nikschadowsky.baall.compiler.symbol.Token;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.NodeType;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.AbstractNode;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.expression.ExpressionNode;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.value.IdentifierAccessNode;
import de.nikschadowsky.baall.compiler.syntaxtree.util.NodeDiagnosticCollector;
import org.jetbrains.annotations.NotNull;

/**
 * @since 30.07.2024
 */
public class VariableReassignmentNodeImpl extends AbstractNode implements VariableReassignmentNode {

    private IdentifierAccessNode identifier;

    private Token operator;

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
    public Token getOperator() {
        return operator;
    }

    public void setOperator(Token operator) {
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
}
