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

    public ForLoopNodeAssertion hasIdentifierMatching(NodeAssertionBuilder<IdentifierNodeAssertion> a) {
        a.assertThat(NodeAssertionFactory.create(actual.getIdentifier()).withFailMessage("Identifier does not match"));
        return this;
    }

    public ForLoopNodeAssertion hasStartIndexMatching(NodeAssertionBuilder<ExpressionNodeAssertion> a) {
        a.assertThat(NodeAssertionFactory.create(actual.getStartIndexExpression())
                                         .withFailMessage("Start index does not match"));
        return this;
    }

    public ForLoopNodeAssertion hasEndIndexMatching(NodeAssertionBuilder<ExpressionNodeAssertion> a) {
        a.assertThat(NodeAssertionFactory.create(actual.getEndIndexExpression())
                                         .withFailMessage("End index does not match"));
        return this;
    }

    public ForLoopNodeAssertion hasBodyMatching(NodeAssertionBuilder<StatementsNodeAssertion> a) {
        a.assertThat(NodeAssertionFactory.create(actual.getBody()).withFailMessage("Body does not match"));
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
        a.assertThat(NodeAssertionFactory.create(actual.getOptionalStepperStatement().orElseThrow())
                                         .withFailMessage("OptionalStepper does not match"));
        return this;
    }
}
