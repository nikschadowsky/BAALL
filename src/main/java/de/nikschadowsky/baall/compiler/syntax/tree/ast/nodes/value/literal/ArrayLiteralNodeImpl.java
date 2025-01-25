package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.literal;

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
 * @since 29.07.2024
 */
public class ArrayLiteralNodeImpl extends AbstractNode implements ArrayLiteralNode {

    private List<ExpressionNode> elements;

    public ArrayLiteralNodeImpl(NodeDiagnosticCollector diagnostics) {
        super(diagnostics);
    }

    @Override
    public @UnmodifiableView List<ExpressionNode> getElements() {
        return NodeUtility.toUnmodifiableList(elements);
    }

    public void setElements(List<ExpressionNode> elements) {
        this.elements = new LinkedList<>(elements);
    }

    @Override
    public @NotNull NodeType getNodeType() {
        return NodeType.ARRAY_LITERAL;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visitArrayLiteral(this);
    }
}
