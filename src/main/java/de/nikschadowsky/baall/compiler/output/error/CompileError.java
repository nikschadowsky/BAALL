package de.nikschadowsky.baall.compiler.output.error;


/**
 * @since 03.01.2025
 */
public record CompileError(CompileStage stage, Diagnostic diagnostic) {

    enum CompileStage {
        LEXER, TOKENIZER, PARSER, SEMANTICS
    }

}
