package de.nikschadowsky.baall.compiler.syntax.analysis;

import de.nikschadowsky.baall.compiler.symbol.Token;
import de.nikschadowsky.baall.compiler.symbol.TokenQueue;
import de.nikschadowsky.baall.compiler.syntax.analysis.result.ParseResult;
import de.nikschadowsky.baall.compiler.syntax.analysis.result.PartialParseResult;
import de.nikschadowsky.baall.compiler.syntax.error.SyntaxDiagnostic;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.ASTNodeFactory;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.access.*;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.expression.ExpressionNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.expression.OperatorNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.expression.OperatorNodeImpl;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.program.StatementsNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.typing.*;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.FieldNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.FieldNodeImpl;
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
    public ParseResult<List<FieldNode>> parseFieldDeclarations(TokenQueue queue) {
        if (SyntaxSet.LANGUAGE_ELEMENTS.get(",").matches(queue.peek())) {
            queue.poll();
            ParseResult<FieldNode> parsedFieldDeclaration =
                    parseFieldDeclaration(queue.branchOff());
            if (parsedFieldDeclaration.isSuccessful()) {
                queue.mergeBranch(parsedFieldDeclaration.getTokenQueueId());
                ParseResult<List<FieldNode>> parsedFunctionParameterList =
                        parseFieldDeclarations(queue.branchOff());
                if (parsedFunctionParameterList.isSuccessful()) {
                    queue.mergeBranch(parsedFunctionParameterList.getTokenQueueId());

                    List<FieldNode> parameterFields = new LinkedList<>();
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
    public ParseResult<FieldNode> parseFieldDeclaration(TokenQueue queue) {
        FieldNodeImpl node = astFactory.createFieldNode();

        ParseResult<TypeNode> parsedTypeNode = parseType(queue.branchOff());
        if (parsedTypeNode.isSuccessful()) {
            queue.mergeBranch(parsedTypeNode.getTokenQueueId());
            node.setType(parsedTypeNode.getParseResult());
            if (SyntaxSet.LANGUAGE_ELEMENTS.get(":").matches(queue.peek())) {
                queue.poll();
                ParseResult<IdentifierNode> parsedIdentifier = parseIdentifier(queue.branchOff());
                if (parsedIdentifier.isSuccessful()) {
                    queue.mergeBranch(parsedIdentifier.getTokenQueueId());
                    node.setIdentifier(parsedIdentifier.getParseResult());
                    return ParseResult.successfulParse(node, queue.getId());
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
    public ParseResult<OperatorNode> parseBinaryOperator(TokenQueue queue) {
        // todo syntax set should differentiate between unary and binary operators
        Set<LanguageElement> validBinaryOperators =
                Stream.of("+", "-", "*", "/", "%", "&", "|", "^", "<<", ">>", "==", "<", ">", "&&", "||")
                      .map(SyntaxSet.LANGUAGE_ELEMENTS::get)
                      .collect(Collectors.toSet());

        if (validBinaryOperators.stream().anyMatch(e -> e.matches(queue.peek()))) {
            OperatorNodeImpl node = astFactory.createOperatorNode();
            node.setOperator(queue.poll());
            return ParseResult.successfulParse(node, queue.getId());
        }
        return ParseResult.unsuccessfulParse(
                new SyntaxDiagnostic(queue.poll(), "Expected an operator!"),
                queue.getId()
        );
    }

    @CompleteParse
    @Override
    public ParseResult<OperatorNode> parseUnaryOperator(TokenQueue queue) {
        Set<LanguageElement> validShorthandOperators =
                Stream.of("++", "--")
                      .map(SyntaxSet.LANGUAGE_ELEMENTS::get)
                      .collect(Collectors.toSet());

        if (validShorthandOperators.stream().anyMatch(e -> e.matches(queue.peek()))) {
            OperatorNodeImpl node = astFactory.createOperatorNode();
            node.setOperator(queue.poll());
            return ParseResult.successfulParse(node, queue.getId());
        }
        return ParseResult.unsuccessfulParse(
                new SyntaxDiagnostic(queue.poll(), "Expected a unary operator!"),
                queue.getId()
        );
    }

    @CompleteParse
    @Override
    public ParseResult<OperatorNode> parseVariableAssignmentOperator(TokenQueue queue) {
        Set<LanguageElement> validShorthandOperators =
                Stream.of("=", "+=", "-=", "*=", "/=", "&=", "|=", "^=")
                      .map(SyntaxSet.LANGUAGE_ELEMENTS::get)
                      .collect(Collectors.toSet());

        if (validShorthandOperators.stream().anyMatch(e -> e.matches(queue.peek()))) {
            OperatorNodeImpl node = astFactory.createOperatorNode();
            node.setOperator(queue.poll());
            return ParseResult.successfulParse(node, queue.getId());
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
    public ParseResult<OperatorNode> parsePrefixOperator(TokenQueue queue) {
        Set<LanguageElement> validShorthandOperators =
                Stream.of("+", "-", "!")
                      .map(SyntaxSet.LANGUAGE_ELEMENTS::get)
                      .collect(Collectors.toSet());

        if (validShorthandOperators.stream().anyMatch(e -> e.matches(queue.peek()))) {
            OperatorNodeImpl node = astFactory.createOperatorNode();
            node.setOperator(queue.poll());
            return ParseResult.successfulParse(node, queue.getId());
        }
        return ParseResult.unsuccessfulParse(
                new SyntaxDiagnostic(queue.poll(), "Expected a prefix operator!"),
                queue.getId()
        );
    }

    @CompleteParse
    @Override
    public ParseResult<IdentifierNode> parseIdentifier(TokenQueue queue) {
        if (SyntaxSet.LANGUAGE_ELEMENTS.get("identifier_primitive").matches(queue.peek())) {
            IdentifierNodeImpl node = astFactory.createIdentifierNode();
            node.setName(queue.poll());
            return ParseResult.successfulParse(node, queue.getId());
        }
        return ParseResult.unsuccessfulParse(
                new SyntaxDiagnostic(queue.poll(), "Expected an identifier!"),
                queue.getId()
        );
    }

    @CompleteParse
    @Override
    public ParseResult<TypeNode> parseType(TokenQueue queue) {
        TypeNode inner;

        Token nextToken = queue.peek();
        if (SyntaxSet.PRIMITIVE_TYPES.stream().anyMatch(e -> e.matches(nextToken))) {
            PrimitiveTypeNodeImpl node = astFactory.createPrimitiveTypeNode();
            node.setKind(PrimitiveTypeNode.PrimitiveTypeKind.findMapping(queue.poll()));
            inner = node;
        } else {
            ElementAccessNode identifier;
            boolean noneSafe = false;
            ParseResult<ElementAccessNode> parsedIdentifier = parseElementAccess(queue.branchOff());
            if (parsedIdentifier.isSuccessful()) {
                queue.mergeBranch(parsedIdentifier.getTokenQueueId());
                identifier = parsedIdentifier.getParseResult();
            } else {
                return ParseResult.unsuccessfulParse(
                        new SyntaxDiagnostic(queue.poll(), "Expected a type!"),
                        queue.getId()
                );
            }
            if (SyntaxSet.LANGUAGE_ELEMENTS.get("!").matches(queue.peek())) {
                queue.poll();
                noneSafe = true;
            }

            IdentifierTypeNodeImpl node = astFactory.createIdentifierTypeNode();
            node.setType(identifier);
            node.setNoneSafe(noneSafe);
            inner = node;
        }

        ParseResult<List<ComplexTypeModifiers>> parsedModifiers = parseTypeModifiers(queue.branchOff());
        if (parsedModifiers.isUnsuccessful()) {
            return ParseResult.unsuccessfulParse(parsedModifiers.getDiagnostic(), queue.getId());
        }
        queue.mergeBranch(parsedModifiers.getTokenQueueId());
        List<ComplexTypeModifiers> complexTypeModifiers = parsedModifiers.getParseResult();

        for (ComplexTypeModifiers complexTypeModifier : complexTypeModifiers) {
            for (int i = 0; i < complexTypeModifier.arrayDimensions; i++) {
                ListTypeNodeImpl node = astFactory.createListTypeNode();
                node.setInner(inner);
                inner = node;
            }

            if (complexTypeModifier.hasFunctionTypes()) {
                FunctionTypeNodeImpl node = astFactory.createFunctionTypeNode();
                node.setParameterTypes(complexTypeModifier.functionTypes().orElseThrow());
                node.setInnerType(inner);
                inner = node;
            }
        }
        return ParseResult.successfulParse(inner, queue.getId());
    }

    @CompleteParse
    @Override
    public ParseResult<List<TypeNode>> parseAdditionalTypes(TokenQueue queue) {
        if (SyntaxSet.LANGUAGE_ELEMENTS.get(",").matches(queue.peek())) {
            queue.poll();
            ParseResult<TypeNode> parsedType = parseType(queue.branchOff());
            if (parsedType.isUnsuccessful()) {
                return ParseResult.unsuccessfulParse(
                        parsedType.getDiagnostic(),
                        queue.getId()
                );
            }

            queue.mergeBranch(parsedType.getTokenQueueId());
            ParseResult<List<TypeNode>> additionalTypes = parseAdditionalTypes(queue.branchOff());
            if (additionalTypes.isUnsuccessful()) {
                return ParseResult.unsuccessfulParse(additionalTypes.getDiagnostic(), queue.getId());
            }
            queue.mergeBranch(additionalTypes.getTokenQueueId());
            List<TypeNode> types = new ArrayList<>();
            types.add(parsedType.getParseResult());
            types.addAll(additionalTypes.getParseResult());

            return ParseResult.successfulParse(types, queue.getId());
        }
        return ParseResult.successfulParse(new ArrayList<>(), queue.getId());
    }

    private ParseResult<List<ComplexTypeModifiers>> parseTypeModifiers(
            TokenQueue queue
    ) {
        List<ComplexTypeModifiers> complexTypeModifiers = new ArrayList<>();

        // 2. array information
        int dimensions = 0;
        while (SyntaxSet.LANGUAGE_ELEMENTS.get("[").matches(queue.peek())) {
            queue.poll();
            if (!SyntaxSet.LANGUAGE_ELEMENTS.get("]").matches(queue.peek())) {
                return ParseResult.unsuccessfulParse(
                        new SyntaxDiagnostic(queue.poll(), "Expected ']'!"),
                        queue.getId()
                );
            }
            queue.poll();
            dimensions++;
        }

        // 3. function signature
        ParseResult<Optional<List<TypeNode>>> parsedFunctionSignature = parseFunctionSignature(queue.branchOff());
        if (parsedFunctionSignature.isUnsuccessful()) {
            return ParseResult.unsuccessfulParse(parsedFunctionSignature.getDiagnostic(), queue.getId());
        }
        queue.mergeBranch(parsedFunctionSignature.getTokenQueueId());

        complexTypeModifiers.add(new ComplexTypeModifiers(
                dimensions,
                parsedFunctionSignature.getParseResult()
        ));
        if (parsedFunctionSignature.getParseResult().isEmpty()) {
            return ParseResult.successfulParse(complexTypeModifiers, queue.getId());
        }

        ParseResult<List<ComplexTypeModifiers>> additionalComplexTypeModifiers =
                parseTypeModifiers(queue.branchOff());
        if (additionalComplexTypeModifiers.isUnsuccessful()) {
            return ParseResult.unsuccessfulParse(additionalComplexTypeModifiers.getDiagnostic(), queue.getId());
        }
        queue.mergeBranch(additionalComplexTypeModifiers.getTokenQueueId());
        complexTypeModifiers.addAll(additionalComplexTypeModifiers.getParseResult());
        return ParseResult.successfulParse(complexTypeModifiers, queue.getId());
    }

    private ParseResult<Optional<List<TypeNode>>> parseFunctionSignature(TokenQueue queue) {
        if (!SyntaxSet.LANGUAGE_ELEMENTS.get("<").matches(queue.peek())) {
            return ParseResult.successfulParse(Optional.empty(), queue.getId());
        }
        queue.poll();

        List<TypeNode> functionSignature = new ArrayList<>();

        ParseResult<TypeNode> parseType = parseType(queue.branchOff());
        if (parseType.isSuccessful()) {
            queue.mergeBranch(parseType.getTokenQueueId());
            functionSignature.add(parseType.getParseResult());

            ParseResult<List<TypeNode>> additionalTypes = parseAdditionalTypes(queue.branchOff());
            if (additionalTypes.isUnsuccessful()) {
                return ParseResult.unsuccessfulParse(additionalTypes.getDiagnostic(), queue.getId());
            }
            queue.mergeBranch(additionalTypes.getTokenQueueId());
            functionSignature.addAll(additionalTypes.getParseResult());
        }

        if (SyntaxSet.LANGUAGE_ELEMENTS.get(">").matches(queue.peek())) {
            queue.poll();
            return ParseResult.successfulParse(Optional.of(functionSignature), queue.getId());
        }
        return ParseResult.unsuccessfulParse(new SyntaxDiagnostic(queue.poll(), "Expected '>'!"), queue.getId());
    }

    private record ComplexTypeModifiers(
            int arrayDimensions,
            Optional<List<TypeNode>> functionTypes
    ) {

        public boolean hasFunctionTypes() {
            return functionTypes.isPresent();
        }
    }

    @CompleteParse
    @Override
    public ParseResult<List<ExpressionNode>> parseListIndexInformation(TokenQueue queue) {
        ParseResult<ExpressionNode> parsedIndex = parseListIndex(queue.branchOff());
        if (parsedIndex.isUnsuccessful()) {
            return ParseResult.successfulParse(List.of(), queue.getId());
        }
        queue.mergeBranch(parsedIndex.getTokenQueueId());
        List<ExpressionNode> indexInformation = new ArrayList<>();
        indexInformation.add(parsedIndex.getParseResult());

        ParseResult<List<ExpressionNode>> parsedArrayTypeDefinitions =
                parseListIndexInformation(queue.branchOff());
        if (!parsedArrayTypeDefinitions.isSuccessful()) {
            return ParseResult.unsuccessfulParse(parsedArrayTypeDefinitions.getDiagnostic(), queue.getId());
        }
        queue.mergeBranch(parsedArrayTypeDefinitions.getTokenQueueId());

        indexInformation.addAll(parsedArrayTypeDefinitions.getParseResult());

        return ParseResult.successfulParse(indexInformation, queue.getId());
    }

    @CompleteParse
    @Override
    public ParseResult<ExpressionNode> parseListIndex(TokenQueue queue) {
        if (!SyntaxSet.LANGUAGE_ELEMENTS.get("[").matches(queue.peek())) {
            return ParseResult.unsuccessfulParse(new SyntaxDiagnostic(queue.peek(), "Expected '['!"), queue.getId());
        }
        queue.poll();

        ParseResult<ExpressionNode> parsedIndex =
                programParser.getExpressionParser().parseExpression(queue.branchOff());
        if (parsedIndex.isUnsuccessful()) {
            return ParseResult.unsuccessfulParse(parsedIndex.getDiagnostic(), queue.getId());
        }
        queue.mergeBranch(parsedIndex.getTokenQueueId());

        if (!SyntaxSet.LANGUAGE_ELEMENTS.get("]").matches(queue.peek())) {
            return ParseResult.unsuccessfulParse(new SyntaxDiagnostic(queue.peek(), "Expected ']'!"), queue.getId());
        }
        queue.poll();

        return ParseResult.successfulParse(parsedIndex.getParseResult(), queue.getId());
    }

    @CompleteParse
    @Override
    public ParseResult<ElementAccessNode> parseElementAccess(TokenQueue queue) {
        int scopeElevations = 0;

        while (SyntaxSet.LANGUAGE_ELEMENTS.get("$").matches(queue.peek())) {
            queue.poll();
            scopeElevations++;
        }

        ParseResult<ElementAccessNode> parsedInner = parseElementAccessWithoutScopeElevation(queue.branchOff());
        if (parsedInner.isUnsuccessful()) {
            return ParseResult.unsuccessfulParse(parsedInner.getDiagnostic(), queue.getId());
        }
        queue.mergeBranch(parsedInner.getTokenQueueId());

        ElementAccessNode inner = parsedInner.getParseResult();

        for (int i = 0; i < scopeElevations; i++) {
            ScopeElevationNodeImpl node = astFactory.createScopeElevationNode();
            node.setInner(inner);
            inner = node;
        }

        return ParseResult.successfulParse(inner, queue.getId());
    }

    private ParseResult<ElementAccessNode> parseElementAccessWithoutScopeElevation(TokenQueue queue) {
        ParseResult<IdentifierNode> parsedIdentifier = parseIdentifier(queue.branchOff());
        if (parsedIdentifier.isUnsuccessful()) {
            return ParseResult.unsuccessfulParse(parsedIdentifier.getDiagnostic(), queue.getId());
        }
        queue.mergeBranch(parsedIdentifier.getTokenQueueId());

        ParseResult<List<ExpressionNode>> parsedArrayIndices = parseListIndexInformation(queue.branchOff());
        if (parsedArrayIndices.isUnsuccessful()) {
            return ParseResult.unsuccessfulParse(parsedArrayIndices.getDiagnostic(), queue.getId());
        }
        queue.mergeBranch(parsedArrayIndices.getTokenQueueId());

        ElementAccessNode self = parsedIdentifier.getParseResult();

        for (ExpressionNode index : parsedArrayIndices.getParseResult()) {
            IndexedAccessNodeImpl indexNode = astFactory.createIndexedAccessNode();
            indexNode.setIndex(index);
            indexNode.setInner(self);
            self = indexNode;
        }

        if (SyntaxSet.LANGUAGE_ELEMENTS.get(".").matches(queue.peek())) {
            queue.poll();

            MemberReferenceNodeImpl node = astFactory.createMemberReferenceNode();

            ParseResult<ElementAccessNode> parsedElementAccess =
                    parseElementAccessWithoutScopeElevation(queue.branchOff());
            if (parsedElementAccess.isUnsuccessful()) {
                return ParseResult.unsuccessfulParse(parsedElementAccess.getDiagnostic(), queue.getId());
            }
            queue.mergeBranch(parsedElementAccess.getTokenQueueId());
            node.setSelf(self);
            node.setInner(parsedElementAccess.getParseResult());

            self = node;
        }

        return ParseResult.successfulParse(self, queue.getId());
    }

    @PartialParse
    @Override
    public PartialParseResult<List<ElementAccessNode>> parseAdditionalElementAccesses(TokenQueue queue) {
        if (!SyntaxSet.LANGUAGE_ELEMENTS.get(",").matches(queue.peek())) {
            return PartialParseResult.successfulParse(List.of(), queue.getId());
        }
        queue.poll();

        ParseResult<ElementAccessNode> parsedElementAccess = parseElementAccess(queue.branchOff());
        if (parsedElementAccess.isUnsuccessful()) {
            return PartialParseResult.unsuccessfulParse(parsedElementAccess.getDiagnostic(), queue.getId());
        }
        queue.mergeBranch(parsedElementAccess.getTokenQueueId());
        List<ElementAccessNode> elements = new ArrayList<>();
        elements.add(parsedElementAccess.getParseResult());

        PartialParseResult<List<ElementAccessNode>> parsedAdditionalElementAccesses =
                parseAdditionalElementAccesses(queue.branchOff());
        if (parsedAdditionalElementAccesses.isUnsuccessful()) {
            return PartialParseResult.partialParse(
                    elements,
                    parsedAdditionalElementAccesses.getDiagnostic(),
                    queue.getId()
            );
        }
        queue.mergeBranch(parsedAdditionalElementAccesses.getTokenQueueId());
        elements.addAll(parsedAdditionalElementAccesses.getParseResult());

        return PartialParseResult.successfulParse(elements, queue.getId());
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
