package de.nikschadowsky.baall.compiler.syntaxtree.cst;

import de.nikschadowsky.baall.compiler.syntaxtree.cst.node.ConcreteSyntaxTreeInternalNode;
import de.nikschadowsky.baall.compiler.syntaxtree.cst.node.ConcreteSyntaxTreeNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * File created on 16.01.2024
 */
public class SyntaxTreeFormatter {

    public static String treeToVisualizedString(@NotNull ConcreteSyntaxTree tree) {
        return traversePreOrder(tree.getRoot());
    }

    private static String traversePreOrder(@Nullable ConcreteSyntaxTreeInternalNode root) {
        if (root == null) return "null";

        StringBuilder builder = new StringBuilder();

        builder.append(root.getValue().getFormatted());

        List<ConcreteSyntaxTreeNode<?>> children = root.getChildren();

        for (int i = 0; i < children.size() - 1; i++) {
            traversePreOrder(builder, "", "├───", children.get(i), true);
        }
        traversePreOrder(builder, "", "└───", children.get(children.size() - 1), false);

        return builder.toString();
    }


    private static void traversePreOrder(
            @NotNull StringBuilder builder,
            @NotNull String padding,
            @NotNull String pointer,
            @NotNull ConcreteSyntaxTreeNode<?> node,
            boolean hasRightSibling
    ) {
        builder.append("\n");
        builder.append(padding);
        builder.append(pointer);
        builder.append(node.getValue().getFormatted());

        String nextPadding = hasRightSibling ? padding + "│   " : padding + "    ";

        if (node.isLeafNode()) return;

        List<ConcreteSyntaxTreeNode<?>> children = ((ConcreteSyntaxTreeInternalNode) node).getChildren();

        for (int i = 0; i < children.size() - 1; i++) {
            traversePreOrder(builder, nextPadding, "├───", children.get(i), true);
        }
        traversePreOrder(builder, nextPadding, "└───", children.get(children.size() - 1), false);
    }
}
