package de.nikschadowsky.baall.compiler._utility.ast.nodes;


import de.nikschadowsky.baall.compiler._utility.BaseAssertion;
import de.nikschadowsky.baall.compiler._utility.ast.NodeAssertionFactory;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.program.ExportsNode;

/**
 * @since 27.02.2025
 */
public class ExportsNodeAssertion extends BaseAssertion<ExportsNodeAssertion, ExportsNode> {

    public ExportsNodeAssertion(ExportsNode actual) {
        super(actual, ExportsNodeAssertion.class);
    }

    public ExportsNodeAssertion hasNamespace() {
        return truthinessAssert(
                node -> "Exports node does not have a namespace",
                node -> node.getNamespace().isPresent()
        );
    }

    public ExportsNodeAssertion hasNoNamespace() {
        return truthinessAssert(
                node -> "Exports node does have a namespace",
                node -> node.getNamespace().isEmpty()
        );
    }

    public ExportsNodeAssertion hasNamespaceName(String name) {
        return hasNamespaceNameMatching(a -> a.hasName(name));
    }

    public ExportsNodeAssertion hasNamespaceNameMatching(NodeAssertionBuilder<ElementAccessNodeAssertion.IdentifierNodeAssertion> a) {
        a.assertThat(NodeAssertionFactory.create(actual.getNamespace().orElseThrow()));
        return this;
    }


    public ExportsNodeAssertion hasExports(int expected) {
        return baseAssert("export count", node -> node.getExportedElements().size(), expected);
    }

    public ExportsNodeAssertion hasNoExports() {
        return hasExports(0);
    }

    public ExportsNodeAssertion hasExportedElementMatching(int index, NodeAssertionBuilder<ElementAccessNodeAssertion> a) {
        a.assertThat(NodeAssertionFactory.create(actual.getExportedElements().get(index)));
        return this;
    }
}
