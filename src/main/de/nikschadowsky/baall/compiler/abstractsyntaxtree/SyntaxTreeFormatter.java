package de.nikschadowsky.baall.compiler.abstractsyntaxtree;

import de.nikschadowsky.baall.compiler.abstractsyntaxtree.node.SyntaxTreeInternalNode;
import de.nikschadowsky.baall.compiler.abstractsyntaxtree.node.SyntaxTreeNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * File created on 16.01.2024
 */
public class SyntaxTreeFormatter {

    public static String treeToVisualizedString(@NotNull SyntaxTree tree) {
        return traversePreOrder(tree.getRoot());
    }

    private static String traversePreOrder(@Nullable SyntaxTreeInternalNode root) {
        if (root == null) return "null";

        StringBuilder builder = new StringBuilder();

        builder.append(root.getValue().getFormatted());

        List<SyntaxTreeNode<?>> children = root.getChildren();

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
            @NotNull SyntaxTreeNode<?> node,
            boolean hasRightSibling
    ) {
        builder.append("\n");
        builder.append(padding);
        builder.append(pointer);
        builder.append(node.getValue().getFormatted());

        String nextPadding = hasRightSibling ? padding + "│   " : padding + "    ";

        if (node.isLeafNode()) return;

        List<SyntaxTreeNode<?>> children = ((SyntaxTreeInternalNode) node).getChildren();

        for (int i = 0; i < children.size() - 1; i++) {
            traversePreOrder(builder, nextPadding, "├───", children.get(i), true);
        }
        traversePreOrder(builder, nextPadding, "└───", children.get(children.size() - 1), false);
    }
}
