package de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.expression;

import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.statement.assignment.ReassignmentNode;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.value.TermNode;

/**
 * @since 29.07.2024
 */
public interface UnaryExpressionNode extends TermNode, ReassignmentNode {

    boolean isPrefix();

}
