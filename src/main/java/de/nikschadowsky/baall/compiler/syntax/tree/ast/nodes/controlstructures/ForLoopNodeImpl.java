package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.controlstructures;

import de.nikschadowsky.baall.compiler.syntax.tree.ast.NodeType;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.AbstractNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.expression.ExpressionNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.program.StatementsNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.assignment.ReassignmentNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.assignment.VariableDeclarationNode;
import de.nikschadowsky.baall.compiler.syntax.tree.traversal.ASTVisitor;
import de.nikschadowsky.baall.compiler.syntax.tree.util.NodeDiagnosticCollector;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;

/**
 * @since 21.04.2024
 */
public final class ForLoopNodeImpl extends AbstractNode implements ForLoopNode {

    private VariableDeclarationNode identifier;
    private ExpressionNode startIndex;
    private ExpressionNode endIndex;
    private ReassignmentNode optionalStepperStatement;
    private StatementsNode body;

    public ForLoopNodeImpl(NodeDiagnosticCollector diagnostics) {
        super(diagnostics);
    }

    @Override
    public VariableDeclarationNode getIdentifier() {
        return identifier;
    }

    public void setIdentifier(VariableDeclarationNode identifier) {
        this.identifier = identifier;
    }

    @Override
    public ExpressionNode getStartIndexExpression() {
        return startIndex;
    }

    public void setStartIndex(ExpressionNode startIndex) {
        this.startIndex = startIndex;
    }

    @Override
    public ExpressionNode getEndIndexExpression() {
        return endIndex;
    }

    public void setEndIndex(ExpressionNode endIndex) {
        this.endIndex = endIndex;
    }

    @Override
    public Optional<ReassignmentNode> getOptionalStepperStatement() {
        return Optional.ofNullable(optionalStepperStatement);
    }

    public void setOptionalStepperStatement(ReassignmentNode optionalStepperStatement) {
        this.optionalStepperStatement = optionalStepperStatement;
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
        return NodeType.FOR;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof ForLoopNodeImpl that)) return false;
        return Objects.equals(getIdentifier(), that.getIdentifier())
                       && Objects.equals(getStartIndexExpression(), that.getStartIndexExpression())
                       && Objects.equals(getEndIndexExpression(), that.getEndIndexExpression())
                       && Objects.equals(getOptionalStepperStatement(), that.getOptionalStepperStatement())
                       && Objects.equals(getBody(), that.getBody())
                       && getNodeType().equals(that.getNodeType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                getIdentifier(),
                getStartIndexExpression(),
                getEndIndexExpression(),
                getOptionalStepperStatement(),
                getBody(),
                getNodeType()
        );
    }

    @Override
    public @NotNull String getDisplayDescriptor() {
        return "for %s=%s..%s%s {%s}".formatted(
                getIdentifier().getIdentifier().getDisplayDescriptor(), // we just want the identifier here
                getStartIndexExpression().getDisplayDescriptor(),
                getEndIndexExpression().getDisplayDescriptor(),
                getOptionalStepperStatement().map(ReassignmentNode::getDisplayDescriptor)
                                             .map("::%s"::formatted)
                                             .orElse(""),
                getBody().getDisplayDescriptor()
        );
    }

    @Override
    public <D, R> R accept(ASTVisitor<D, R> visitor, D data) {
        return visitor.visitForLoop(this, data);
    }
}
