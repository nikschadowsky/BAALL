package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.program;

import de.nikschadowsky.baall.compiler.syntax.error.SyntaxDiagnostic;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.Node;

import java.util.List;

/**
 * Represents a BAALL program.
 *
 * @since 30.07.2024
 */
public sealed interface ProgramNode extends Node permits ProgramNodeImpl {

    /**
     * Imports of this program.
     *
     * @return imports
     */
    ImportsNode getImports();

    /**
     * Root body of this program.
     *
     * @return statements of this body
     */
    StatementsNode getStatements();

    /**
     * Exports of this program.
     *
     * @return exports
     */
    ExportsNode getExports();

    /**
     * Collected syntax diagnostics that occurred during the parse process.
     *
     * @return syntax diagnostics
     */
    List<SyntaxDiagnostic> getSyntaxDiagnostics();
}
