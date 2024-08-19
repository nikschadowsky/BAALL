package de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.program;

import de.nikschadowsky.baall.compiler.symbol.Token;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.Node;

import java.util.List;

/**
 * @since 30.07.2024
 */
public interface ImportsNode extends Node {

    List<Token> getImports();

}
