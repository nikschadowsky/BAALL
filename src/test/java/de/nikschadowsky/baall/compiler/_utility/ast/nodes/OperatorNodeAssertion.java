package de.nikschadowsky.baall.compiler._utility.ast.nodes;


import de.nikschadowsky.baall.compiler._utility.BaseAssertion;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.expression.OperatorNode;

/**
 * @since 13.02.2025
 */
public class OperatorNodeAssertion extends BaseAssertion<OperatorNodeAssertion, OperatorNode> {

    public OperatorNodeAssertion(OperatorNode actual) {
        super(actual, OperatorNodeAssertion.class);
    }

    public OperatorNodeAssertion hasOperator(String operator) {
        return baseAssert("operator", n -> n.getOperator().value(), operator);
    }
}
