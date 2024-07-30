package de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.value;

import de.nikschadowsky.baall.compiler.symbol.Token;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.NodeType;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.AbstractNode;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.expression.ExpressionNode;
import de.nikschadowsky.baall.compiler.syntaxtree.util.NodeDiagnosticCollector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * File created on 11.07.2024
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
    public List<ExpressionNode> getArrayIndices() {
        return arrayIndexes;
    }

    public void setArrayIndexes(List<ExpressionNode> arrayIndex) {
        this.arrayIndexes = new ArrayList<>(arrayIndex);
    }

    @Override
    public @NotNull NodeType getNodeType() {
        return NodeType.IDENTIFIER_ACCESS;
    }
}
