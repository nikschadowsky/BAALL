package de.nikschadowsky.baall.compiler.syntaxtree.cst.node;

import de.nikschadowsky.baall.compiler.grammar.GrammarNonterminal;
import de.nikschadowsky.baall.compiler.grammar.GrammarSymbol;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.List;

/**
 * File created on 14.01.2024
 */
public non-sealed class SyntaxTreeInternalNode extends SyntaxTreeNode<GrammarNonterminal> {

    private boolean isModifiable;
    private final List<SyntaxTreeNode<? extends GrammarSymbol>> children = new ArrayList<>();

    public SyntaxTreeInternalNode(GrammarNonterminal symbol, int depth) {
        super(symbol, depth);
    }

    public void addChild(SyntaxTreeNode<?> child) {
        children.add(child);
    }

    public @Unmodifiable List<SyntaxTreeNode<?>> getChildren() {
        return children;
    }


    public boolean isModifiable() {
        return isModifiable;
    }

    public void setUnmodifiable() {
        isModifiable = false;
    }

    @Override
    public String toString() {
        return "%s: %s".formatted(super.toString(), children);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SyntaxTreeInternalNode inode) {
            return super.equals(inode) && children.equals(inode.children) && isModifiable() == inode.isModifiable();
        }
        return false;
    }
}
