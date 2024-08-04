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
public class ArrayLiteralNodeImpl extends AbstractNode implements ArrayLiteralNode {

    private List<ExpressionNode> elements;

    public ArrayLiteralNodeImpl(NodeDiagnosticCollector diagnostics) {
        super(diagnostics);
    }

    @Override
    public @UnmodifiableView List<ExpressionNode> getElements() {
        return Collections.unmodifiableList(elements);
    }

    public void setElements(List<ExpressionNode> elements) {
        this.elements = new LinkedList<>(elements);
    }

    @Override
    public @NotNull NodeType getNodeType() {
        return NodeType.ARRAY_LITERAL;
    }
}
