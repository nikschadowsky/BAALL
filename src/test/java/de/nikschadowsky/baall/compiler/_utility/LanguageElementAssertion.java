package de.nikschadowsky.baall.compiler._utility;


import de.nikschadowsky.baall.compiler.symbol.Token;
import de.nikschadowsky.baall.compiler.util.LanguageElement;

/**
 * @since 15.08.2024
 */
public class LanguageElementAssertion extends BaseAssertion<LanguageElementAssertion, LanguageElement> {

    protected LanguageElementAssertion(LanguageElement actual) {
        super(actual, LanguageElementAssertion.class);
    }

    public LanguageElementAssertion matches(Token expected) {
        return truthinessAssert(
                le -> "\n<%s>\n does not match\n<%s>".formatted(actual, expected),
                le -> le.matches(expected)
        );
    }

    public LanguageElementAssertion doesNotMatch(Token expected) {
        return falsenessAssert(
                le -> "\n<%s>\n does match\n<%s>".formatted(actual, expected),
                le -> le.matches(expected)
        );
    }
}
