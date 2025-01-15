package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.controlstructures;

import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.expression.ExpressionNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.program.StatementsNode;

/**
 * @since 30.07.2024
 */
public interface WhileLoopNode extends ControlStructureNode {

    ExpressionNode getCondition();

    StatementsNode getBody();

}
