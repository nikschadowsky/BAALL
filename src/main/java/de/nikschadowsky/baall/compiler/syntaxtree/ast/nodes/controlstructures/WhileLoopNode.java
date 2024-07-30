package de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.controlstructures;

import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.expression.ExpressionNode;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.program.StatementsNode;

/**
 * File created on 30.07.2024
 */
public interface WhileLoopNode extends ControlStructureNode {

    ExpressionNode getCondition();

    StatementsNode getBody();

}
