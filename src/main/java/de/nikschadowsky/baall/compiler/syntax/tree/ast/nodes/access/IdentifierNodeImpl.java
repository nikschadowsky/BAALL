package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.access;


import de.nikschadowsky.baall.compiler.symbol.Token;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.NodeType;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.AbstractNode;
import de.nikschadowsky.baall.compiler.syntax.tree.traversal.ASTVisitor;
import de.nikschadowsky.baall.compiler.syntax.tree.util.NodeDiagnosticCollector;
import org.jetbrains.annotations.NotNull;

/**
 * @since 27.01.2025
 */
public class IdentifierNodeImpl extends AbstractNode implements IdentifierNode {

    private Token name;

    public IdentifierNodeImpl(NodeDiagnosticCollector diagnostics) {
        super(diagnostics);
    }

    @Override
    public Token getIdentifier() {
        return name;
    }

    public void setName(Token name) {
        this.name = name;
    }

    @Override
    public @NotNull NodeType getNodeType() {
        return NodeType.IDENTIFIER;
    }

    @Override
    public <D, R> R accept(ASTVisitor<D, R> visitor, D data) {
        return visitor.visitIdentifier(this, data);
    }
}
