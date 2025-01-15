package de.nikschadowsky.baall.compiler.syntax;

import de.nikschadowsky.baall.compiler._utility.TestCompileInformation;
import de.nikschadowsky.baall.compiler._utility.TokenQueueTestBuilder;
import de.nikschadowsky.baall.compiler.output.error.CompileException;
import de.nikschadowsky.baall.compiler.symbol.TokenQueue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
}