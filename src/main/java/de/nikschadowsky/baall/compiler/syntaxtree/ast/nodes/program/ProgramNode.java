package de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.program;

import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.Node;

/**
 * File created on 30.07.2024
 */
public interface ProgramNode extends Node {

    ImportsNode getImports();

    StatementsNode getStatements();

    ExportsNode getExports();

}
