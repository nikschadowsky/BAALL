package de.nikschadowsky.baall.compiler._utility.ast.nodes;


import de.nikschadowsky.baall.compiler._utility.BaseAssertion;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.program.StatementsNode;

/**
 * @since 12.02.2025
 */
public class StatementsNodeAssertion extends BaseAssertion<StatementsNodeAssertion, StatementsNode> {

    public StatementsNodeAssertion(StatementsNode actual) {
        super(actual, StatementsNodeAssertion.class);
    }

    public StatementsNodeAssertion hasStatements(int expected) {
        return baseAssert("statement count", node -> node.getStatements().size(), expected);
    }

    public StatementsNodeAssertion hasNoStatements() {
        return hasStatements(0);
    }

}
