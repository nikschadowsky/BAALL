package de.nikschadowsky.baall.compiler._utility.ast.nodes;


import de.nikschadowsky.baall.compiler._utility.BaseAssertion;
import de.nikschadowsky.baall.compiler._utility.ast.NodeAssertionFactory;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.access.*;
import de.nikschadowsky.baall.compiler.tokenizer.TokenType;

/**
 * @since 13.03.2025
 */
public class ElementAccessNodeAssertion extends BaseAssertion<ElementAccessNodeAssertion, ElementAccessNode> {

    public ElementAccessNodeAssertion(ElementAccessNode actual) {
        super(actual, ElementAccessNodeAssertion.class);
    }

    public MemberReferenceNodeAssertion isMemberReference() {
        isInstanceOf(MemberReferenceNode.class);
        return NodeAssertionFactory.create((MemberReferenceNode) actual);
    }

    public ScopeElevationNodeAssertion isScopeElevation() {
        isInstanceOf(ScopeElevationNode.class);
        return NodeAssertionFactory.create((ScopeElevationNode) actual);
    }

    public IndexedAccessNodeAssertion isIndexedAccess() {
        isInstanceOf(IndexedAccessNode.class);
        return NodeAssertionFactory.create((IndexedAccessNode) actual);
    }

    public IdentifierNodeAssertion isIdentifier() {
        isInstanceOf(IdentifierNode.class);
        return NodeAssertionFactory.create((IdentifierNode) actual);
    }

    public static class MemberReferenceNodeAssertion extends BaseAssertion<MemberReferenceNodeAssertion, MemberReferenceNode> {

        public MemberReferenceNodeAssertion(MemberReferenceNode actual) {
            super(actual, MemberReferenceNodeAssertion.class);
        }

        public ElementAccessNodeAssertion mapToInner() {
            return NodeAssertionFactory.create(actual.getInnerIdentifier());
        }

        public MemberReferenceNodeAssertion hasSelfMatching(NodeAssertionBuilder<ElementAccessNodeAssertion> a) {
            a.assertThat(NodeAssertionFactory.create(actual.getSelf()));
            return this;
        }
    }

    public static class ScopeElevationNodeAssertion extends BaseAssertion<ScopeElevationNodeAssertion, ScopeElevationNode> {

        public ScopeElevationNodeAssertion(ScopeElevationNode actual) {
            super(actual, ScopeElevationNodeAssertion.class);
        }

        public ElementAccessNodeAssertion mapToInner() {
            return NodeAssertionFactory.create(actual.getInnerIdentifier());
        }
    }

    public static class IndexedAccessNodeAssertion extends BaseAssertion<IndexedAccessNodeAssertion, IndexedAccessNode> {

        public IndexedAccessNodeAssertion(IndexedAccessNode actual) {
            super(actual, IndexedAccessNodeAssertion.class);
        }

        public IndexedAccessNodeAssertion hasIndexMatching(NodeAssertionBuilder<ExpressionNodeAssertion> a) {
            a.assertThat(NodeAssertionFactory.create(actual.getIndex()));
            return this;
        }

        public ElementAccessNodeAssertion mapToInner() {
            return NodeAssertionFactory.create(actual.getInnerIdentifier());
        }
    }

    /**
     * @since 12.02.2025
     */
    public static class IdentifierNodeAssertion extends BaseAssertion<IdentifierNodeAssertion, IdentifierNode> {

        public IdentifierNodeAssertion(IdentifierNode actual) {
            super(actual, IdentifierNodeAssertion.class);
        }

        public IdentifierNodeAssertion hasName(String expected) {
            return baseAssert("identifier", n -> n.getIdentifier().value(), expected);
        }

        public IdentifierNodeAssertion hasTokenType(TokenType expected) {
            return baseAssert("identifier type", n -> n.getIdentifier().type(), expected);
        }
    }
}
