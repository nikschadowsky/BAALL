package de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.statement.controlstatement.exceptionhandling;

import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.Node;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.program.StatementsNode;

/**
 * @since 11.08.2024
 */
public interface EnsureStatementNode extends Node {

    StatementsNode getBody();

}
