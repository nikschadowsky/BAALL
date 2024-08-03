package de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.expression;

import de.nikschadowsky.baall.compiler.symbol.Token;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.NodeType;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.AbstractNode;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.value.IdentifierAccessNode;
import de.nikschadowsky.baall.compiler.syntaxtree.util.NodeDiagnosticCollector;
import org.jetbrains.annotations.NotNull;

/**
 * File created on 29.07.2024
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
}
