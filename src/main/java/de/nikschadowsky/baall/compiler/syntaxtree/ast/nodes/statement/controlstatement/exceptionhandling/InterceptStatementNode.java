package de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.statement.controlstatement.exceptionhandling;

import de.nikschadowsky.baall.compiler.symbol.Token;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.Node;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.program.StatementsNode;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.value.IdentifierAccessNode;

import java.util.List;

/**
 * @since 11.08.2024
 */
public interface InterceptStatementNode extends Node {

    List<IdentifierAccessNode> getExceptions();

    Token getExceptionIdentifier();

    StatementsNode getBody();

}
