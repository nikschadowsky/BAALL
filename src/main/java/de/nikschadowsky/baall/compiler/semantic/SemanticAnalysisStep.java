package de.nikschadowsky.baall.compiler.semantic;


import de.nikschadowsky.baall.compiler.CompileInformation;
import de.nikschadowsky.baall.compiler.Step;
import de.nikschadowsky.baall.compiler.StepOptions;
import de.nikschadowsky.baall.compiler.output.BaallFile;
import de.nikschadowsky.baall.compiler.output.error.CompileException;
import de.nikschadowsky.baall.compiler.semantic.attribute.Scope;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.Node;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.program.ProgramNode;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * @since 22.01.2025
 */
public class SemanticAnalysisStep extends Step<ProgramNode, BaallFile> {

    public SemanticAnalysisStep(@Nullable StepOptions options) {
        super(options);
    }

    @Override
    public BaallFile executeStep(
            ProgramNode programNode,
            CompileInformation compileInformation
    ) throws CompileException {
        Map<Node, Scope> scopes = new HashMap<>();

        // 1st pass: collect all function declarations and save them in a map. the map contains information about the function declaration and calls to these functions (including the scopes)
        // 2nd pass: collect type information on all identifiers. unrecognized identifiers (not contained in the symbol table) should be flagged as such and possibly allowed types.
        // 3rd     : if symbol table contains unrecognized elements try resolving imports lazily
        // 4th     : if symbol table still contains unrecognized elements, throw exception
        // 5th     : check if functions get called from allowed scopes.

        return null;
    }

}
