package de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.typing;

import de.nikschadowsky.baall.compiler.symbol.Token;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.NodeType;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.AbstractNode;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.expression.ExpressionNode;
import de.nikschadowsky.baall.compiler.syntaxtree.util.NodeDiagnosticCollector;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * File created on 11.07.2024
 */
public class TypeNodeImpl extends AbstractNode implements TypeNode {

    private List<ExpressionNode> arrayDimensionDefinitions;

    private Token identifier;

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

    public void setArrayDimensionDefinitions(List<ExpressionNode> arrayTypeDefinition) {
        arrayDimensionDefinitions = arrayTypeDefinition;
    }

    @Override
    public List<ExpressionNode> getArrayDimensionDefinitions() {
        return arrayDimensionDefinitions;
    }

    @Override
    public @NotNull NodeType getNodeType() {
        return NodeType.TYPE;
    }
}
