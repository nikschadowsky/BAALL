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

@Generated("by BAALL-Parser-Gen")
public class StatementParserImpl implements StatementParser {

    private final ProgramParser programParser;
    private final ASTNodeFactory astFactory;

    public StatementParserImpl(ProgramParser programParser, ASTNodeFactory astFactory) {
        this.programParser = programParser;
        this.astFactory = astFactory;
    }

    @PartialParse
    @Override
    public PartialParseResult<List<Token>> parseImports(TokenQueue queue) {
        ImportsNodeImpl node = astFactory.createImportNode();
        List<Token> imports = new LinkedList<>();
        List<SyntaxDiagnostic> diagnostics = new ArrayList<>();
        boolean isPartial = false;

        if (!SyntaxSet.LANGUAGE_ELEMENTS.get("use").matches(queue.peek())) {
            return PartialParseResult.successfulParse(new LinkedList<>(), queue.getId());
        }
        queue.poll();

        if (!SyntaxSet.LANGUAGE_ELEMENTS.get("string_primitive").matches(queue.peek())) {
            diagnostics.add(new SyntaxDiagnostic(queue.poll(), "Expected a string!"));
            isPartial = true;
        } else {
            imports.add(queue.poll());
        }
        if (!SyntaxSet.LANGUAGE_ELEMENTS.get(";").matches(queue.peek())) {
            diagnostics.add(new SyntaxDiagnostic(queue.poll(), "Expected ';'!"));
            isPartial = true;
            queue.skipOver(SyntaxSet.LANGUAGE_ELEMENTS.get(";"));
        }
        queue.poll();

        PartialParseResult<List<Token>> parsedImports = parseImports(queue.branchOff());
        if (parsedImports.isUnsuccessful()) {
            diagnostics.add(parsedImports.getDiagnostic());
            isPartial = true;
        } else {
            if (parsedImports.isPartial()) {
                diagnostics.add(parsedImports.getDiagnostic());
                isPartial = true;
            }
            queue.mergeBranch(parsedImports.getTokenQueueId());
            imports.addAll(parsedImports.getParseResult());
        }
        node.setImports(imports);

        if (isPartial) {
            return PartialParseResult.partialParse(imports, diagnostics.get(0), queue.getId());
        }
        return PartialParseResult.successfulParse(imports, queue.getId());
    }

    @PartialParse
    @Override
    public PartialParseResult<StatementsNode> parseStatements(TokenQueue queue) {
        StatementsNodeImpl node = astFactory.createStatementsNode();
        List<StatementNode> statements = new LinkedList<>();
        List<SyntaxDiagnostic> diagnostics = new ArrayList<>();
        boolean isPartial = false;

        PartialParseResult<StatementNode> parsedStatement = parseDelimitedStatement(queue.branchOff());
        if (parsedStatement.isUnsuccessful()) {
            node.setStatements(new LinkedList<>());
            return PartialParseResult.successfulParse(node, queue.getId());
        }

        if (parsedStatement.isPartial()) {
            diagnostics.add(parsedStatement.getDiagnostic());
            isPartial = true;
        }
        queue.mergeBranch(parsedStatement.getTokenQueueId());
        statements.add(parsedStatement.getParseResult());

        PartialParseResult<StatementsNode> parsedStatements = parseStatements(queue.branchOff());
        if (parsedStatements.isUnsuccessful()) {
            diagnostics.add(parsedStatements.getDiagnostic());
            isPartial = true;
        } else {
            if (parsedStatements.isPartial()) {
                diagnostics.add(parsedStatements.getDiagnostic());
                isPartial = true;
            }
            queue.mergeBranch(parsedStatements.getTokenQueueId());
            statements.addAll(parsedStatements.getParseResult().getStatements());
        }

        node.setStatements(statements);
        if (isPartial) {
            return PartialParseResult.partialParse(node, diagnostics.get(0), queue.getId());
        }
        return PartialParseResult.successfulParse(node, queue.getId());
    }

    @PartialParse
    @Override
    public PartialParseResult<StatementNode> parseDelimitedStatement(TokenQueue queue) {
        StatementNode parseResult;
        List<SyntaxDiagnostic> diagnostics = new ArrayList<>();
        boolean isPartial = false;

        StatementNode statement;

        // control structures
        PartialParseResult<ControlStructureNode> parsedControlStructure =
                programParser.getControlStructureParser().parseControlStructure(queue.branchOff());
        if (parsedControlStructure.isSuccessful()) {
            queue.mergeBranch(parsedControlStructure.getTokenQueueId());
            return PartialParseResult.successfulParse(parsedControlStructure.getParseResult(), queue.getId());
        } else if (parsedControlStructure.isPartial()) {
            queue.mergeBranch(parsedControlStructure.getTokenQueueId());
            return PartialParseResult.partialParse(
                    parsedControlStructure.getParseResult(),
                    parsedControlStructure.getDiagnostic(),
                    queue.getId()
            );
        }

        PartialParseResult<StatementNode> parsedSimpleStatement = parseSimpleStatement(queue.branchOff());
        if (parsedSimpleStatement.isUnsuccessful()) {
            return PartialParseResult.unsuccessfulParse(parsedSimpleStatement.getDiagnostic(), queue.getId());
        }
        if (parsedSimpleStatement.isPartial()) {
            diagnostics.add(parsedSimpleStatement.getDiagnostic());
            isPartial = true;
        }

        queue.mergeBranch(parsedSimpleStatement.getTokenQueueId());
        parseResult = parsedSimpleStatement.getParseResult();

        if (!SyntaxSet.LANGUAGE_ELEMENTS.get(";").matches(queue.peek())) {
            diagnostics.add(new SyntaxDiagnostic(queue.poll(), "Expected ';'!"));
            isPartial = true;
            queue.skipOver(SyntaxSet.LANGUAGE_ELEMENTS.get(";"));
        }
        if (isPartial) {
            return PartialParseResult.partialParse(
                    parseResult,
                    diagnostics.get(0),
                    queue.getId()
            );
        }
        return PartialParseResult.successfulParse(
                parseResult,
                queue.getId()
        );
    }

    @PartialParse
    @Override
    public PartialParseResult<StatementNode> parseSimpleStatement(TokenQueue queue) {
        PartialParseResult<DeclarationNode> parsedDeclaration = parseDeclaration(queue.branchOff());
        if (parsedDeclaration.isSuccessful()) {
            queue.mergeBranch(parsedDeclaration.getTokenQueueId());
            return PartialParseResult.successfulParse(parsedDeclaration.getParseResult(), queue.getId());
        }

        PartialParseResult<ReassignmentNode> parsedReassignment = parseReassignment(queue.branchOff());
        if (parsedReassignment.isSuccessful()) {
            queue.mergeBranch(parsedReassignment.getTokenQueueId());
            return PartialParseResult.successfulParse(parsedReassignment.getParseResult(), queue.getId());
        }

        PartialParseResult<FunctionCallNode> parsedFunctionCall =
                programParser.getExpressionParser().parseFunctionCall(queue.branchOff());
        if (parsedFunctionCall.isSuccessful()) {
            queue.mergeBranch(parsedFunctionCall.getTokenQueueId());
            return PartialParseResult.successfulParse(parsedFunctionCall.getParseResult(), queue.getId());
        }

        ParseResult<ControlStatementNode> parsedControlStatement =
                programParser.getControlStatementParser().parseControlStatement(queue.branchOff());
        if (parsedControlStatement.isSuccessful()) {
            queue.mergeBranch(parsedControlStatement.getTokenQueueId());
            return PartialParseResult.successfulParse(parsedControlStatement.getParseResult(), queue.getId());
        }

        if (parsedDeclaration.isPartial()) {
            queue.mergeBranch(parsedDeclaration.getTokenQueueId());
            return PartialParseResult.partialParse(
                    parsedDeclaration.getParseResult(),
                    parsedDeclaration.getDiagnostic(),
                    queue.getId()
            );
        }
        if (parsedReassignment.isPartial()) {
            queue.mergeBranch(parsedReassignment.getTokenQueueId());
            return PartialParseResult.partialParse(
                    parsedReassignment.getParseResult(),
                    parsedReassignment.getDiagnostic(),
                    queue.getId()
            );
        }
        if (parsedFunctionCall.isPartial()) {
            queue.mergeBranch(parsedFunctionCall.getTokenQueueId());
            return PartialParseResult.partialParse(
                    parsedFunctionCall.getParseResult(),
                    parsedFunctionCall.getDiagnostic(),
                    queue.getId()
            );
        }
        return PartialParseResult.unsuccessfulParse(
                new SyntaxDiagnostic(queue.poll(), "Not a statement!"),
                queue.getId()
        );
    }

    @PartialParse
    @Override
    public PartialParseResult<DeclarationNode> parseDeclaration(TokenQueue queue) {
        PartialParseResult<ConstantDeclarationNode> parsedConstantDeclaration =
                parseConstantDeclaration(queue.branchOff());

        if (parsedConstantDeclaration.isSuccessful()) {
            queue.mergeBranch(parsedConstantDeclaration.getTokenQueueId());
            return PartialParseResult.successfulParse(parsedConstantDeclaration.getParseResult(), queue.getId());
        }

        PartialParseResult<VariableDeclarationNode> parsedVariableDeclaration =
                parseVariableDeclaration(queue.branchOff());
        if (parsedVariableDeclaration.isSuccessful()) {
            queue.mergeBranch(parsedVariableDeclaration.getTokenQueueId());
            return PartialParseResult.successfulParse(parsedVariableDeclaration.getParseResult(), queue.getId());
        }

        // any partial declaration is considered a variable declaration since we cannot differentiate between them.
        if (parsedVariableDeclaration.isPartial()) {
            queue.mergeBranch(parsedVariableDeclaration.getTokenQueueId());
            return PartialParseResult.partialParse(
                    parsedVariableDeclaration.getParseResult(),
                    parsedVariableDeclaration.getDiagnostic(),
                    queue.getId()
            );
        }
        return PartialParseResult.unsuccessfulParse(
                new SyntaxDiagnostic(queue.poll(), "Not a statement!"),
                queue.getId()
        );
    }

    @PartialParse
    @Override
    public PartialParseResult<VariableDeclarationNode> parseVariableDeclaration(TokenQueue queue) {
        VariableDeclarationNodeImpl node = astFactory.createVariableDeclarationNode();
        List<SyntaxDiagnostic> diagnostics = new ArrayList<>();
        boolean isPartial = false;

        ParseResult<TypeNode> parsedType = programParser.getAuxiliaryParser().parseType(queue.branchOff());
        if (parsedType.isUnsuccessful()) {
            return PartialParseResult.unsuccessfulParse(
                    new SyntaxDiagnostic(queue.poll(), "Not a statement!"),
                    queue.getId()
            );
        }
        queue.mergeBranch(parsedType.getTokenQueueId());
        node.setType(parsedType.getParseResult());

        if (!SyntaxSet.LANGUAGE_ELEMENTS.get(":").matches(queue.peek())) {
            diagnostics.add(new SyntaxDiagnostic(queue.poll(), "Expected ':'!"));
            isPartial = true;
        } else {
            queue.poll();
        }

        ParseResult<Token> parsedIdentifier = programParser.getAuxiliaryParser().parseIdentifier(queue.branchOff());
        if (parsedIdentifier.isUnsuccessful()) {
            diagnostics.add(parsedIdentifier.getDiagnostic());
            isPartial = true;
        } else {
            queue.mergeBranch(parsedIdentifier.getTokenQueueId());
            node.setIdentifier(parsedIdentifier.getParseResult());
        }

        if (SyntaxSet.LANGUAGE_ELEMENTS.get("=").matches(queue.peek())) {
            queue.poll();

            ParseResult<ExpressionNode> parsedExpression =
                    programParser.getExpressionParser().parseExpression(queue.branchOff());
            if (parsedExpression.isUnsuccessful()) {
                diagnostics.add(new SyntaxDiagnostic(
                        parsedExpression.getDiagnostic().getToken(),
                        "Expected an expression!"
                ));
                isPartial = true;
            } else {
                queue.mergeBranch(parsedExpression.getTokenQueueId());
                node.setInitializationValue(parsedExpression.getParseResult());
            }
        }

        if (isPartial) {
            queue.skipTo(SyntaxSet.LANGUAGE_ELEMENTS.get(";"));
            return PartialParseResult.partialParse(node, diagnostics.get(0), queue.getId());
        }
        return PartialParseResult.successfulParse(node, queue.getId());
    }

    @PartialParse
    @Override
    public PartialParseResult<ConstantDeclarationNode> parseConstantDeclaration(TokenQueue queue) {
        ConstantDeclarationNodeImpl node = astFactory.createConstantDeclarationNode();
        List<SyntaxDiagnostic> diagnostics = new ArrayList<>();
        boolean isPartial = false;

        ParseResult<TypeNode> parsedType = programParser.getAuxiliaryParser().parseType(queue.branchOff());
        if (parsedType.isUnsuccessful()) {
            return PartialParseResult.unsuccessfulParse(
                    new SyntaxDiagnostic(queue.poll(), "Not a statement!"),
                    queue.getId()
            );
        }
        queue.mergeBranch(parsedType.getTokenQueueId());
        node.setType(parsedType.getParseResult());

        if (!SyntaxSet.LANGUAGE_ELEMENTS.get(":").matches(queue.peek())) {
            diagnostics.add(new SyntaxDiagnostic(queue.poll(), "Expected ':'!"));
            isPartial = true;
        } else {
            queue.poll();
        }

        ParseResult<Token> parsedIdentifier = programParser.getAuxiliaryParser().parseIdentifier(queue.branchOff());
        if (parsedIdentifier.isUnsuccessful()) {
            diagnostics.add(parsedIdentifier.getDiagnostic());
            isPartial = true;
        } else {
            queue.mergeBranch(parsedIdentifier.getTokenQueueId());
            node.setIdentifier(parsedIdentifier.getParseResult());
        }

        if (!SyntaxSet.LANGUAGE_ELEMENTS.get(":=").matches(queue.peek())) {
            diagnostics.add(new SyntaxDiagnostic(queue.poll(), "Expected ':='!"));
            isPartial = true;
        } else {
            queue.poll();
        }
        ParseResult<ExpressionNode> parsedExpression =
                programParser.getExpressionParser().parseExpression(queue.branchOff());
        if (parsedExpression.isUnsuccessful()) {
            diagnostics.add(new SyntaxDiagnostic(
                    parsedExpression.getDiagnostic().getToken(),
                    "Expected an expression!"
            ));
            isPartial = true;
        } else {
            queue.mergeBranch(parsedExpression.getTokenQueueId());
            node.setInitializationValue(parsedExpression.getParseResult());
        }


        if (isPartial) {
            queue.skipTo(SyntaxSet.LANGUAGE_ELEMENTS.get(";"));
            return PartialParseResult.partialParse(node, diagnostics.get(0), queue.getId());
        }
        return PartialParseResult.successfulParse(node, queue.getId());
    }

    @PartialParse
    @Override
    public PartialParseResult<ReassignmentNode> parseReassignment(TokenQueue queue) {
        ParseResult<UnaryExpressionNode> parsedUnaryExpression =
                programParser.getExpressionParser().parseUnaryExpression(queue.branchOff());
        if (parsedUnaryExpression.isSuccessful()) {
            queue.mergeBranch(parsedUnaryExpression.getTokenQueueId());
            return PartialParseResult.successfulParse(parsedUnaryExpression.getParseResult(), queue.getId());
        }

        PartialParseResult<VariableReassignmentNode> parsedVariableReassignment =
                parseVariableReassignment(queue.branchOff());
        if (parsedVariableReassignment.isSuccessful()) {
            queue.mergeBranch(parsedVariableReassignment.getTokenQueueId());
            return PartialParseResult.successfulParse(parsedVariableReassignment.getParseResult(), queue.getId());
        } else if (parsedVariableReassignment.isPartial()) {
            queue.mergeBranch(parsedVariableReassignment.getTokenQueueId());
            return PartialParseResult.partialParse(
                    parsedVariableReassignment.getParseResult(),
                    parsedVariableReassignment.getDiagnostic(),
                    queue.getId()
            );
        }

        return PartialParseResult.unsuccessfulParse(
                new SyntaxDiagnostic(queue.poll(), "Not a satatement!"),
                queue.getId()
        );
    }

    @PartialParse
    @Override
    public PartialParseResult<VariableReassignmentNode> parseVariableReassignment(TokenQueue queue) {
        VariableReassignmentNodeImpl node = astFactory.createVariableReassignmentNode();
        List<SyntaxDiagnostic> diagnostics = new ArrayList<>();
        boolean isPartial = false;

        ParseResult<IdentifierAccessNode> parsedIdentifier =
                programParser.getAuxiliaryParser().parseIdentifierAccess(queue.branchOff());
        if (parsedIdentifier.isUnsuccessful()) {
            return PartialParseResult.unsuccessfulParse(
                    new SyntaxDiagnostic(queue.poll(), "Not a statement!"),
                    queue.getId()
            );
        }
        queue.mergeBranch(parsedIdentifier.getTokenQueueId());
        node.setIdentifier(parsedIdentifier.getParseResult());

        ParseResult<Token> parsedShorthandOperator =
                programParser.getAuxiliaryParser().parseVariableAssignmentOperator(queue.branchOff());
        if (parsedShorthandOperator.isSuccessful()) {
            queue.mergeBranch(parsedShorthandOperator.getTokenQueueId());
            node.setOperator(parsedShorthandOperator.getParseResult());
        } else {
            diagnostics.add(parsedShorthandOperator.getDiagnostic());
            isPartial = true;
            queue.poll();
        }

        ParseResult<ExpressionNode> parsedExpression =
                programParser.getExpressionParser().parseExpression(queue.branchOff());
        if (parsedExpression.isSuccessful()) {
            queue.mergeBranch(parsedExpression.getTokenQueueId());
            node.setValueExpression(parsedExpression.getParseResult());
        } else {
            diagnostics.add(parsedExpression.getDiagnostic());
            isPartial = true;
            queue.skipTo(SyntaxSet.LANGUAGE_ELEMENTS.get(";"));
        }

        if (isPartial) {
            return PartialParseResult.partialParse(node, diagnostics.get(0), queue.getId());
        }
        return PartialParseResult.successfulParse(node, queue.getId());
    }

    @PartialParse
    @Override
    public PartialParseResult<ExportsNode> parseExports(TokenQueue queue) {
        ExportsNodeImpl node = astFactory.createExportNode();
        node.setExports(new LinkedList<>());
        List<SyntaxDiagnostic> diagnostics = new ArrayList<>();
        boolean isPartial = false;

        if (!SyntaxSet.LANGUAGE_ELEMENTS.get("export").matches(queue.peek())) {
            return PartialParseResult.successfulParse(node, queue.getId());
        }
        queue.poll();

        if (SyntaxSet.LANGUAGE_ELEMENTS.get("{").matches(queue.peek())) {
            queue.poll();

            if (SyntaxSet.LANGUAGE_ELEMENTS.get("}").matches(queue.peek())) {
                queue.poll();
                if (!SyntaxSet.LANGUAGE_ELEMENTS.get(";").matches(queue.peek())) {
                    diagnostics.add(new SyntaxDiagnostic(queue.poll(), "Expected ';'!"));
                    isPartial = true;
                }
                queue.poll();
                return PartialParseResult.successfulParse(node, queue.getId());
            } else {
                ParseResult<IdentifierAccessNode> parsedElement =
                        programParser.getAuxiliaryParser().parseIdentifierAccess(queue.branchOff());
                List<IdentifierAccessNode> elements = new LinkedList<>();

                if (parsedElement.isSuccessful()) {
                    queue.mergeBranch(parsedElement.getTokenQueueId());
                    elements.add(parsedElement.getParseResult());
                    node.setExports(elements);
                } else {
                    diagnostics.add(parsedElement.getDiagnostic());
                    isPartial = true;
                }

                PartialParseResult<List<IdentifierAccessNode>> parsedElements =
                        programParser.getAuxiliaryParser().parseAdditionalIdentifierAccesses(queue.branchOff());

                if (parsedElements.isUnsuccessful()) {
                    diagnostics.add(parsedElements.getDiagnostic());
                    isPartial = true;
                } else {
                    if (parsedElements.isPartial()) {
                        diagnostics.add(parsedElements.getDiagnostic());
                        isPartial = true;
                    }
                    queue.mergeBranch(parsedElements.getTokenQueueId());
                    elements.addAll(parsedElements.getParseResult());
                    node.setExports(elements);
                }

                if (!SyntaxSet.LANGUAGE_ELEMENTS.get("}").matches(queue.peek())) {
                    diagnostics.add(new SyntaxDiagnostic(queue.poll(), "Expected '}'!"));
                    isPartial = true;
                }
                queue.poll();

                if (!SyntaxSet.LANGUAGE_ELEMENTS.get(";").matches(queue.peek())) {
                    diagnostics.add(new SyntaxDiagnostic(queue.poll(), "Expected ';'!"));
                    isPartial = true;
                }
                queue.poll();

                if (isPartial) {
                    return PartialParseResult.partialParse(node, diagnostics.get(0), queue.getId());
                }
                return PartialParseResult.successfulParse(node, queue.getId());
            }
        }
        return PartialParseResult.successfulParse(node, queue.getId());
    }
}
