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
public class ControlStructureParser {
    @PartialParse
    public static PartialParseResult<ControlStructureNode> parseControlStructure(TokenQueue queue, ASTNodeFactory astFactory) {
        PartialParseResult<? extends ControlStructureNode> parseResult;

        parseResult = parseForLoop(queue.branchOff(), astFactory);
        if (parseResult.isSuccessful()) {
            queue.mergeBranch();
            return PartialParseResult.successfulParse(parseResult.getParseResult());
        } else if (parseResult.isPartial()) {
            queue.mergeBranch();
            return PartialParseResult.partialParse(parseResult.getParseResult(), parseResult.getDiagnostic());
        }

        parseResult = parseWhileLoop(queue.branchOff(), astFactory);
        if (parseResult.isSuccessful()) {
            queue.mergeBranch();
            return PartialParseResult.successfulParse(parseResult.getParseResult());
        } else if (parseResult.isPartial()) {
            queue.mergeBranch();
            return PartialParseResult.partialParse(parseResult.getParseResult(), parseResult.getDiagnostic());
        }

        parseResult = parseConditional(queue.branchOff(), astFactory);
        if (parseResult.isSuccessful()) {
            queue.mergeBranch();
            return PartialParseResult.successfulParse(parseResult.getParseResult());
        } else if (parseResult.isPartial()) {
            queue.mergeBranch();
            return PartialParseResult.partialParse(parseResult.getParseResult(), parseResult.getDiagnostic());
        }
        parseResult = parseTryStatement(queue.branchOff(), astFactory);
        if (parseResult.isSuccessful()) {
            queue.mergeBranch();
            return PartialParseResult.successfulParse(parseResult.getParseResult());
        } else if (parseResult.isPartial()) {
            queue.mergeBranch();
            return PartialParseResult.partialParse(parseResult.getParseResult(), parseResult.getDiagnostic());
        }

        return PartialParseResult.unsuccessfulParse(new SyntaxDiagnostic(queue.peek(), "Not a statement!"));
    }

    @PartialParse
    public static PartialParseResult<ConditionalNode> parseConditional(TokenQueue queue, ASTNodeFactory astFactory) {
        ConditionalNodeImpl node = astFactory.createConditionalNode();
        List<SyntaxDiagnostic> diagnostics = new ArrayList<>();
        boolean isPartiallyParsed = false;

        ParseResult<ExpressionNode> parsedExpression = ExpressionParser.parseExpression(queue.branchOff(), astFactory);
        if (parsedExpression.isUnsuccessful()) {
            return PartialParseResult.partialParse(node, parsedExpression.getDiagnostic());
        }
        queue.mergeBranch();
        node.setCondition(parsedExpression.getParseResult());

        if (!SyntaxSet.LANGUAGE_ELEMENTS.get("?").matches(queue.peek())) {
            diagnostics.add(new SyntaxDiagnostic(queue.peek(), "Expected '?'!"));
            isPartiallyParsed = true;
        }
        queue.poll();

        if (SyntaxSet.LANGUAGE_ELEMENTS.get("{").matches(queue.poll())) {
            PartialParseResult<StatementsNode> parsedStatements = Parser.parseStatements(queue.branchOff(), astFactory);
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
        PartialParseResult<ConditionalNode> parsedElseBlock = parseElseBlock(queue.branchOff(), astFactory);
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
    public static PartialParseResult<ConditionalNode> parseElseBlock(TokenQueue queue, ASTNodeFactory astFactory) {
        List<SyntaxDiagnostic> diagnostics = new ArrayList<>();
        boolean isPartiallyParsed = false;

        if (!SyntaxSet.LANGUAGE_ELEMENTS.get("|").matches(queue.peek())) {
            return PartialParseResult.unsuccessfulParse(new SyntaxDiagnostic(queue.peek(), "Expected '|'!"));
        }
        queue.poll();

        if (SyntaxSet.LANGUAGE_ELEMENTS.get("{").matches(queue.peek())) {
            queue.poll();

            ConditionalNodeImpl node = astFactory.createConditionalNode();
            node.setConditionBranch(ConditionalNodeImpl.ConditionBranch.ELSE);
            PartialParseResult<StatementsNode> parsedStatements = Parser.parseStatements(queue.branchOff(), astFactory);
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


        PartialParseResult<ConditionalNode> parsedConditional = parseConditional(queue.branchOff(), astFactory);
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
                queue.peek(),
                "Expected '{' or an expression!"
        ));
    }

    @PartialParse
    public static PartialParseResult<ForLoopNode> parseForLoop(TokenQueue queue, ASTNodeFactory astFactory) {
        ForLoopNodeImpl node = astFactory.createForLoopNode();
        List<SyntaxDiagnostic> diagnostics = new ArrayList<>();
        boolean isPartiallyParsed = false;

        if (!SyntaxSet.LANGUAGE_ELEMENTS.get("for").matches(queue.peek())) {
            return PartialParseResult.unsuccessfulParse(new SyntaxDiagnostic(queue.peek(), "Expected 'for'!"));
        }
        queue.poll();

        ParseResult<Token> parsedIdentifier = AuxiliaryParser.parseIdentifier(queue.branchOff(), astFactory);
        if (parsedIdentifier.isSuccessful()) {
            queue.mergeBranch();
            node.setIdentifier(parsedIdentifier.getParseResult());
        } else {
            diagnostics.add(new SyntaxDiagnostic(queue.peek(), "Expected an identifier!"));
            isPartiallyParsed = true;
        }

        if (SyntaxSet.LANGUAGE_ELEMENTS.get("=").matches(queue.peek())) {
            queue.poll();
            ParseResult<ExpressionNode> parsedStartIndexExpression =
                    ExpressionParser.parseExpression(queue.branchOff(), astFactory);
            if (parsedStartIndexExpression.isSuccessful()) {
                queue.mergeBranch();
                node.setStartIndex(parsedStartIndexExpression.getParseResult());
            } else {
                diagnostics.add(new SyntaxDiagnostic(queue.peek(), "Expected an expression!"));
                isPartiallyParsed = true;
            }
        } else {
            diagnostics.add(new SyntaxDiagnostic(queue.poll(), "Expected '='!"));
            isPartiallyParsed = true;
        }

        if (SyntaxSet.LANGUAGE_ELEMENTS.get("..").matches(queue.peek())) {
            queue.poll();
            ParseResult<ExpressionNode> parsedEndIndexExpression =
                    ExpressionParser.parseExpression(queue.branchOff(), astFactory);
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
                    Parser.parseReassignment(queue.branchOff(), astFactory);
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
                    Parser.parseStatements(queue.branchOff(), astFactory);
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
    public static PartialParseResult<WhileLoopNode> parseWhileLoop(TokenQueue queue, ASTNodeFactory astFactory) {
        WhileLoopNodeImpl node = astFactory.createWhileLoopNode();
        List<SyntaxDiagnostic> diagnostics = new ArrayList<>();
        boolean isPartiallyParsed = false;

        if (!SyntaxSet.LANGUAGE_ELEMENTS.get("while").matches(queue.peek())) {
            return PartialParseResult.unsuccessfulParse(new SyntaxDiagnostic(queue.peek(), "Expected 'while'!"));
        }
        queue.poll();

        ParseResult<ExpressionNode> parsedExpression = ExpressionParser.parseExpression(queue.branchOff(), astFactory);
        if (parsedExpression.isSuccessful()) {
            queue.mergeBranch();
            node.setCondition(parsedExpression.getParseResult());
        } else {
            diagnostics.add(parsedExpression.getDiagnostic());
            isPartiallyParsed = true;
        }

        if (SyntaxSet.LANGUAGE_ELEMENTS.get("{").matches(queue.peek())) {
            queue.poll();
            PartialParseResult<StatementsNode> parsedStatements = Parser.parseStatements(queue.branchOff(), astFactory);
            if (parsedStatements.isSuccessful()) {
                queue.mergeBranch();
                node.setBody(parsedStatements.getParseResult());
            } else if (parsedStatements.isPartial()) {
                queue.mergeBranch();
                node.setBody(parsedStatements.getParseResult());
                diagnostics.add(parsedStatements.getDiagnostic());
                isPartiallyParsed = true;
            } else {
                diagnostics.add(new SyntaxDiagnostic(queue.peek(), "Expected a statement!"));
                isPartiallyParsed = true;

            }
            if (!SyntaxSet.LANGUAGE_ELEMENTS.get("}").matches(queue.peek())) {
                diagnostics.add(new SyntaxDiagnostic(queue.peek(), "Expected '}'!"));
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
    public static PartialParseResult<TryStatementNode> parseTryStatement(TokenQueue queue, ASTNodeFactory astFactory) {
        TryStatementNodeImpl node = astFactory.createTryStatementNode();
        List<InterceptStatementNode> interceptStatements = new LinkedList<>();
        List<SyntaxDiagnostic> diagnostics = new ArrayList<>();
        boolean isPartial = false;

        if (!SyntaxSet.LANGUAGE_ELEMENTS.get("try").matches(queue.peek())) {
            return PartialParseResult.unsuccessfulParse(new SyntaxDiagnostic(queue.poll(), "Not a statement!"));
        }
        queue.poll();

        PartialParseResult<StatementsNode> parsedBody = AuxiliaryParser.parseCodeBlock(queue.branchOff(), astFactory);
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
                parseInterceptStatement(queue.branchOff(), astFactory);
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
                parseInterceptStatements(queue.branchOff(), astFactory);
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
                parseEnsureStatement(queue.branchOff(), astFactory);
        if (parsedEnsureStatement.isUnsuccessful()) {
            diagnostics.add(parsedEnsureStatement.getDiagnostic());
            isPartial = true;
        } else {
            if (parsedEnsureStatement.isPartial()) {
                diagnostics.add(parsedEnsureStatement.getDiagnostic());
                isPartial = true;
            }
        }

        if (isPartial) {
            return PartialParseResult.partialParse(node, diagnostics.get(0));
        }

        return PartialParseResult.successfulParse(node);
    }

    @PartialParse
    public static PartialParseResult<List<InterceptStatementNode>> parseInterceptStatements(TokenQueue queue, ASTNodeFactory astFactory) {
        List<InterceptStatementNode> interceptStatements = new LinkedList<>();
        List<SyntaxDiagnostic> diagnostics = new ArrayList<>();
        boolean isPartial = false;

        if (!SyntaxSet.LANGUAGE_ELEMENTS.get(",").matches(queue.peek())) {
            return PartialParseResult.successfulParse(interceptStatements);
        }
        queue.poll();

        PartialParseResult<InterceptStatementNode> parsedInterceptStatement =
                parseInterceptStatement(queue.branchOff(), astFactory);

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


        ParseResult<List<InterceptStatementNode>> parsedInterceptStatements =
                parseInterceptStatements(queue.branchOff(), astFactory);
        if (parsedInterceptStatements.isUnsuccessful()) {
            diagnostics.add(parsedInterceptStatements.getDiagnostic());
            isPartial = true;
        } else {
            if (parsedInterceptStatement.isPartial()) {
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
    public static PartialParseResult<InterceptStatementNode> parseInterceptStatement(TokenQueue queue, ASTNodeFactory astFactory) {
        InterceptStatementNodeImpl node = astFactory.createInterceptStatementNode();
        List<SyntaxDiagnostic> diagnostics = new ArrayList<>();
        boolean isPartial = false;

        if (!SyntaxSet.LANGUAGE_ELEMENTS.get("intercept").matches(queue.peek())) {
            return PartialParseResult.unsuccessfulParse(new SyntaxDiagnostic(queue.peek(), "Not a statement!"));
        }
        queue.poll();

        PartialParseResult<List<IdentifierAccessNode>> parsedInterceptedExceptions =
                AuxiliaryParser.parseIdentifierAccesses(queue.branchOff(), astFactory);
        if (parsedInterceptedExceptions.isUnsuccessful()) {
            diagnostics.add(parsedInterceptedExceptions.getDiagnostic());
            isPartial = true;
        } else {
            if (parsedInterceptedExceptions.isPartial()) {
                diagnostics.add(parsedInterceptedExceptions.getDiagnostic());
                isPartial = true;
            }
            queue.mergeBranch();
            node.setInterceptedExceptions(parsedInterceptedExceptions.getParseResult());
        }

        if (!SyntaxSet.LANGUAGE_ELEMENTS.get(":").matches(queue.peek())) {
            diagnostics.add(new SyntaxDiagnostic(queue.poll(), "Expected ':'!"));
            isPartial = true;
        } else {
            queue.poll();
        }

        ParseResult<Token> parsedExceptionIdentifier = AuxiliaryParser.parseIdentifier(queue.branchOff(), astFactory);
        if (parsedExceptionIdentifier.isUnsuccessful()) {
            diagnostics.add(parsedExceptionIdentifier.getDiagnostic());
            isPartial = true;
        } else {
            queue.mergeBranch();
            node.setRaisedExceptionIdentifier(parsedExceptionIdentifier.getParseResult());
        }

        PartialParseResult<StatementsNode> parsedBody = AuxiliaryParser.parseCodeBlock(queue.branchOff(), astFactory);
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
    public static PartialParseResult<EnsureStatementNode> parseEnsureStatement(TokenQueue queue, ASTNodeFactory astFactory) {
        EnsureStatementNodeImpl node = astFactory.createEnsureStatementNode();
        List<SyntaxDiagnostic> diagnostics = new ArrayList<>();
        boolean isPartial = false;

        if (!SyntaxSet.LANGUAGE_ELEMENTS.get("ensure").matches(queue.peek())) {
            return PartialParseResult.unsuccessfulParse(new SyntaxDiagnostic(queue.poll(), "Not a statement!"));
        }
        queue.poll();

        PartialParseResult<StatementsNode> parsedBody = AuxiliaryParser.parseCodeBlock(queue.branchOff(), astFactory);
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
}
