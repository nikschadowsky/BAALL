package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.literal;

import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.FieldNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.LiteralNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.List;

/**
 * Represents a struct type definition.
 *
 * @since 29.07.2024
 */
public interface StructDefinitionLiteralNode extends LiteralNode {

    /**
     * Fields of the struct type.
     *
     * @return struct fields
     */
    @NotNull
    @UnmodifiableView
    List<FieldNode> getFields();
}
