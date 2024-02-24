package de.nikschadowsky.compiler.syntaxtree.cst.node;

import de.nikschadowsky.compiler.lexer.tokens.Token;

/**
 * File created on 14.01.2024
 */
public non-sealed class ConcreteSyntaxTreeLeafNode extends ConcreteSyntaxTreeNode<Token> implements ConcreteSyntaxTreeLeaf {

    public ConcreteSyntaxTreeLeafNode(Token value, int depth) {
        super(value, depth);
    }

    @Override
    public boolean isLeafNode() {
        return true;
    }

}
