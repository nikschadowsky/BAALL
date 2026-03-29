package de.nikschadowsky.baall.compiler.syntax;

import de.nikschadowsky.baall.compiler.CompileInformation;
import de.nikschadowsky.baall.compiler._utility.TestCompileInformation;
import de.nikschadowsky.baall.compiler._utility.TokenQueueTestBuilder;
import de.nikschadowsky.baall.compiler.lexer.LexerRow;
import de.nikschadowsky.baall.compiler.lexer.LexerStep;
import de.nikschadowsky.baall.compiler.output.error.CompileException;
import de.nikschadowsky.baall.compiler.symbol.TokenQueue;
import de.nikschadowsky.baall.compiler.tokenizer.BetterTokenizerStep;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @since 14.01.2025
 */
class ParserStepTest {

    private ParserStep parserStep;

    @BeforeEach
    void setUp() {
        parserStep = new ParserStep(null);
    }

    @Test
    void executeStep() {
        TokenQueue input = new TokenQueueTestBuilder().identifier("MyFunction")
                                                      .separator("(")
                                                      .number("1234")
                                                      .separator(",")
                                                      .bool("true")
                                                      .separator(",")
                                                      .string("StringValue")
                                                      .separator(")")
                                                      .separator(";")
                                                      .build();

        assertThatCode(() -> parserStep.executeStep(input, new TestCompileInformation())).doesNotThrowAnyException();


        TokenQueue incompleteInput = new TokenQueueTestBuilder().identifier("MyFunction")
                                                                .separator("(")
                                                                .number("1234")
                                                                .separator(",")
                                                                .bool("true")
                                                                .separator(",")
                                                                .string("StringValue")
                                                                .separator(";")
                                                                .build();
        assertThatThrownBy(() -> parserStep.executeStep(incompleteInput, new TestCompileInformation())).isInstanceOf(
                CompileException.class);
    }

    @Test
    void testIntegration() throws CompileException {
        CompileInformation compileInformation = new TestCompileInformation();

        String input = """
                use "math.ba";
                // use java "some.java";
                
                /*
                this is a block comment
                */
                number<string,boolean<>>[]: myFunction = none;
                """;

        LexerStep lexer = new LexerStep(null);
        List<LexerRow> lines = lexer.executeStep(input.lines().toList(), compileInformation);
        TokenQueue tokens = new BetterTokenizerStep(null).executeStep(lines, compileInformation);

        assertThatCode(
                () -> parserStep.executeStep(tokens, compileInformation)
        ).doesNotThrowAnyException();
    }
}