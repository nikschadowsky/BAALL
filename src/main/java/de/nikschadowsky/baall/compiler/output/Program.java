package de.nikschadowsky.baall.compiler.output;

import de.nikschadowsky.baall.compiler.syntaxtree.cst.ConcreteSyntaxTree;

import java.nio.file.Path;

/**
 * File created on 19.01.2024
 */
public class Program {

    private final Path destinationPath;
    private final Path sourcePath;
    private final String sourceCode;

    private String preprocessedCode;

    private ConcreteSyntaxTree concreteSyntaxTree;

    private boolean isCompiled;

    public boolean isCompiled() {
        return isCompiled;
    }

    public Program(Path sourcePath, String sourceCode, Path destinationPath) {
        this.sourceCode = sourceCode;
        this.sourcePath = sourcePath;
        this.destinationPath = destinationPath;
    }

    public void readSourceAndSanitize(){

    }
}
