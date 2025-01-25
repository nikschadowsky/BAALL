package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.literal;

import de.nikschadowsky.baall.compiler.semantic.traversal.ASTVisitor;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.NodeType;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.AbstractNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.expression.ExpressionNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.IdentifierAccessNode;
import de.nikschadowsky.baall.compiler.syntax.tree.util.NodeDiagnosticCollector;
import de.nikschadowsky.baall.compiler.syntax.tree.util.NodeUtility;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.LinkedList;
import java.util.List;

/**
 * @since 29.07.2024
 */
public class StructInitializationLiteralNodeImpl extends AbstractNode implements StructInitializationLiteralNode {

    private IdentifierAccessNode identifier;

    private List<ExpressionNode> arguments;

    public StructInitializationLiteralNodeImpl(NodeDiagnosticCollector diagnostics) {
        super(diagnostics);
    }

    @Override
    public IdentifierAccessNode getIdentifier() {
        return identifier;
    }

    public void setIdentifier(IdentifierAccessNode identifier) {
        this.identifier = identifier;
    }

    @Override
    public @UnmodifiableView List<ExpressionNode> getArguments() {
        return NodeUtility.toUnmodifiableList(arguments);
    }

    public void setArguments(List<ExpressionNode> arguments) {
        this.arguments = new LinkedList<>(arguments);
    }

    @Override
    public @NotNull NodeType getNodeType() {
        return NodeType.STRUCT_INITIALIZATION;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visitStructInitializationLiteral(this);
    }
}
