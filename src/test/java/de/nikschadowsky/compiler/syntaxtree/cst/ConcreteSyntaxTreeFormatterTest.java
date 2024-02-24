package de.nikschadowsky.compiler.syntaxtree.cst;

import de.nikschadowsky.compiler._utility.GrammarUtility;
import de.nikschadowsky.compiler.grammar.GrammarNonterminal;
import de.nikschadowsky.compiler.syntaxtree.cst.node.ConcreteSyntaxTreeInternalNode;
import de.nikschadowsky.compiler.syntaxtree.cst.node.ConcreteSyntaxTreeLeafNode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * File created on 19.01.2024
 */
class ConcreteSyntaxTreeFormatterTest {

    @Test
    void treeToVisualizedString() {
        String target = """
                ROOT
                ├───A
                │   ├───B
                │   │   ├───'c'
                │   │   └───C
                │   │       └───D
                │   │           ├───'d'
                │   │           ├───'e'
                │   │           └───E
                │   │               ├───'f'
                │   │               ├───'g'
                │   │               └───'h'
                │   └───'b'
                ├───'a'
                └───B
                    ├───'c'
                    └───C
                        └───D
                            ├───'d'
                            ├───'e'
                            └───E
                                ├───'f'
                                ├───'g'
                                └───'h'""";




        String visualization = SyntaxTreeFormatter.treeToVisualizedString(getTree());

        System.out.println(visualization);
        assertEquals(target, visualization, "Expected: " + target);
    }

    private ConcreteSyntaxTree getTree(){
        ConcreteSyntaxTreeInternalNode root = new ConcreteSyntaxTreeInternalNode(new GrammarNonterminal("ROOT"), 0);

        GrammarNonterminal[] nonterminals = new GrammarNonterminal[5];
        ConcreteSyntaxTreeLeafNode[] leaves = new ConcreteSyntaxTreeLeafNode[8];
        ConcreteSyntaxTreeInternalNode[] inodes = new ConcreteSyntaxTreeInternalNode[5];

        for (int i = 0; i < 5; i++) {
            nonterminals[i] = new GrammarNonterminal(Character.toString((char) ('A' + i)));
        }

        for (int i = 0; i < 8; i++) {
            leaves[i] = new ConcreteSyntaxTreeLeafNode(GrammarUtility.getTokenWithTypeAny(Character.toString((char) ('a' + i))), 0);
        }

        for (int i = 0; i < 5; i++) {
            inodes[i] = new ConcreteSyntaxTreeInternalNode(nonterminals[i], 0);
        }

        root.addChild(inodes[0]);
        root.addChild(leaves[0]);
        root.addChild(inodes[1]);

        inodes[0].addChild(inodes[1]);
        inodes[0].addChild(leaves[1]);
        inodes[0].setUnmodifiable();

        inodes[1].addChild(leaves[2]);
        inodes[1].addChild(inodes[2]);
        inodes[1].setUnmodifiable();

        inodes[2].addChild(inodes[3]);
        inodes[2].setUnmodifiable();

        inodes[3].addChild(leaves[3]);
        inodes[3].addChild(leaves[4]);
        inodes[3].addChild(inodes[4]);
        inodes[3].setUnmodifiable();

        inodes[4].addChild(leaves[5]);
        inodes[4].addChild(leaves[6]);
        inodes[4].addChild(leaves[7]);
        inodes[4].setUnmodifiable();

        return new ConcreteSyntaxTree(root);
    }
}