package de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.value.literal;

import de.nikschadowsky.baall.compiler.syntaxtree.ast.NodeType;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.AbstractNode;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.expression.ExpressionNode;
import de.nikschadowsky.baall.compiler.syntaxtree.util.NodeDiagnosticCollector;
import de.nikschadowsky.baall.compiler.syntaxtree.util.NodeUtility;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.LinkedList;
import java.util.List;

/**
 * @since 29.07.2024
 */
public class StructInitializationLiteralNodeImpl extends AbstractNode implements StructInitializationLiteralNode {

    private List<ExpressionNode> arguments;

    public StructInitializationLiteralNodeImpl(NodeDiagnosticCollector diagnostics) {
        super(diagnostics);
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
        return NodeType.STRUCT_INITIALIZATION;
    }
}
