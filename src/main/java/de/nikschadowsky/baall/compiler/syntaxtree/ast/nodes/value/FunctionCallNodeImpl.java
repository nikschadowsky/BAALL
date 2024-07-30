package de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.value;

import de.nikschadowsky.baall.compiler.syntaxtree.ast.NodeType;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.AbstractNode;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.expression.ExpressionNode;
import de.nikschadowsky.baall.compiler.syntaxtree.util.NodeDiagnosticCollector;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * File created on 25.07.2024
 */
public class FunctionCallNodeImpl extends AbstractNode implements FunctionCallNode {

    private IdentifierAccessNodeImpl functionIdentifier;

    private List<ExpressionNode> arguments;

    public FunctionCallNodeImpl(NodeDiagnosticCollector diagnostics) {
        super(diagnostics);
    }

    @Override
    public IdentifierAccessNode getFunctionIdentifier() {
        return functionIdentifier;
    }

    public void setFunctionIdentifier(IdentifierAccessNodeImpl functionIdentifier) {
        this.functionIdentifier = functionIdentifier;
    }

    @Override
    public List<ExpressionNode> getArguments() {
        return arguments;
    }

    public void setArguments(List<ExpressionNode> arguments) {
        this.arguments = arguments;
    }

    @Override
    public @NotNull NodeType getNodeType() {
        return NodeType.FUNCTION_CALL;
    }
}
