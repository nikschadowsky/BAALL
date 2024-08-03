package de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.statement.assignment;

import de.nikschadowsky.baall.compiler.symbol.Token;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.statement.StatementNode;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.value.IdentifierAccessNode;

/**
 * File created on 29.07.2024
 */
public interface ReassignmentNode extends StatementNode {

    IdentifierAccessNode getIdentifierAccess();

    Token getOperator();

}
