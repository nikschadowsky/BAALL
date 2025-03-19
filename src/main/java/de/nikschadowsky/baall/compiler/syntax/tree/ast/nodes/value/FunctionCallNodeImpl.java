package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value;

import de.nikschadowsky.baall.compiler.syntax.tree.ast.NodeType;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.AbstractNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.access.ElementAccessNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.expression.ExpressionNode;
import de.nikschadowsky.baall.compiler.syntax.tree.traversal.ASTVisitor;
import de.nikschadowsky.baall.compiler.syntax.tree.util.NodeDiagnosticCollector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @since 25.07.2024
 */
public class FunctionCallNodeImpl extends AbstractNode implements FunctionCallNode {

    private ElementAccessNode functionIdentifier;
    private List<ExpressionNode> arguments = Collections.emptyList();

    public FunctionCallNodeImpl(NodeDiagnosticCollector diagnostics) {
        super(diagnostics);
    }

    @Override
    public ElementAccessNode getFunctionIdentifier() {
        return functionIdentifier;
    }

    public void setFunctionIdentifier(ElementAccessNode functionIdentifier) {
        this.functionIdentifier = functionIdentifier;
    }

    @Override
    public @NotNull @UnmodifiableView List<ExpressionNode> getArguments() {
        return Collections.unmodifiableList(arguments);
    }

    public void setArguments(@NotNull List<ExpressionNode> arguments) {
        this.arguments = new ArrayList<>(arguments);
    }

    @Override
    public @NotNull NodeType getNodeType() {
        return NodeType.FUNCTION_CALL;
    }

    @Override
    public <D, R> R accept(ASTVisitor<D, R> visitor, D data) {
        return visitor.visitFunctionCall(this, data);
    }
}
