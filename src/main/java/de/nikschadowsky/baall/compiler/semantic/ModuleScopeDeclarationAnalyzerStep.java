package de.nikschadowsky.baall.compiler.semantic;


import de.nikschadowsky.baall.compiler.CompileInformation;
import de.nikschadowsky.baall.compiler.Step;
import de.nikschadowsky.baall.compiler.StepOptions;
import de.nikschadowsky.baall.compiler.output.error.CompileException;
import de.nikschadowsky.baall.compiler.symbol.SymbolTable;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.program.ProgramNode;
import de.nikschadowsky.baall.compiler.util.Pair;
import org.jetbrains.annotations.Nullable;

/**
 * @since 19.01.2025
 */
public class ModuleScopeDeclarationAnalyzerStep extends Step<ProgramNode, Pair<ProgramNode, SymbolTable>> {

    public ModuleScopeDeclarationAnalyzerStep(@Nullable StepOptions options) {
        super(options);
    }

    @Override
    public Pair<ProgramNode, SymbolTable> executeStep(
            ProgramNode programNode,
            CompileInformation compileInformation
    ) throws CompileException {
        // find

        return null;
    }
}
