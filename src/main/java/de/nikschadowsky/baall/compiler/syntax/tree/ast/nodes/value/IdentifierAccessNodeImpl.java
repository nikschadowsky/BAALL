package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value;

import de.nikschadowsky.baall.compiler.syntax.tree.ast.NodeType;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.AbstractNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.expression.ExpressionNode;
import de.nikschadowsky.baall.compiler.syntax.tree.traversal.ASTVisitor;
import de.nikschadowsky.baall.compiler.syntax.tree.util.NodeDiagnosticCollector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @since 11.07.2024
 */
public class IdentifierAccessNodeImpl extends AbstractNode implements IdentifierAccessNode {

    private IdentifierNode identifier;
    private List<ExpressionNode> arrayIndices = Collections.emptyList();

    public IdentifierAccessNodeImpl(NodeDiagnosticCollector diagnostics) {
        super(diagnostics);
    }

    @Override
    public IdentifierNode getIdentifier() {
        return identifier;
    }

    public void setIdentifier(IdentifierNode identifier) {
        this.identifier = identifier;
    }

    @Override
    public @NotNull @UnmodifiableView List<ExpressionNode> getArrayIndices() {
        return Collections.unmodifiableList(arrayIndices);
    }

    public void setArrayIndices(@NotNull List<ExpressionNode> arrayIndex) {
        this.arrayIndices = new ArrayList<>(arrayIndex);
    }

    @Override
    public @NotNull NodeType getNodeType() {
        return NodeType.IDENTIFIER_ACCESS;
    }

    @Override
    public <D, R> R accept(ASTVisitor<D, R> visitor, D data) {
        return visitor.visitIdentifierAccess(this, data);
    }
}
