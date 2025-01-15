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
            queue.mergeBranch(parseResult.getTokenQueueId());
            return PartialParseResult.successfulParse(parseResult.getParseResult(), queue.getId());
        } else if (parseResult.isPartial()) {
            queue.mergeBranch(parseResult.getTokenQueueId());
            return PartialParseResult.partialParse(
                    parseResult.getParseResult(),
                    parseResult.getDiagnostic(),
                    queue.getId()
            );
        }

        parseResult = parseWhileLoop(queue.branchOff());
        if (parseResult.isSuccessful()) {
            queue.mergeBranch(parseResult.getTokenQueueId());
            return PartialParseResult.successfulParse(parseResult.getParseResult(), queue.getId());
        } else if (parseResult.isPartial()) {
            queue.mergeBranch(parseResult.getTokenQueueId());
            return PartialParseResult.partialParse(
                    parseResult.getParseResult(),
                    parseResult.getDiagnostic(),
                    queue.getId()
            );
        }

        parseResult = parseConditional(queue.branchOff());
        if (parseResult.isSuccessful()) {
            queue.mergeBranch(parseResult.getTokenQueueId());
            return PartialParseResult.successfulParse(parseResult.getParseResult(), queue.getId());
        } else if (parseResult.isPartial()) {
            queue.mergeBranch(parseResult.getTokenQueueId());
            return PartialParseResult.partialParse(
                    parseResult.getParseResult(),
                    parseResult.getDiagnostic(),
                    queue.getId()
            );
        }
        parseResult = parseTryStatement(queue.branchOff());
        if (parseResult.isSuccessful()) {
            queue.mergeBranch(parseResult.getTokenQueueId());
            return PartialParseResult.successfulParse(parseResult.getParseResult(), queue.getId());
        } else if (parseResult.isPartial()) {
            queue.mergeBranch(parseResult.getTokenQueueId());
            return PartialParseResult.partialParse(
                    parseResult.getParseResult(),
                    parseResult.getDiagnostic(),
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
    public PartialParseResult<ConditionalNode> parseConditional(TokenQueue queue) {
        ConditionalNodeImpl node = astFactory.createConditionalNode();
        List<SyntaxDiagnostic> diagnostics = new ArrayList<>();
        boolean isPartiallyParsed = false;

        node.setConditionBranch(ConditionalNode.ConditionBranch.IF);

        ParseResult<ExpressionNode> parsedExpression =
                programParser.getExpressionParser().parseExpression(queue.branchOff());
        if (parsedExpression.isUnsuccessful()) {
            return PartialParseResult.unsuccessfulParse(parsedExpression.getDiagnostic(), queue.getId());
        }
        queue.mergeBranch(parsedExpression.getTokenQueueId());
        node.setCondition(parsedExpression.getParseResult());

        if (!SyntaxSet.LANGUAGE_ELEMENTS.get("?").matches(queue.peek())) {
            diagnostics.add(new SyntaxDiagnostic(queue.poll(), "Expected '?'!"));
            isPartiallyParsed = true;
        }
        queue.poll();

        PartialParseResult<StatementsNode> parsedCodeBlock =
                programParser.getAuxiliaryParser().parseCodeBlock(queue.branchOff());
        if (parsedCodeBlock.isUnsuccessful()) {
            diagnostics.add(parsedCodeBlock.getDiagnostic());
            isPartiallyParsed = true;
            queue.skipOver(SyntaxSet.LANGUAGE_ELEMENTS.get("}"));
        } else {
            if (parsedCodeBlock.isPartial()) {
                diagnostics.add(parsedCodeBlock.getDiagnostic());
                isPartiallyParsed = true;
            }
            queue.mergeBranch(parsedCodeBlock.getTokenQueueId());
            node.setThenBlock(parsedCodeBlock.getParseResult());
        }

        PartialParseResult<ConditionalNode> parsedElseBlock = parseElseBlock(queue.branchOff());
        if (parsedElseBlock.isSuccessful()) {
            queue.mergeBranch(parsedElseBlock.getTokenQueueId());
            node.setElseBranch(parsedElseBlock.getParseResult());
        } else if (parsedElseBlock.isPartial()) {
            queue.mergeBranch(parsedElseBlock.getTokenQueueId());
            node.setElseBranch(parsedElseBlock.getParseResult());
            diagnostics.add(parsedElseBlock.getDiagnostic());
            isPartiallyParsed = true;
        }

        if (isPartiallyParsed) {
            return PartialParseResult.partialParse(node, diagnostics.get(0), queue.getId());
        }
        return PartialParseResult.successfulParse(node, queue.getId());
    }

    @PartialParse
    @Override
    public PartialParseResult<ConditionalNode> parseElseBlock(TokenQueue queue) {
        List<SyntaxDiagnostic> diagnostics = new ArrayList<>();
        boolean isPartiallyParsed = false;

        if (!SyntaxSet.LANGUAGE_ELEMENTS.get("|").matches(queue.peek())) {
            return PartialParseResult.unsuccessfulParse(
                    new SyntaxDiagnostic(queue.poll(), "Expected '|'!"),
                    queue.getId()
            );
        }
        queue.poll();

        PartialParseResult<StatementsNode> parsedCodeBlock =
                programParser.getAuxiliaryParser().parseCodeBlock(queue.branchOff());
        // if partial or successful we have an else in our hands
        if (!parsedCodeBlock.isUnsuccessful()) {
            ConditionalNodeImpl node = astFactory.createConditionalNode();
            node.setConditionBranch(ConditionalNodeImpl.ConditionBranch.ELSE);

            if (parsedCodeBlock.isPartial()) {
                diagnostics.add(parsedCodeBlock.getDiagnostic());
                isPartiallyParsed = true;
            }
            queue.mergeBranch(parsedCodeBlock.getTokenQueueId());

            node.setThenBlock(parsedCodeBlock.getParseResult());

            if (isPartiallyParsed) {
                return PartialParseResult.partialParse(node, diagnostics.get(0), queue.getId());
            }
            return PartialParseResult.successfulParse(node, queue.getId());

        } else {
            PartialParseResult<ConditionalNode> parsedConditional = parseConditional(queue.branchOff());
            if (parsedConditional.isSuccessful()) {
                queue.mergeBranch(parsedConditional.getTokenQueueId());
                ConditionalNode node = parsedConditional.getParseResult();
                return PartialParseResult.successfulParse(node, queue.getId());
            } else if (parsedConditional.isPartial()) {
                queue.mergeBranch(parsedConditional.getTokenQueueId());
                return PartialParseResult.partialParse(
                        parsedConditional.getParseResult(),
                        parsedConditional.getDiagnostic(),
                        queue.getId()
                );
            }
        }
        Token current = queue.peek();
        queue.skipOver(SyntaxSet.LANGUAGE_ELEMENTS.get("}"));
        return PartialParseResult.partialParse(
                astFactory.createConditionalNode(),
                new SyntaxDiagnostic(current, "Expected '{' or an expression!"),
                queue.getId()
        );
    }

    @PartialParse
    @Override
    public PartialParseResult<ForLoopNode> parseForLoop(TokenQueue queue) {
        ForLoopNodeImpl node = astFactory.createForLoopNode();
        List<SyntaxDiagnostic> diagnostics = new ArrayList<>();
        boolean isPartiallyParsed = false;

        if (!SyntaxSet.LANGUAGE_ELEMENTS.get("for").matches(queue.peek())) {
            return PartialParseResult.unsuccessfulParse(
                    new SyntaxDiagnostic(queue.poll(), "Expected 'for'!"),
                    queue.getId()
            );
        }
        queue.poll();

        ParseResult<Token> parsedIdentifier = programParser.getAuxiliaryParser().parseIdentifier(queue.branchOff());
        if (parsedIdentifier.isSuccessful()) {
            queue.mergeBranch(parsedIdentifier.getTokenQueueId());
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
                queue.mergeBranch(parsedStartIndexExpression.getTokenQueueId());
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
                queue.mergeBranch(parsedEndIndexExpression.getTokenQueueId());
                node.setEndIndex(parsedEndIndexExpression.getParseResult());
            } else {
                diagnostics.add(new SyntaxDiagnostic(queue.peek(), "Expected an expression!"));
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
                queue.mergeBranch(parsedOptionalForStepper.getTokenQueueId());
                node.setOptionalStepperStatement(parsedOptionalForStepper.getParseResult());
            } else {
                diagnostics.add(new SyntaxDiagnostic(queue.peek(), "Expected a reassignment statement!"));
                isPartiallyParsed = true;
            }
        }

        PartialParseResult<StatementsNode> parsedCodeBlock =
                programParser.getAuxiliaryParser().parseCodeBlock(queue.branchOff());
        if (parsedCodeBlock.isUnsuccessful()) {
            diagnostics.add(parsedCodeBlock.getDiagnostic());
            isPartiallyParsed = true;
            queue.skipOver(SyntaxSet.LANGUAGE_ELEMENTS.get("}"));
        } else {
            if (parsedCodeBlock.isPartial()) {
                diagnostics.add(parsedCodeBlock.getDiagnostic());
                isPartiallyParsed = true;
            }
            queue.mergeBranch(parsedCodeBlock.getTokenQueueId());
            node.setBody(parsedCodeBlock.getParseResult());
        }

        if (isPartiallyParsed) {
            return PartialParseResult.partialParse(node, diagnostics.get(0), queue.getId());
        }
        return PartialParseResult.successfulParse(node, queue.getId());
    }

    @PartialParse
    @Override
    public PartialParseResult<WhileLoopNode> parseWhileLoop(TokenQueue queue) {
        WhileLoopNodeImpl node = astFactory.createWhileLoopNode();
        List<SyntaxDiagnostic> diagnostics = new ArrayList<>();
        boolean isPartiallyParsed = false;

        if (!SyntaxSet.LANGUAGE_ELEMENTS.get("while").matches(queue.peek())) {
            return PartialParseResult.unsuccessfulParse(
                    new SyntaxDiagnostic(queue.poll(), "Expected 'while'!"),
                    queue.getId()
            );
        }
        queue.poll();

        ParseResult<ExpressionNode> parsedExpression =
                programParser.getExpressionParser().parseExpression(queue.branchOff());
        if (parsedExpression.isSuccessful()) {
            queue.mergeBranch(parsedExpression.getTokenQueueId());
            node.setCondition(parsedExpression.getParseResult());
        } else {
            queue.skipTo(SyntaxSet.LANGUAGE_ELEMENTS.get("{"));
            diagnostics.add(parsedExpression.getDiagnostic());
            isPartiallyParsed = true;
        }
        PartialParseResult<StatementsNode> parsedCodeBlock =
                programParser.getAuxiliaryParser().parseCodeBlock(queue.branchOff());
        if (parsedCodeBlock.isUnsuccessful()) {
            diagnostics.add(parsedCodeBlock.getDiagnostic());
            isPartiallyParsed = true;
            queue.skipOver(SyntaxSet.LANGUAGE_ELEMENTS.get("}"));
        } else {
            if (parsedCodeBlock.isPartial()) {
                diagnostics.add(parsedCodeBlock.getDiagnostic());
                isPartiallyParsed = true;
            }
            queue.mergeBranch(parsedCodeBlock.getTokenQueueId());
            node.setBody(parsedCodeBlock.getParseResult());
        }

        if (isPartiallyParsed) {
            return PartialParseResult.partialParse(node, diagnostics.get(0), queue.getId());
        }

        return PartialParseResult.successfulParse(node, queue.getId());
    }

    @PartialParse
    @Override
    public PartialParseResult<TryStatementNode> parseTryStatement(TokenQueue queue) {
        TryStatementNodeImpl node = astFactory.createTryStatementNode();
        List<InterceptStatementNode> interceptStatements = new LinkedList<>();
        List<SyntaxDiagnostic> diagnostics = new ArrayList<>();
        boolean isPartial = false;

        if (!SyntaxSet.LANGUAGE_ELEMENTS.get("try").matches(queue.peek())) {
            return PartialParseResult.unsuccessfulParse(
                    new SyntaxDiagnostic(queue.poll(), "Not a statement!"),
                    queue.getId()
            );
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
            queue.mergeBranch(parsedBody.getTokenQueueId());
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
            queue.mergeBranch(parsedInterceptStatement.getTokenQueueId());
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
            queue.mergeBranch(parsedInterceptStatements.getTokenQueueId());
            interceptStatements.addAll(parsedInterceptStatements.getParseResult());
        }
        node.setInterceptBlocks(interceptStatements);

        PartialParseResult<EnsureStatementNode> parsedEnsureStatement = parseEnsureStatement(queue.branchOff());
        if (!parsedEnsureStatement.isUnsuccessful()) {
            if (parsedEnsureStatement.isPartial()) {
                diagnostics.add(parsedEnsureStatement.getDiagnostic());
                isPartial = true;
            }
            queue.mergeBranch(parsedEnsureStatement.getTokenQueueId());
            node.setEnsureBlock(parsedEnsureStatement.getParseResult());
        }

        if (isPartial) {
            return PartialParseResult.partialParse(node, diagnostics.get(0), queue.getId());
        }

        return PartialParseResult.successfulParse(node, queue.getId());
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
            return PartialParseResult.successfulParse(interceptStatements, queue.getId());
        } else {
            if (parsedInterceptStatement.isPartial()) {
                diagnostics.add(parsedInterceptStatement.getDiagnostic());
                isPartial = true;
            }
            queue.mergeBranch(parsedInterceptStatement.getTokenQueueId());
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
            queue.mergeBranch(parsedInterceptStatements.getTokenQueueId());
            interceptStatements.addAll(parsedInterceptStatements.getParseResult());
        }
        if (isPartial) {
            return PartialParseResult.partialParse(interceptStatements, diagnostics.get(0), queue.getId());
        }
        return PartialParseResult.successfulParse(interceptStatements, queue.getId());
    }

    @PartialParse
    @Override
    public PartialParseResult<InterceptStatementNode> parseInterceptStatement(TokenQueue queue) {
        InterceptStatementNodeImpl node = astFactory.createInterceptStatementNode();
        List<IdentifierAccessNode> interceptedExceptions = new LinkedList<>();
        List<SyntaxDiagnostic> diagnostics = new ArrayList<>();
        boolean isPartial = false;

        if (!SyntaxSet.LANGUAGE_ELEMENTS.get("intercept").matches(queue.peek())) {
            return PartialParseResult.unsuccessfulParse(
                    new SyntaxDiagnostic(queue.poll(), "Expected 'intercept'!"),
                    queue.getId()
            );
        }
        queue.poll();
        ParseResult<IdentifierAccessNode> parsedInterceptedException =
                programParser.getAuxiliaryParser().parseIdentifierAccess(queue.branchOff());
        if (parsedInterceptedException.isUnsuccessful()) {
            diagnostics.add(parsedInterceptedException.getDiagnostic());
            isPartial = true;
        } else {
            queue.mergeBranch(parsedInterceptedException.getTokenQueueId());
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
            queue.mergeBranch(parsedAdditionalInterceptedExceptions.getTokenQueueId());
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
                queue.mergeBranch(parsedExceptionIdentifier.getTokenQueueId());
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
            queue.mergeBranch(parsedBody.getTokenQueueId());
            node.setBody(parsedBody.getParseResult());
        }

        if (isPartial) {
            return PartialParseResult.partialParse(node, diagnostics.get(0), queue.getId());
        }

        return PartialParseResult.successfulParse(node, queue.getId());
    }

    @PartialParse
    @Override
    public PartialParseResult<EnsureStatementNode> parseEnsureStatement(TokenQueue queue) {
        EnsureStatementNodeImpl node = astFactory.createEnsureStatementNode();
        List<SyntaxDiagnostic> diagnostics = new ArrayList<>();
        boolean isPartial = false;

        if (!SyntaxSet.LANGUAGE_ELEMENTS.get("ensure").matches(queue.peek())) {
            return PartialParseResult.unsuccessfulParse(
                    new SyntaxDiagnostic(queue.poll(), "Expected 'ensure'!"),
                    queue.getId()
            );
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
            queue.mergeBranch(parsedBody.getTokenQueueId());
            node.setBody(parsedBody.getParseResult());
        }

        if (isPartial) {
            return PartialParseResult.partialParse(node, diagnostics.get(0), queue.getId());
        }
        return PartialParseResult.successfulParse(node, queue.getId());
    }
}
