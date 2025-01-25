package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.controlstatement.exceptionhandling;

import de.nikschadowsky.baall.compiler.semantic.traversal.ASTVisitor;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.NodeType;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.AbstractNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.program.StatementsNode;
import de.nikschadowsky.baall.compiler.syntax.tree.util.NodeDiagnosticCollector;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * @since 11.08.2024
 */
public class TryStatementNodeImpl extends AbstractNode implements TryStatementNode {

    private StatementsNode body;
    private List<InterceptStatementNode> interceptBlocks;
    private EnsureStatementNode ensureBlock;

    public TryStatementNodeImpl(NodeDiagnosticCollector diagnostics) {
        super(diagnostics);
    }

    @Override
    public StatementsNode getBody() {
        return body;
    }

    public void setBody(StatementsNode body) {
        this.body = body;
    }

    @Override
    public List<InterceptStatementNode> getInterceptBlocks() {
        return interceptBlocks;
    }

    public void setInterceptBlocks(List<InterceptStatementNode> interceptBlocks) {
        this.interceptBlocks = new LinkedList<>(interceptBlocks);
    }

    @Override
    public Optional<EnsureStatementNode> getEnsureBlock() {
        return Optional.ofNullable(ensureBlock);
    }

    public void setEnsureBlock(EnsureStatementNode ensureBlock) {
        this.ensureBlock = ensureBlock;
    }

    @Override
    public @NotNull NodeType getNodeType() {
        return NodeType.TRY;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visitTry(this);
    }
}
