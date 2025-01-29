package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.controlstatement.exceptionhandling;

import de.nikschadowsky.baall.compiler.syntax.tree.ast.NodeType;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.AbstractNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.program.StatementsNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.IdentifierAccessNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.IdentifierNode;
import de.nikschadowsky.baall.compiler.syntax.tree.traversal.ASTVisitor;
import de.nikschadowsky.baall.compiler.syntax.tree.util.NodeDiagnosticCollector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @since 11.08.2024
 */
public class InterceptStatementNodeImpl extends AbstractNode implements InterceptStatementNode {

    private List<IdentifierAccessNode> interceptedExceptions = Collections.emptyList();
    private IdentifierNode raisedExceptionIdentifier;
    private StatementsNode body;

    public InterceptStatementNodeImpl(NodeDiagnosticCollector diagnostics) {
        super(diagnostics);
    }

    @Override
    public @UnmodifiableView @NotNull List<IdentifierAccessNode> getInterceptedExceptions() {
        return Collections.unmodifiableList(interceptedExceptions);
    }

    public void setInterceptedExceptions(@NotNull List<IdentifierAccessNode> interceptedExceptions) {
        this.interceptedExceptions = new ArrayList<>(interceptedExceptions);
    }

    @Override
    public IdentifierNode getRaisedExceptionIdentifier() {
        return raisedExceptionIdentifier;
    }

    public void setRaisedExceptionIdentifier(IdentifierNode raisedExceptionIdentifier) {
        this.raisedExceptionIdentifier = raisedExceptionIdentifier;
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
        return NodeType.INTERCEPT;
    }

    @Override
    public <D, R> Optional<R> accept(ASTVisitor<D, R> visitor, D data) {
        return visitor.visitIntercept(this, data);
    }
}
