package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.program;

import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.Node;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.access.ElementAccessNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.access.IdentifierNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.List;
import java.util.Optional;

/**
 * Represents the exports of a program.
 *
 * @since 30.07.2024
 */
public interface ExportsNode extends Node {

    /**
     * Exported elements referenced by their identifier access nodes.
     *
     * @return exported elements
     */
    @NotNull
    @UnmodifiableView
    List<ElementAccessNode> getExportedElements();

    /**
     * Optional namespace of the exports. A namespace allows for referencing exported fields in another program when
     * there are conflicting element names.
     *
     * @return optional namespace identifier
     * @apiNote This optional is empty when there are no exports
     */
    Optional<IdentifierNode> getNamespace();

}
