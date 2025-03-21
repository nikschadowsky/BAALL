package de.nikschadowsky.baall.compiler._utility;


import de.nikschadowsky.baall.compiler._utility.ast.NodeAssertionFactory;
import de.nikschadowsky.baall.compiler.syntax.analysis.result.ParseResult;

/**
 * @since 15.08.2024
 */
public class ParseResultAssertion<T> extends BaseAssertion<ParseResultAssertion<T>, ParseResult<T>> {

    protected ParseResultAssertion(ParseResult<T> actual) {
        super(actual, ParseResultAssertion.class);
    }

    public SuccessfulParseResultAssertion<T> isSuccessful() {
        truthinessAssert(
                pr -> "ParseResult is not successful!\n syntax diagnostic: " + pr.getDiagnostic()
                                                                                 .getMessage(),
                ParseResult::isSuccessful
        );
        return new SuccessfulParseResultAssertion<>(actual);
    }

    public UnsuccessfulParseResultAssertion<T> isUnsuccessful() {
        truthinessAssert(pr -> "ParseResult is successful!", ParseResult::isUnsuccessful);
        return new UnsuccessfulParseResultAssertion<>(actual);
    }

    public static class SuccessfulParseResultAssertion<ACT> extends BaseAssertion<SuccessfulParseResultAssertion<ACT>, ParseResult<ACT>> {
        public SuccessfulParseResultAssertion(ParseResult<ACT> actual) {
            super(actual, SuccessfulParseResultAssertion.class);
        }

        public <ASS extends BaseAssertion<ASS, ? super ACT>> ASS map(NodeAssertionFactory<ASS, ACT> factory) {
            return factory.map(actual.getParseResult());
        }
    }

    public static class UnsuccessfulParseResultAssertion<T> extends BaseAssertion<UnsuccessfulParseResultAssertion<T>, ParseResult<T>> {
        public UnsuccessfulParseResultAssertion(ParseResult<T> actual) {
            super(actual, UnsuccessfulParseResultAssertion.class);
        }

        public UnsuccessfulParseResultAssertion<T> syntaxDiagnosticContains(String expected) {
            return truthinessAssert(
                    pr -> "Syntax diagnostic:\n<%s>\n does not contain:\n<%s>".formatted(
                            pr.getDiagnostic().getMessage(),
                            expected
                    ), pr -> pr.getDiagnostic().getMessage().contains(expected)
            );
        }

        public UnsuccessfulParseResultAssertion<T> syntaxDiagnosticEquals(String expected) {
            return truthinessAssert(
                    pr -> "Syntax diagnostic:\n<%s>\n does not equal:\n<%s>".formatted(
                            pr.getDiagnostic()
                              .getMessage(), expected
                    ),
                    pr -> expected.equals(pr.getDiagnostic().getMessage())
            );
        }

    }
}