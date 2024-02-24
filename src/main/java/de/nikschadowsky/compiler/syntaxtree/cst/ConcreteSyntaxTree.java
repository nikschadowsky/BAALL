package de.nikschadowsky.compiler.syntaxtree.cst;

import de.nikschadowsky.compiler.syntaxtree.cst.node.ConcreteSyntaxTreeInternalNode;

import java.util.Objects;

/**
 * File created on 14.01.2024
 */
public class ConcreteSyntaxTree {

    private final ConcreteSyntaxTreeInternalNode root;

    public ConcreteSyntaxTree(ConcreteSyntaxTreeInternalNode root) {
        this.root = root;
    }

    public ConcreteSyntaxTreeInternalNode getRoot() {
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
            return root.equals(tree.getRoot());
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(root);

    }
}
