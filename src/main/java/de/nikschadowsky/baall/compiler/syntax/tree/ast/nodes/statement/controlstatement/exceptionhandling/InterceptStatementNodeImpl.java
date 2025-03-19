package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.controlstatement.exceptionhandling;

import de.nikschadowsky.baall.compiler.syntax.tree.ast.NodeType;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.AbstractNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.access.IdentifierNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.program.StatementsNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.typing.TypeNode;
import de.nikschadowsky.baall.compiler.syntax.tree.traversal.ASTVisitor;
import de.nikschadowsky.baall.compiler.syntax.tree.util.NodeDiagnosticCollector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @since 11.08.2024
 */
public class InterceptStatementNodeImpl extends AbstractNode implements InterceptStatementNode {

    private List<TypeNode> interceptedExceptions = Collections.emptyList();
    private IdentifierNode raisedExceptionIdentifier;
    private StatementsNode body;

    public InterceptStatementNodeImpl(NodeDiagnosticCollector diagnostics) {
        super(diagnostics);
    }

    @Override
    public @UnmodifiableView @NotNull List<TypeNode> getInterceptedExceptions() {
        return Collections.unmodifiableList(interceptedExceptions);
    }

    public void setInterceptedExceptions(@NotNull List<TypeNode> interceptedExceptions) {
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
    public <D, R> R accept(ASTVisitor<D, R> visitor, D data) {
        return visitor.visitInterceptStatement(this, data);
    }
}
