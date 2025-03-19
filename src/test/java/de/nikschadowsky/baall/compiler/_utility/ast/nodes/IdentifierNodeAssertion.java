package de.nikschadowsky.baall.compiler._utility.ast.nodes;


import de.nikschadowsky.baall.compiler._utility.BaseAssertion;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.access.IdentifierNode;
import de.nikschadowsky.baall.compiler.tokenizer.TokenType;

/**
 * @since 12.02.2025
 */
public class IdentifierNodeAssertion extends BaseAssertion<IdentifierNodeAssertion, IdentifierNode> {

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
