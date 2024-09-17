package de.nikschadowsky.baall.compiler.syntax.analysis;

import de.nikschadowsky.baall.compiler.symbol.Token;
import de.nikschadowsky.baall.compiler.symbol.TokenQueue;
import de.nikschadowsky.baall.compiler.syntax.analysis.result.ParseResult;
import de.nikschadowsky.baall.compiler.syntax.analysis.result.PartialParseResult;
import de.nikschadowsky.baall.compiler.syntax.error.SyntaxDiagnostic;
import de.nikschadowsky.baall.compiler.syntax.util.PartialParse;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.ASTNodeFactory;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.controlstructures.*;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.expression.ExpressionNode;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.program.StatementsNode;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.statement.assignment.ReassignmentNode;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.statement.controlstatement.exceptionhandling.*;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.value.IdentifierAccessNode;
import de.nikschadowsky.baall.compiler.util.SyntaxSet;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @since 11.08.2024
 */
public class ControlStructureParserImpl implements ControlStructureParser {

    private final ProgramParser programParser;
    private final ASTNodeFactory astFactory;

    public ControlStructureParserImpl(ProgramParser programParser, ASTNodeFactory astFactory) {
        this.programParser = programParser;
        this.astFactory = astFactory;
    }

    @PartialParse
    @Override
    public PartialParseResult<ControlStructureNode> parseControlStructure(TokenQueue queue) {
        PartialParseResult<? extends ControlStructureNode> parseResult;

        parseResult = parseForLoop(queue.branchOff());
        if (parseResult.isSuccessful()) {
            queue.mergeBranch();
            return PartialParseResult.successfulParse(parseResult.getParseResult());
        } else if (parseResult.isPartial()) {
            queue.mergeBranch();
            return PartialParseResult.partialParse(parseResult.getParseResult(), parseResult.getDiagnostic());
        }

        parseResult = parseWhileLoop(queue.branchOff());
        if (parseResult.isSuccessful()) {
            queue.mergeBranch();
            return PartialParseResult.successfulParse(parseResult.getParseResult());
        } else if (parseResult.isPartial()) {
            queue.mergeBranch();
            return PartialParseResult.partialParse(parseResult.getParseResult(), parseResult.getDiagnostic());
        }

        parseResult = parseConditional(queue.branchOff());
        if (parseResult.isSuccessful()) {
            queue.mergeBranch();
            return PartialParseResult.successfulParse(parseResult.getParseResult());
        } else if (parseResult.isPartial()) {
            queue.mergeBranch();
            return PartialParseResult.partialParse(parseResult.getParseResult(), parseResult.getDiagnostic());
        }
        parseResult = parseTryStatement(queue.branchOff());
        if (parseResult.isSuccessful()) {
            queue.mergeBranch();
            return PartialParseResult.successfulParse(parseResult.getParseResult());
        } else if (parseResult.isPartial()) {
            queue.mergeBranch();
            return PartialParseResult.partialParse(parseResult.getParseResult(), parseResult.getDiagnostic());
        }

        return PartialParseResult.unsuccessfulParse(new SyntaxDiagnostic(queue.poll(), "Not a statement!"));
    }

    @PartialParse
    @Override
    public PartialParseResult<ConditionalNode> parseConditional(TokenQueue queue) {
        ConditionalNodeImpl node = astFactory.createConditionalNode();
        List<SyntaxDiagnostic> diagnostics = new ArrayList<>();
        boolean isPartiallyParsed = false;

        ParseResult<ExpressionNode> parsedExpression =
                programParser.getExpressionParser().parseExpression(queue.branchOff());
        if (parsedExpression.isUnsuccessful()) {
            return PartialParseResult.partialParse(node, parsedExpression.getDiagnostic());
        }
        queue.mergeBranch();
        node.setCondition(parsedExpression.getParseResult());

        if (!SyntaxSet.LANGUAGE_ELEMENTS.get("?").matches(queue.peek())) {
            diagnostics.add(new SyntaxDiagnostic(queue.poll(), "Expected '?'!"));
            isPartiallyParsed = true;
        }
        queue.poll();

        if (SyntaxSet.LANGUAGE_ELEMENTS.get("{").matches(queue.poll())) {
            PartialParseResult<StatementsNode> parsedStatements =
                    programParser.getStatementParser().parseStatements(queue.branchOff());
            if (parsedStatements.isSuccessful()) {
                queue.mergeBranch();
                node.setThenBlock(parsedStatements.getParseResult());
            } else if (parsedStatements.isPartial()) {
                queue.mergeBranch();
                node.setThenBlock(parsedStatements.getParseResult());
                diagnostics.add(parsedStatements.getDiagnostic());
                isPartiallyParsed = true;
            } else {
                diagnostics.add(new SyntaxDiagnostic(queue.poll(), "Expected a statement!"));
                isPartiallyParsed = true;
            }

            if (!SyntaxSet.LANGUAGE_ELEMENTS.get("}").matches(queue.peek())) {
                diagnostics.add(new SyntaxDiagnostic(queue.poll(), "Expected '}'!"));
                isPartiallyParsed = true;
            }
            queue.poll();
        } else {
            diagnostics.add(new SyntaxDiagnostic(queue.poll(), "Expected '{'!"));
            isPartiallyParsed = true;
        }
        PartialParseResult<ConditionalNode> parsedElseBlock = parseElseBlock(queue.branchOff());
        if (parsedElseBlock.isSuccessful()) {
            queue.mergeBranch();
            node.setElseBranch(parsedElseBlock.getParseResult());
        } else if (parsedElseBlock.isPartial()) {
            queue.mergeBranch();
            node.setElseBranch(parsedElseBlock.getParseResult());
            diagnostics.add(parsedElseBlock.getDiagnostic());
            isPartiallyParsed = true;
        } else {
            diagnostics.add(parsedElseBlock.getDiagnostic());
            isPartiallyParsed = true;
        }

        if (isPartiallyParsed) {
            queue.skipOver(SyntaxSet.LANGUAGE_ELEMENTS.get("}"));
            return PartialParseResult.partialParse(node, diagnostics.get(0));
        }
        return PartialParseResult.successfulParse(node);
    }

    @PartialParse
    @Override
    public PartialParseResult<ConditionalNode> parseElseBlock(TokenQueue queue) {
        List<SyntaxDiagnostic> diagnostics = new ArrayList<>();
        boolean isPartiallyParsed = false;

        if (!SyntaxSet.LANGUAGE_ELEMENTS.get("|").matches(queue.peek())) {
            return PartialParseResult.unsuccessfulParse(new SyntaxDiagnostic(queue.poll(), "Expected '|'!"));
        }
        queue.poll();

        if (SyntaxSet.LANGUAGE_ELEMENTS.get("{").matches(queue.peek())) {
            queue.poll();

            ConditionalNodeImpl node = astFactory.createConditionalNode();
            node.setConditionBranch(ConditionalNodeImpl.ConditionBranch.ELSE);
            PartialParseResult<StatementsNode> parsedStatements =
                    programParser.getStatementParser().parseStatements(queue.branchOff());
            if (parsedStatements.isSuccessful()) {
                queue.mergeBranch();

                node.setThenBlock(parsedStatements.getParseResult());
                if (SyntaxSet.LANGUAGE_ELEMENTS.get("}").matches(queue.peek())) {
                    queue.poll();
                    return PartialParseResult.successfulParse(node);
                }
                return PartialParseResult.partialParse(node, new SyntaxDiagnostic(queue.poll(), "Expected '}'!"));
            }
            queue.skipOver(SyntaxSet.LANGUAGE_ELEMENTS.get("}"));
            return PartialParseResult.partialParse(node, parsedStatements.getDiagnostic());
        }


        PartialParseResult<ConditionalNode> parsedConditional = parseConditional(queue.branchOff());
        if (parsedConditional.isSuccessful()) {
            queue.mergeBranch();
            ConditionalNode node = parsedConditional.getParseResult();
            return PartialParseResult.successfulParse(node);
        } else if (parsedConditional.isPartial()) {
            queue.mergeBranch();
            return PartialParseResult.partialParse(
                    parsedConditional.getParseResult(),
                    parsedConditional.getDiagnostic()
            );
        }
        return PartialParseResult.unsuccessfulParse(new SyntaxDiagnostic(
                queue.poll(),
                "Expected '{' or an expression!"
        ));
    }

    @PartialParse
    @Override
    public PartialParseResult<ForLoopNode> parseForLoop(TokenQueue queue) {
        ForLoopNodeImpl node = astFactory.createForLoopNode();
        List<SyntaxDiagnostic> diagnostics = new ArrayList<>();
        boolean isPartiallyParsed = false;

        if (!SyntaxSet.LANGUAGE_ELEMENTS.get("for").matches(queue.peek())) {
            return PartialParseResult.unsuccessfulParse(new SyntaxDiagnostic(queue.poll(), "Expected 'for'!"));
        }
        queue.poll();

        ParseResult<Token> parsedIdentifier = programParser.getAuxiliaryParser().parseIdentifier(queue.branchOff());
        if (parsedIdentifier.isSuccessful()) {
            queue.mergeBranch();
            node.setIdentifier(parsedIdentifier.getParseResult());
        } else {
            diagnostics.add(new SyntaxDiagnostic(queue.poll(), "Expected an identifier!"));
            isPartiallyParsed = true;
        }

        if (SyntaxSet.LANGUAGE_ELEMENTS.get("=").matches(queue.peek())) {
            queue.poll();
            ParseResult<ExpressionNode> parsedStartIndexExpression =
                    programParser.getExpressionParser().parseExpression(queue.branchOff());
            if (parsedStartIndexExpression.isSuccessful()) {
                queue.mergeBranch();
                node.setStartIndex(parsedStartIndexExpression.getParseResult());
            } else {
                diagnostics.add(new SyntaxDiagnostic(queue.poll(), "Expected an expression!"));
                isPartiallyParsed = true;
            }
        } else {
            diagnostics.add(new SyntaxDiagnostic(queue.poll(), "Expected '='!"));
            isPartiallyParsed = true;
        }

        if (SyntaxSet.LANGUAGE_ELEMENTS.get("..").matches(queue.peek())) {
            queue.poll();
            ParseResult<ExpressionNode> parsedEndIndexExpression =
                    programParser.getExpressionParser().parseExpression(queue.branchOff());
            if (parsedEndIndexExpression.isSuccessful()) {
                queue.mergeBranch();
                node.setEndIndex(parsedEndIndexExpression.getParseResult());
            } else {
                diagnostics.add(new SyntaxDiagnostic(queue.getPointer(), "Expected an expression!"));
                isPartiallyParsed = true;
            }
        } else {
            diagnostics.add(new SyntaxDiagnostic(queue.poll(), "Expected '..'!"));
            isPartiallyParsed = true;
        }

        if (SyntaxSet.LANGUAGE_ELEMENTS.get("::").matches(queue.peek())) {
            queue.poll();
            node.setHasOptionalStepperStatement(true);
            ParseResult<ReassignmentNode> parsedOptionalForStepper =
                    programParser.getStatementParser().parseReassignment(queue.branchOff());
            if (parsedOptionalForStepper.isSuccessful()) {
                queue.mergeBranch();
                node.setOptionalStepperStatement(parsedOptionalForStepper.getParseResult());
            } else {
                diagnostics.add(new SyntaxDiagnostic("Expected a reassignment statement!"));
                isPartiallyParsed = true;
            }
        }

        if (SyntaxSet.LANGUAGE_ELEMENTS.get("{").matches(queue.peek())) {
            queue.poll();
            PartialParseResult<StatementsNode> parsedStatements =
                    programParser.getStatementParser().parseStatements(queue.branchOff());
            if (parsedStatements.isSuccessful()) {
                queue.mergeBranch();
                node.setBody(parsedStatements.getParseResult());
            } else if (parsedStatements.isPartial()) {
                queue.mergeBranch();
                node.setBody(parsedStatements.getParseResult());
                diagnostics.add(parsedStatements.getDiagnostic());
                isPartiallyParsed = true;
            } else {
                diagnostics.add(new SyntaxDiagnostic(queue.poll(), "Expected a statement!"));
                isPartiallyParsed = true;
            }

            if (!SyntaxSet.LANGUAGE_ELEMENTS.get("}").matches(queue.peek())) {
                diagnostics.add(new SyntaxDiagnostic(queue.poll(), "Expected '}'!"));
                isPartiallyParsed = true;
            }
            queue.poll();
        } else {
            diagnostics.add(new SyntaxDiagnostic(queue.poll(), "Expected '{'!"));
            isPartiallyParsed = true;
        }

        if (isPartiallyParsed) {
            queue.skipOver(SyntaxSet.LANGUAGE_ELEMENTS.get("}"));
            return PartialParseResult.partialParse(node, diagnostics.get(0));
        }
        return PartialParseResult.successfulParse(node);
    }

    @PartialParse
    @Override
    public PartialParseResult<WhileLoopNode> parseWhileLoop(TokenQueue queue) {
        WhileLoopNodeImpl node = astFactory.createWhileLoopNode();
        List<SyntaxDiagnostic> diagnostics = new ArrayList<>();
        boolean isPartiallyParsed = false;

        if (!SyntaxSet.LANGUAGE_ELEMENTS.get("while").matches(queue.peek())) {
            return PartialParseResult.unsuccessfulParse(new SyntaxDiagnostic(queue.poll(), "Expected 'while'!"));
        }
        queue.poll();

        ParseResult<ExpressionNode> parsedExpression =
                programParser.getExpressionParser().parseExpression(queue.branchOff());
        if (parsedExpression.isSuccessful()) {
            queue.mergeBranch();
            node.setCondition(parsedExpression.getParseResult());
        } else {
            queue.skipTo(SyntaxSet.LANGUAGE_ELEMENTS.get("{"));
            diagnostics.add(parsedExpression.getDiagnostic());
            isPartiallyParsed = true;
        }

        if (SyntaxSet.LANGUAGE_ELEMENTS.get("{").matches(queue.peek())) {
            queue.poll();
            PartialParseResult<StatementsNode> parsedStatements =
                    programParser.getStatementParser().parseStatements(queue.branchOff());
            if (parsedStatements.isSuccessful()) {
                queue.mergeBranch();
                node.setBody(parsedStatements.getParseResult());
            } else if (parsedStatements.isPartial()) {
                queue.mergeBranch();
                node.setBody(parsedStatements.getParseResult());
                diagnostics.add(parsedStatements.getDiagnostic());
                isPartiallyParsed = true;
            } else {
                diagnostics.add(new SyntaxDiagnostic(queue.poll(), "Expected a statement!"));
                isPartiallyParsed = true;

            }
            if (!SyntaxSet.LANGUAGE_ELEMENTS.get("}").matches(queue.peek())) {
                queue.skipOver(SyntaxSet.LANGUAGE_ELEMENTS.get("}"));
                diagnostics.add(new SyntaxDiagnostic(queue.poll(), "Expected '}'!"));
                isPartiallyParsed = true;
            }
            queue.poll();

        } else {
            diagnostics.add(new SyntaxDiagnostic(queue.poll(), "Expected '{'!"));
            isPartiallyParsed = true;
            queue.skipOver(SyntaxSet.LANGUAGE_ELEMENTS.get("}"));
        }
        if (isPartiallyParsed) {
            return PartialParseResult.partialParse(node, diagnostics.get(0));
        }

        return PartialParseResult.successfulParse(node);
    }

    @PartialParse
    @Override
    public PartialParseResult<TryStatementNode> parseTryStatement(TokenQueue queue) {
        TryStatementNodeImpl node = astFactory.createTryStatementNode();
        List<InterceptStatementNode> interceptStatements = new LinkedList<>();
        List<SyntaxDiagnostic> diagnostics = new ArrayList<>();
        boolean isPartial = false;

        if (!SyntaxSet.LANGUAGE_ELEMENTS.get("try").matches(queue.peek())) {
            return PartialParseResult.unsuccessfulParse(new SyntaxDiagnostic(queue.poll(), "Not a statement!"));
        }
        queue.poll();

        PartialParseResult<StatementsNode> parsedBody =
                programParser.getAuxiliaryParser().parseCodeBlock(queue.branchOff());
        if (parsedBody.isUnsuccessful()) {
            diagnostics.add(parsedBody.getDiagnostic());
            isPartial = true;
        } else {
            if (parsedBody.isPartial()) {
                diagnostics.add(parsedBody.getDiagnostic());
                isPartial = true;
            }
            queue.mergeBranch();
            node.setBody(parsedBody.getParseResult());
        }

        PartialParseResult<InterceptStatementNode> parsedInterceptStatement =
                parseInterceptStatement(queue.branchOff());
        if (parsedInterceptStatement.isUnsuccessful()) {
            diagnostics.add(parsedInterceptStatement.getDiagnostic());
            isPartial = true;
        } else {
            if (parsedInterceptStatement.isPartial()) {
                diagnostics.add(parsedInterceptStatement.getDiagnostic());
                isPartial = true;
            }
            queue.mergeBranch();
            interceptStatements.add(parsedInterceptStatement.getParseResult());
        }

        PartialParseResult<List<InterceptStatementNode>> parsedInterceptStatements =
                parseInterceptStatements(queue.branchOff());
        if (parsedInterceptStatements.isUnsuccessful()) {
            diagnostics.add(parsedInterceptStatements.getDiagnostic());
            isPartial = true;
        } else {
            if (parsedInterceptStatements.isPartial()) {
                diagnostics.add(parsedInterceptStatements.getDiagnostic());
                isPartial = true;
            }
            queue.mergeBranch();
            interceptStatements.addAll(parsedInterceptStatements.getParseResult());
        }
        node.setInterceptBlocks(interceptStatements);

        PartialParseResult<EnsureStatementNode> parsedEnsureStatement =
                parseEnsureStatement(queue.branchOff());
        if (!parsedEnsureStatement.isUnsuccessful()) {
            if (parsedEnsureStatement.isPartial()) {
                diagnostics.add(parsedEnsureStatement.getDiagnostic());
                isPartial = true;
            }
            queue.mergeBranch();
            node.setEnsureBlock(parsedEnsureStatement.getParseResult());
        }

        if (isPartial) {
            return PartialParseResult.partialParse(node, diagnostics.get(0));
        }

        return PartialParseResult.successfulParse(node);
    }

    @PartialParse
    @Override
    public PartialParseResult<List<InterceptStatementNode>> parseInterceptStatements(TokenQueue queue) {
        List<InterceptStatementNode> interceptStatements = new LinkedList<>();
        List<SyntaxDiagnostic> diagnostics = new ArrayList<>();
        boolean isPartial = false;

        PartialParseResult<InterceptStatementNode> parsedInterceptStatement =
                parseInterceptStatement(queue.branchOff());

        if (parsedInterceptStatement.isUnsuccessful()) {
            return PartialParseResult.successfulParse(interceptStatements);
        } else {
            if (parsedInterceptStatement.isPartial()) {
                diagnostics.add(parsedInterceptStatement.getDiagnostic());
                isPartial = true;
            }
            queue.mergeBranch();
            interceptStatements.add(parsedInterceptStatement.getParseResult());
        }

        PartialParseResult<List<InterceptStatementNode>> parsedInterceptStatements =
                parseInterceptStatements(queue.branchOff());
        if (parsedInterceptStatements.isUnsuccessful()) {
            diagnostics.add(parsedInterceptStatements.getDiagnostic());
            isPartial = true;
        } else {
            if (parsedInterceptStatements.isPartial()) {
                diagnostics.add(parsedInterceptStatements.getDiagnostic());
                isPartial = true;
            }
            queue.mergeBranch();
            interceptStatements.addAll(parsedInterceptStatements.getParseResult());
        }
        if (isPartial) {
            return PartialParseResult.partialParse(interceptStatements, diagnostics.get(0));
        }
        return PartialParseResult.successfulParse(interceptStatements);
    }

    @PartialParse
    @Override
    public PartialParseResult<InterceptStatementNode> parseInterceptStatement(TokenQueue queue) {
        InterceptStatementNodeImpl node = astFactory.createInterceptStatementNode();
        List<IdentifierAccessNode> interceptedExceptions = new LinkedList<>();
        List<SyntaxDiagnostic> diagnostics = new ArrayList<>();
        boolean isPartial = false;

        if (!SyntaxSet.LANGUAGE_ELEMENTS.get("intercept").matches(queue.peek())) {
            return PartialParseResult.unsuccessfulParse(new SyntaxDiagnostic(queue.poll(), "Expected 'intercept'!"));
        }
        queue.poll();
        ParseResult<IdentifierAccessNode> parsedInterceptedException =
                programParser.getAuxiliaryParser().parseIdentifierAccess(queue.branchOff());
        if (parsedInterceptedException.isUnsuccessful()) {
            diagnostics.add(parsedInterceptedException.getDiagnostic());
            isPartial = true;
        } else {
            queue.mergeBranch();
            interceptedExceptions.add(parsedInterceptedException.getParseResult());
        }

        PartialParseResult<List<IdentifierAccessNode>> parsedAdditionalInterceptedExceptions =
                programParser.getAuxiliaryParser().parseAdditionalIdentifierAccesses(queue.branchOff());
        if (parsedAdditionalInterceptedExceptions.isUnsuccessful()) {
            diagnostics.add(parsedAdditionalInterceptedExceptions.getDiagnostic());
            isPartial = true;
        } else {
            if (parsedAdditionalInterceptedExceptions.isPartial()) {
                diagnostics.add(parsedAdditionalInterceptedExceptions.getDiagnostic());
                isPartial = true;
            }
            queue.mergeBranch();
            interceptedExceptions.addAll(parsedAdditionalInterceptedExceptions.getParseResult());
        }
        node.setInterceptedExceptions(interceptedExceptions);

        if (!SyntaxSet.LANGUAGE_ELEMENTS.get(":").matches(queue.peek())) {
            diagnostics.add(new SyntaxDiagnostic(queue.peek(), "Expected ':'!"));
            isPartial = true;
            queue.skipTo(SyntaxSet.LANGUAGE_ELEMENTS.get("{"));
        } else {
            queue.poll();
            ParseResult<Token> parsedExceptionIdentifier =
                    programParser.getAuxiliaryParser().parseIdentifier(queue.branchOff());
            if (parsedExceptionIdentifier.isUnsuccessful()) {
                diagnostics.add(parsedExceptionIdentifier.getDiagnostic());
                isPartial = true;
            } else {
                queue.mergeBranch();
                node.setRaisedExceptionIdentifier(parsedExceptionIdentifier.getParseResult());
            }
        }

        PartialParseResult<StatementsNode> parsedBody =
                programParser.getAuxiliaryParser().parseCodeBlock(queue.branchOff());
        if (parsedBody.isUnsuccessful()) {
            diagnostics.add(parsedBody.getDiagnostic());
            isPartial = true;
        } else {
            if (parsedBody.isPartial()) {
                diagnostics.add(parsedBody.getDiagnostic());
                isPartial = true;
            }
            queue.mergeBranch();
            node.setBody(parsedBody.getParseResult());
        }

        if (isPartial) {
            return PartialParseResult.partialParse(node, diagnostics.get(0));
        }

        return PartialParseResult.successfulParse(node);
    }

    @PartialParse
    @Override
    public PartialParseResult<EnsureStatementNode> parseEnsureStatement(TokenQueue queue) {
        EnsureStatementNodeImpl node = astFactory.createEnsureStatementNode();
        List<SyntaxDiagnostic> diagnostics = new ArrayList<>();
        boolean isPartial = false;

        if (!SyntaxSet.LANGUAGE_ELEMENTS.get("ensure").matches(queue.peek())) {
            return PartialParseResult.unsuccessfulParse(new SyntaxDiagnostic(queue.poll(), "Expected 'ensure'!"));
        }
        queue.poll();

        PartialParseResult<StatementsNode> parsedBody =
                programParser.getAuxiliaryParser().parseCodeBlock(queue.branchOff());
        if (parsedBody.isUnsuccessful()) {
            diagnostics.add(parsedBody.getDiagnostic());
            isPartial = true;
            queue.skipOver(SyntaxSet.LANGUAGE_ELEMENTS.get("}"));
        } else {
            if (parsedBody.isPartial()) {
                diagnostics.add(parsedBody.getDiagnostic());
                isPartial = true;
            }
            queue.mergeBranch();
            node.setBody(parsedBody.getParseResult());
        }

        if (isPartial) {
            return PartialParseResult.partialParse(node, diagnostics.get(0));
        }
        return PartialParseResult.successfulParse(node);
    }
}
