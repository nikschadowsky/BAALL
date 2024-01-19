package de.nikschadowsky.baall.compiler.abstractSyntaxTree;

import de.nikschadowsky.baall.compiler.grammar.GrammarSymbol;

/**
 * File created on 14.01.2024
 */
public sealed abstract class SyntaxTreeNode<T extends GrammarSymbol> permits SyntaxTreeLeafNode, SyntaxTreeInternalNode{

    private final T value;

    public SyntaxTreeNode(T value) {
        this.value = value;
    }

    public boolean isLeafNode() {
        return this instanceof SyntaxTreeLeaf;
    }

    public T getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value.getFormatted();
    }

}