package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.controlstatement.exceptionhandling;

import de.nikschadowsky.baall.compiler.syntax.tree.ast.NodeType;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.AbstractNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.Node;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.program.StatementsNode;
import de.nikschadowsky.baall.compiler.syntax.tree.traversal.ASTVisitor;
import de.nikschadowsky.baall.compiler.syntax.tree.util.NodeDiagnosticCollector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @since 11.08.2024
 */
public final class TryStatementNodeImpl extends AbstractNode implements TryStatementNode {

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
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof TryStatementNodeImpl that)) return false;
        return Objects.equals(getBody(), that.getBody())
                       && Objects.equals(getInterceptBlocks(), that.getInterceptBlocks())
                       && Objects.equals(getEnsureBlock(), that.getEnsureBlock())
                       && getNodeType().equals(that.getNodeType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getBody(), getInterceptBlocks(), getEnsureBlock(), getNodeType());
    }

    @Override
    public @NotNull String getDisplayDescriptor() {
        return "try {s} %s%s".formatted(
                getInterceptBlocks().stream()
                                    .map(InterceptStatementNode::getDisplayDescriptor)
                                    .collect(Collectors.joining(" ")),
                getEnsureBlock().map(Node::getDisplayDescriptor)
                                .map(" %s"::formatted)
                                .orElse("")
        );
    }

    @Override
    public <D, R> R accept(ASTVisitor<D, R> visitor, D data) {
        return visitor.visitTryStatement(this, data);
    }
}
