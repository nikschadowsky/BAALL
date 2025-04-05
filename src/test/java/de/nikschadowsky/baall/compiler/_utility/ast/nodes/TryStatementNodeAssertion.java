package de.nikschadowsky.baall.compiler._utility.ast.nodes;


import de.nikschadowsky.baall.compiler._utility.BaseAssertion;
import de.nikschadowsky.baall.compiler._utility.ast.NodeAssertionFactory;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.controlstatement.exceptionhandling.EnsureStatementNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.controlstatement.exceptionhandling.InterceptStatementNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.controlstatement.exceptionhandling.TryStatementNode;

/**
 * @since 16.02.2025
 */
public class TryStatementNodeAssertion extends BaseAssertion<TryStatementNodeAssertion, TryStatementNode> {

    public TryStatementNodeAssertion(TryStatementNode actual) {
        super(actual, TryStatementNodeAssertion.class);
    }

    public TryStatementNodeAssertion hasBodyMatching(NodeAssertionBuilder<StatementsNodeAssertion> a) {
        a.assertThat(NodeAssertionFactory.create(actual.getBody()));
        return this;
    }

    public TryStatementNodeAssertion hasInterceptBlocks(int expectedSize) {
        return baseAssert("intercept blocks", node -> node.getInterceptBlocks().size(), expectedSize);
    }

    public TryStatementNodeAssertion hasInterceptBlockMatching(int index, NodeAssertionBuilder<InterceptStatementNodeAssertion> a) {
        a.assertThat(NodeAssertionFactory.create(actual.getInterceptBlocks().get(index)));
        return this;
    }

    public EnsureStatementNodeAssertion hasEnsureBlock() {
        truthinessAssert(node -> "Try does not have an ensure block", node -> node.getEnsureBlock().isPresent());
        return NodeAssertionFactory.create(actual.getEnsureBlock().orElseThrow());
    }

    public TryStatementNodeAssertion doesNotHaveEnsureBlock() {
        truthinessAssert(node -> "Try does not have an ensure block", node -> node.getEnsureBlock().isEmpty());
        return this;
    }

    public static class EnsureStatementNodeAssertion extends BaseAssertion<EnsureStatementNodeAssertion, EnsureStatementNode> {

        public EnsureStatementNodeAssertion(EnsureStatementNode actual) {
            super(actual, EnsureStatementNodeAssertion.class);
        }

        public EnsureStatementNodeAssertion hasBodyMatching(NodeAssertionBuilder<StatementsNodeAssertion> a) {
            a.assertThat(NodeAssertionFactory.create(actual.getBody()));
            return this;
        }
    }

    /**
     * @since 22.02.2025
     */
    public static class InterceptStatementNodeAssertion extends BaseAssertion<InterceptStatementNodeAssertion, InterceptStatementNode> {

        public InterceptStatementNodeAssertion(InterceptStatementNode actual) {
            super(actual, InterceptStatementNodeAssertion.class);
        }

        public InterceptStatementNodeAssertion hasIdentifier(String identifier) {
            return hasIdentifierMatching(a -> a.hasIdentifierName(identifier));
        }

        public InterceptStatementNodeAssertion hasIdentifierMatching(NodeAssertionBuilder<StatementNodeAssertion.VariableDeclarationNodeAssertion> a) {
            a.assertThat(NodeAssertionFactory.create(actual.getCaughtException()));
            return this;
        }

        public InterceptStatementNodeAssertion hasExceptionMatching(NodeAssertionBuilder<TypeNodeAssertion> a) {
            a.assertThat(NodeAssertionFactory.create(actual.getCaughtException().getType()));
            return this;
        }

        public InterceptStatementNodeAssertion hasBodyMatching(NodeAssertionBuilder<StatementsNodeAssertion> a) {
            a.assertThat(NodeAssertionFactory.create(actual.getBody()));
            return this;
        }
    }
}
