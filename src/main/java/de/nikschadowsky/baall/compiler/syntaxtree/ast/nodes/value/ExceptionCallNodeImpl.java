package de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.value;

import de.nikschadowsky.baall.compiler.syntaxtree.ast.NodeType;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.AbstractNode;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.expression.ExpressionNode;
import de.nikschadowsky.baall.compiler.syntaxtree.util.NodeDiagnosticCollector;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

/**
 * @since 11.08.2024
 */
public class ExceptionCallNodeImpl extends AbstractNode implements ExceptionCallNode {

    private IdentifierAccessNode identifier;

    private List<ExpressionNode> arguments;

    public ExceptionCallNodeImpl(NodeDiagnosticCollector diagnostics) {
        super(diagnostics);
    }

    @Override
    public IdentifierAccessNode getExceptionIdentifier() {
        return identifier;
    }

    public void setExceptionIdentifier(IdentifierAccessNode identifier) {
        this.identifier = identifier;
    }

    @Override
    public List<ExpressionNode> getArguments() {
        return arguments;
    }

    public void setArguments(List<ExpressionNode> arguments) {
        this.arguments = new LinkedList<>(arguments);
    }

    @Override
    public @NotNull NodeType getNodeType() {
        return NodeType.EXCEPTION_CREATION;
    }

}
