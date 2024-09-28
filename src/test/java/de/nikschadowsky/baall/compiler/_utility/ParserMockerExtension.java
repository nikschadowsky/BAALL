package de.nikschadowsky.baall.compiler._utility;


import de.nikschadowsky.baall.compiler.symbol.Token;
import de.nikschadowsky.baall.compiler.symbol.TokenQueue;
import de.nikschadowsky.baall.compiler.syntax.analysis.ExpressionParser;
import de.nikschadowsky.baall.compiler.syntax.analysis.ProgramParser;
import de.nikschadowsky.baall.compiler.syntax.analysis.StatementParser;
import de.nikschadowsky.baall.compiler.syntax.analysis.result.ParseResult;
import de.nikschadowsky.baall.compiler.syntax.analysis.result.PartialParseResult;
import de.nikschadowsky.baall.compiler.syntax.error.SyntaxDiagnostic;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import static org.mockito.Mockito.when;

/**
 * @since 24.08.2024
 */
public class ParserMockerExtension implements AfterEachCallback {

    private final Map<Class<?>, Object> mockedParsers = new HashMap<>();

    private static <T> Answer<ParseResult<T>> getAnswer(T answer, String[] returnUnsuccessfulOn, boolean returnPartial) {
        return invocationOnMock -> {
            TokenQueue queue = invocationOnMock.<TokenQueue>getArgument(0);
            Token polled = queue.poll();
            if (Objects.isNull(polled) || Arrays.asList(returnUnsuccessfulOn).contains(polled.value())) {
                SyntaxDiagnostic diagnostic = new SyntaxDiagnostic(
                        "Parser mock consumed an illegal token '" + polled + "'");
                if (returnPartial) {
                    return PartialParseResult.unsuccessfulParse(diagnostic,queue.getId());
                }
                return ParseResult.unsuccessfulParse(diagnostic,queue.getId());
            }
            if (returnPartial) {
                return PartialParseResult.successfulParse(answer,queue.getId());
            }
            return ParseResult.successfulParse(answer, queue.getId());
        };
    }

    public <T> void mockExpressionParserExecution(
            ProgramParser programParserMock,
            Function<ExpressionParser, ParseResult<T>> mockedMethod,
            T expectedResult,
            String... returnUnsuccessfulOn) {
        ExpressionParser parserMock = getParser(ExpressionParser.class);

        when(programParserMock.getExpressionParser()).thenReturn(parserMock);
        when(mockedMethod.apply(parserMock)).thenAnswer(getAnswer(
                expectedResult,
                returnUnsuccessfulOn,
                false
        ));
    }

    public <T> void mockStatementParserExecution(
            ProgramParser programParserMock,
            Function<StatementParser, ParseResult<T>> mockedMethod,
            T expectedResult,
            String... returnUnsuccessfulOn
    ) {
        StatementParser parserMock = getParser(StatementParser.class);

        when(programParserMock.getStatementParser()).thenReturn(parserMock);
        when(mockedMethod.apply(parserMock)).thenAnswer(getAnswer(expectedResult, returnUnsuccessfulOn, true));
    }

    private <T> T getParser(Class<T> clazz) {
        if (mockedParsers.containsKey(clazz)) {
            return clazz.cast(mockedParsers.get(clazz));
        }

        T mockObject = Mockito.mock(clazz);
        mockedParsers.put(clazz, mockObject);
        return mockObject;
    }

    @Override
    public void afterEach(ExtensionContext context) {
        mockedParsers.clear();
    }
}
