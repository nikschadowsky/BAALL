package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value;

import de.nikschadowsky.baall.compiler.semantic.traversal.ASTVisitor;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.NodeType;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.AbstractNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.expression.ExpressionNode;
import de.nikschadowsky.baall.compiler.syntax.tree.util.NodeDiagnosticCollector;
import de.nikschadowsky.baall.compiler.syntax.tree.util.NodeUtility;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.LinkedList;
import java.util.List;

/**
 * @since 25.07.2024
 */
public class FunctionCallNodeImpl extends AbstractNode implements FunctionCallNode {

    private IdentifierAccessNode functionIdentifier;

    private List<ExpressionNode> arguments;

    public FunctionCallNodeImpl(NodeDiagnosticCollector diagnostics) {
        super(diagnostics);
    }

    @Override
    public IdentifierAccessNode getFunctionIdentifier() {
        return functionIdentifier;
    }

    public void setFunctionIdentifier(IdentifierAccessNode functionIdentifier) {
        this.functionIdentifier = functionIdentifier;
    }

    @Override
    public @UnmodifiableView List<ExpressionNode> getArguments() {
        return NodeUtility.toUnmodifiableList(arguments);
    }

    public void setArguments(List<ExpressionNode> arguments) {
        this.arguments = new LinkedList<>(arguments);
    }

    @Override
    public @NotNull NodeType getNodeType() {
        return NodeType.FUNCTION_CALL;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visitFunctionCall(this);
    }
}
