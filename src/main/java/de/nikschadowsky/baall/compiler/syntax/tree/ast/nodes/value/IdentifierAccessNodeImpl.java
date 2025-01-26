package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value;

import de.nikschadowsky.baall.compiler.symbol.Token;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.NodeType;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.AbstractNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.expression.ExpressionNode;
import de.nikschadowsky.baall.compiler.syntax.tree.traversal.ASTVisitor;
import de.nikschadowsky.baall.compiler.syntax.tree.util.NodeDiagnosticCollector;
import de.nikschadowsky.baall.compiler.syntax.tree.util.NodeUtility;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * @since 11.07.2024
 */
public class IdentifierAccessNodeImpl extends AbstractNode implements IdentifierAccessNode {

    private Token identifier;

    private List<ExpressionNode> arrayIndexes;

    public IdentifierAccessNodeImpl(NodeDiagnosticCollector diagnostics) {
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
    public @UnmodifiableView List<ExpressionNode> getArrayIndices() {
        return NodeUtility.toUnmodifiableList(arrayIndexes);
    }

    public void setArrayIndexes(List<ExpressionNode> arrayIndex) {
        this.arrayIndexes = new LinkedList<>(arrayIndex);
    }

    @Override
    public @NotNull NodeType getNodeType() {
        return NodeType.IDENTIFIER_ACCESS;
    }

    @Override
    public <D, R> Optional<R> accept(ASTVisitor<D, R> visitor, D data) {
        return visitor.visitIdentifierAccess(this, data);
    }
}
