package de.nikschadowsky.baall.compiler._utility;


import de.nikschadowsky.baall.compiler.symbol.Token;
import de.nikschadowsky.baall.compiler.symbol.TokenQueue;
import de.nikschadowsky.baall.compiler.syntax.analysis.ExpressionParser;
import de.nikschadowsky.baall.compiler.syntax.analysis.ProgramParser;
import de.nikschadowsky.baall.compiler.syntax.analysis.StatementParser;
import de.nikschadowsky.baall.compiler.syntax.analysis.result.ParseResult;
import de.nikschadowsky.baall.compiler.syntax.analysis.result.PartialParseResult;
import de.nikschadowsky.baall.compiler.syntax.error.SyntaxDiagnostic;
import org.mockito.stubbing.Answer;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @since 24.08.2024
 */
public class ParserMocker {

    private static <T> Answer<ParseResult<T>> getAnswer(T answer, String[] returnUnsuccessfulOn, boolean returnPartial) {
        return invocationOnMock -> {
            Token polled = invocationOnMock.<TokenQueue>getArgument(0).poll();
            if (Objects.isNull(polled) || Arrays.asList(returnUnsuccessfulOn).contains(polled.value())) {
                SyntaxDiagnostic diagnostic = new SyntaxDiagnostic(
                        "Parser mock consumed an illegal token '" + polled + "'");
                if (returnPartial) {
                    return PartialParseResult.unsuccessfulParse(diagnostic);
                }
                return ParseResult.unsuccessfulParse(diagnostic);
            }
            if (returnPartial) {
                return PartialParseResult.successfulParse(answer);
            }
            return ParseResult.successfulParse(answer);
        };
    }

    public static <T> void mockExpressionParserExecution(
            ProgramParser programParserMock,
            Function<ExpressionParser, ParseResult<T>> mockedMethod,
            T expectedResult,
            String... returnUnsuccessfulOn) {
        ExpressionParser expressionParserMock = mock(ExpressionParser.class);

        when(programParserMock.getExpressionParser()).thenReturn(expressionParserMock);
        when(mockedMethod.apply(expressionParserMock)).thenAnswer(getAnswer(
                expectedResult,
                returnUnsuccessfulOn,
                false
        ));
    }

    public static <T> void mockStatementParserExecution(
            ProgramParser programParserMock,
            Function<StatementParser, ParseResult<T>> mockedMethod,
            T expectedResult,
            String... returnUnsuccessfulOn
    ) {
        StatementParser statementParser = mock(StatementParser.class);

        when(programParserMock.getStatementParser()).thenReturn(statementParser);
        when(mockedMethod.apply(statementParser)).thenAnswer(getAnswer(expectedResult, returnUnsuccessfulOn, true));
    }
}
