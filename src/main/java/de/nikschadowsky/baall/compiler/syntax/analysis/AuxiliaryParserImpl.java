package de.nikschadowsky.baall.compiler.syntax.analysis;

import de.nikschadowsky.baall.compiler.symbol.Token;
import de.nikschadowsky.baall.compiler.symbol.TokenQueue;
import de.nikschadowsky.baall.compiler.syntax.analysis.result.ParseResult;
import de.nikschadowsky.baall.compiler.syntax.analysis.result.PartialParseResult;
import de.nikschadowsky.baall.compiler.syntax.error.SyntaxDiagnostic;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.ASTNodeFactory;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.expression.ExpressionNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.program.StatementsNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.typing.TypeNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.typing.TypeNodeImpl;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.IdentifierAccessNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.IdentifierAccessNodeImpl;
import de.nikschadowsky.baall.compiler.syntax.util.CompleteParse;
import de.nikschadowsky.baall.compiler.syntax.util.PartialParse;
import de.nikschadowsky.baall.compiler.util.LanguageElement;
import de.nikschadowsky.baall.compiler.util.SyntaxSet;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @since 11.08.2024
 */
public class AuxiliaryParserImpl implements AuxiliaryParser {

    private final ProgramParser programParser;
    private final ASTNodeFactory astFactory;

    public AuxiliaryParserImpl(ProgramParser programParser, ASTNodeFactory astFactory) {
        this.programParser = programParser;
        this.astFactory = astFactory;
    }

    @CompleteParse
    @Override
    public ParseResult<List<Map.Entry<TypeNode, Token>>> parseFieldDeclarations(TokenQueue queue) {
        if (SyntaxSet.LANGUAGE_ELEMENTS.get(",").matches(queue.peek())) {
            queue.poll();
            ParseResult<Map.Entry<TypeNode, Token>> parsedFieldDeclaration =
                    parseFieldDeclaration(queue.branchOff());
            if (parsedFieldDeclaration.isSuccessful()) {
                queue.mergeBranch(parsedFieldDeclaration.getTokenQueueId());
                ParseResult<List<Map.Entry<TypeNode, Token>>> parsedFunctionParameterList =
                        parseFieldDeclarations(queue.branchOff());
                if (parsedFunctionParameterList.isSuccessful()) {
                    queue.mergeBranch(parsedFunctionParameterList.getTokenQueueId());

                    List<Map.Entry<TypeNode, Token>> parameterFields = new LinkedList<>();
                    parameterFields.add(parsedFieldDeclaration.getParseResult());
                    parameterFields.addAll(parsedFunctionParameterList.getParseResult());
                    return ParseResult.successfulParse(parameterFields, queue.getId());
                }
                return ParseResult.unsuccessfulParse(parsedFieldDeclaration.getDiagnostic(), queue.getId());
            }
            return ParseResult.unsuccessfulParse(parsedFieldDeclaration.getDiagnostic(), queue.getId());
        }
        return ParseResult.successfulParse(new LinkedList<>(), queue.getId());
    }

    @CompleteParse
    @Override
    public ParseResult<Map.Entry<TypeNode, Token>> parseFieldDeclaration(TokenQueue queue) {
        ParseResult<TypeNode> parsedTypeNode = parseType(queue.branchOff());
        if (parsedTypeNode.isSuccessful()) {
            queue.mergeBranch(parsedTypeNode.getTokenQueueId());
            if (SyntaxSet.LANGUAGE_ELEMENTS.get(":").matches(queue.peek())) {
                queue.poll();
                ParseResult<Token> parsedIdentifier = parseIdentifier(queue.branchOff());
                if (parsedIdentifier.isSuccessful()) {
                    queue.mergeBranch(parsedIdentifier.getTokenQueueId());
                    return ParseResult.successfulParse(
                            Map.entry(
                                    parsedTypeNode.getParseResult(),
                                    parsedIdentifier.getParseResult()
                            ), queue.getId()
                    );
                }
                return ParseResult.unsuccessfulParse(parsedIdentifier.getDiagnostic(), queue.getId());
            }
            return ParseResult.unsuccessfulParse(new SyntaxDiagnostic(queue.poll(), "Expected ':'!"), queue.getId());
        }
        return ParseResult.unsuccessfulParse(parsedTypeNode.getDiagnostic(), queue.getId());
    }

    @CompleteParse
    @Override
    public ParseResult<List<ExpressionNode>> parseArgumentList(TokenQueue queue) {
        if (SyntaxSet.LANGUAGE_ELEMENTS.get(",").matches(queue.peek())) {
            queue.poll();
            ParseResult<ExpressionNode> parsedExpression =
                    programParser.getExpressionParser().parseExpression(queue.branchOff());
            if (parsedExpression.isSuccessful()) {
                queue.mergeBranch(parsedExpression.getTokenQueueId());
                ParseResult<List<ExpressionNode>> parsedArguments = parseArgumentList(queue.branchOff());
                if (parsedArguments.isSuccessful()) {
                    queue.mergeBranch(parsedArguments.getTokenQueueId());

                    List<ExpressionNode> arguments = new LinkedList<>();
                    arguments.add(parsedExpression.getParseResult());
                    arguments.addAll(parsedArguments.getParseResult());
                    return ParseResult.successfulParse(arguments, queue.getId());
                }
                return ParseResult.unsuccessfulParse(parsedArguments.getDiagnostic(), queue.getId());
            }
            return ParseResult.unsuccessfulParse(parsedExpression.getDiagnostic(), queue.getId());
        }
        return ParseResult.successfulParse(new LinkedList<>(), queue.getId());
    }

    @CompleteParse
    @Override
    public ParseResult<Token> parseBinaryOperator(TokenQueue queue) {
        // todo syntax set should differentiate between unary and binary operators
        Set<LanguageElement> validBinaryOperators =
                Stream.of("+", "-", "*", "/", "%", "&", "|", "^", "<<", ">>", "==", "<", ">", "&&", "||")
                      .map(SyntaxSet.LANGUAGE_ELEMENTS::get)
                      .collect(Collectors.toSet());

        if (validBinaryOperators.stream().anyMatch(e -> e.matches(queue.peek()))) {
            return ParseResult.successfulParse(queue.poll(), queue.getId());
        }
        return ParseResult.unsuccessfulParse(
                new SyntaxDiagnostic(queue.poll(), "Expected an operator!"),
                queue.getId()
        );
    }

    @CompleteParse
    @Override
    public ParseResult<Token> parseUnaryOperator(TokenQueue queue) {
        Set<LanguageElement> validShorthandOperators =
                Stream.of("++", "--")
                      .map(SyntaxSet.LANGUAGE_ELEMENTS::get)
                      .collect(Collectors.toSet());

        if (validShorthandOperators.stream().anyMatch(e -> e.matches(queue.peek()))) {
            return ParseResult.successfulParse(queue.poll(), queue.getId());
        }
        return ParseResult.unsuccessfulParse(
                new SyntaxDiagnostic(queue.poll(), "Expected a unary operator!"),
                queue.getId()
        );
    }

    @CompleteParse
    @Override
    public ParseResult<Token> parseVariableAssignmentOperator(TokenQueue queue) {
        Set<LanguageElement> validShorthandOperators =
                Stream.of("=", "+=", "-=", "*=", "/=", "&=", "|=", "^=")
                      .map(SyntaxSet.LANGUAGE_ELEMENTS::get)
                      .collect(Collectors.toSet());

        if (validShorthandOperators.stream().anyMatch(e -> e.matches(queue.peek()))) {
            return ParseResult.successfulParse(queue.poll(), queue.getId());
        }
        return ParseResult.unsuccessfulParse(
                new SyntaxDiagnostic(
                        queue.poll(),
                        "Expected an assignment operator!"
                ), queue.getId()
        );
    }

    @CompleteParse
    @Override
    public ParseResult<Token> parsePrefixOperator(TokenQueue queue) {
        Set<LanguageElement> validShorthandOperators =
                Stream.of("+", "-", "!")
                      .map(SyntaxSet.LANGUAGE_ELEMENTS::get)
                      .collect(Collectors.toSet());

        if (validShorthandOperators.stream().anyMatch(e -> e.matches(queue.peek()))) {
            return ParseResult.successfulParse(queue.poll(), queue.getId());
        }
        return ParseResult.unsuccessfulParse(
                new SyntaxDiagnostic(queue.poll(), "Expected a prefix operator!"),
                queue.getId()
        );
    }

    @CompleteParse
    @Override
    public ParseResult<Token> parseIdentifier(TokenQueue queue) {
        if (SyntaxSet.LANGUAGE_ELEMENTS.get("identifier_primitive").matches(queue.peek())) {
            return ParseResult.successfulParse(queue.poll(), queue.getId());
        }
        return ParseResult.unsuccessfulParse(
                new SyntaxDiagnostic(queue.poll(), "Expected an identifier!"),
                queue.getId()
        );
    }

    @CompleteParse
    @Override
    public ParseResult<TypeNode> parseType(TokenQueue queue) {
        TypeNodeImpl node = astFactory.createTypeNode();

        Token nextToken = queue.peek();
        if (SyntaxSet.SIMPLE_TYPES.stream().anyMatch(e -> e.matches(nextToken))) {
            node.setType(queue.poll());
            node.setNoneSafe(true);
        } else {
            ParseResult<Token> parsedIdentifier = parseIdentifier(queue.branchOff());
            if (parsedIdentifier.isSuccessful()) {
                queue.mergeBranch(parsedIdentifier.getTokenQueueId());
                node.setType(parsedIdentifier.getParseResult());
            } else {
                return ParseResult.unsuccessfulParse(
                        new SyntaxDiagnostic(queue.poll(), "Expected a type!"),
                        queue.getId()
                );
            }
            // todo extend tests using types
            if (SyntaxSet.LANGUAGE_ELEMENTS.get("!").matches(queue.peek())) {
                queue.poll();
                node.setNoneSafe(true);
            } else {
                node.setNoneSafe(false);
            }
        }
        ParseResult<List<ExpressionNode>> parsedArrayTypeDefinitions =
                parseArrayTypeDefinition(queue.branchOff());
        if (parsedArrayTypeDefinitions.isSuccessful()) {
            queue.mergeBranch(parsedArrayTypeDefinitions.getTokenQueueId());

            node.setArrayDimensionDefinitions(parsedArrayTypeDefinitions.getParseResult());
            return ParseResult.successfulParse(node, queue.getId());
        }
        return ParseResult.unsuccessfulParse(parsedArrayTypeDefinitions.getDiagnostic(), queue.getId());
    }

    @CompleteParse
    @Override
    public ParseResult<List<ExpressionNode>> parseArrayTypeDefinition(TokenQueue queue) {
        if (!SyntaxSet.LANGUAGE_ELEMENTS.get("[").matches(queue.peek())) {
            return ParseResult.successfulParse(new LinkedList<>(), queue.getId());
        }
        queue.poll();

        ParseResult<ExpressionNode> parsedExpression =
                programParser.getExpressionParser().parseExpression(queue.branchOff());

        ExpressionNode expressionNode = null;
        if (!SyntaxSet.LANGUAGE_ELEMENTS.get("]").matches(queue.peek())) {
            if (parsedExpression.isUnsuccessful()) {
                return ParseResult.unsuccessfulParse(parsedExpression.getDiagnostic(), queue.getId());
            }
            queue.mergeBranch(parsedExpression.getTokenQueueId());
            expressionNode = parsedExpression.getParseResult();
        }
        if (SyntaxSet.LANGUAGE_ELEMENTS.get("]").matches(queue.peek())) {
            queue.poll();
            ParseResult<List<ExpressionNode>> parsedArrayTypeDefinitions =
                    parseArrayTypeDefinition(queue.branchOff());
            if (parsedArrayTypeDefinitions.isSuccessful()) {
                queue.mergeBranch(parsedArrayTypeDefinitions.getTokenQueueId());

                List<ExpressionNode> arrayTypeDefinitions = new LinkedList<>();
                arrayTypeDefinitions.add(expressionNode);
                arrayTypeDefinitions.addAll(parsedArrayTypeDefinitions.getParseResult());

                return ParseResult.successfulParse(arrayTypeDefinitions, queue.getId());
            }
            return ParseResult.unsuccessfulParse(parsedArrayTypeDefinitions.getDiagnostic(), queue.getId());
        }
        return ParseResult.unsuccessfulParse(new SyntaxDiagnostic(queue.poll(), "Expected ']'!"), queue.getId());
    }

    @CompleteParse
    @Override
    public ParseResult<List<ExpressionNode>> parseArrayIndexInformation(TokenQueue queue) {
        if (!SyntaxSet.LANGUAGE_ELEMENTS.get("[").matches(queue.peek())) {
            return ParseResult.successfulParse(new LinkedList<>(), queue.getId());
        }
        queue.poll();

        ParseResult<ExpressionNode> parsedExpression =
                programParser.getExpressionParser().parseExpression(queue.branchOff());
        if (parsedExpression.isSuccessful()) {
            queue.mergeBranch(parsedExpression.getTokenQueueId());
            if (SyntaxSet.LANGUAGE_ELEMENTS.get("]").matches(queue.peek())) {
                queue.poll();
                ParseResult<List<ExpressionNode>> parsedArrayTypeDefinitions =
                        parseArrayIndexInformation(queue.branchOff());
                if (parsedArrayTypeDefinitions.isSuccessful()) {
                    queue.mergeBranch(parsedArrayTypeDefinitions.getTokenQueueId());

                    List<ExpressionNode> arrayDimensions = new LinkedList<>();
                    arrayDimensions.add(parsedExpression.getParseResult());
                    arrayDimensions.addAll(parsedArrayTypeDefinitions.getParseResult());

                    return ParseResult.successfulParse(arrayDimensions, queue.getId());
                }
                return ParseResult.unsuccessfulParse(parsedArrayTypeDefinitions.getDiagnostic(), queue.getId());
            }
            return ParseResult.unsuccessfulParse(new SyntaxDiagnostic(queue.poll(), "Expected ']'!"), queue.getId());
        }
        queue.skipOver(SyntaxSet.LANGUAGE_ELEMENTS.get("]"));
        return ParseResult.unsuccessfulParse(parsedExpression.getDiagnostic(), queue.getId());
    }

    @CompleteParse
    @Override
    public ParseResult<IdentifierAccessNode> parseIdentifierAccess(TokenQueue queue) {
        IdentifierAccessNodeImpl node = astFactory.createIdentifierAccessNode();
        ParseResult<Token> parsedIdentifier = parseIdentifier(queue.branchOff());
        if (parsedIdentifier.isSuccessful()) {
            queue.mergeBranch(parsedIdentifier.getTokenQueueId());
            node.setIdentifier(parsedIdentifier.getParseResult());
            ParseResult<List<ExpressionNode>> parsedOptionalArrayIndices =
                    parseArrayIndexInformation(queue.branchOff());
            if (parsedOptionalArrayIndices.isSuccessful()) {
                queue.mergeBranch(parsedOptionalArrayIndices.getTokenQueueId());
                node.setArrayIndexes(parsedOptionalArrayIndices.getParseResult());
                return ParseResult.successfulParse(node, queue.getId());
            }
            return ParseResult.unsuccessfulParse(parsedOptionalArrayIndices.getDiagnostic(), queue.getId());
        }
        return ParseResult.unsuccessfulParse(parsedIdentifier.getDiagnostic(), queue.getId());
    }

    @PartialParse
    @Override
    public PartialParseResult<List<IdentifierAccessNode>> parseAdditionalIdentifierAccesses(TokenQueue queue) {
        if (SyntaxSet.LANGUAGE_ELEMENTS.get(",").matches(queue.peek())) {
            queue.poll();
            ParseResult<IdentifierAccessNode> parsedIdentifierAccess =
                    parseIdentifierAccess(queue.branchOff());
            if (parsedIdentifierAccess.isSuccessful()) {
                queue.mergeBranch(parsedIdentifierAccess.getTokenQueueId());
                PartialParseResult<List<IdentifierAccessNode>> parsedIdentifierAccesses =
                        parseAdditionalIdentifierAccesses(queue.branchOff());
                List<IdentifierAccessNode> identifierAccessNodes = new LinkedList<>();
                identifierAccessNodes.add(parsedIdentifierAccess.getParseResult());

                if (parsedIdentifierAccesses.isSuccessful()) {
                    queue.mergeBranch(parsedIdentifierAccesses.getTokenQueueId());
                    identifierAccessNodes.addAll(parsedIdentifierAccesses.getParseResult());
                    return PartialParseResult.successfulParse(identifierAccessNodes, queue.getId());
                } else if (parsedIdentifierAccesses.isPartial()) {
                    return PartialParseResult.partialParse(
                            identifierAccessNodes,
                            parsedIdentifierAccesses.getDiagnostic(), queue.getId()
                    );
                }
                return PartialParseResult.unsuccessfulParse(parsedIdentifierAccesses.getDiagnostic(), queue.getId());
            }

            return PartialParseResult.partialParse(
                    new LinkedList<>(),
                    parsedIdentifierAccess.getDiagnostic(),
                    queue.getId()
            );
        }
        // epsilon
        return PartialParseResult.successfulParse(new LinkedList<>(), queue.getId());
    }

    @PartialParse
    @Override
    public PartialParseResult<StatementsNode> parseCodeBlock(TokenQueue queue) {
        List<SyntaxDiagnostic> diagnostics = new ArrayList<>();
        boolean isPartial = false;

        if (!SyntaxSet.LANGUAGE_ELEMENTS.get("{").matches(queue.peek())) {
            return PartialParseResult.unsuccessfulParse(
                    new SyntaxDiagnostic(queue.poll(), "Expected '{'!"),
                    queue.getId()
            );
        }
        queue.poll();

        PartialParseResult<StatementsNode> parsedStatements =
                programParser.getStatementParser().parseStatements(queue.branchOff());
        if (parsedStatements.isUnsuccessful()) {
            return PartialParseResult.unsuccessfulParse(parsedStatements.getDiagnostic(), queue.getId());
        } else {
            if (parsedStatements.isPartial()) {
                diagnostics.add(parsedStatements.getDiagnostic());
                isPartial = true;
            }
            queue.mergeBranch(parsedStatements.getTokenQueueId());
        }

        if (!SyntaxSet.LANGUAGE_ELEMENTS.get("}").matches(queue.peek())) {
            diagnostics.add(new SyntaxDiagnostic(queue.poll(), "Expected '}'!"));
            isPartial = true;
            queue.skipOver(SyntaxSet.LANGUAGE_ELEMENTS.get("}"));
        }
        queue.poll();

        if (isPartial) {
            return PartialParseResult.partialParse(
                    parsedStatements.getParseResult(),
                    diagnostics.get(0),
                    queue.getId()
            );
        }

        return PartialParseResult.successfulParse(parsedStatements.getParseResult(), queue.getId());
    }
}
