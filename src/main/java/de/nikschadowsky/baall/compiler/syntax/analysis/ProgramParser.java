package de.nikschadowsky.baall.compiler.syntax.analysis;


import de.nikschadowsky.baall.compiler.symbol.Token;
import de.nikschadowsky.baall.compiler.symbol.TokenQueue;
import de.nikschadowsky.baall.compiler.syntax.analysis.result.ParseResult;
import de.nikschadowsky.baall.compiler.syntax.analysis.result.PartialParseResult;
import de.nikschadowsky.baall.compiler.syntax.error.SyntaxDiagnostic;
import de.nikschadowsky.baall.compiler.syntax.util.PartialParse;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.ASTNodeFactory;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.program.*;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.value.IdentifierAccessNode;
import de.nikschadowsky.baall.compiler.util.SyntaxSet;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @since 25.08.2024
 */
public class ProgramParser {

    private final AuxiliaryParser auxiliaryParser;
    private final StatementParser statementParser;
    private final ExpressionParser expressionParser;
    private final ControlStructureParser controlStructureParser;
    private final ControlStatementParser controlStatementParser;
    private final LiteralParser literalParser;

    public ProgramParser() {
        this.statementParser = new StatementParser(this);
        this.auxiliaryParser = new AuxiliaryParser(this);
        this.expressionParser = new ExpressionParser(this);
        this.literalParser = new LiteralParser(this);
        this.controlStatementParser = new ControlStatementParser(this);
        this.controlStructureParser = new ControlStructureParser(this);
    }

    @PartialParse
    public PartialParseResult<ProgramNode> parseProgram(TokenQueue queue, ASTNodeFactory astFactory) {
        ProgramNodeImpl node = astFactory.createProgramNode();
        List<SyntaxDiagnostic> diagnostics = new ArrayList<>();
        boolean isPartial = false;

        PartialParseResult<List<Token>> parsedImports = parseImports(queue.branchOff(), astFactory);
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

        PartialParseResult<ExportsNode> parsedExports = parseExports(queue.branchOff(), astFactory);
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


    @PartialParse
    public PartialParseResult<List<Token>> parseImports(TokenQueue queue, ASTNodeFactory astFactory) {
        ImportsNodeImpl node = astFactory.createImportNode();
        List<Token> imports = new LinkedList<>();
        List<SyntaxDiagnostic> diagnostics = new ArrayList<>();
        boolean isPartial = false;

        if (!SyntaxSet.LANGUAGE_ELEMENTS.get("use").matches(queue.peek())) {
            return PartialParseResult.successfulParse(new LinkedList<>());
        }
        queue.poll();

        if (!SyntaxSet.LANGUAGE_ELEMENTS.get("_STRING").matches(queue.peek())) {
            diagnostics.add(new SyntaxDiagnostic(queue.poll(), "Expected a string!"));
            isPartial = true;
        }
        imports.add(queue.poll());

        if (!SyntaxSet.LANGUAGE_ELEMENTS.get(";").matches(queue.peek())) {
            diagnostics.add(new SyntaxDiagnostic(queue.poll(), "Expected ';'!"));
            isPartial = true;
            queue.skipOver(SyntaxSet.LANGUAGE_ELEMENTS.get(";"));
        }
        queue.poll();

        PartialParseResult<List<Token>> parsedImports = parseImports(queue.branchOff(), astFactory);
        if (parsedImports.isUnsuccessful()) {
            diagnostics.add(parsedImports.getDiagnostic());
            isPartial = true;
        } else {
            if (parsedImports.isPartial()) {
                diagnostics.add(parsedImports.getDiagnostic());
                isPartial = true;
            }
            queue.mergeBranch();
            imports.addAll(parsedImports.getParseResult());
        }
        node.setImports(imports);

        if (isPartial) {
            return PartialParseResult.partialParse(imports, diagnostics.get(0));
        }
        return PartialParseResult.successfulParse(imports);
    }

    @PartialParse
    public PartialParseResult<ExportsNode> parseExports(TokenQueue queue, ASTNodeFactory astFactory) {
        ExportsNodeImpl node = astFactory.createExportNode();
        node.setExports(new LinkedList<>());
        List<SyntaxDiagnostic> diagnostics = new ArrayList<>();
        boolean isPartial = false;

        if (!SyntaxSet.LANGUAGE_ELEMENTS.get("export").matches(queue.poll())) {
            return PartialParseResult.successfulParse(node);
        }

        if (SyntaxSet.LANGUAGE_ELEMENTS.get("{").matches(queue.peek())) {
            queue.poll();

            if (SyntaxSet.LANGUAGE_ELEMENTS.get("}").matches(queue.peek())) {
                return PartialParseResult.successfulParse(node);
            } else {
                ParseResult<IdentifierAccessNode> parsedElement =
                        auxiliaryParser.parseIdentifierAccess(queue.branchOff(), astFactory);
                List<IdentifierAccessNode> elements = new LinkedList<>();

                if (parsedElement.isSuccessful()) {
                    queue.mergeBranch();
                    elements.add(parsedElement.getParseResult());
                    node.setExports(elements);
                } else {
                    diagnostics.add(parsedElement.getDiagnostic());
                    isPartial = true;
                }

                PartialParseResult<List<IdentifierAccessNode>> parsedElements =
                        auxiliaryParser.parseAdditionalIdentifierAccesses(queue.branchOff(), astFactory);
                if (parsedElements.isSuccessful()) {
                    queue.mergeBranch();
                    elements.addAll(parsedElements.getParseResult());
                    node.setExports(elements);
                }
                if (parsedElements.isPartial()) {
                    elements.addAll(parsedElements.getParseResult());
                    node.setExports(elements);
                    diagnostics.add(parsedElements.getDiagnostic());
                    isPartial = true;
                } else {
                    diagnostics.add(parsedElements.getDiagnostic());
                    isPartial = true;
                }

                if (!SyntaxSet.LANGUAGE_ELEMENTS.get("}").matches(queue.peek())) {
                    diagnostics.add(new SyntaxDiagnostic(queue.poll(), "Expected '}'!"));
                    isPartial = true;
                }
                queue.poll();

                if (isPartial) {
                    return PartialParseResult.partialParse(node, diagnostics.get(0));
                }
                return PartialParseResult.successfulParse(node);
            }
        }
        return PartialParseResult.successfulParse(node);
    }

    public AuxiliaryParser getAuxiliaryParser() {
        return auxiliaryParser;
    }

    public StatementParser getProgramParser() {
        return statementParser;
    }

    public ExpressionParser getExpressionParser() {
        return expressionParser;
    }

    public ControlStructureParser getControlStructureParser() {
        return controlStructureParser;
    }

    public ControlStatementParser getControlStatementParser() {
        return controlStatementParser;
    }

    public LiteralParser getLiteralParser() {
        return literalParser;
    }
}
