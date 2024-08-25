package de.nikschadowsky.baall.compiler.syntax.analysis;


import de.nikschadowsky.baall.compiler.symbol.Token;
import de.nikschadowsky.baall.compiler.symbol.TokenQueue;
import de.nikschadowsky.baall.compiler.syntax.analysis.result.PartialParseResult;
import de.nikschadowsky.baall.compiler.syntax.error.SyntaxDiagnostic;
import de.nikschadowsky.baall.compiler.syntax.util.PartialParse;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.ASTNodeFactory;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.program.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @since 25.08.2024
 */
public class ProgramParserImpl implements ProgramParser {

    private final AuxiliaryParser auxiliaryParser;
    private final StatementParser statementParser;
    private final ExpressionParser expressionParser;
    private final ControlStructureParser controlStructureParser;
    private final ControlStatementParser controlStatementParser;
    private final LiteralParser literalParser;

    public ProgramParserImpl() {
        this.statementParser = new StatementParserImpl(this);
        this.auxiliaryParser = new AuxiliaryParserImpl(this);
        this.expressionParser = new ExpressionParserImpl(this);
        this.literalParser = new LiteralParserImpl(this);
        this.controlStatementParser = new ControlStatementParserImpl(this);
        this.controlStructureParser = new ControlStructureParserImpl(this);
    }

    @PartialParse
    @Override
    public PartialParseResult<ProgramNode> parseProgram(TokenQueue queue, ASTNodeFactory astFactory) {
        ProgramNodeImpl node = astFactory.createProgramNode();
        List<SyntaxDiagnostic> diagnostics = new ArrayList<>();
        boolean isPartial = false;

        PartialParseResult<List<Token>> parsedImports = statementParser.parseImports(queue.branchOff(), astFactory);
        if (parsedImports.isSuccessful() || parsedImports.isPartial()) {
            queue.mergeBranch();
            ImportsNodeImpl imports = astFactory.createImportNode();
            imports.setImports(parsedImports.getParseResult());
            node.setImports(imports);
        }
        if (!parsedImports.isSuccessful()) {
            diagnostics.add(parsedImports.getDiagnostic());
            isPartial = true;
        }


        PartialParseResult<StatementsNode> parsedStatements = statementParser.parseStatements(queue.branchOff(), astFactory);
        if (parsedStatements.isSuccessful() || parsedStatements.isPartial()) {
            queue.mergeBranch();
            node.setStatements(parsedStatements.getParseResult());
        }
        if (!parsedStatements.isSuccessful()) {
            diagnostics.add(parsedStatements.getDiagnostic());
            isPartial = true;
        }

        PartialParseResult<ExportsNode> parsedExports = statementParser.parseExports(queue.branchOff(), astFactory);
        if (parsedExports.isSuccessful() || parsedExports.isPartial()) {
            queue.mergeBranch();
            node.setExports(parsedExports.getParseResult());
        }
        if (!parsedExports.isSuccessful()) {
            diagnostics.add(parsedExports.getDiagnostic());
            isPartial = true;
        }

        if (!queue.hasReachedEndOfFile()) {
            diagnostics.add(new SyntaxDiagnostic(queue.poll(), "Illegal token"));
            isPartial = true;
        }

        node.setDiagnostics(diagnostics);

        if (isPartial) {
            return PartialParseResult.partialParse(node, diagnostics.get(0));
        }

        return PartialParseResult.successfulParse(node);
    }

    @Override
    public AuxiliaryParser getAuxiliaryParser() {
        return auxiliaryParser;
    }

    @Override
    public StatementParser getStatementParser() {
        return statementParser;
    }

    @Override
    public ExpressionParser getExpressionParser() {
        return expressionParser;
    }

    @Override
    public ControlStructureParser getControlStructureParser() {
        return controlStructureParser;
    }

    @Override
    public ControlStatementParser getControlStatementParser() {
        return controlStatementParser;
    }

    @Override
    public LiteralParser getLiteralParser() {
        return literalParser;
    }
}
