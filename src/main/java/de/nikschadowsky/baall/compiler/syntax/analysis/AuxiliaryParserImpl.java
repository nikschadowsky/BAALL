package de.nikschadowsky.baall.compiler.syntax.analysis;

import de.nikschadowsky.baall.compiler.symbol.Token;
import de.nikschadowsky.baall.compiler.symbol.TokenQueue;
import de.nikschadowsky.baall.compiler.syntax.analysis.result.ParseResult;
import de.nikschadowsky.baall.compiler.syntax.analysis.result.PartialParseResult;
import de.nikschadowsky.baall.compiler.syntax.error.SyntaxDiagnostic;
import de.nikschadowsky.baall.compiler.syntax.util.CompleteParse;
import de.nikschadowsky.baall.compiler.syntax.util.PartialParse;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.ASTNodeFactory;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.expression.ExpressionNode;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.program.StatementsNode;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.typing.TypeNode;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.typing.TypeNodeImpl;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.value.IdentifierAccessNode;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.value.IdentifierAccessNodeImpl;
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
                queue.mergeBranch();
                ParseResult<List<Map.Entry<TypeNode, Token>>> parsedFunctionParameterList =
                        parseFieldDeclarations(queue.branchOff());
                if (parsedFunctionParameterList.isSuccessful()) {
                    queue.mergeBranch();

                    List<Map.Entry<TypeNode, Token>> parameterFields = new LinkedList<>();
                    parameterFields.add(parsedFieldDeclaration.getParseResult());
                    parameterFields.addAll(parsedFunctionParameterList.getParseResult());
                    return ParseResult.successfulParse(parameterFields);
                }
                return ParseResult.unsuccessfulParse(parsedFieldDeclaration.getDiagnostic());
            }
            return ParseResult.unsuccessfulParse(parsedFieldDeclaration.getDiagnostic());
        }
        return ParseResult.successfulParse(new LinkedList<>());
    }

    @CompleteParse
    @Override
    public ParseResult<Map.Entry<TypeNode, Token>> parseFieldDeclaration(TokenQueue queue) {
        ParseResult<TypeNode> parsedTypeNode = parseType(queue.branchOff());
        if (parsedTypeNode.isSuccessful()) {
            queue.mergeBranch();
            if (SyntaxSet.LANGUAGE_ELEMENTS.get(":").matches(queue.peek())) {
                queue.poll();
                ParseResult<Token> parsedIdentifier = parseIdentifier(queue.branchOff());
                if (parsedIdentifier.isSuccessful()) {
                    queue.mergeBranch();
                    return ParseResult.successfulParse(Map.entry(
                            parsedTypeNode.getParseResult(),
                            parsedIdentifier.getParseResult()
                    ));
                }
                return ParseResult.unsuccessfulParse(parsedIdentifier.getDiagnostic());
            }
            return ParseResult.unsuccessfulParse(new SyntaxDiagnostic(queue.poll(), "Expected ':'!"));
        }
        return ParseResult.unsuccessfulParse(parsedTypeNode.getDiagnostic());
    }

    @CompleteParse
    @Override
    public ParseResult<List<ExpressionNode>> parseArgumentList(TokenQueue queue) {
        if (SyntaxSet.LANGUAGE_ELEMENTS.get(",").matches(queue.peek())) {
            queue.poll();
            ParseResult<ExpressionNode> parsedExpression =
                    programParser.getExpressionParser().parseExpression(queue.branchOff());
            if (parsedExpression.isSuccessful()) {
                queue.mergeBranch();
                ParseResult<List<ExpressionNode>> parsedArguments = parseArgumentList(queue.branchOff());
                if (parsedArguments.isSuccessful()) {
                    queue.mergeBranch();

                    List<ExpressionNode> arguments = new LinkedList<>();
                    arguments.add(parsedExpression.getParseResult());
                    arguments.addAll(parsedArguments.getParseResult());
                    return ParseResult.successfulParse(arguments);
                }
                return ParseResult.unsuccessfulParse(parsedArguments.getDiagnostic());
            }
            return ParseResult.unsuccessfulParse(parsedExpression.getDiagnostic());
        }
        return ParseResult.successfulParse(new LinkedList<>());
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
            return ParseResult.successfulParse(queue.poll());
        }
        return ParseResult.unsuccessfulParse(new SyntaxDiagnostic(queue.peek(), "Expected an operator!"));
    }

    @CompleteParse
    @Override
    public ParseResult<Token> parseUnaryOperator(TokenQueue queue) {
        Set<LanguageElement> validShorthandOperators =
                Stream.of("++", "--")
                      .map(SyntaxSet.LANGUAGE_ELEMENTS::get)
                      .collect(Collectors.toSet());

        if (validShorthandOperators.stream().anyMatch(e -> e.matches(queue.peek()))) {
            return ParseResult.successfulParse(queue.poll());
        }
        return ParseResult.unsuccessfulParse(new SyntaxDiagnostic(queue.peek(), "Expected a unary operator!"));
    }

    @CompleteParse
    @Override
    public ParseResult<Token> parseShorthandOperator(TokenQueue queue) {
        Set<LanguageElement> validShorthandOperators =
                Stream.of("=", ":=", "+=", "-=", "*=", "/=", "&=", "|=", "^=")
                      .map(SyntaxSet.LANGUAGE_ELEMENTS::get)
                      .collect(Collectors.toSet());

        if (validShorthandOperators.stream().anyMatch(e -> e.matches(queue.peek()))) {
            return ParseResult.successfulParse(queue.poll());
        }
        return ParseResult.unsuccessfulParse(new SyntaxDiagnostic(
                queue.peek(),
                "Expected a assignment operator!"
        ));
    }

    @CompleteParse
    @Override
    public ParseResult<Token> parseIdentifier(TokenQueue queue) {
        if (SyntaxSet.LANGUAGE_ELEMENTS.get("identifier_primitive").matches(queue.peek())) {
            return ParseResult.successfulParse(queue.poll());
        }
        return ParseResult.unsuccessfulParse(new SyntaxDiagnostic(queue.poll(), "Expected an identifier!"));
    }

    @CompleteParse
    @Override
    public ParseResult<TypeNode> parseType(TokenQueue queue) {
        TypeNodeImpl node = astFactory.createTypeNode();
        ParseResult<Token> parsedSimpleType = parseSimpleType(queue.branchOff());
        if (parsedSimpleType.isSuccessful()) {
            queue.mergeBranch();

            node.setType(parsedSimpleType.getParseResult());
            ParseResult<List<ExpressionNode>> parsedArrayTypeDefinitions =
                    parseArrayTypeDefinition(queue.branchOff());
            if (parsedArrayTypeDefinitions.isSuccessful()) {
                queue.mergeBranch();

                node.setArrayDimensionDefinitions(parsedArrayTypeDefinitions.getParseResult());
                return ParseResult.successfulParse(node);
            }
            return ParseResult.unsuccessfulParse(parsedArrayTypeDefinitions.getDiagnostic());
        }
        return ParseResult.unsuccessfulParse(new SyntaxDiagnostic(queue.peek(), "Expected a type!"));
    }

    @CompleteParse
    @Override
    public ParseResult<Token> parseSimpleType(TokenQueue queue) {
        Token nextToken = queue.peek();

        if (SyntaxSet.SIMPLE_TYPES.stream().anyMatch(e -> e.matches(nextToken))) {
            return ParseResult.successfulParse(queue.poll());
        } else {
            // check if type is identifier
            ParseResult<Token> parsedIdentifier = parseIdentifier(queue.branchOff());
            if (parsedIdentifier.isSuccessful()) {
                queue.mergeBranch();
                return ParseResult.successfulParse(parsedIdentifier.getParseResult());
            }
        }
        return ParseResult.unsuccessfulParse(new SyntaxDiagnostic(queue.peek(), "Expected a type!"));
    }

    @CompleteParse
    @Override
    public ParseResult<List<ExpressionNode>> parseArrayTypeDefinition(TokenQueue queue) {
        if (!SyntaxSet.LANGUAGE_ELEMENTS.get("[").matches(queue.peek())) {
            return ParseResult.successfulParse(new LinkedList<>());
        }
        queue.poll();

        ParseResult<ExpressionNode> parsedExpression =
                programParser.getExpressionParser().parseExpression(queue.branchOff());

        ExpressionNode expressionNode = null;
        if (!SyntaxSet.LANGUAGE_ELEMENTS.get("]").matches(queue.peek())) {
            if (parsedExpression.isUnsuccessful()) {
                return ParseResult.unsuccessfulParse(parsedExpression.getDiagnostic());
            }
            queue.mergeBranch();
            expressionNode = parsedExpression.getParseResult();
        }
        if (SyntaxSet.LANGUAGE_ELEMENTS.get("]").matches(queue.peek())) {
            queue.poll();
            ParseResult<List<ExpressionNode>> parsedArrayTypeDefinitions =
                    parseArrayTypeDefinition(queue.branchOff());
            if (parsedArrayTypeDefinitions.isSuccessful()) {
                queue.mergeBranch();

                List<ExpressionNode> arrayTypeDefinitions = new LinkedList<>();
                arrayTypeDefinitions.add(expressionNode);
                arrayTypeDefinitions.addAll(parsedArrayTypeDefinitions.getParseResult());

                return ParseResult.successfulParse(arrayTypeDefinitions);
            }
            return ParseResult.unsuccessfulParse(parsedArrayTypeDefinitions.getDiagnostic());
        }
        return ParseResult.unsuccessfulParse(new SyntaxDiagnostic(queue.poll(), "Expected ']'!"));
    }

    @CompleteParse
    @Override
    public ParseResult<List<ExpressionNode>> parseArrayIndexInformation(TokenQueue queue) {
        if (!SyntaxSet.LANGUAGE_ELEMENTS.get("[").matches(queue.peek())) {
            return ParseResult.successfulParse(new LinkedList<>());
        }
        queue.poll();

        ParseResult<ExpressionNode> parsedExpression =
                programParser.getExpressionParser().parseExpression(queue.branchOff());
        if (parsedExpression.isSuccessful()) {
            queue.mergeBranch();
            if (SyntaxSet.LANGUAGE_ELEMENTS.get("]").matches(queue.peek())) {
                queue.poll();
                ParseResult<List<ExpressionNode>> parsedArrayTypeDefinitions =
                        parseArrayIndexInformation(queue.branchOff());
                if (parsedArrayTypeDefinitions.isSuccessful()) {
                    queue.mergeBranch();

                    List<ExpressionNode> arrayDimensions = new LinkedList<>();
                    arrayDimensions.add(parsedExpression.getParseResult());
                    arrayDimensions.addAll(parsedArrayTypeDefinitions.getParseResult());

                    return ParseResult.successfulParse(arrayDimensions);
                }
                return ParseResult.unsuccessfulParse(parsedArrayTypeDefinitions.getDiagnostic());
            }
            return ParseResult.unsuccessfulParse(new SyntaxDiagnostic(queue.poll(), "Expected ']'!"));
        }
        queue.skipOver(SyntaxSet.LANGUAGE_ELEMENTS.get("]"));
        return ParseResult.unsuccessfulParse(parsedExpression.getDiagnostic());
    }

    @CompleteParse
    @Override
    public ParseResult<IdentifierAccessNode> parseIdentifierAccess(TokenQueue queue) {
        IdentifierAccessNodeImpl node = astFactory.createIdentifierAccessNode();
        ParseResult<Token> parsedIdentifier = parseIdentifier(queue.branchOff());
        if (parsedIdentifier.isSuccessful()) {
            queue.mergeBranch();
            node.setIdentifier(parsedIdentifier.getParseResult());
            ParseResult<List<ExpressionNode>> parsedOptionalArrayIndices =
                    parseArrayIndexInformation(queue.branchOff());
            if (parsedOptionalArrayIndices.isSuccessful()) {
                queue.mergeBranch();
                node.setArrayIndexes(parsedOptionalArrayIndices.getParseResult());
                return ParseResult.successfulParse(node);
            }
            return ParseResult.unsuccessfulParse(parsedOptionalArrayIndices.getDiagnostic());
        }
        return ParseResult.unsuccessfulParse(parsedIdentifier.getDiagnostic());
    }

    @PartialParse
    @Override
    public PartialParseResult<List<IdentifierAccessNode>> parseAdditionalIdentifierAccesses(TokenQueue queue) {
        if (SyntaxSet.LANGUAGE_ELEMENTS.get(",").matches(queue.peek())) {
            queue.poll();
            ParseResult<IdentifierAccessNode> parsedIdentifierAccess =
                    parseIdentifierAccess(queue.branchOff());
            if (parsedIdentifierAccess.isSuccessful()) {
                queue.mergeBranch();
                PartialParseResult<List<IdentifierAccessNode>> parsedIdentifierAccesses =
                        parseAdditionalIdentifierAccesses(queue.branchOff());
                List<IdentifierAccessNode> identifierAccessNodes = new LinkedList<>();
                identifierAccessNodes.add(parsedIdentifierAccess.getParseResult());

                if (parsedIdentifierAccesses.isSuccessful()) {
                    queue.mergeBranch();
                    identifierAccessNodes.addAll(parsedIdentifierAccesses.getParseResult());
                    return PartialParseResult.successfulParse(identifierAccessNodes);
                } else if (parsedIdentifierAccesses.isPartial()) {
                    return PartialParseResult.partialParse(
                            identifierAccessNodes,
                            parsedIdentifierAccesses.getDiagnostic()
                    );
                }
                return PartialParseResult.unsuccessfulParse(parsedIdentifierAccesses.getDiagnostic());
            }

            return PartialParseResult.partialParse(new LinkedList<>(), parsedIdentifierAccess.getDiagnostic());
        }
        // epsilon
        return PartialParseResult.successfulParse(new LinkedList<>());
    }

    @PartialParse
    @Override
    public PartialParseResult<StatementsNode> parseCodeBlock(TokenQueue queue) {
        List<SyntaxDiagnostic> diagnostics = new ArrayList<>();
        boolean isPartial = false;

        if (!SyntaxSet.LANGUAGE_ELEMENTS.get("{").matches(queue.peek())) {
            return PartialParseResult.unsuccessfulParse(new SyntaxDiagnostic(queue.poll(), "Expected '{'!"));
        }
        queue.poll();

        PartialParseResult<StatementsNode> parsedStatements =
                programParser.getStatementParser().parseStatements(queue.branchOff());
        if (parsedStatements.isUnsuccessful()) {
            return PartialParseResult.unsuccessfulParse(parsedStatements.getDiagnostic());
        } else {
            if (parsedStatements.isPartial()) {
                diagnostics.add(parsedStatements.getDiagnostic());
                isPartial = true;
            }
            queue.mergeBranch();
        }

        if (!SyntaxSet.LANGUAGE_ELEMENTS.get("}").matches(queue.peek())) {
            diagnostics.add(new SyntaxDiagnostic(queue.poll(), "Expected '}'!"));
            isPartial = true;
            queue.skipOver(SyntaxSet.LANGUAGE_ELEMENTS.get("}"));
        }

        if (isPartial) {
            return PartialParseResult.partialParse(parsedStatements.getParseResult(), diagnostics.get(0));
        }

        return PartialParseResult.successfulParse(parsedStatements.getParseResult());
    }
}
