package de.nikschadowsky.baall.compiler._utility.ast.nodes;


import de.nikschadowsky.baall.compiler._utility.BaseAssertion;
import de.nikschadowsky.baall.compiler._utility.ast.NodeAssertionFactory;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.controlstructures.ConditionalNode;

/**
 * @since 16.02.2025
 */
public class ConditionalNodeAssertion extends BaseAssertion<ConditionalNodeAssertion, ConditionalNode> {

    public ConditionalNodeAssertion(ConditionalNode actual) {
        super(actual, ConditionalNodeAssertion.class);
    }

    public ConditionalNodeAssertion hasConditionMatching(NodeAssertionBuilder<ExpressionNodeAssertion> a) {
        a.assertThat(NodeAssertionFactory.create(actual.getCondition()));
        return this;
    }

    public ConditionalNodeAssertion isIfBranch() {
        return baseAssert("branch type", ConditionalNode::getConditionBranch, ConditionalNode.ConditionBranch.IF);
    }

    public ConditionalNodeAssertion isElseBranch() {
        return baseAssert("branch type", ConditionalNode::getConditionBranch, ConditionalNode.ConditionBranch.ELSE);
    }

    public ConditionalNodeAssertion hasBodyMatching(NodeAssertionBuilder<StatementsNodeAssertion> a) {
        a.assertThat(NodeAssertionFactory.create(actual.getThenBlock()));
        return this;
    }

    public ConditionalNodeAssertion hasElseBranch() {
        truthinessAssert(
                node -> "Node does not have else branch",
                node -> node.getElseBranch().isPresent()
        );
        return NodeAssertionFactory.create(actual.getElseBranch().orElseThrow());
    }

    public ConditionalNodeAssertion hasNoElseBranch() {
        return truthinessAssert(
                node -> "Node does not have else branch",
                node -> node.getElseBranch().isEmpty()
        );
    }

}
