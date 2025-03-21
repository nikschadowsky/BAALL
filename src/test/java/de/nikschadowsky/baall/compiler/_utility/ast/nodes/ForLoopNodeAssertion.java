package de.nikschadowsky.baall.compiler._utility.ast.nodes;


import de.nikschadowsky.baall.compiler._utility.BaseAssertion;
import de.nikschadowsky.baall.compiler._utility.ast.NodeAssertionFactory;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.controlstructures.ForLoopNode;

/**
 * @since 16.02.2025
 */
public class ForLoopNodeAssertion extends BaseAssertion<ForLoopNodeAssertion, ForLoopNode> {

    public ForLoopNodeAssertion(ForLoopNode actual) {
        super(actual, ForLoopNodeAssertion.class);
    }

    public ForLoopNodeAssertion hasIdentifier(String expected) {
        return hasIdentifierMatching(a -> a.hasName(expected));
    }

    public ForLoopNodeAssertion hasIdentifierMatching(NodeAssertionBuilder<ElementAccessNodeAssertion.IdentifierNodeAssertion> a) {
        a.assertThat(NodeAssertionFactory.create(actual.getIdentifier()));
        return this;
    }

    public ForLoopNodeAssertion hasStartIndexMatching(NodeAssertionBuilder<ExpressionNodeAssertion> a) {
        a.assertThat(NodeAssertionFactory.create(actual.getStartIndexExpression()));
        return this;
    }

    public ForLoopNodeAssertion hasEndIndexMatching(NodeAssertionBuilder<ExpressionNodeAssertion> a) {
        a.assertThat(NodeAssertionFactory.create(actual.getEndIndexExpression()));
        return this;
    }

    public ForLoopNodeAssertion hasBodyMatching(NodeAssertionBuilder<StatementsNodeAssertion> a) {
        a.assertThat(NodeAssertionFactory.create(actual.getBody()));
        return this;
    }

    public ForLoopNodeAssertion hasOptionalStepper() {
        return truthinessAssert(
                node -> "For loop does not have optional stepper",
                node -> node.getOptionalStepperStatement().isPresent()
        );
    }

    public ForLoopNodeAssertion doesNotHaveOptionalStepper() {
        return truthinessAssert(
                node -> "For loop does have optional stepper",
                node -> node.getOptionalStepperStatement().isEmpty()
        );
    }

    public ForLoopNodeAssertion hasOptionalStepperMatching(NodeAssertionBuilder<StatementNodeAssertion.ReassignmentNodeAssertion> a) {
        a.assertThat(NodeAssertionFactory.create(actual.getOptionalStepperStatement().orElseThrow()));
        return this;
    }
}
