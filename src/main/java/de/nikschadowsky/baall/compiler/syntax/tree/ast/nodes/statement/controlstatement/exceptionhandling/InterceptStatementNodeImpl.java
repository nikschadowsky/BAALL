package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.controlstatement.exceptionhandling;

import de.nikschadowsky.baall.compiler.symbol.Token;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.NodeType;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.AbstractNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.program.StatementsNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.IdentifierAccessNode;
import de.nikschadowsky.baall.compiler.syntax.tree.traversal.ASTVisitor;
import de.nikschadowsky.baall.compiler.syntax.tree.util.NodeDiagnosticCollector;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

/**
 * @since 11.08.2024
 */
public class InterceptStatementNodeImpl extends AbstractNode implements InterceptStatementNode {

    private List<IdentifierAccessNode> interceptedExceptions;
    private Token raisedExceptionIdentifier;
    private StatementsNode body;


    public InterceptStatementNodeImpl(NodeDiagnosticCollector diagnostics) {
        super(diagnostics);
    }

    @Override
    public List<IdentifierAccessNode> getInterceptedExceptions() {
        return interceptedExceptions;
    }

    public void setInterceptedExceptions(List<IdentifierAccessNode> interceptedExceptions) {
        this.interceptedExceptions = new LinkedList<>(interceptedExceptions);
    }

    @Override
    public Token getRaisedExceptionIdentifier() {
        return raisedExceptionIdentifier;
    }

    public void setRaisedExceptionIdentifier(Token raisedExceptionIdentifier) {
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
    public void accept(ASTVisitor visitor) {
        visitor.visitIntercept(this);
    }
}
