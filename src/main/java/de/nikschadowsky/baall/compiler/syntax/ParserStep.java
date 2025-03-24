package de.nikschadowsky.baall.compiler.syntax;


import de.nikschadowsky.baall.compiler.CompileInformation;
import de.nikschadowsky.baall.compiler.Step;
import de.nikschadowsky.baall.compiler.StepOptions;
import de.nikschadowsky.baall.compiler.output.error.CompileException;
import de.nikschadowsky.baall.compiler.symbol.TokenQueue;
import de.nikschadowsky.baall.compiler.syntax.analysis.ProgramParser;
import de.nikschadowsky.baall.compiler.syntax.analysis.ProgramParserImpl;
import de.nikschadowsky.baall.compiler.syntax.analysis.result.PartialParseResult;
import de.nikschadowsky.baall.compiler.syntax.error.SyntaxDiagnostic;
import de.nikschadowsky.baall.compiler.syntax.error.SyntaxException;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.program.ProgramNode;
import org.jetbrains.annotations.Nullable;

/**
 * @since 14.01.2025
 */
public class ParserStep extends Step<TokenQueue, ProgramNode, SyntaxDiagnostic> {

    public ParserStep(@Nullable StepOptions options) {
        super(options);
    }

    @Override
    public ProgramNode executeStep(TokenQueue tokenQueue, CompileInformation compileInformation) throws CompileException {
        ProgramParser parser = new ProgramParserImpl();

        PartialParseResult<ProgramNode> parseResult = parser.parseProgram(tokenQueue);

        if(parseResult.isSuccessful()) {
            return parseResult.getParseResult();
        }
        throw new SyntaxException(parseResult.getDiagnostic());
    }
}
