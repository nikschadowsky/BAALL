package de.nikschadowsky.baall.compiler.output.error;


import de.nikschadowsky.baall.compiler.lexer.error.LexerDiagnostic;
import de.nikschadowsky.baall.compiler.syntax.error.SyntaxDiagnostic;

import java.util.ArrayList;
import java.util.List;

/**
 * @since 03.01.2025
 */
public final class CompileErrorFactory {

    private static final CompileErrorFactory INSTANCE = new CompileErrorFactory();

    private final List<CompileError> errors = new ArrayList<>();

    private CompileErrorFactory() {}

    public static void createLexerCompileError(LexerDiagnostic diagnostic) {
        INSTANCE.errors.add(new CompileError(CompileError.CompileStage.LEXER, diagnostic));
    }

    public static void createParserCompileError(SyntaxDiagnostic diagnostic) {
        INSTANCE.errors.add(new CompileError(CompileError.CompileStage.PARSER, diagnostic));
    }

}
