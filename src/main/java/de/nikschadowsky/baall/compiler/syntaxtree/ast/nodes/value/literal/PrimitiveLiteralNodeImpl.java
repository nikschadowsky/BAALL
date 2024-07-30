package de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.value.literal;

import de.nikschadowsky.baall.compiler.symbol.Token;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.NodeType;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.AbstractNode;
import de.nikschadowsky.baall.compiler.syntaxtree.util.NodeDiagnosticCollector;
import org.jetbrains.annotations.NotNull;

/**
 * File created on 29.07.2024
 */
public class PrimitiveLiteralNodeImpl extends AbstractNode implements PrimitiveLiteralNode {

    private Token primitiveValue;

    public PrimitiveLiteralNodeImpl(NodeDiagnosticCollector diagnostics) {
        super(diagnostics);
    }

    @Override
    public Token getPrimitiveValue() {
        return primitiveValue;
    }

    public void setPrimitiveValue(Token primitiveValue) {
        this.primitiveValue = primitiveValue;
    }

    @Override
    public @NotNull NodeType getNodeType() {
        return NodeType.PRIMITIVE;
    }
}
