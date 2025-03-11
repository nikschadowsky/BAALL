package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.typing;


import de.nikschadowsky.baall.compiler.syntax.tree.ast.NodeType;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.AbstractNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.expression.ExpressionNode;
import de.nikschadowsky.baall.compiler.syntax.tree.traversal.ASTVisitor;
import de.nikschadowsky.baall.compiler.syntax.tree.util.NodeDiagnosticCollector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Optional;

/**
 * @since 09.03.2025
 */
public class NestedTypeNodeImpl extends AbstractNode implements NestedTypeNode {

    private ExpressionNode arrayDimension;
    private TypeNode inner;

    public NestedTypeNodeImpl(NodeDiagnosticCollector diagnostics) {
        super(diagnostics);
    }

    @Override
    public TypeNode getInnerType() {
        return inner;
    }

    public void setInner(TypeNode inner) {
        this.inner = inner;
    }

    @Override
    public @NotNull @UnmodifiableView Optional<ExpressionNode> getArrayDimension() {
        return Optional.ofNullable(arrayDimension);
    }

    public void setArrayDimension(ExpressionNode arrayDimension) {
        this.arrayDimension = arrayDimension;
    }

    @Override
    public boolean isNoneSafe() {
        return true;
    }

    @Override
    public @NotNull NodeType getNodeType() {
        return NodeType.NESTED_TYPE;
    }

    @Override
    public <D, R> R accept(ASTVisitor<D, R> visitor, D data) {
        return visitor.visitNestedType(this, data);
    }
}
