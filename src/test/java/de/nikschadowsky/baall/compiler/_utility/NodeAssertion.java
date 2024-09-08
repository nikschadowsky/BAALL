package de.nikschadowsky.baall.compiler._utility;


import de.nikschadowsky.baall.compiler.syntaxtree.ast.NodeType;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.Node;

/**
 * @since 08.09.2024
 */
public class NodeAssertion extends BaseAssertion<NodeAssertion, Node> {

    protected NodeAssertion(Node actual) {
        super(actual, NodeAssertion.class);
    }

    public NodeAssertion hasNodeType(NodeType expectedNodeType) {
        return baseAssert("nodeType", Node::getNodeType, expectedNodeType);
    }
}
