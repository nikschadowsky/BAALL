package de.nikschadowsky.baall.compiler._utility.ast.nodes;


import de.nikschadowsky.baall.compiler._utility.BaseAssertion;
import de.nikschadowsky.baall.compiler._utility.ast.NodeAssertionFactory;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.FieldNode;

/**
 * @since 12.02.2025
 */
public class FieldNodeAssertion extends BaseAssertion<FieldNodeAssertion, FieldNode> {

    public FieldNodeAssertion(FieldNode actual) {
        super(actual, FieldNodeAssertion.class);
    }

    public FieldNodeAssertion hasName(String expected) {
        NodeAssertionFactory.create(actual.getIdentifier()).hasName(expected);
        return this;
    }

    public FieldNodeAssertion hasTypeMatching(NodeAssertionBuilder<TypeNodeAssertion> a) {
        a.assertThat(NodeAssertionFactory.create(actual.getType()).withFailMessage("Type does not match"));
        return this;
    }
}
