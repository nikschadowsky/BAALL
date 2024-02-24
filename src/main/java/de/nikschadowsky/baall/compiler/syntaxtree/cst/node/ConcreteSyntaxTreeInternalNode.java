package de.nikschadowsky.baall.compiler.syntaxtree.cst.node;

import de.nikschadowsky.baall.compiler.grammar.GrammarNonterminal;
import de.nikschadowsky.baall.compiler.grammar.GrammarSymbol;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * File created on 14.01.2024
 */
public non-sealed class ConcreteSyntaxTreeInternalNode extends ConcreteSyntaxTreeNode<GrammarNonterminal> {

    private boolean isModifiable;
    private final List<ConcreteSyntaxTreeNode<? extends GrammarSymbol>> children = new ArrayList<>();

    public ConcreteSyntaxTreeInternalNode(GrammarNonterminal symbol, int depth) {
        super(symbol, depth);
    }

    @Override
    public boolean isLeafNode() {
        return getChildren().isEmpty();
    }

    public void addChild(ConcreteSyntaxTreeNode<?> child) {
        children.add(child);
    }

    public @Unmodifiable List<ConcreteSyntaxTreeNode<?>> getChildren() {
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
        if (obj instanceof ConcreteSyntaxTreeInternalNode inode) {
            return super.equals(inode) && children.equals(inode.children) && isModifiable() == inode.isModifiable();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), isModifiable, children);
    }
}
