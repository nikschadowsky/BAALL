package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.literal;

import de.nikschadowsky.baall.compiler.syntax.tree.ast.NodeType;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.AbstractNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.expression.ExpressionNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.IdentifierAccessNode;
import de.nikschadowsky.baall.compiler.syntax.tree.traversal.ASTVisitor;
import de.nikschadowsky.baall.compiler.syntax.tree.util.NodeDiagnosticCollector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @since 29.07.2024
 */
public class StructInitializationLiteralNodeImpl extends AbstractNode implements StructInitializationLiteralNode {

    private IdentifierAccessNode identifier;
    private List<ExpressionNode> arguments = Collections.emptyList();

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
    public @NotNull @UnmodifiableView List<ExpressionNode> getArguments() {
        return Collections.unmodifiableList(arguments);
    }

    public void setArguments(@NotNull List<ExpressionNode> arguments) {
        this.arguments = new ArrayList<>(arguments);
    }

    @Override
    public @NotNull NodeType getNodeType() {
        return NodeType.STRUCT_INITIALIZATION;
    }

    @Override
    public <D, R> Optional<R> accept(ASTVisitor<D, R> visitor, D data) {
        return visitor.visitStructInitializationLiteral(this, data);
    }
}
