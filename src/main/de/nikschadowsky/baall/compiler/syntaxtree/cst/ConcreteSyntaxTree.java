package de.nikschadowsky.baall.compiler.syntaxtree.cst;

import de.nikschadowsky.baall.compiler.syntaxtree.cst.node.SyntaxTreeInternalNode;

/**
 * File created on 14.01.2024
 */
public class ConcreteSyntaxTree {

    private final SyntaxTreeInternalNode root;

    public ConcreteSyntaxTree(SyntaxTreeInternalNode root) {
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

        if (obj instanceof ConcreteSyntaxTree tree) {
            return getRoot().equals(tree.getRoot());
        }

        return false;
    }
}
