package de.nikschadowsky.baall.compiler._utility;


import de.nikschadowsky.baall.compiler.syntax.analysis.result.ParseResult;

import java.util.NoSuchElementException;
import java.util.function.Predicate;

/**
 * @since 15.08.2024
 */
public class ParseResultAssertion<T> extends BaseAssertion<ParseResultAssertion<T>, ParseResult<T>> {

    protected ParseResultAssertion(ParseResult<T> actual) {
        super(actual, ParseResultAssertion.class);
    }

    public ParseResultAssertion<T> isSuccessful() {
        return truthnessAssert(() -> "ParseResult is not successful!", ParseResult::isSuccessful);
    }

    public ParseResultAssertion<T> isUnsuccessful() {
        return truthnessAssert(() -> "ParseResult is successful!", ParseResult::isUnsuccessful);
    }

    public ParseResultAssertion<T> resultMatches(Predicate<T> predicate) {
        return truthnessAssert(() -> "Result does not match predicate!", pr -> {
            try {
                return predicate.test(pr.getParseResult());
            } catch (NoSuchElementException e) {
                failWithMessage("No ParseResult could be extracted since ParseResult is unsuccessful!");
                return false;
            }
        });
    }

    public ParseResultAssertion<T> syntaxDiagnosticContains(String expected) {
        return truthnessAssert(() -> "Syntax diagnostic:\n<%s>\n does not contain:\n<%s>".formatted(
                actual.getDiagnostic()
                      .getMessage(),
                expected
        ), (pr) -> {
            try {
                return pr.getDiagnostic().getMessage().contains(expected);
            } catch (NoSuchElementException e) {
                failWithMessage("No diagnostic could be extracted since ParseResult is successful!");
                return false;
            }
        });
    }
}