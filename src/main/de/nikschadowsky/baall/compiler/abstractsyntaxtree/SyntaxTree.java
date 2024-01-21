package de.nikschadowsky.baall.compiler.abstractsyntaxtree;

import de.nikschadowsky.baall.compiler.abstractsyntaxtree.node.SyntaxTreeInternalNode;

/**
 * File created on 14.01.2024
 */
public class SyntaxTree {

    private final SyntaxTreeInternalNode root;

    public SyntaxTree(SyntaxTreeInternalNode root) {
        this.root = root;
    }

    public SyntaxTreeInternalNode getRoot() {
        return root;
    }

    @Override
    public String toString() {
        return super.toString() + ": " + root.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;

        if (obj instanceof SyntaxTree tree) {
            return getRoot().equals(tree.getRoot());
        }

        return false;
    }
}
