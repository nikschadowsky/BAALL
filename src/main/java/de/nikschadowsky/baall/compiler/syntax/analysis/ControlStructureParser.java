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

import java.util.ArrayList;
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

        if (!Parser.TERMINAL_MAP.get("?").symbolMatches(queue.peek())) {
            diagnostics.add(new SyntaxDiagnostic(queue.peek(), "Expected '?'!"));
            isPartiallyParsed = true;
        }
        queue.poll();

        if (Parser.TERMINAL_MAP.get("{").symbolMatches(queue.poll())) {
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

            if (!Parser.TERMINAL_MAP.get("}").symbolMatches(queue.peek())) {
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
            queue.skipOver(Parser.TERMINAL_MAP.get("}"));
            return PartialParseResult.partialParse(node, diagnostics.get(0));
        }
        return PartialParseResult.successfulParse(node);
    }

    @PartialParse
    public static PartialParseResult<ConditionalNode> parseElseBlock(TokenQueue queue, ASTNodeFactory astFactory) {
        List<SyntaxDiagnostic> diagnostics = new ArrayList<>();
        boolean isPartiallyParsed = false;

        if (!Parser.TERMINAL_MAP.get("|").symbolMatches(queue.peek())) {
            return PartialParseResult.unsuccessfulParse(new SyntaxDiagnostic(queue.peek(), "Expected '|'!"));
        }
        queue.poll();

        if (Parser.TERMINAL_MAP.get("{").symbolMatches(queue.peek())) {
            queue.poll();

            ConditionalNodeImpl node = astFactory.createConditionalNode();
            node.setConditionBranch(ConditionalNodeImpl.ConditionBranch.ELSE);
            PartialParseResult<StatementsNode> parsedStatements = Parser.parseStatements(queue.branchOff(), astFactory);
            if (parsedStatements.isSuccessful()) {
                queue.mergeBranch();

                node.setThenBlock(parsedStatements.getParseResult());
                if (Parser.TERMINAL_MAP.get("}").symbolMatches(queue.peek())) {
                    queue.poll();
                    return PartialParseResult.successfulParse(node);
                }
                return PartialParseResult.partialParse(node, new SyntaxDiagnostic(queue.poll(), "Expected '}'!"));
            }
            queue.skipOver(Parser.TERMINAL_MAP.get("}"));
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

        if (!Parser.TERMINAL_MAP.get("for").symbolMatches(queue.peek())) {
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

        if (Parser.TERMINAL_MAP.get("=").symbolMatches(queue.peek())) {
            queue.poll();
            ParseResult<ExpressionNode> parsedStartIndexExpression = ExpressionParser.parseExpression(queue.branchOff(), astFactory);
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

        if (Parser.TERMINAL_MAP.get("..").symbolMatches(queue.peek())) {
            queue.poll();
            ParseResult<ExpressionNode> parsedEndIndexExpression = ExpressionParser.parseExpression(queue.branchOff(), astFactory);
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

        if (Parser.TERMINAL_MAP.get("::").symbolMatches(queue.peek())) {
            queue.poll();
            node.setHasOptionalStepperStatement(true);
            ParseResult<ReassignmentNode> parsedOptionalForStepper = Parser.parseReassignment(queue.branchOff(), astFactory);
            if (parsedOptionalForStepper.isSuccessful()) {
                queue.mergeBranch();
                node.setOptionalStepperStatement(parsedOptionalForStepper.getParseResult());
            } else {
                diagnostics.add(new SyntaxDiagnostic("Expected a reassignment statement!"));
                isPartiallyParsed = true;
            }
        }

        if (Parser.TERMINAL_MAP.get("{").symbolMatches(queue.peek())) {
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

            if (!Parser.TERMINAL_MAP.get("}").symbolMatches(queue.peek())) {
                diagnostics.add(new SyntaxDiagnostic(queue.poll(), "Expected '}'!"));
                isPartiallyParsed = true;
            }
            queue.poll();
        } else {
            diagnostics.add(new SyntaxDiagnostic(queue.poll(), "Expected '{'!"));
            isPartiallyParsed = true;
        }

        if (isPartiallyParsed) {
            queue.skipOver(Parser.TERMINAL_MAP.get("}"));
            return PartialParseResult.partialParse(node, diagnostics.get(0));
        }
        return PartialParseResult.successfulParse(node);
    }

    @PartialParse
    public static PartialParseResult<WhileLoopNode> parseWhileLoop(TokenQueue queue, ASTNodeFactory astFactory) {
        WhileLoopNodeImpl node = astFactory.createWhileLoopNode();
        List<SyntaxDiagnostic> diagnostics = new ArrayList<>();
        boolean isPartiallyParsed = false;

        if (!Parser.TERMINAL_MAP.get("while").symbolMatches(queue.peek())) {
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

        if (Parser.TERMINAL_MAP.get("{").symbolMatches(queue.peek())) {
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
            if (!Parser.TERMINAL_MAP.get("}").symbolMatches(queue.peek())) {
                diagnostics.add(new SyntaxDiagnostic(queue.peek(), "Expected '}'!"));
                isPartiallyParsed = true;
            }
            queue.poll();

        } else {
            diagnostics.add(new SyntaxDiagnostic(queue.poll(), "Expected '{'!"));
            isPartiallyParsed = true;
        }
        if (isPartiallyParsed) {
            queue.skipOver(Parser.TERMINAL_MAP.get("}"));
            return PartialParseResult.partialParse(node, diagnostics.get(0));
        }

        return PartialParseResult.successfulParse(node);
    }
}
