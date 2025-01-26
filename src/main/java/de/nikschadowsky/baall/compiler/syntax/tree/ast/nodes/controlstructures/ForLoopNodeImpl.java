package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.controlstructures;

import de.nikschadowsky.baall.compiler.symbol.Token;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.NodeType;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.AbstractNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.expression.ExpressionNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.program.StatementsNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.assignment.ReassignmentNode;
import de.nikschadowsky.baall.compiler.syntax.tree.traversal.ASTVisitor;
import de.nikschadowsky.baall.compiler.syntax.tree.util.NodeDiagnosticCollector;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * @since 21.04.2024
 */
public class ForLoopNodeImpl extends AbstractNode implements ForLoopNode {

    private Token identifier;

    private ExpressionNode startIndex;

    private ExpressionNode endIndex;

    private ReassignmentNode optionalStepperStatement;

    private StatementsNode body;

    private boolean hasOptionalStepperStatement;

    public ForLoopNodeImpl(NodeDiagnosticCollector diagnostics) {
        super(diagnostics);
    }

    @Override
    public Token getIdentifier() {
        return identifier;
    }

    public void setIdentifier(Token identifier) {
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
    public boolean hasOptionalStepperStatement() {
        return hasOptionalStepperStatement;
    }

    public void setHasOptionalStepperStatement(boolean hasOptionalStepperStatement) {
        this.hasOptionalStepperStatement = hasOptionalStepperStatement;
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
    public void accept(ASTVisitor visitor) {
        visitor.visitForLoop(this);
    }
}
