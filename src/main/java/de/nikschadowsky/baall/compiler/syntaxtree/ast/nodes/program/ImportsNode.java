package de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.program;

import de.nikschadowsky.baall.compiler.symbol.Token;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.Node;

import java.util.List;

/**
 * File created on 30.07.2024
 */
public interface ImportsNode extends Node {

    List<Token> getImports();

}
