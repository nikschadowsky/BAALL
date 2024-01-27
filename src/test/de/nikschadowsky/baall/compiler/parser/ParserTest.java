package de.nikschadowsky.baall.compiler.parser;

import de.nikschadowsky.baall.compiler._utility.GrammarUtility;
import de.nikschadowsky.baall.compiler.grammar.Grammar;
import de.nikschadowsky.baall.compiler.grammar.GrammarNonterminal;
import de.nikschadowsky.baall.compiler.grammar.GrammarReader;
import de.nikschadowsky.baall.compiler.syntaxtree.cst.ConcreteSyntaxTree;
import de.nikschadowsky.baall.compiler.syntaxtree.cst.SyntaxTreeFormatter;
import de.nikschadowsky.baall.compiler.syntaxtree.cst.node.ConcreteSyntaxTreeInternalNode;
import de.nikschadowsky.baall.compiler.syntaxtree.cst.node.ConcreteSyntaxTreeLeafNode;
import de.nikschadowsky.baall.compiler.syntaxtree.cst.node.ConcreteSyntaxTreeNode;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;

import static de.nikschadowsky.baall.compiler._utility.GrammarUtility.getTokenQueue;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * File created on 20.01.2024
 */
class ParserTest {


    Grammar g;

    @Test
    void generateSyntaxTree() {
        g = GrammarReader.getInstance().generateGrammar("test_resources/ParserTestGrammar.grammar");

        ConcreteSyntaxTree tree = new Parser().parse(g, new LinkedList<>(
                getTokenQueue("c", "d", "e", "f", "g", "h", "b", "a", "c", "d", "e", "f", "g", "h")));

        System.out.println(SyntaxTreeFormatter.treeToVisualizedString(tree));

        assertEquals(getTree(), tree);
    }

    private ConcreteSyntaxTree getTree() {

        ConcreteSyntaxTreeInternalNode A = getInternalNode("A", 1)
                .addC(getInternalNode("B", 2)
                        .addC(getLeafNode("c", 3))
                        .addC(getInternalNode("C", 3)
                                .addC(getInternalNode("D", 4)
                                        .addC(getLeafNode("d", 5))
                                        .addC(getLeafNode("e", 5))
                                        .addC(getInternalNode("E", 5)
                                                .addC(getLeafNode("f", 6))
                                                .addC(getLeafNode("g", 6))
                                                .addC(getLeafNode("h", 6))
                                        )
                                )
                        )
                )
                .addC(getLeafNode("b", 2));


        ConcreteSyntaxTreeInternalNode B = getInternalNode("B", 1)
                .addC(getLeafNode("c", 2))
                .addC(getInternalNode("C", 2)
                        .addC(getInternalNode("D", 3)
                                .addC(getLeafNode("d", 4))
                                .addC(getLeafNode("e", 4))
                                .addC(getInternalNode("E", 4)
                                        .addC(getLeafNode("f", 5))
                                        .addC(getLeafNode("g", 5))
                                        .addC(getLeafNode("h", 5))
                                )
                        )

                );

        InternalNodeWrapperConcrete root = getInternalNode("ROOT", 0);

        root.addC(A).addC(getLeafNode("a", 1)).addC(B);

        return new ConcreteSyntaxTree(root);

    }

    private InternalNodeWrapperConcrete getInternalNode(String nonterminalId, int depth) {
        return new InternalNodeWrapperConcrete(GrammarUtility.getNonterminal(g, nonterminalId), depth);
    }

    private ConcreteSyntaxTreeLeafNode getLeafNode(String tokenValue, int depth) {
        return new ConcreteSyntaxTreeLeafNode(GrammarUtility.getTokenWithTypeAny(tokenValue), depth);
    }


    private static class InternalNodeWrapperConcrete extends ConcreteSyntaxTreeInternalNode {
        public InternalNodeWrapperConcrete(GrammarNonterminal s, int depth) {
            super(s, depth);
        }

        public InternalNodeWrapperConcrete addC(ConcreteSyntaxTreeNode<?> child) {
            addChild(child);
            return this;
        }

    }
}