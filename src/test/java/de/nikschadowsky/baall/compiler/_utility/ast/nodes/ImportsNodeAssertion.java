package de.nikschadowsky.baall.compiler._utility.ast.nodes;


import de.nikschadowsky.baall.compiler._utility.BaseAssertion;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.program.ImportsNode;

/**
 * @since 27.02.2025
 */
public class ImportsNodeAssertion extends BaseAssertion<ImportsNodeAssertion, ImportsNode> {

    public ImportsNodeAssertion(ImportsNode actual) {
        super(actual, ImportsNodeAssertion.class);
    }

    public ImportsNodeAssertion hasImports(int expected) {
        return baseAssert("import count", node -> node.getImports().size(), expected);
    }

    public ImportsNodeAssertion hasNoImports() {
        return hasImports(0);
    }

    public ImportsNodeAssertion hasImport(int index, String imported) {
        return truthinessAssert(
                node -> "import does not match",
                node -> node.getImports().get(index).value().equals(imported)
        );
    }
}
