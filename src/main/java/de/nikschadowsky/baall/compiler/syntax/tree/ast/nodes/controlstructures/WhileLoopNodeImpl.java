package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.controlstructures;

import de.nikschadowsky.baall.compiler.syntax.tree.ast.NodeType;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.AbstractNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.expression.ExpressionNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.program.StatementsNode;
import de.nikschadowsky.baall.compiler.syntax.tree.traversal.ASTVisitor;
import de.nikschadowsky.baall.compiler.syntax.tree.util.NodeDiagnosticCollector;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @since 21.04.2024
 */
public final class WhileLoopNodeImpl extends AbstractNode implements WhileLoopNode {

    private ExpressionNode condition;
    private StatementsNode body;

    public WhileLoopNodeImpl(NodeDiagnosticCollector diagnostics) {
        super(diagnostics);
    }

    @Override
    public ExpressionNode getCondition() {
        return condition;
    }

    public void setCondition(ExpressionNode condition) {
        this.condition = condition;
    }

    @Override
    public StatementsNode getBody() {
        return body;
    }

    public void setBody(StatementsNode body) {
        this.body = body;
    }

    @Override
    public @NotNull NodeType getNodeType() {
        return NodeType.WHILE;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof WhileLoopNodeImpl that)) return false;
        return Objects.equals(getCondition(), that.getCondition())
                       && Objects.equals(getBody(), that.getBody())
                       && getNodeType().equals(that.getNodeType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCondition(), getBody(), getNodeType());
    }

    @Override
    public @NotNull String getDisplayDescriptor() {
        return "while %s {%s}".formatted(getCondition().getDisplayDescriptor(), getBody().getDisplayDescriptor());
    }

    @Override
    public <D, R> R accept(ASTVisitor<D, R> visitor, D data) {
        return visitor.visitWhileLoop(this, data);
    }
}
