package de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.statement.assignment;

import de.nikschadowsky.baall.compiler.symbol.Token;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.NodeType;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.AbstractNode;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.expression.ExpressionNode;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.typing.TypeNode;
import de.nikschadowsky.baall.compiler.syntaxtree.util.NodeDiagnosticCollector;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * @since 30.07.2024
 */
public class VariableDeclarationNodeImpl extends AbstractNode implements VariableDeclarationNode {

    private TypeNode type;

    private Token identifier;

    private ExpressionNode initializationValue;

    public VariableDeclarationNodeImpl(NodeDiagnosticCollector diagnostics) {
        super(diagnostics);
    }

    @Override
    public TypeNode getType() {
        return type;
    }

    public void setType(TypeNode type) {
        this.type = type;
    }

    @Override
    public Token getIdentifier() {
        return identifier;
    }

    public void setIdentifier(Token identifier) {
        this.identifier = identifier;
    }

    @Override
    public Optional<ExpressionNode> getInitializationValue() {
        return Optional.ofNullable(initializationValue);
    }

    public void setInitializationValue(ExpressionNode initializationValue) {
        this.initializationValue = initializationValue;
    }

    @Override
    public @NotNull NodeType getNodeType() {
        return NodeType.VARIABLE_DECLARATION;
    }
}
