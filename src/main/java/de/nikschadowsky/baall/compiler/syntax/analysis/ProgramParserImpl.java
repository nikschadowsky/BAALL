package de.nikschadowsky.baall.compiler.syntax.analysis;


import de.nikschadowsky.baall.compiler.symbol.Token;
import de.nikschadowsky.baall.compiler.symbol.TokenQueue;
import de.nikschadowsky.baall.compiler.syntax.analysis.result.PartialParseResult;
import de.nikschadowsky.baall.compiler.syntax.error.SyntaxDiagnostic;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.ASTNodeFactory;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.program.*;
import de.nikschadowsky.baall.compiler.syntax.tree.util.NodeDiagnosticCollector;
import de.nikschadowsky.baall.compiler.syntax.util.PartialParse;

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

    private final ASTNodeFactory astFactory = new ASTNodeFactory(new NodeDiagnosticCollector());

    public ProgramParserImpl() {
        this.statementParser = new StatementParserImpl(this, astFactory);
        this.auxiliaryParser = new AuxiliaryParserImpl(this, astFactory);
        this.expressionParser = new ExpressionParserImpl(this, astFactory);
        this.literalParser = new LiteralParserImpl(this, astFactory);
        this.controlStatementParser = new ControlStatementParserImpl(this, astFactory);
        this.controlStructureParser = new ControlStructureParserImpl(this, astFactory);
    }

    @PartialParse
    @Override
    public PartialParseResult<ProgramNode> parseProgram(TokenQueue queue) {
        ProgramNodeImpl node = astFactory.createProgramNode();
        List<SyntaxDiagnostic> diagnostics = new ArrayList<>();
        boolean isPartial = false;

        PartialParseResult<List<Token>> parsedImports = statementParser.parseImports(queue.branchOff());
        if (parsedImports.isSuccessful() || parsedImports.isPartial()) {
            queue.mergeBranch(parsedImports.getTokenQueueId());
            ImportsNodeImpl imports = astFactory.createImportNode();
            imports.setImports(parsedImports.getParseResult());
            node.setImports(imports);
        }
        if (!parsedImports.isSuccessful()) {
            diagnostics.add(parsedImports.getDiagnostic());
            isPartial = true;
        }


        PartialParseResult<StatementsNode> parsedStatements = statementParser.parseStatements(queue.branchOff());
        if (parsedStatements.isSuccessful() || parsedStatements.isPartial()) {
            queue.mergeBranch(parsedStatements.getTokenQueueId());
            node.setStatements(parsedStatements.getParseResult());
        }
        if (!parsedStatements.isSuccessful()) {
            diagnostics.add(parsedStatements.getDiagnostic());
            isPartial = true;
        }

        PartialParseResult<ExportsNode> parsedExports = statementParser.parseExports(queue.branchOff());
        if (parsedExports.isSuccessful() || parsedExports.isPartial()) {
            queue.mergeBranch(parsedExports.getTokenQueueId());
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
            return PartialParseResult.partialParse(node, diagnostics.get(0), queue.getId());
        }

        return PartialParseResult.successfulParse(node, queue.getId());
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
