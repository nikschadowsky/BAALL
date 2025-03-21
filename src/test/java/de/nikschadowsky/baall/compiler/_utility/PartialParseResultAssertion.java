package de.nikschadowsky.baall.compiler._utility;


import de.nikschadowsky.baall.compiler._utility.ast.NodeAssertionFactory;
import de.nikschadowsky.baall.compiler.syntax.analysis.result.ParseResult;
import de.nikschadowsky.baall.compiler.syntax.analysis.result.PartialParseResult;

/**
 * @since 15.08.2024
 */
public class PartialParseResultAssertion<T> extends BaseAssertion<PartialParseResultAssertion<T>, PartialParseResult<T>> {

    protected PartialParseResultAssertion(PartialParseResult<T> actual) {
        super(actual, PartialParseResultAssertion.class);
    }

    public SuccessfulParseResultAssertion<T> isSuccessful() {
        truthinessAssert(
                pr -> "ParseResult is " + (pr.isPartial() ? "partial" : "unsuccessful") + "!\n syntax diagnostic: " + pr.getDiagnostic()
                                                                                                                        .getMessage(),
                PartialParseResult::isSuccessful
        );
        return new SuccessfulParseResultAssertion<>(actual);
    }

    public UnsuccessfulParseResultAssertion<T> isUnsuccessful() {
        truthinessAssert(
                pr -> pr.isSuccessful() ? "ParseResult is successful" : "ParseResult is partial!\n syntax diagnostic: " + pr.getDiagnostic()
                                                                                                                            .getMessage(),
                PartialParseResult::isUnsuccessful
        );
        return new UnsuccessfulParseResultAssertion<>(actual);
    }

    public PartiallyParsedParseResultAssertion<T> isPartiallyParsed() {
        truthinessAssert(
                pr -> pr.isSuccessful() ?
                        "ParseResult is successful!" :
                        "ParseResult  is unsuccessful!\n syntax diagnostic: " + pr.getDiagnostic().getMessage(),
                PartialParseResult::isPartial
        );
        return new PartiallyParsedParseResultAssertion<>(actual);
    }

    public static class SuccessfulParseResultAssertion<ACT> extends BaseAssertion<SuccessfulParseResultAssertion<ACT>, PartialParseResult<ACT>> {
        public SuccessfulParseResultAssertion(PartialParseResult<ACT> actual) {
            super(actual, SuccessfulParseResultAssertion.class);
        }

        public <ASS extends BaseAssertion<ASS, ? super ACT>> ASS map(NodeAssertionFactory<ASS, ACT> factory) {
            return factory.map(actual.getParseResult());
        }
    }

    public static class UnsuccessfulParseResultAssertion<T> extends BaseAssertion<UnsuccessfulParseResultAssertion<T>, ParseResult<T>> {
        public UnsuccessfulParseResultAssertion(PartialParseResult<T> actual) {
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

    public static class PartiallyParsedParseResultAssertion<ACT> extends BaseAssertion<PartiallyParsedParseResultAssertion<ACT>, PartialParseResult<ACT>> {
        public PartiallyParsedParseResultAssertion(PartialParseResult<ACT> actual) {
            super(actual, PartiallyParsedParseResultAssertion.class);
        }

        public PartiallyParsedParseResultAssertion<ACT> syntaxDiagnosticContains(String expected) {
            return truthinessAssert(
                    pr -> "Syntax diagnostic:\n<%s>\n does not contain:\n<%s>".formatted(
                            pr.getDiagnostic().getMessage(),
                            expected
                    ), pr -> pr.getDiagnostic().getMessage().contains(expected)
            );
        }

        public PartiallyParsedParseResultAssertion<ACT> syntaxDiagnosticEquals(String expected) {
            return truthinessAssert(
                    pr -> "Syntax diagnostic:\n<%s>\n does not equal:\n<%s>".formatted(
                            pr.getDiagnostic()
                              .getMessage(), expected
                    ),
                    pr -> expected.equals(pr.getDiagnostic().getMessage())
            );
        }

        public <ASS extends BaseAssertion<ASS, ? super ACT>> ASS map(NodeAssertionFactory<ASS, ACT> factory) {
            return factory.map(actual.getParseResult());
        }
    }
}