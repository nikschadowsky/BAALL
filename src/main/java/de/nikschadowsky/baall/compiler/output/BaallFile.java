package de.nikschadowsky.baall.compiler.output;


import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.program.ProgramNode;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @since 22.01.2025
 */
public class BaallFile {

    private final String fileName;

    private final ProgramNode program;

    private final SymbolTable symbolTable;

    public BaallFile(@NotNull String fileName, @NotNull ProgramNode program, @NotNull SymbolTable symbolTable) {
        this.fileName = fileName;
        this.program = program;
        this.symbolTable = symbolTable;
    }

    public String getFileName() {
        return fileName;
    }

    public ProgramNode getProgram() {
        return program;
    }

    public boolean isExporting() {
        return program.getExports().getNamespace().isPresent();
    }

    public List<BaallFileReference> getImports() {
        return program.getImports().getImports().stream().map(token -> new BaallFileReference(token.value())).toList();
    }
}
