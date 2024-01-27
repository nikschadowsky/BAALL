package de.nikschadowsky.baall.compiler.syntaxtree.cst.node;

import de.nikschadowsky.baall.compiler.lexer.tokens.Token;

/**
 * File created on 14.01.2024
 */
public non-sealed class SyntaxTreeLeafNode extends SyntaxTreeNode<Token> implements SyntaxTreeLeaf {

    public SyntaxTreeLeafNode(Token value, int depth) {
        super(value, depth);
    }

}
