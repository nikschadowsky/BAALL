package de.nikschadowsky.baall.compiler.abstractsyntaxtree;

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

    public SyntaxTreeInternalNode(GrammarNonterminal symbol) {
        super(symbol);
    }

    public void addChild(SyntaxTreeNode<? extends GrammarSymbol> child) {
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
}
