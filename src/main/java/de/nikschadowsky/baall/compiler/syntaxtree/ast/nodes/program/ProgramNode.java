package de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.program;

import de.nikschadowsky.baall.compiler.syntax.error.SyntaxDiagnostic;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.Node;

import java.util.List;

/**
 * File created on 30.07.2024
 */
public interface ProgramNode extends Node {

    ImportsNode getImports();

    StatementsNode getStatements();

    ExportsNode getExports();

    List<SyntaxDiagnostic> getSyntaxDiagnostics();
}
