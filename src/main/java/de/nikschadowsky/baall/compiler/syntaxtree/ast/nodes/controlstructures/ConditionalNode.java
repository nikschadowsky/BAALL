package de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.controlstructures;

import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.expression.ExpressionNode;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.program.StatementsNode;

import java.util.Optional;

/**
 * @since 30.07.2024
 */
public interface ConditionalNode extends ControlStructureNode{

    ExpressionNode getCondition();

    StatementsNode getThenBlock();

    Optional<ConditionalNode> getElseBranch();

    ConditionBranch getConditionBranch();

    enum ConditionBranch {
        IF, ELSE
    }
}
