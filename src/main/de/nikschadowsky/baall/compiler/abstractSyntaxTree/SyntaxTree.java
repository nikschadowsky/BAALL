package de.nikschadowsky.baall.compiler.abstractSyntaxTree;

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
        return super.toString();
    }
}
