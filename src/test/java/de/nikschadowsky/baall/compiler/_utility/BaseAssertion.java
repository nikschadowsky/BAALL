package de.nikschadowsky.baall.compiler._utility;


import de.nikschadowsky.baall.compiler.lexer.LexerRow;
import de.nikschadowsky.baall.compiler.symbol.TokenQueue;
import de.nikschadowsky.baall.compiler.syntax.analysis.result.ParseResult;
import de.nikschadowsky.baall.compiler.syntax.analysis.result.PartialParseResult;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.Node;
import de.nikschadowsky.baall.compiler.util.LanguageElement;
import org.assertj.core.api.AbstractAssert;

import java.util.Objects;
import java.util.function.Function;

/**
 * @since 15.08.2024
 */
public class BaseAssertion<ASSERTION extends AbstractAssert<ASSERTION, OBJECT>, OBJECT>
        extends AbstractAssert<ASSERTION, OBJECT> {

    protected BaseAssertion(OBJECT actual, Class<?> selfType) {
        super(actual, selfType);
    }

    protected <V> ASSERTION baseAssert(String attributeName, Function<OBJECT, V> valueGetter, V expected) {
        isNotNull();

        final V actualValue = valueGetter.apply(this.actual);
        if (!Objects.equals(actualValue, expected)) {
            failWithMessage(
                    "\nExpected %s of:\n <%s>\n to be:\n <%s>\n but was:\n <%s>",
                    attributeName,
                    actual,
                    expected,
                    actualValue
            );
        }

        return myself;
    }

    protected ASSERTION truthinessAssert(Function<OBJECT, String> failMessageProvider, Function<OBJECT, Boolean> valueGetter) {
        isNotNull();

        if (!valueGetter.apply(this.actual)) {
            failWithMessage(failMessageProvider.apply(actual));
        }
        return myself;
    }

    protected ASSERTION falsenessAssert(Function<OBJECT, String> failMessageProvider, Function<OBJECT, Boolean> valueGetter) {
        isNotNull();

        if (valueGetter.apply(this.actual)) {
            failWithMessage(failMessageProvider.apply(actual));
        }
        return myself;
    }

    public static LanguageElementAssertion assertThat(LanguageElement actual) {
        return new LanguageElementAssertion(actual);
    }

    public static <T> ParseResultAssertion<T> assertThat(ParseResult<T> actual) {
        return new ParseResultAssertion<>(actual);
    }

    public static <T> PartialParseResultAssertion<T> assertThat(PartialParseResult<T> actual) {
        return new PartialParseResultAssertion<>(actual);
    }

    public static NodeAssertion assertThat(Node actual) {
        return new NodeAssertion(actual);
    }

    public static TokenQueueAssertion assertThat(TokenQueue actual) {
        return new TokenQueueAssertion(actual);
    }

    public static LexerRowAssertion assertThat(LexerRow actual) {
        return new LexerRowAssertion(actual);
    }
}
