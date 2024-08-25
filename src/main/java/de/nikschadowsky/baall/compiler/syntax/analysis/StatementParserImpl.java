package de.nikschadowsky.baall.compiler.syntax.analysis;

import de.nikschadowsky.baall.compiler.symbol.Token;
import de.nikschadowsky.baall.compiler.symbol.TokenQueue;
import de.nikschadowsky.baall.compiler.syntax.analysis.result.ParseResult;
import de.nikschadowsky.baall.compiler.syntax.analysis.result.PartialParseResult;
import de.nikschadowsky.baall.compiler.syntax.error.SyntaxDiagnostic;
import de.nikschadowsky.baall.compiler.syntax.util.PartialParse;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.ASTNodeFactory;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.controlstructures.ControlStructureNode;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.expression.ExpressionNode;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.expression.UnaryExpressionNode;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.program.*;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.statement.StatementNode;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.statement.assignment.*;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.statement.controlstatement.ControlStatementNode;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.typing.TypeNode;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.value.FunctionCallNode;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.value.IdentifierAccessNode;
import de.nikschadowsky.baall.compiler.util.SyntaxSet;

import javax.annotation.processing.Generated;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Generated("by BAALL-StatementParser-Gen")
public class StatementParserImpl implements StatementParser {

    private final ProgramParser programParser;

    public StatementParserImpl(ProgramParser programParser) {
        this.programParser = programParser;
    }

    @PartialParse
    @Override
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
    @Override
    public PartialParseResult<StatementsNode> parseStatements(TokenQueue queue, ASTNodeFactory astFactory) {
        StatementsNodeImpl node = astFactory.createStatementsNode();
        List<StatementNode> statements = new LinkedList<>();
        List<SyntaxDiagnostic> diagnostics = new ArrayList<>();
        boolean isPartial = false;

        PartialParseResult<StatementNode> parsedStatement = parseStatement(queue.branchOff(), astFactory);
        if (parsedStatement.isUnsuccessful()) {
            node.setStatements(new LinkedList<>());
            return PartialParseResult.successfulParse(node);
        }

        if (parsedStatement.isPartial()) {
            diagnostics.add(parsedStatement.getDiagnostic());
            isPartial = true;
        }
        queue.mergeBranch();
        statements.add(parsedStatement.getParseResult());

        PartialParseResult<StatementsNode> parsedStatements = parseStatements(queue.branchOff(), astFactory);
        if (parsedStatements.isUnsuccessful()) {
            diagnostics.add(parsedStatements.getDiagnostic());
            isPartial = true;
        } else {
            if (parsedStatements.isPartial()) {
                diagnostics.add(parsedStatements.getDiagnostic());
                isPartial = true;
            }
            queue.mergeBranch();
            statements.addAll(parsedStatements.getParseResult().getStatements());
        }

        node.setStatements(statements);
        if (isPartial) {
            return PartialParseResult.partialParse(node, diagnostics.get(0));
        }
        return PartialParseResult.successfulParse(node);
    }

    @PartialParse
    @Override
    public PartialParseResult<StatementNode> parseStatement(TokenQueue queue, ASTNodeFactory astFactory) {
        PartialParseResult<? extends StatementNode> parseResult;
        List<SyntaxDiagnostic> diagnostics = new ArrayList<>();
        boolean isPartial = false;

        StatementNode statement;

        // control structures
        PartialParseResult<ControlStructureNode> parsedControlStructure =
                programParser.getControlStructureParser().parseControlStructure(queue.branchOff(), astFactory);
        if (parsedControlStructure.isSuccessful()) {
            queue.mergeBranch();
            return PartialParseResult.successfulParse(parsedControlStructure.getParseResult());
        } else if (parsedControlStructure.isPartial()) {
            queue.mergeBranch();
            return PartialParseResult.partialParse(
                    parsedControlStructure.getParseResult(),
                    parsedControlStructure.getDiagnostic()
            );
        }

        PartialParseResult<StatementNode> parsedRawStatement = parseRawStatement(queue.branchOff(), astFactory);
        if (parsedRawStatement.isSuccessful()) {
            queue.mergeBranch();
            return PartialParseResult.successfulParse(parsedRawStatement.getParseResult());
        } else if (parsedRawStatement.isPartial()) {
            queue.mergeBranch();
            diagnostics.add(parsedRawStatement.getDiagnostic());
            isPartial = true;
        }

        if (!SyntaxSet.LANGUAGE_ELEMENTS.get(";").matches(queue.peek())) {
            diagnostics.add(new SyntaxDiagnostic(queue.poll(), "Expected ';'!"));
            isPartial = true;
        }

        if (isPartial) {
            return PartialParseResult.partialParse(parsedRawStatement.getParseResult(), diagnostics.get(0));
        }
        return PartialParseResult.unsuccessfulParse(new SyntaxDiagnostic(queue.poll(), "Not a statement!"));
    }

    @PartialParse
    @Override
    public PartialParseResult<StatementNode> parseRawStatement(TokenQueue queue, ASTNodeFactory astFactory) {
        PartialParseResult<DeclarationNode> parsedDeclaration = parseDeclaration(queue.branchOff(), astFactory);

        if (parsedDeclaration.isSuccessful()) {
            queue.mergeBranch();
            return PartialParseResult.successfulParse(parsedDeclaration.getParseResult());
        }

        PartialParseResult<ReassignmentNode> parsedReassignment = parseReassignment(queue.branchOff(), astFactory);
        if (parsedReassignment.isSuccessful()) {
            queue.mergeBranch();
            return PartialParseResult.successfulParse(parsedReassignment.getParseResult());
        }

        PartialParseResult<FunctionCallNode> parsedFunctionCall =
                programParser.getExpressionParser().parseFunctionCall(queue.branchOff(), astFactory);
        if (parsedFunctionCall.isSuccessful()) {
            queue.mergeBranch();
            return PartialParseResult.successfulParse(parsedFunctionCall.getParseResult());
        }

        ParseResult<ControlStatementNode> parsedControlStatement =
                programParser.getControlStatementParser().parseControlStatement(queue.branchOff(), astFactory);
        if (parsedControlStatement.isSuccessful()) {
            queue.mergeBranch();
            return PartialParseResult.successfulParse(parsedControlStatement.getParseResult());
        }

        if (parsedDeclaration.isPartial()) {
            queue.mergeBranch();
            return PartialParseResult.partialParse(
                    parsedDeclaration.getParseResult(),
                    parsedDeclaration.getDiagnostic()
            );
        }
        if (parsedReassignment.isPartial()) {
            queue.mergeBranch();
            return PartialParseResult.partialParse(
                    parsedReassignment.getParseResult(),
                    parsedReassignment.getDiagnostic()
            );
        }
        if (parsedFunctionCall.isPartial()) {
            queue.mergeBranch();
            return PartialParseResult.partialParse(
                    parsedFunctionCall.getParseResult(),
                    parsedFunctionCall.getDiagnostic()
            );
        }
        return PartialParseResult.unsuccessfulParse(new SyntaxDiagnostic(queue.poll(), "Not a statement!"));
    }

    @PartialParse
    @Override
    public PartialParseResult<DeclarationNode> parseDeclaration(TokenQueue queue, ASTNodeFactory astFactory) {
        PartialParseResult<ConstantDeclarationNode> parsedConstantDeclaration =
                parseConstantDeclaration(queue.branchOff(), astFactory);

        if (parsedConstantDeclaration.isSuccessful()) {
            queue.mergeBranch();
            return PartialParseResult.successfulParse(parsedConstantDeclaration.getParseResult());
        }

        PartialParseResult<VariableDeclarationNode> parsedVariableDeclaration =
                parseVariableDeclaration(queue.branchOff(), astFactory);
        if (parsedVariableDeclaration.isSuccessful()) {
            queue.mergeBranch();
            return PartialParseResult.successfulParse(parsedVariableDeclaration.getParseResult());
        }
        // since const and var declarations are so closely related, checking one for partiality is sufficient and
        // since var declaration allow for more flexibility a partial declaration is always considered being a variable
        if (parsedVariableDeclaration.isPartial()) {
            queue.mergeBranch();
            return PartialParseResult.partialParse(
                    parsedVariableDeclaration.getParseResult(),
                    parsedVariableDeclaration.getDiagnostic()
            );
        }
        return PartialParseResult.unsuccessfulParse(new SyntaxDiagnostic(queue.poll(), "Not a statement!"));
    }

    @PartialParse
    @Override
    public PartialParseResult<VariableDeclarationNode> parseVariableDeclaration(TokenQueue queue, ASTNodeFactory astFactory) {
        VariableDeclarationNodeImpl node = astFactory.createVariableDeclarationNode();
        List<SyntaxDiagnostic> diagnostics = new ArrayList<>();
        boolean isPartial = false;

        ParseResult<TypeNode> parsedType = programParser.getAuxiliaryParser().parseType(queue.branchOff(), astFactory);
        if (parsedType.isUnsuccessful()) {
            return PartialParseResult.unsuccessfulParse(new SyntaxDiagnostic(queue.poll(), "Not a statement!"));
        }
        queue.mergeBranch();
        node.setType(parsedType.getParseResult());

        if (!SyntaxSet.LANGUAGE_ELEMENTS.get(":").matches(queue.peek())) {
            diagnostics.add(new SyntaxDiagnostic(queue.poll(), "Expected ':'!"));
            isPartial = true;
        }
        queue.poll();

        ParseResult<Token> parsedIdentifier =
                programParser.getAuxiliaryParser().parseIdentifier(queue.branchOff(), astFactory);
        if (parsedIdentifier.isUnsuccessful()) {
            diagnostics.add(parsedIdentifier.getDiagnostic());
            isPartial = true;
        }
        queue.mergeBranch();
        node.setIdentifier(parsedIdentifier.getParseResult());

        if (SyntaxSet.LANGUAGE_ELEMENTS.get("=").matches(queue.peek())) {
            queue.poll();

            ParseResult<ExpressionNode> parsedExpression =
                    programParser.getExpressionParser().parseExpression(queue.branchOff(), astFactory);
            if (parsedExpression.isUnsuccessful()) {
                diagnostics.add(parsedExpression.getDiagnostic());
                isPartial = true;
            }
            queue.mergeBranch();
            node.setInitializationValue(parsedExpression.getParseResult());
        }

        if (isPartial) {
            return PartialParseResult.partialParse(node, diagnostics.get(0));
        }
        return PartialParseResult.successfulParse(node);
    }

    @PartialParse
    @Override
    public PartialParseResult<ConstantDeclarationNode> parseConstantDeclaration(TokenQueue queue, ASTNodeFactory astFactory) {
        ConstantDeclarationNodeImpl node = astFactory.createConstantDeclarationNode();
        List<SyntaxDiagnostic> diagnostics = new ArrayList<>();
        boolean isPartial = false;

        ParseResult<TypeNode> parsedType = programParser.getAuxiliaryParser().parseType(queue.branchOff(), astFactory);
        if (parsedType.isUnsuccessful()) {
            return PartialParseResult.unsuccessfulParse(new SyntaxDiagnostic(queue.poll(), "Not a statement!"));
        }
        queue.mergeBranch();
        node.setType(parsedType.getParseResult());

        if (!SyntaxSet.LANGUAGE_ELEMENTS.get(":").matches(queue.peek())) {
            diagnostics.add(new SyntaxDiagnostic(queue.poll(), "Expected ':'!"));
            isPartial = true;
        }
        queue.poll();

        ParseResult<Token> parsedIdentifier =
                programParser.getAuxiliaryParser().parseIdentifier(queue.branchOff(), astFactory);
        if (parsedIdentifier.isUnsuccessful()) {
            diagnostics.add(parsedIdentifier.getDiagnostic());
            isPartial = true;
        }
        queue.mergeBranch();
        node.setIdentifier(parsedIdentifier.getParseResult());

        if (!SyntaxSet.LANGUAGE_ELEMENTS.get(":=").matches(queue.peek())) {
            diagnostics.add(new SyntaxDiagnostic(queue.poll(), "Expected ':='!"));
            isPartial = true;
        } else {
            queue.poll();

            ParseResult<ExpressionNode> parsedExpression =
                    programParser.getExpressionParser().parseExpression(queue.branchOff(), astFactory);
            if (parsedExpression.isUnsuccessful()) {
                diagnostics.add(parsedExpression.getDiagnostic());
                isPartial = true;
            }
            queue.mergeBranch();
            node.setInitializationValue(parsedExpression.getParseResult());
        }

        if (isPartial) {
            return PartialParseResult.partialParse(node, diagnostics.get(0));
        }
        return PartialParseResult.successfulParse(node);
    }

    @PartialParse
    @Override
    public PartialParseResult<ReassignmentNode> parseReassignment(TokenQueue queue, ASTNodeFactory astFactory) {
        ParseResult<UnaryExpressionNode> parsedUnaryExpression =
                programParser.getExpressionParser().parseUnaryExpression(queue.branchOff(), astFactory);
        if (parsedUnaryExpression.isSuccessful()) {
            queue.mergeBranch();
            return PartialParseResult.successfulParse(parsedUnaryExpression.getParseResult());
        }

        PartialParseResult<VariableReassignmentNode> parsedVariableReassignment =
                parseVariableReassignment(queue.branchOff(), astFactory);
        if (parsedVariableReassignment.isSuccessful()) {
            queue.mergeBranch();
            return PartialParseResult.successfulParse(parsedVariableReassignment.getParseResult());
        } else if (parsedVariableReassignment.isPartial()) {
            queue.mergeBranch();
            return PartialParseResult.partialParse(
                    parsedVariableReassignment.getParseResult(),
                    parsedVariableReassignment.getDiagnostic()
            );
        }

        return PartialParseResult.unsuccessfulParse(new SyntaxDiagnostic(queue.poll(), "Not a satatement!"));
    }

    @PartialParse
    @Override
    public PartialParseResult<VariableReassignmentNode> parseVariableReassignment(TokenQueue queue, ASTNodeFactory astFactory) {
        VariableReassignmentNodeImpl node = astFactory.createVariableReassignmentNode();
        List<SyntaxDiagnostic> diagnostics = new ArrayList<>();
        boolean isPartial = false;

        ParseResult<IdentifierAccessNode> parsedIdentifier =
                programParser.getAuxiliaryParser().parseIdentifierAccess(queue.branchOff(), astFactory);
        if (parsedIdentifier.isUnsuccessful()) {
            return PartialParseResult.unsuccessfulParse(new SyntaxDiagnostic(queue.poll(), "Not a statement!"));
        }
        queue.mergeBranch();
        node.setIdentifier(parsedIdentifier.getParseResult());

        ParseResult<Token> parsedShorthandOperator =
                programParser.getAuxiliaryParser().parseShorthandOperator(queue.branchOff(), astFactory);
        if (parsedShorthandOperator.isSuccessful()) {
            queue.mergeBranch();
            node.setOperator(parsedShorthandOperator.getParseResult());
        } else {
            diagnostics.add(parsedShorthandOperator.getDiagnostic());
            isPartial = true;
        }

        ParseResult<ExpressionNode> parsedExpression =
                programParser.getExpressionParser().parseExpression(queue.branchOff(), astFactory);
        if (parsedExpression.isSuccessful()) {
            queue.mergeBranch();
            node.setValueExpression(parsedExpression.getParseResult());
        } else {
            diagnostics.add(parsedExpression.getDiagnostic());
            isPartial = true;
        }

        if (isPartial) {
            return PartialParseResult.partialParse(node, diagnostics.get(0));
        }
        return PartialParseResult.successfulParse(node);
    }

    @PartialParse
    @Override
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
                        programParser.getAuxiliaryParser().parseIdentifierAccess(queue.branchOff(), astFactory);
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
                        programParser.getAuxiliaryParser()
                                     .parseAdditionalIdentifierAccesses(queue.branchOff(), astFactory);

                if (parsedElements.isUnsuccessful()) {
                    diagnostics.add(parsedElements.getDiagnostic());
                    isPartial = true;
                } else {
                    if (parsedElements.isPartial()) {
                        diagnostics.add(parsedElements.getDiagnostic());
                        isPartial = true;
                    }
                    queue.mergeBranch();
                    elements.addAll(parsedElements.getParseResult());
                    node.setExports(elements);
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
}
