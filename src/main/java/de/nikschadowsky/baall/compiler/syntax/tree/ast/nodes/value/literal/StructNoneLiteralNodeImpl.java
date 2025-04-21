package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.literal;


import de.nikschadowsky.baall.compiler.syntax.tree.ast.NodeType;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.AbstractNode;
import de.nikschadowsky.baall.compiler.syntax.tree.traversal.ASTVisitor;
import de.nikschadowsky.baall.compiler.syntax.tree.util.NodeDiagnosticCollector;
import org.jetbrains.annotations.NotNull;

/**
 * @since 03.03.2025
 */
public final class StructNoneLiteralNodeImpl extends AbstractNode implements StructNoneLiteralNode {

    public StructNoneLiteralNodeImpl(NodeDiagnosticCollector diagnostics) {
        super(diagnostics);
    }

    @Override
    public @NotNull NodeType getNodeType() {
        return NodeType.NONE;
    }

    @Override
    public <D, R> R accept(ASTVisitor<D, R> visitor, D data) {
        return visitor.visitStructNoneLiteral(this, data);
    }
}
