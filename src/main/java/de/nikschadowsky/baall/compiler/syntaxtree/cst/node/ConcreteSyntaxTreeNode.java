package de.nikschadowsky.baall.compiler.syntaxtree.cst.node;


import java.util.Objects;

/**
 * File created on 14.01.2024
 */
public sealed abstract class ConcreteSyntaxTreeNode<T> permits ConcreteSyntaxTreeLeafNode, ConcreteSyntaxTreeInternalNode {

    private final T value;

    private final int depth;

    public ConcreteSyntaxTreeNode(T value, int depth) {
        this.value = value;
        this.depth = depth;
    }

    public abstract boolean isLeafNode();

    public T getValue() {
        return value;
    }

    public int getNodeDepth() {
        return depth;
    }

    @Override
    public String toString() {
        return "%s(%s)".formatted(value, getNodeDepth());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;

        if (obj instanceof ConcreteSyntaxTreeNode<?> leafNode) {
            return getNodeDepth() == leafNode.getNodeDepth() && getValue().equals(leafNode.getValue());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, depth);
    }
}