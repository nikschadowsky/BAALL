package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.program;

import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.Node;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.IdentifierAccessNode;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.List;

/**
 * @since 30.07.2024
 */
public interface ExportsNode extends Node {

    @UnmodifiableView
    List<IdentifierAccessNode> getExportedElements();

}
