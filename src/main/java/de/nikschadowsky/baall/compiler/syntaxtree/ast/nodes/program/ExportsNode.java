package de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.program;

import de.nikschadowsky.baall.compiler.syntaxtree.ast.Node;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.value.IdentifierAccessNode;

import java.util.List;

/**
 * File created on 30.07.2024
 */
public interface ExportsNode extends Node {

    List<IdentifierAccessNode> getExportedElements();

}
