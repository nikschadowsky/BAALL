package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.typing;

import de.nikschadowsky.baall.compiler.syntax.tree.ast.NodeType;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.AbstractNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.expression.ExpressionNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.IdentifierNode;
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
public class TypeNodeImpl extends AbstractNode implements TypeNode {

    private List<ExpressionNode> arrayDimensionDefinitions = Collections.emptyList();
    private IdentifierNode identifier;
    private boolean noneSafe;

    public TypeNodeImpl(NodeDiagnosticCollector diagnostics) {
        super(diagnostics);
    }

    public void setType(IdentifierNode identifier) {
        this.identifier = identifier;
    }

    @Override
    public IdentifierNode getType() {
        return identifier;
    }

    @Override
    public @NotNull @UnmodifiableView List<ExpressionNode> getArrayDimensionDefinitions() {
        return Collections.unmodifiableList(arrayDimensionDefinitions);
    }

    public void setArrayDimensionDefinitions(@NotNull List<ExpressionNode> arrayTypeDefinition) {
        arrayDimensionDefinitions = new ArrayList<>(arrayTypeDefinition);
    }

    @Override
    public boolean isNoneSafe() {
        return noneSafe;
    }

    public void setNoneSafe(boolean noneSafe) {
        this.noneSafe = noneSafe;
    }

    @Override
    public @NotNull NodeType getNodeType() {
        return NodeType.TYPE;
    }

    @Override
    public <D, R> R accept(ASTVisitor<D, R> visitor, D data) {
        return visitor.visitType(this, data);
    }
}
