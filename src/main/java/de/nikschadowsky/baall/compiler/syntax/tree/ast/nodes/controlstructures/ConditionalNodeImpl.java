package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.controlstructures;

import de.nikschadowsky.baall.compiler.syntax.tree.ast.NodeType;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.AbstractNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.expression.ExpressionNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.program.StatementsNode;
import de.nikschadowsky.baall.compiler.syntax.tree.traversal.ASTVisitor;
import de.nikschadowsky.baall.compiler.syntax.tree.util.NodeDiagnostic;
import de.nikschadowsky.baall.compiler.syntax.tree.util.NodeDiagnosticCollector;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;

/**
 * @since 24.07.2024
 */
public final class ConditionalNodeImpl extends AbstractNode implements ConditionalNode {

    private ExpressionNode condition;
    private StatementsNode thenBranch;
    private ConditionalNode elseBranch;
    private ConditionBranch conditionBranch;

    public ConditionalNodeImpl(NodeDiagnosticCollector diagnostics) {
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
    public StatementsNode getThenBlock() {
        return thenBranch;
    }

    public void setThenBlock(StatementsNode thenBranch) {
        this.thenBranch = thenBranch;
    }

    @Override
    public Optional<ConditionalNode> getElseBranch() {
        return Optional.ofNullable(elseBranch);
    }

    public void setElseBranch(ConditionalNode elseBranch) {
        if (ConditionBranch.ELSE.equals(conditionBranch)) {
            getDiagnosticCollector().report(new NodeDiagnostic(
                    "Cannot set ELSE branch on ELSE type node!",
                    null,
                    NodeDiagnostic.ReportingLevel.WARN
            ));
            return;
        }
        this.elseBranch = elseBranch;

    }

    @Override
    public ConditionBranch getConditionBranch() {
        return conditionBranch;
    }

    public void setConditionBranch(ConditionBranch conditionBranch) {
        this.conditionBranch = conditionBranch;
    }

    @Override
    public @NotNull NodeType getNodeType() {
        return NodeType.IF;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof ConditionalNodeImpl that)) return false;
        return Objects.equals(getCondition(), that.getCondition())
                       && Objects.equals(getThenBlock(), that.getThenBlock())
                       && Objects.equals(getElseBranch(), that.getElseBranch())
                       && getConditionBranch() == that.getConditionBranch()
                       && getNodeType().equals(that.getNodeType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCondition(), getThenBlock(), getElseBranch(), getConditionBranch(), getNodeType());
    }

    @Override
    public @NotNull String getDisplayDescriptor() {
        return "%s? {%s}%s".formatted(
                getCondition().getDisplayDescriptor(),
                getThenBlock().getDisplayDescriptor(),
                getElseBranch().map(ConditionalNode::getDisplayDescriptor).map(" | %s"::formatted).orElse("")
        );
    }

    @Override
    public <D, R> R accept(ASTVisitor<D, R> visitor, D data) {
        return visitor.visitConditional(this, data);
    }
}
