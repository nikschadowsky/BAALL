package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.controlstatement.exceptionhandling;

import de.nikschadowsky.baall.compiler.syntax.tree.ast.NodeType;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.AbstractNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.program.StatementsNode;
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
public class TryStatementNodeImpl extends AbstractNode implements TryStatementNode {

    private StatementsNode body;
    private List<InterceptStatementNode> interceptBlocks = Collections.emptyList();
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
    public @NotNull @UnmodifiableView List<InterceptStatementNode> getInterceptBlocks() {
        return Collections.unmodifiableList(interceptBlocks);
    }

    public void setInterceptBlocks(@NotNull List<InterceptStatementNode> interceptBlocks) {
        this.interceptBlocks = new ArrayList<>(interceptBlocks);
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
    public <D, R> R accept(ASTVisitor<D, R> visitor, D data) {
        return visitor.visitTry(this, data);
    }
}
