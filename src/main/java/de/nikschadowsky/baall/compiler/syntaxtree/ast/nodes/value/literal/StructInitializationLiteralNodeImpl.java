package de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.value.literal;

import de.nikschadowsky.baall.compiler.syntaxtree.ast.NodeType;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.AbstractNode;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.expression.ExpressionNode;
import de.nikschadowsky.baall.compiler.syntaxtree.util.NodeDiagnosticCollector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * File created on 29.07.2024
 */
public class StructInitializationLiteralNodeImpl extends AbstractNode implements StructInitializationLiteralNode {

    private List<ExpressionNode> arguments;

    public static StructInitializationLiteralNode createNone(NodeDiagnosticCollector diagnostics) {
        StructInitializationLiteralNodeImpl node =
                new StructInitializationLiteralNodeImpl(diagnostics);

        node.setArguments(new LinkedList<>());
        return node;
    }

    public StructInitializationLiteralNodeImpl(NodeDiagnosticCollector diagnostics) {
        super(diagnostics);
    }

    @Override
    public @UnmodifiableView List<ExpressionNode> getArguments() {
        return Collections.unmodifiableList(arguments);
    }

    public void setArguments(List<ExpressionNode> arguments) {
        this.arguments = arguments;
    }

    @Override
    public @NotNull NodeType getNodeType() {
        return NodeType.STRUCT_INITIALIZATION;
    }
}
