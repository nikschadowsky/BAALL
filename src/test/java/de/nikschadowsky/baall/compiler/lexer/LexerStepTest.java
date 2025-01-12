package de.nikschadowsky.baall.compiler.lexer;

import de.nikschadowsky.baall.compiler._utility.BaseAssertion;
import de.nikschadowsky.baall.compiler._utility.TestCompileInformation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class LexerStepTest {

    private LexerStep lexerStep;

    private static final String TEST_SOURCE_CODE = """
            TestFile 1234 // this comment is to be removed by the lexerStep
            TestFile 4321 /* this too should be removed */
            
            // Regular Comment
            " // Comment inside String  "
            " String "
            " /* Block Comment inside String */ "
            " /* Block Comment inside String with end-symbol outside "*/
            /* Block Comment one line*/
            /*
            BLOCK COMMENT
            */
            """;

    @BeforeEach
    void setUp() {
        lexerStep = new LexerStep(null);
    }

    @Test
    void testPreprocessing() {
        List<LexerRow> rows = lexerStep.executeStep(TEST_SOURCE_CODE.lines().toList(), new TestCompileInformation());

        assertThat(rows).hasSize(6);
        BaseAssertion.assertThat(rows.get(0)).hasRowIndex(1).hasContent("TestFile 1234");
        BaseAssertion.assertThat(rows.get(1)).hasRowIndex(2).hasContent("TestFile 4321");
        BaseAssertion.assertThat(rows.get(2)).hasRowIndex(5).hasContent("\" // Comment inside String  \"");
        BaseAssertion.assertThat(rows.get(3)).hasRowIndex(6).hasContent("\" String \"");
        BaseAssertion.assertThat(rows.get(4)).hasRowIndex(7).hasContent("\" /* Block Comment inside String */ \"");
        BaseAssertion.assertThat(rows.get(5)).hasRowIndex(8).hasContent("\" /* Block Comment inside String with end-symbol outside \"*/");
    }

}
