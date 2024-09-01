package de.nikschadowsky.baall.compiler.syntaxtree.cst;

import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.Node;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.program.ProgramNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @since 16.01.2024
 */
public class SyntaxTreeFormatter {

    public static String treeToVisualizedString(@NotNull ProgramNode tree) {
        return traversePreOrder(tree);
    }

    private static String traversePreOrder(@Nullable Node root) {
        if (root == null) return "null";

        StringBuilder builder = new StringBuilder();

        /*builder.append(root);

        List<ConcreteSyntaxTreeNode> children = root.getChildren();

        for (int i = 0; i < children.size() - 1; i++) {
            traversePreOrder(builder, "", "├───", children.get(i), true);
        }
        traversePreOrder(builder, "", "└───", children.get(children.size() - 1), false);
        */
        return builder.toString();
    }


    private static void traversePreOrder(
            @NotNull StringBuilder builder,
            @NotNull String padding,
            @NotNull String pointer,
            @NotNull Node node,
            boolean hasRightSibling
    ) {
        builder.append("\n");
        builder.append(padding);
        builder.append(pointer);
        builder.append(node);

        String nextPadding = hasRightSibling ? padding + "│   " : padding + "    ";

        /*if (node.isLeafNode()) return;

        List<ConcreteSyntaxTreeNode<?>> children = ((ConcreteSyntaxTreeInternalNode) node).getChildren();

        for (int i = 0; i < children.size() - 1; i++) {
            traversePreOrder(builder, nextPadding, "├───", children.get(i), true);
        }
        traversePreOrder(builder, nextPadding, "└───", children.get(children.size() - 1), false); */
    }
}
