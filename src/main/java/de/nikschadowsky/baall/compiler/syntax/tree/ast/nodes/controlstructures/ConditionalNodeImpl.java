package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.controlstructures;

import de.nikschadowsky.baall.compiler.syntax.tree.ast.NodeType;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.AbstractNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.expression.ExpressionNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.program.StatementsNode;
import de.nikschadowsky.baall.compiler.syntax.tree.traversal.ASTVisitor;
import de.nikschadowsky.baall.compiler.syntax.tree.util.NodeDiagnostic;
import de.nikschadowsky.baall.compiler.syntax.tree.util.NodeDiagnosticCollector;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * @since 24.07.2024
 */
public class ConditionalNodeImpl extends AbstractNode implements ConditionalNode {

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
    public <D, R> Optional<R> accept(ASTVisitor<D, R> visitor, D data) {
        return visitor.visitConditional(this, data);
    }
}
