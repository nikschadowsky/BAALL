package de.nikschadowsky.baall.compiler._utility.ast.nodes;


import de.nikschadowsky.baall.compiler._utility.BaseAssertion;
import de.nikschadowsky.baall.compiler._utility.ast.NodeAssertionFactory;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.controlstructures.WhileLoopNode;

/**
 * @since 16.02.2025
 */
public class WhileLoopNodeAssertion extends BaseAssertion<WhileLoopNodeAssertion, WhileLoopNode> {

    public WhileLoopNodeAssertion(WhileLoopNode actual) {
        super(actual, WhileLoopNodeAssertion.class);
    }

    public WhileLoopNodeAssertion hasConditionMatching(NodeAssertionBuilder<ExpressionNodeAssertion> a) {
        a.assertThat(NodeAssertionFactory.create(actual.getCondition()));
        return this;
    }

    public WhileLoopNodeAssertion hasBodyMatching(NodeAssertionBuilder<StatementsNodeAssertion> a) {
        a.assertThat(NodeAssertionFactory.create(actual.getBody()));
        return this;
    }
}
