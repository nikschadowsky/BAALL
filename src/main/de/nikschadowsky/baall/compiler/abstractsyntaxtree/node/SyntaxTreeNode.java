package de.nikschadowsky.baall.compiler.abstractsyntaxtree.node;

import de.nikschadowsky.baall.compiler.grammar.GrammarSymbol;

/**
 * File created on 14.01.2024
 */
public sealed abstract class SyntaxTreeNode<T extends GrammarSymbol> permits SyntaxTreeLeafNode, SyntaxTreeInternalNode {

    private final T value;

    private final int depth;

    public SyntaxTreeNode(T value, int depth) {
        this.value = value;
        this.depth = depth;
    }

    public boolean isLeafNode() {
        return this instanceof SyntaxTreeLeaf;
    }

    public T getValue() {
        return value;
    }

    public int getNodeDepth() {
        return depth;
    }

    @Override
    public String toString() {
        return "%s(%s)".formatted(value.getFormatted(), getNodeDepth());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;

        if (obj instanceof SyntaxTreeNode<?> leafNode) {
            return getNodeDepth() == leafNode.getNodeDepth() && getValue().equals(leafNode.getValue());
        }
        return false;
    }
}