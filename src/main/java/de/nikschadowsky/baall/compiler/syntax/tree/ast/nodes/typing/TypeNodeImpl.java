package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.typing;

import de.nikschadowsky.baall.compiler.symbol.Token;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.NodeType;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.AbstractNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.expression.ExpressionNode;
import de.nikschadowsky.baall.compiler.syntax.tree.util.NodeDiagnosticCollector;
import de.nikschadowsky.baall.compiler.syntax.tree.util.NodeUtility;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.LinkedList;
import java.util.List;

/**
 * @since 11.07.2024
 */
public class TypeNodeImpl extends AbstractNode implements TypeNode {

    private List<ExpressionNode> arrayDimensionDefinitions;

    private Token identifier;

    private boolean noneSafe;

    public TypeNodeImpl(NodeDiagnosticCollector diagnostics) {
        super(diagnostics);
    }

    public void setType(Token identifier) {
        this.identifier = identifier;
    }

    @Override
    public Token getType() {
        return identifier;
    }

    @Override
    public @UnmodifiableView List<ExpressionNode> getArrayDimensionDefinitions() {
        return NodeUtility.toUnmodifiableList(arrayDimensionDefinitions);
    }

    public void setArrayDimensionDefinitions(List<ExpressionNode> arrayTypeDefinition) {
        arrayDimensionDefinitions = new LinkedList<>(arrayTypeDefinition);
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
}
