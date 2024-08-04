package de.nikschadowsky.baall.compiler.syntax.analysis;

import de.nikschadowsky.baall.compiler.symbol.Token;
import de.nikschadowsky.baall.compiler.symbol.TokenQueue;
import de.nikschadowsky.baall.compiler.syntax.error.SyntaxDiagnostic;
import de.nikschadowsky.baall.compiler.syntax.error.SyntaxDiagnosticCollector;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.ASTNodeFactory;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.controlstructures.*;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.expression.*;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.program.ExportsNode;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.program.ExportsNodeImpl;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.program.StatementsNode;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.program.StatementsNodeImpl;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.statement.StatementNode;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.statement.assignment.*;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.statement.controlstatement.ControlStatementNode;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.statement.controlstatement.ReturnStatementNodeImpl;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.typing.TypeNode;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.typing.TypeNodeImpl;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.value.*;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.value.literal.*;
import de.nikschadowsky.baall.compiler.syntaxtree.util.NodeDiagnosticCollector;

import javax.annotation.processing.Generated;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Generated("by BAALL-Parser-Gen")
public class Parser {
    private static final Map<String, TerminalSymbol> TERMINAL_MAP = generateMapEntries();

    private TokenQueue queue;

    private final ASTNodeFactory astFactory = new ASTNodeFactory(new NodeDiagnosticCollector());

    private final SyntaxDiagnosticCollector syntaxDiagnosticCollector = new SyntaxDiagnosticCollector();

    public Parser(TokenQueue tokens) {
        this.queue = tokens;
    }

    private static Map<String, TerminalSymbol> generateMapEntries() {
        return Map.ofEntries(
                Map.entry("use", new TerminalSymbol(TerminalType.ANY, "use")),
                Map.entry("_STRING", new TerminalSymbol(TerminalType.STRING, "_STRING")),
                Map.entry(";", new TerminalSymbol(TerminalType.ANY, ";")),
                Map.entry(":", new TerminalSymbol(TerminalType.ANY, ":")),
                Map.entry("=", new TerminalSymbol(TerminalType.ANY, "=")),
                Map.entry(":=", new TerminalSymbol(TerminalType.ANY, ":=")),
                Map.entry("(", new TerminalSymbol(TerminalType.ANY, "(")),
                Map.entry(")", new TerminalSymbol(TerminalType.ANY, ")")),
                Map.entry("[", new TerminalSymbol(TerminalType.ANY, "[")),
                Map.entry("]", new TerminalSymbol(TerminalType.ANY, "]")),
                Map.entry(",", new TerminalSymbol(TerminalType.ANY, ",")),
                Map.entry("none", new TerminalSymbol(TerminalType.ANY, "none")),
                Map.entry("{", new TerminalSymbol(TerminalType.ANY, "{")),
                Map.entry("}", new TerminalSymbol(TerminalType.ANY, "}")),
                Map.entry("?", new TerminalSymbol(TerminalType.ANY, "?")),
                Map.entry("|", new TerminalSymbol(TerminalType.ANY, "|")),
                Map.entry("for", new TerminalSymbol(TerminalType.ANY, "for")),
                Map.entry("..", new TerminalSymbol(TerminalType.ANY, "..")),
                Map.entry("::", new TerminalSymbol(TerminalType.ANY, "::")),
                Map.entry("while", new TerminalSymbol(TerminalType.ANY, "while")),
                Map.entry("break", new TerminalSymbol(TerminalType.ANY, "break")),
                Map.entry("continue", new TerminalSymbol(TerminalType.ANY, "continue")),
                Map.entry("return", new TerminalSymbol(TerminalType.ANY, "return")),
                Map.entry("+", new TerminalSymbol(TerminalType.ANY, "+")),
                Map.entry("-", new TerminalSymbol(TerminalType.ANY, "-")),
                Map.entry("*", new TerminalSymbol(TerminalType.ANY, "*")),
                Map.entry("/", new TerminalSymbol(TerminalType.ANY, "/")),
                Map.entry("%", new TerminalSymbol(TerminalType.ANY, "%")),
                Map.entry("&", new TerminalSymbol(TerminalType.ANY, "&")),
                Map.entry("^", new TerminalSymbol(TerminalType.ANY, "^")),
                Map.entry("<<", new TerminalSymbol(TerminalType.ANY, "<<")),
                Map.entry(">>", new TerminalSymbol(TerminalType.ANY, ">>")),
                Map.entry("==", new TerminalSymbol(TerminalType.ANY, "==")),
                Map.entry("<", new TerminalSymbol(TerminalType.ANY, "<")),
                Map.entry(">", new TerminalSymbol(TerminalType.ANY, ">")),
                Map.entry("&&", new TerminalSymbol(TerminalType.ANY, "&&")),
                Map.entry("||", new TerminalSymbol(TerminalType.ANY, "||")),
                Map.entry("++", new TerminalSymbol(TerminalType.ANY, "++")),
                Map.entry("--", new TerminalSymbol(TerminalType.ANY, "--")),
                Map.entry("+=", new TerminalSymbol(TerminalType.ANY, "+=")),
                Map.entry("-=", new TerminalSymbol(TerminalType.ANY, "-=")),
                Map.entry("*=", new TerminalSymbol(TerminalType.ANY, "*=")),
                Map.entry("/=", new TerminalSymbol(TerminalType.ANY, "/=")),
                Map.entry("&=", new TerminalSymbol(TerminalType.ANY, "&=")),
                Map.entry("|=", new TerminalSymbol(TerminalType.ANY, "|=")),
                Map.entry("^=", new TerminalSymbol(TerminalType.ANY, "^=")),
                Map.entry("!", new TerminalSymbol(TerminalType.ANY, "!")),
                Map.entry("_NUMBER", new TerminalSymbol(TerminalType.NUMBER, "_NUMBER")),
                Map.entry("_BOOLEAN", new TerminalSymbol(TerminalType.BOOLEAN, "_BOOLEAN")),
                Map.entry("string", new TerminalSymbol(TerminalType.ANY, "string")),
                Map.entry("number", new TerminalSymbol(TerminalType.ANY, "number")),
                Map.entry("boolean", new TerminalSymbol(TerminalType.ANY, "boolean")),
                Map.entry("struct", new TerminalSymbol(TerminalType.ANY, "struct")),
                Map.entry("function", new TerminalSymbol(TerminalType.ANY, "function")),
                Map.entry("_IDENTIFIER", new TerminalSymbol(TerminalType.IDENTIFIER, "_IDENTIFIER")),
                Map.entry("export", new TerminalSymbol(TerminalType.ANY, "export"))
        );
    }

    private boolean parseProgram(TokenQueue queue) {
        if (parseImport(queue.branchOff())) {
            queue.mergeBranch();
            if (parseStatements(queue)) {
                if (parseExport(queue)) {
                    return true;
                }
            }
        }
        // TODO add behaviour when this symbol couldn't be parsed
        throw new RuntimeException("Expected '?' but got '?'!");
    }

    private boolean parseImport(TokenQueue queue) {
        if (TERMINAL_MAP.get("use").symbolMatches(queue.poll())) {
            if (TERMINAL_MAP.get("_STRING").symbolMatches(queue.poll())) {
                if (TERMINAL_MAP.get(";").symbolMatches(queue.poll())) {
                    if (parseImport(queue)) {
                        return true;
                    }
                }
            }
        }
        return true;
    }

    private ParseResult<StatementsNode> parseStatements(TokenQueue queue) {
        StatementsNodeImpl node = astFactory.createStatementsNode();

        ParseResult<StatementNode> parsedStatement = parseStatement(queue.branchOff());
        if (parsedStatement.isSuccessful()) {
            queue.mergeBranch();
            ParseResult<StatementsNode> statementsNodeParseResult = parseStatements(queue.branchOff());
            if (statementsNodeParseResult.isSuccessful()) {
                queue.mergeBranch();

                List<StatementNode> statements = new LinkedList<>();
                statements.add(parsedStatement.getParseResult());
                statements.addAll(statementsNodeParseResult.getParseResult().getStatements());

                node.setStatements(statements);
                return ParseResult.successfulParse(node);
            }
        }
        node.setStatements(new LinkedList<>());
        return ParseResult.successfulParse(node);
    }

    private ParseResult<StatementNode> parseStatement(TokenQueue queue) {
        ParseResult<? extends StatementNode> parseResult;

        parseResult = parseDeclaration(queue.branchOff());
        if (parseResult.isSuccessful()) {
            queue.mergeBranch();
            if (TERMINAL_MAP.get(";").symbolMatches(queue.poll())) {
                return ParseResult.successfulParse(parseResult.getParseResult());
            }
        }

        parseResult = parseReassignment(queue.branchOff());
        if (parseResult.isSuccessful()) {
            queue.mergeBranch();
            if (TERMINAL_MAP.get(";").symbolMatches(queue.poll())) {
                return ParseResult.successfulParse(parseResult.getParseResult());
            }
        }

        parseResult = parseFunctionCall(queue.branchOff());
        if (parseResult.isSuccessful()) {
            queue.mergeBranch();
            if (TERMINAL_MAP.get(";").symbolMatches(queue.poll())) {
                return ParseResult.successfulParse(parseResult.getParseResult());
            }
        }

        parseResult = parseControlStatement(queue.branchOff());
        if (parseResult.isSuccessful()) {
            queue.mergeBranch();
            if (TERMINAL_MAP.get(";").symbolMatches(queue.poll())) {
                return ParseResult.successfulParse(parseResult.getParseResult());
            }
        }

        parseResult = parseControlStructure(queue.branchOff());
        if (parseResult.isSuccessful()) {
            queue.mergeBranch();
            return ParseResult.successfulParse(parseResult.getParseResult());
        }

        queue.skipOver(TERMINAL_MAP.get(";"));
        return ParseResult.unsuccessfulParse(new SyntaxDiagnostic());
    }

    private ParseResult<DeclarationNode> parseDeclaration(TokenQueue queue) {
        ParseResult<TypeNode> parsedType = parseType(queue.branchOff());
        if (parsedType.isSuccessful()) {
            queue.mergeBranch();
            if (TERMINAL_MAP.get(":").symbolMatches(queue.poll())) {
                ParseResult<Token> parsedIdentifier = parseIdentifier(queue.branchOff());
                if (parsedIdentifier.isSuccessful()) {
                    queue.mergeBranch();
                    if (TERMINAL_MAP.get("=").symbolMatches(queue.peek())) {
                        queue.poll();
                        VariableDeclarationNodeImpl node = astFactory.createVariableDeclarationNode();

                        ParseResult<ExpressionNode> parsedExpression = parseExpression(queue.branchOff());
                        if (parsedExpression.isSuccessful()) {
                            queue.mergeBranch();
                            node.setType(parsedType.getParseResult());
                            node.setIdentifier(parsedIdentifier.getParseResult());
                            node.setInitializationValue(parsedExpression.getParseResult());
                            return ParseResult.successfulParse(node);
                        }
                    } else if (TERMINAL_MAP.get(":=").symbolMatches(queue.peek())) {
                        queue.poll();
                        ConstantDeclarationNodeImpl node = astFactory.createConstantDeclarationNode();

                        ParseResult<ExpressionNode> parsedExpression = parseExpression(queue.branchOff());
                        if (parsedExpression.isSuccessful()) {
                            queue.mergeBranch();

                            node.setType(parsedType.getParseResult());
                            node.setIdentifier(parsedIdentifier.getParseResult());
                            node.setInitializationValue(parsedExpression.getParseResult());
                            return ParseResult.successfulParse(node);
                        }
                    }
                    // declaration without initialization
                    VariableDeclarationNodeImpl node = astFactory.createVariableDeclarationNode();
                    node.setType(parsedType.getParseResult());
                    node.setIdentifier(parsedIdentifier.getParseResult());

                    return ParseResult.successfulParse(node);
                }
            }
        }
        return ParseResult.unsuccessfulParse(new SyntaxDiagnostic());
    }

    private ParseResult<ReassignmentNode> parseReassignment(TokenQueue queue) {
        ParseResult<UnaryExpressionNode> parsedUnaryExpression = parseUnaryExpression(queue.branchOff());
        if (parsedUnaryExpression.isSuccessful()) {
            queue.mergeBranch();
            return ParseResult.successfulParse(parsedUnaryExpression.getParseResult());
        }

        ParseResult<VariableReassignmentNode> parsedVariableReassignment = parseVariableReassignment(queue.branchOff());
        if (parsedVariableReassignment.isSuccessful()) {
            queue.mergeBranch();
            return ParseResult.successfulParse(parsedVariableReassignment.getParseResult());
        }

        return ParseResult.unsuccessfulParse(new SyntaxDiagnostic());
    }

    private ParseResult<VariableReassignmentNode> parseVariableReassignment(TokenQueue queue) {
        VariableReassignmentNodeImpl node = astFactory.createVariableReassignmentNode();

        ParseResult<IdentifierAccessNode> parsedIdentifier = parseIdentifierAccess(queue.branchOff());
        if (parsedIdentifier.isSuccessful()) {
            queue.mergeBranch();

            node.setIdentifier(parsedIdentifier.getParseResult());
            ParseResult<Token> parsedShorthandOperator = parseShorthandOperator(queue.branchOff());
            if (parsedShorthandOperator.isSuccessful()) {
                queue.mergeBranch();

                node.setOperator(parsedShorthandOperator.getParseResult());
                ParseResult<ExpressionNode> parsedExpression = parseExpression(queue.branchOff());
                if (parsedExpression.isSuccessful()) {
                    queue.mergeBranch();

                    node.setValueExpression(parsedExpression.getParseResult());
                    return ParseResult.successfulParse(node);
                }
            }
        }
        return ParseResult.unsuccessfulParse(new SyntaxDiagnostic());
    }

    private ParseResult<ExpressionNode> parseExpression(TokenQueue queue) {
        if (TERMINAL_MAP.get("(").symbolMatches(queue.peek())) {
            queue.poll();

            ParenthesizedExpressionNodeImpl node = astFactory.createParenthesizedExpressionNode();
            ParseResult<ExpressionNode> parsedExpression = parseExpression(queue.branchOff());
            if (parsedExpression.isSuccessful()) {
                queue.mergeBranch();

                if (TERMINAL_MAP.get(")").symbolMatches(queue.poll())) {
                    node.setInnerExpression(parsedExpression.getParseResult());
                    return ParseResult.successfulParse(node);
                }
            }
            queue.skipOver(TERMINAL_MAP.get(")"));
        }

        ParseResult<Token> parsedPrefixOperator = parsePrefixOperator(queue.branchOff());
        if (parsedPrefixOperator.isSuccessful()) {
            queue.mergeBranch();

            PrefixOperationNodeImpl node = astFactory.createPrefixOperationNode();
            node.setOperator(parsedPrefixOperator.getParseResult());

            ParseResult<ExpressionNode> parsedPrefixOperationExpression = parseExpression(queue.branchOff());
            if (parsedPrefixOperationExpression.isSuccessful()) {
                queue.mergeBranch();

                node.setOperand(parsedPrefixOperationExpression.getParseResult());
                return ParseResult.successfulParse(node);
            }
        }

        ParseResult<ValueNode> parsedValue = parseValue(queue.branchOff());
        if (parsedValue.isSuccessful()) {
            queue.mergeBranch();

            ParseResult<Token> parsedBinaryOperator = parseBinaryOperator(queue.branchOff());
            if (parsedBinaryOperator.isSuccessful()) {
                queue.mergeBranch();

                ParseResult<ExpressionNode> parsedBinaryRightExpression = parseExpression(queue.branchOff());
                if (parsedBinaryRightExpression.isSuccessful()) {
                    queue.mergeBranch();

                    BinaryExpressionNodeImpl node = astFactory.createBinaryExpressionNode();
                    node.setLeftOperand(parsedValue.getParseResult());
                    node.setOperator(parsedBinaryOperator.getParseResult());
                    node.setRightOperand(parsedBinaryRightExpression.getParseResult());
                    return ParseResult.successfulParse(node);
                }
            }
            return ParseResult.successfulParse(parsedValue.getParseResult());
        }

        return ParseResult.unsuccessfulParse(new SyntaxDiagnostic());
    }

    private ParseResult<ValueNode> parseValue(TokenQueue queue) {
        ParseResult<? extends ValueNode> parseResult;

        parseResult = parseLiteral(queue.branchOff());
        if (parseResult.isSuccessful()) {
            queue.mergeBranch();
            return ParseResult.successfulParse(parseResult.getParseResult());
        }

        parseResult = parseIdentifierAccess(queue.branchOff());
        if (parseResult.isSuccessful()) {
            queue.mergeBranch();
            return ParseResult.successfulParse(parseResult.getParseResult());
        }

        parseResult = parseFunctionCall(queue.branchOff());
        if (parseResult.isSuccessful()) {
            queue.mergeBranch();
            return ParseResult.successfulParse(parseResult.getParseResult());
        }

        parseResult = parseUnaryExpression(queue.branchOff());
        if (parseResult.isSuccessful()) {
            queue.mergeBranch();
            return ParseResult.successfulParse(parseResult.getParseResult());
        }

        return ParseResult.unsuccessfulParse(new SyntaxDiagnostic());
    }

    private ParseResult<ArrayLiteralNode> parseArrayLiteral(TokenQueue queue) {
        if (TERMINAL_MAP.get("[").symbolMatches(queue.peek())) {
            queue.poll();
            ArrayLiteralNodeImpl node = astFactory.createArrayLiteralNode();
            if (TERMINAL_MAP.get("]").symbolMatches(queue.poll())) {
                node.setElements(new LinkedList<>());
                return ParseResult.successfulParse(node);
            }
            ParseResult<ExpressionNode> parsedExpression = parseExpression(queue.branchOff());
            if (parsedExpression.isSuccessful()) {
                queue.mergeBranch();

                ParseResult<List<ExpressionNode>> parsedArrayElements = parseArrayElementList(queue.branchOff());
                if (parsedArrayElements.isSuccessful()) {
                    queue.mergeBranch();

                    if (TERMINAL_MAP.get("]").symbolMatches(queue.poll())) {

                        List<ExpressionNode> elements = new LinkedList<>();
                        elements.add(parsedExpression.getParseResult());
                        elements.addAll(parsedArrayElements.getParseResult());
                        node.setElements(elements);
                        return ParseResult.successfulParse(node);
                    }
                }
                queue.skipOver(TERMINAL_MAP.get("]"));
            }
        }
        return ParseResult.unsuccessfulParse(new SyntaxDiagnostic());
    }

    private ParseResult<List<ExpressionNode>> parseArrayElementList(TokenQueue queue) {
        if (TERMINAL_MAP.get(",").symbolMatches(queue.peek())) {
            queue.poll();
            ParseResult<ExpressionNode> parsedExpression = parseExpression(queue.branchOff());
            if (parsedExpression.isSuccessful()) {
                queue.mergeBranch();

                ParseResult<List<ExpressionNode>> parsedArrayElements = parseArrayElementList(queue.branchOff());
                if (parsedArrayElements.isSuccessful()) {
                    queue.mergeBranch();

                    List<ExpressionNode> elements = new LinkedList<>();
                    elements.add(parsedExpression.getParseResult());
                    elements.addAll(parsedArrayElements.getParseResult());
                    return ParseResult.successfulParse(elements);
                }
            }
        }
        return ParseResult.successfulParse(new LinkedList<>());
    }

    private ParseResult<StructDefinitionLiteralNode> parseStructDefinition(TokenQueue queue) {
        if (TERMINAL_MAP.get("(").symbolMatches(queue.peek())) {
            queue.poll();

            StructDefinitionLiteralNodeImpl node =
                    astFactory.createStructDefinitionLiteralNode();
            ParseResult<Map.Entry<TypeNode, Token>> firstStructField = parseStructField(queue.branchOff());
            if (firstStructField.isSuccessful()) {
                queue.mergeBranch();

                ParseResult<List<Map.Entry<TypeNode, Token>>> parsedStructFields =
                        parseStructFieldList(queue.branchOff());
                if (parsedStructFields.isSuccessful()) {
                    queue.mergeBranch();

                    List<Map.Entry<TypeNode, Token>> fields = new LinkedList<>();
                    fields.add(firstStructField.getParseResult());
                    fields.addAll(parsedStructFields.getParseResult());
                    node.setFields(fields);
                    if (TERMINAL_MAP.get(")").symbolMatches(queue.poll())) {
                        return ParseResult.successfulParse(node);
                    }
                }
            }
            queue.skipOver(TERMINAL_MAP.get(")"));
        }
        return ParseResult.unsuccessfulParse(new SyntaxDiagnostic());
    }

    private ParseResult<List<Map.Entry<TypeNode, Token>>> parseStructFieldList(TokenQueue queue) {
        if (TERMINAL_MAP.get(",").symbolMatches(queue.peek())) {
            queue.poll();
            ParseResult<Map.Entry<TypeNode, Token>> parsedStructField = parseStructField(queue.branchOff());
            if (parsedStructField.isSuccessful()) {
                queue.mergeBranch();

                ParseResult<List<Map.Entry<TypeNode, Token>>> parsedStructFields =
                        parseStructFieldList(queue.branchOff());
                if (parsedStructFields.isSuccessful()) {
                    queue.mergeBranch();

                    List<Map.Entry<TypeNode, Token>> fields = new LinkedList<>();
                    fields.add(parsedStructField.getParseResult());
                    fields.addAll(parsedStructFields.getParseResult());
                    return ParseResult.successfulParse(fields);
                }
            }
        }
        return ParseResult.successfulParse(new LinkedList<>());
    }

    private ParseResult<Map.Entry<TypeNode, Token>> parseStructField(TokenQueue queue) {
        ParseResult<TypeNode> parsedType = parseType(queue.branchOff());
        if (parsedType.isSuccessful()) {
            queue.mergeBranch();

            if (TERMINAL_MAP.get(":").symbolMatches(queue.poll())) {
                ParseResult<Token> parsedIdentifier = parseIdentifier(queue.branchOff());
                if (parsedIdentifier.isSuccessful()) {
                    queue.mergeBranch();

                    return ParseResult.successfulParse(Map.entry(
                            parsedType.getParseResult(),
                            parsedIdentifier.getParseResult()
                    ));
                }
            }
        }
        return ParseResult.unsuccessfulParse(new SyntaxDiagnostic());
    }

    private ParseResult<StructInitializationLiteralNode> parseStructInitialization(TokenQueue queue) {
        if (TERMINAL_MAP.get("(").symbolMatches(queue.peek())) {
            StructInitializationLiteralNodeImpl node =
                    astFactory.createStructInitializationLiteralNode();
            queue.poll();
            ParseResult<ExpressionNode> parsedExpression = parseExpression(queue.branchOff());
            if (parsedExpression.isSuccessful()) {
                queue.mergeBranch();

                ParseResult<List<ExpressionNode>> parsedArguments = parseFilledStructFieldList(queue.branchOff());
                if (parsedArguments.isSuccessful()) {
                    queue.mergeBranch();

                    if (TERMINAL_MAP.get(")").symbolMatches(queue.poll())) {
                        List<ExpressionNode> arguments = new LinkedList<>();
                        arguments.add(parsedExpression.getParseResult());
                        arguments.addAll(parsedArguments.getParseResult());
                        node.setArguments(arguments);
                        return ParseResult.successfulParse(node);
                    }
                }
            }
            queue.skipOver(TERMINAL_MAP.get(")"));
        } else if (TERMINAL_MAP.get("none").symbolMatches(queue.poll())) {
            return ParseResult.successfulParse(astFactory.createStructInitializationNone());
        }

        return ParseResult.unsuccessfulParse(new SyntaxDiagnostic());
    }

    private ParseResult<List<ExpressionNode>> parseFilledStructFieldList(TokenQueue queue) {
        if (TERMINAL_MAP.get(",").symbolMatches(queue.peek())) {
            queue.poll();
            ParseResult<ExpressionNode> parsedExpression = parseExpression(queue.branchOff());
            if (parsedExpression.isSuccessful()) {
                queue.mergeBranch();
                ParseResult<List<ExpressionNode>> parsedArguments = parseFilledStructFieldList(queue.branchOff());
                if (parsedArguments.isSuccessful()) {
                    queue.mergeBranch();

                    List<ExpressionNode> arguments = new LinkedList<>();
                    arguments.add(parsedExpression.getParseResult());
                    arguments.addAll(parsedArguments.getParseResult());
                    return ParseResult.successfulParse(arguments);
                }
            }
        }
        return ParseResult.successfulParse(new LinkedList<>());
    }

    private ParseResult<FunctionDefinitionNode> parseFunctionDefinition(TokenQueue queue) {
        FunctionDefinitionNodeImpl node = astFactory.createFunctionDefinitionNode();
        if (TERMINAL_MAP.get("{").symbolMatches(queue.peek())) {
            queue.poll();
            ParseResult<StatementsNode> parsedStatements = parseStatements(queue.branchOff());
            if (parsedStatements.isSuccessful()) {
                queue.mergeBranch();

                node.setParameters(new LinkedList<>());
                node.setFunctionBody(parsedStatements.getParseResult());
                if (TERMINAL_MAP.get("}").symbolMatches(queue.poll())) {
                    return ParseResult.successfulParse(node);
                }
            }
            queue.skipOver(TERMINAL_MAP.get("}"));
        } else {
            ParseResult<Map.Entry<TypeNode, Token>> parsedFirstFunctionParameter =
                    parseFunctionParameter(queue.branchOff());
            if (parsedFirstFunctionParameter.isSuccessful()) {
                queue.mergeBranch();
                ParseResult<List<Map.Entry<TypeNode, Token>>> parsedFunctionParameters =
                        parseFunctionParameterList(queue.branchOff());
                if (parsedFunctionParameters.isSuccessful()) {
                    queue.mergeBranch();
                    List<Map.Entry<TypeNode, Token>> parameterList = parsedFunctionParameters.getParseResult();
                    parameterList.add(0, parsedFirstFunctionParameter.getParseResult());

                    node.setParameters(parameterList);
                    if (TERMINAL_MAP.get("{").symbolMatches(queue.poll())) {
                        ParseResult<StatementsNode> parsedStatements = parseStatements(queue.branchOff());
                        if (parsedStatements.isSuccessful()) {
                            queue.mergeBranch();

                            node.setFunctionBody(parsedStatements.getParseResult());
                            if (TERMINAL_MAP.get("}").symbolMatches(queue.poll())) {
                                return ParseResult.successfulParse(node);
                            }
                        }
                        queue.skipOver(TERMINAL_MAP.get("}"));
                    }
                }
            }
        }
        return ParseResult.unsuccessfulParse(new SyntaxDiagnostic());
    }

    private ParseResult<List<Map.Entry<TypeNode, Token>>> parseFunctionParameterList(TokenQueue queue) {
        if (TERMINAL_MAP.get(",").symbolMatches(queue.peek())) {
            queue.poll();
            ParseResult<Map.Entry<TypeNode, Token>> parsedFunctionParameter = parseFunctionParameter(queue.branchOff());
            if (parsedFunctionParameter.isSuccessful()) {
                queue.mergeBranch();
                ParseResult<List<Map.Entry<TypeNode, Token>>> parsedFunctionParameterList =
                        parseFunctionParameterList(queue.branchOff());
                if (parsedFunctionParameterList.isSuccessful()) {
                    queue.mergeBranch();

                    List<Map.Entry<TypeNode, Token>> parameterFields = new LinkedList<>();
                    parameterFields.add(parsedFunctionParameter.getParseResult());
                    parameterFields.addAll(parsedFunctionParameterList.getParseResult());
                    return ParseResult.successfulParse(parameterFields);
                }
            }
        }
        return ParseResult.successfulParse(new LinkedList<>());
    }

    private ParseResult<Map.Entry<TypeNode, Token>> parseFunctionParameter(TokenQueue queue) {
        ParseResult<TypeNode> parsedTypeNode = parseType(queue.branchOff());
        if (parsedTypeNode.isSuccessful()) {
            if (TERMINAL_MAP.get(":").symbolMatches(queue.poll())) {
                queue.mergeBranch();
                ParseResult<Token> parsedIdentifier = parseIdentifier(queue.branchOff());
                if (parsedIdentifier.isSuccessful()) {
                    queue.mergeBranch();
                    return ParseResult.successfulParse(Map.entry(
                            parsedTypeNode.getParseResult(),
                            parsedIdentifier.getParseResult()
                    ));
                }
            }
        }
        return ParseResult.unsuccessfulParse(new SyntaxDiagnostic());
    }

    // todo refactor to reduce nesting
    private ParseResult<FunctionCallNode> parseFunctionCall(TokenQueue queue) {
        FunctionCallNodeImpl node = astFactory.createFunctionCallNode();

        ParseResult<IdentifierAccessNode> parsedIdentifierValueAccess = parseIdentifierAccess(queue.branchOff());
        if (parsedIdentifierValueAccess.isSuccessful()) {
            queue.mergeBranch();

            node.setFunctionIdentifier(parsedIdentifierValueAccess.getParseResult());
            if (TERMINAL_MAP.get("(").symbolMatches(queue.poll())) {
                if (TERMINAL_MAP.get(")").symbolMatches(queue.poll())) {
                    node.setArguments(new LinkedList<>());
                    return ParseResult.successfulParse(node);
                } else {
                    ParseResult<ExpressionNode> parsedExpression = parseExpression(queue.branchOff());
                    if (parsedExpression.isSuccessful()) {
                        queue.mergeBranch();

                        ParseResult<List<ExpressionNode>> parsedFunctionArguments =
                                parseFunctionArgumentList(queue.branchOff());
                        if (parsedFunctionArguments.isSuccessful()) {
                            queue.mergeBranch();

                            List<ExpressionNode> functionArguments = parsedFunctionArguments.getParseResult();
                            functionArguments.add(0, parsedExpression.getParseResult());
                            node.setArguments(functionArguments);

                            if (TERMINAL_MAP.get(")").symbolMatches(queue.poll())) {
                                return ParseResult.successfulParse(node);
                            }
                        }
                    }
                    queue.skipOver(TERMINAL_MAP.get(")"));
                }
            }
        }
        return ParseResult.unsuccessfulParse(new SyntaxDiagnostic());
    }

    private ParseResult<List<ExpressionNode>> parseFunctionArgumentList(TokenQueue queue) {
        if (TERMINAL_MAP.get(",").symbolMatches(queue.peek())) {
            queue.poll();
            ParseResult<ExpressionNode> parsedExpression = parseExpression(queue.branchOff());
            if (parsedExpression.isSuccessful()) {
                queue.mergeBranch();
                ParseResult<List<ExpressionNode>> parsedFunctionArgumentList =
                        parseFunctionArgumentList(queue.branchOff());
                if (parsedFunctionArgumentList.isSuccessful()) {
                    queue.mergeBranch();

                    List<ExpressionNode> arguments = new LinkedList<>();
                    arguments.add(parsedExpression.getParseResult());
                    arguments.addAll(parsedFunctionArgumentList.getParseResult());

                    return ParseResult.successfulParse(arguments);
                }
            }
        }
        return ParseResult.successfulParse(new LinkedList<>());
    }

    private ParseResult<ControlStructureNode> parseControlStructure(TokenQueue queue) {
        ParseResult<? extends ControlStructureNode> parseResult;

        parseResult = parseForLoop(queue.branchOff());
        if (parseResult.isSuccessful()) {
            queue.mergeBranch();
            return ParseResult.successfulParse(parseResult.getParseResult());
        }

        parseResult = parseWhileLoop(queue.branchOff());
        if (parseResult.isSuccessful()) {
            queue.mergeBranch();
            return ParseResult.successfulParse(parseResult.getParseResult());
        }

        parseResult = parseConditional(queue.branchOff());
        if (parseResult.isSuccessful()) {
            queue.mergeBranch();
            return ParseResult.successfulParse(parseResult.getParseResult());
        }
        return ParseResult.unsuccessfulParse(new SyntaxDiagnostic());
    }

    private ParseResult<ConditionalNode> parseConditional(TokenQueue queue) {
        ConditionalNodeImpl node = astFactory.createConditionalNode();

        ParseResult<ExpressionNode> parsedExpression = parseExpression(queue.branchOff());
        if (parsedExpression.isSuccessful()) {
            queue.mergeBranch();

            node.setCondition(parsedExpression.getParseResult());
            if (TERMINAL_MAP.get("?").symbolMatches(queue.poll())) {
                if (TERMINAL_MAP.get("{").symbolMatches(queue.poll())) {
                    ParseResult<StatementsNode> parsedStatements = parseStatements(queue.branchOff());
                    if (parsedStatements.isSuccessful()) {
                        queue.mergeBranch();

                        node.setThenBlock(parsedStatements.getParseResult());
                        if (TERMINAL_MAP.get("}").symbolMatches(queue.poll())) {
                            ParseResult<ConditionalNodeImpl> parsedElseBlock = parseElseBlock(queue.branchOff());
                            if (parsedElseBlock.isSuccessful()) {
                                queue.mergeBranch();

                                node.setElseBranch(parsedElseBlock.getParseResult());
                                return ParseResult.successfulParse(node);
                            }
                        }
                    }
                }
            }
            queue.skipOver(TERMINAL_MAP.get("}"));

        }
        return ParseResult.unsuccessfulParse(new SyntaxDiagnostic());
    }

    private ParseResult<ConditionalNode> parseElseBlock(TokenQueue queue) {
        if (TERMINAL_MAP.get("|").symbolMatches(queue.peek())) {
            queue.poll();
            if (TERMINAL_MAP.get("{").symbolMatches(queue.peek())) {
                queue.poll();

                ConditionalNodeImpl node = astFactory.createConditionalNode();
                node.setConditionBranch(ConditionalNodeImpl.ConditionBranch.ELSE);
                ParseResult<StatementsNode> parsedStatements = parseStatements(queue.branchOff());
                if (parsedStatements.isSuccessful()) {
                    queue.mergeBranch();

                    node.setThenBlock(parsedStatements.getParseResult());
                    if (TERMINAL_MAP.get("}").symbolMatches(queue.poll())) {
                        return ParseResult.successfulParse(node);
                    }
                }
            } else {
                ParseResult<ConditionalNode> parsedConditional = parseConditional(queue.branchOff());
                if (parsedConditional.isSuccessful()) {
                    queue.mergeBranch();

                    ConditionalNode node = parsedConditional.getParseResult();
                    return ParseResult.successfulParse(node);
                }
            }
        }
        return ParseResult.unsuccessfulParse(new SyntaxDiagnostic());
    }

    // todo refactor to reduce nesting
    private ParseResult<ForLoopNode> parseForLoop(TokenQueue queue) {
        ForLoopNodeImpl node = astFactory.createForLoopNode();

        if (TERMINAL_MAP.get("for").symbolMatches(queue.peek())) {
            queue.poll();
            ParseResult<Token> parsedIdentifier = parseIdentifier(queue.branchOff());
            if (parsedIdentifier.isSuccessful()) {
                queue.mergeBranch();
                node.setIdentifier(parsedIdentifier.getParseResult());
                if (TERMINAL_MAP.get("=").symbolMatches(queue.poll())) {
                    ParseResult<ExpressionNode> parsedStartIndexExpression = parseExpression(queue.branchOff());
                    queue.mergeBranch();
                    if (parsedStartIndexExpression.isSuccessful()) {
                        node.setStartIndex(parsedStartIndexExpression.getParseResult());
                        if (TERMINAL_MAP.get("..").symbolMatches(queue.poll())) {
                            ParseResult<ExpressionNode> parsedEndIndexExpression = parseExpression(queue.branchOff());
                            if (parsedEndIndexExpression.isSuccessful()) {
                                queue.mergeBranch();
                                node.setEndIndex(parsedEndIndexExpression.getParseResult());
                                ParseResult<ReassignmentNode> parseOptionalIncrementor =
                                        parseOptionalForIncrementor(queue.branchOff());
                                if (parseOptionalIncrementor.isSuccessful()) {
                                    queue.mergeBranch();
                                    node.setOptionalStepperFunction(parseOptionalIncrementor.getParseResult());
                                    if (TERMINAL_MAP.get("{").symbolMatches(queue.poll())) {
                                        ParseResult<StatementsNode> parsedStatements =
                                                parseStatements(queue.branchOff());
                                        if (parsedStatements.isSuccessful()) {
                                            queue.mergeBranch();
                                            if (TERMINAL_MAP.get("}").symbolMatches(queue.poll())) {
                                                return ParseResult.successfulParse(node);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return ParseResult.unsuccessfulParse(new SyntaxDiagnostic());
    }

    private ParseResult<ReassignmentNode> parseOptionalForIncrementor(TokenQueue queue) {
        VariableReassignmentNodeImpl node = astFactory.createVariableReassignmentNode();
        if (TERMINAL_MAP.get("::").symbolMatches(queue.peek())) {
            queue.poll();
            ParseResult<ReassignmentNode> parsedReassignment = parseReassignment(queue.branchOff());
            if (parsedReassignment.isSuccessful()) {
                queue.mergeBranch();
                return ParseResult.successfulParse(parsedReassignment.getParseResult());
            }
        }
        return ParseResult.successfulParse(node);
    }

    // todo refactor to reduce nesting
    private ParseResult<WhileLoopNode> parseWhileLoop(TokenQueue queue) {
        WhileLoopNodeImpl node = astFactory.createWhileLoopNode();
        if (TERMINAL_MAP.get("while").symbolMatches(queue.peek())) {
            queue.poll();
            ParseResult<ExpressionNode> parsedExpression = parseExpression(queue.branchOff());
            if (parsedExpression.isSuccessful()) {
                queue.mergeBranch();
                node.setCondition(parsedExpression.getParseResult());
                if (TERMINAL_MAP.get("{").symbolMatches(queue.poll())) {
                    ParseResult<StatementsNode> parsedStatements = parseStatements(queue.branchOff());
                    if (parsedStatements.isSuccessful()) {
                        queue.mergeBranch();
                        node.setBody(parsedStatements.getParseResult());
                        if (TERMINAL_MAP.get("}").symbolMatches(queue.poll())) {
                            return ParseResult.successfulParse(node);
                        }
                    }
                }
            } else {
                queue.skipOver(TERMINAL_MAP.get("}"));
            }
        }
        return ParseResult.unsuccessfulParse(new SyntaxDiagnostic());
    }

    private ParseResult<ControlStatementNode> parseControlStatement(TokenQueue queue) {
        if (TERMINAL_MAP.get("break").symbolMatches(queue.peek())) {
            queue.poll();
            return ParseResult.successfulParse(astFactory.createBreakStatementNode());
        }
        if (TERMINAL_MAP.get("continue").symbolMatches(queue.peek())) {
            queue.poll();
            return ParseResult.successfulParse(astFactory.createContinueStatementNode());
        }
        if (TERMINAL_MAP.get("return").symbolMatches(queue.peek())) {
            queue.poll();
            ReturnStatementNodeImpl node = astFactory.createReturnStatementNode();

            ParseResult<ExpressionNode> parsedExpression = parseExpression(queue.branchOff());
            if (parsedExpression.isSuccessful()) {
                queue.mergeBranch();

                node.setReturnExpression(parsedExpression.getParseResult());
                return ParseResult.successfulParse(node);
            }

        }
        return ParseResult.unsuccessfulParse(new SyntaxDiagnostic());
    }

    private ParseResult<UnaryExpressionNode> parseUnaryExpression(TokenQueue queue) {
        ParseResult<Token> parsedUnaryOperator;
        ParseResult<IdentifierAccessNode> parsedIdentifier;
        UnaryExpressionNodeImpl node;

        parsedUnaryOperator = parseUnaryOperator(queue.branchOff());
        if (parsedUnaryOperator.isSuccessful()) {
            queue.mergeBranch();

            node = astFactory.createUnaryExpressionNode();
            node.setIsPrefix(true);
            node.setOperator(parsedUnaryOperator.getParseResult());
            parsedIdentifier = parseIdentifierAccess(queue.branchOff());
            if (parsedIdentifier.isSuccessful()) {
                queue.mergeBranch();

                node.setIdentifierAccess(parsedIdentifier.getParseResult());
                return ParseResult.successfulParse(node);
            } else {
                return ParseResult.unsuccessfulParse(new SyntaxDiagnostic());
            }
        }

        parsedIdentifier = parseIdentifierAccess(queue.branchOff());
        if (parsedIdentifier.isSuccessful()) {
            queue.mergeBranch();

            node = astFactory.createUnaryExpressionNode();
            node.setIdentifierAccess(parsedIdentifier.getParseResult());
            parsedUnaryOperator = parseUnaryOperator(queue.branchOff());
            if (parsedUnaryOperator.isSuccessful()) {
                queue.mergeBranch();
                node.setOperator(parsedUnaryOperator.getParseResult());
                node.setIsPrefix(false);

                return ParseResult.successfulParse(node);
            }
        }

        return ParseResult.unsuccessfulParse(new SyntaxDiagnostic());
    }

    private ParseResult<Token> parseBinaryOperator(TokenQueue queue) {
        Set<TerminalSymbol> validBinaryOperators =
                Stream.of("+", "-", "*", "/", "%", "&", "|", "^", "<<", ">>", "==", "<", ">", "&&", "||")
                      .map(TERMINAL_MAP::get)
                      .collect(Collectors.toSet());

        if (validBinaryOperators.stream().anyMatch(e -> e.symbolMatches(queue.peek()))) {
            return ParseResult.successfulParse(queue.poll());
        }
        return ParseResult.unsuccessfulParse(new SyntaxDiagnostic());
    }

    private ParseResult<Token> parseUnaryOperator(TokenQueue queue) {
        Set<TerminalSymbol> validShorthandOperators =
                Stream.of("++", "--")
                      .map(TERMINAL_MAP::get)
                      .collect(Collectors.toSet());

        if (validShorthandOperators.stream().anyMatch(e -> e.symbolMatches(queue.peek()))) {
            return ParseResult.successfulParse(queue.poll());
        }
        return ParseResult.unsuccessfulParse(new SyntaxDiagnostic());
    }

    private ParseResult<Token> parseShorthandOperator(TokenQueue queue) {
        Set<TerminalSymbol> validShorthandOperators =
                Stream.of("=", ":=", "+=", "-=", "*=", "/=", "&=", "|=", "^=")
                      .map(TERMINAL_MAP::get)
                      .collect(Collectors.toSet());

        if (validShorthandOperators.stream().anyMatch(e -> e.symbolMatches(queue.peek()))) {
            return ParseResult.successfulParse(queue.poll());
        }
        return ParseResult.unsuccessfulParse(new SyntaxDiagnostic());
    }

    private ParseResult<Token> parsePrefixOperator(TokenQueue queue) {
        Set<TerminalSymbol> validShorthandOperators =
                Stream.of("+", "-", "!")
                      .map(TERMINAL_MAP::get)
                      .collect(Collectors.toSet());

        if (validShorthandOperators.stream().anyMatch(e -> e.symbolMatches(queue.peek()))) {
            return ParseResult.successfulParse(queue.poll());
        }
        return ParseResult.unsuccessfulParse(new SyntaxDiagnostic());
    }

    // todo refactor
    private ParseResult<LiteralNode> parseLiteral(TokenQueue queue) {
        ParseResult<PrimitiveLiteralNode> parsedPrimitiveLiteral =
                parsePrimitiveLiteral(queue.branchOff());
        if (parsedPrimitiveLiteral.isSuccessful()) {
            queue.mergeBranch();
            return ParseResult.successfulParse(parsedPrimitiveLiteral.getParseResult());
        }

        ParseResult<ArrayLiteralNode> parsedArrayLiteral = parseArrayLiteral(queue.branchOff());
        if (parsedArrayLiteral.isSuccessful()) {
            queue.mergeBranch();
            return ParseResult.successfulParse(parsedArrayLiteral.getParseResult());
        }

        ParseResult<StructDefinitionLiteralNode> parseStructDefinitionLiteral =
                parseStructDefinition(queue.branchOff());
        if (parseStructDefinitionLiteral.isSuccessful()) {
            queue.mergeBranch();
            return ParseResult.successfulParse(parseStructDefinitionLiteral.getParseResult());
        }

        ParseResult<StructInitializationLiteralNode>
                parsedStructInitializationLiteral =
                parseStructInitialization(queue.branchOff());
        if (parsedStructInitializationLiteral.isSuccessful()) {
            queue.mergeBranch();
            return ParseResult.successfulParse(parsedStructInitializationLiteral.getParseResult());
        }

        return ParseResult.unsuccessfulParse(new SyntaxDiagnostic());
    }

    private ParseResult<PrimitiveLiteralNode> parsePrimitiveLiteral(TokenQueue queue) {
        Set<TerminalSymbol> validPrimitives = Stream.of("_STRING", "_NUMBER", "_BOOLEAN")
                                                    .map(TERMINAL_MAP::get)
                                                    .collect(Collectors.toSet());
        Token nextToken = queue.peek();

        if (validPrimitives.stream().anyMatch(e -> e.symbolMatches(nextToken))) {
            queue.poll();
            PrimitiveLiteralNodeImpl node = astFactory.createPrimitiveLiteralNode();
            node.setPrimitiveValue(nextToken);
            return ParseResult.successfulParse(node);
        }

        return ParseResult.unsuccessfulParse(new SyntaxDiagnostic());
    }

    private ParseResult<TypeNode> parseType(TokenQueue queue) {
        TypeNodeImpl node = astFactory.createTypeNode();
        ParseResult<Token> parsedSimpleType = parseSimpleType(queue.branchOff());
        if (parsedSimpleType.isSuccessful()) {
            queue.mergeBranch();

            node.setType(parsedSimpleType.getParseResult());
            ParseResult<List<ExpressionNode>> parsedArrayTypeDefinitions =
                    parseOptionalArrayTypeDefinition(queue.branchOff());
            if (parsedArrayTypeDefinitions.isSuccessful()) {
                queue.mergeBranch();

                node.setArrayDimensionDefinitions(parsedArrayTypeDefinitions.getParseResult());
                return ParseResult.successfulParse(node);
            }
        }
        return ParseResult.unsuccessfulParse(new SyntaxDiagnostic());
    }

    private ParseResult<Token> parseSimpleType(TokenQueue queue) {
        Set<TerminalSymbol> validSimpleTypes = Stream.of("string", "number", "boolean", "struct", "function")
                                                     .map(TERMINAL_MAP::get)
                                                     .collect(Collectors.toSet());
        Token nextToken = queue.peek();

        if (validSimpleTypes.stream().anyMatch(e -> e.symbolMatches(nextToken))) {
            return ParseResult.successfulParse(queue.poll());
        } else {
            // check if type is identifier
            ParseResult<Token> parsedIdentifier = parseIdentifier(queue.branchOff());
            if (parsedIdentifier.isSuccessful()) {
                queue.mergeBranch();
                return ParseResult.successfulParse(parsedIdentifier.getParseResult());
            }
        }
        return ParseResult.unsuccessfulParse(new SyntaxDiagnostic());
    }

    /**
     * Parses optional array type definition on types
     *
     * @param queue
     * @return
     */
    private ParseResult<List<ExpressionNode>> parseOptionalArrayTypeDefinition(TokenQueue queue) {
        if (TERMINAL_MAP.get("[").symbolMatches(queue.peek())) {
            queue.poll();
            ParseResult<ExpressionNode> parsedExpression = parseExpression(queue.branchOff());
            if (parsedExpression.isSuccessful()) {
                queue.mergeBranch();

                if (TERMINAL_MAP.get("]").symbolMatches(queue.poll())) {
                    ParseResult<List<ExpressionNode>> parsedArrayTypeDefinitions =
                            parseOptionalArrayTypeDefinition(queue.branchOff());
                    if (parsedArrayTypeDefinitions.isSuccessful()) {
                        queue.mergeBranch();

                        List<ExpressionNode> arrayTypeDefinitions = new LinkedList<>();
                        arrayTypeDefinitions.add(parsedExpression.getParseResult());
                        arrayTypeDefinitions.addAll(parsedArrayTypeDefinitions.getParseResult());

                        return ParseResult.successfulParse(arrayTypeDefinitions);
                    }
                }
            }
            queue.skipOver(TERMINAL_MAP.get("]"));
            return ParseResult.unsuccessfulParse(new SyntaxDiagnostic());
        }
        // epsilon
        return ParseResult.successfulParse(new LinkedList<>());
    }

    /**
     * Parses an access of a value through an identifier with optional array indexing.
     *
     * @param queue
     * @return
     */
    private ParseResult<IdentifierAccessNode> parseIdentifierAccess(TokenQueue queue) {
        IdentifierAccessNodeImpl node = astFactory.createIdentifierAccessNode();
        ParseResult<Token> identifierParseResult = parseIdentifier(queue.branchOff());
        if (identifierParseResult.isSuccessful()) {
            queue.mergeBranch();
            node.setIdentifier(identifierParseResult.getParseResult());
            ParseResult<List<ExpressionNode>> parsedOptionalArrayIndices =
                    parseOptionalArrayIndex(queue.branchOff());
            if (parsedOptionalArrayIndices.isSuccessful()) {
                queue.mergeBranch();
                node.setArrayIndexes(parsedOptionalArrayIndices.getParseResult());
                return ParseResult.successfulParse(node);
            }
        }
        return ParseResult.unsuccessfulParse(new SyntaxDiagnostic());
    }

    /**
     * Parses an identifier token and writes its value into the passed node.
     *
     * @param queue
     * @param node
     * @return a parse result containing the filled passed node on success, or an unsuccessful parse on failure.
     */
    private ParseResult<Token> parseIdentifier(TokenQueue queue) {
        if (TERMINAL_MAP.get("_IDENTIFIER").symbolMatches(queue.peek())) {
            return ParseResult.successfulParse(queue.poll());
        }
        return ParseResult.unsuccessfulParse(new SyntaxDiagnostic());
    }

    /**
     * @param queue
     * @return
     */
    private ParseResult<List<ExpressionNode>> parseOptionalArrayIndex(TokenQueue queue) {
        if (TERMINAL_MAP.get("[").symbolMatches(queue.peek())) {
            queue.poll();

            ParseResult<ExpressionNode> parsedExpression = parseExpression(queue.branchOff());
            if (parsedExpression.isSuccessful()) {
                queue.mergeBranch();

                if (TERMINAL_MAP.get("]").symbolMatches(queue.poll())) {
                    ParseResult<List<ExpressionNode>> parsedOptionalArrayIndex =
                            parseOptionalArrayIndex(queue.branchOff());

                    if (parsedOptionalArrayIndex.isSuccessful()) {
                        queue.mergeBranch();

                        List<ExpressionNode> arrayIndices = new LinkedList<>();
                        arrayIndices.add(parsedExpression.getParseResult());
                        arrayIndices.addAll(parsedOptionalArrayIndex.getParseResult());
                        return ParseResult.successfulParse(arrayIndices);
                    }
                }
            }
            queue.skipOver(TERMINAL_MAP.get("]"));
            return ParseResult.unsuccessfulParse(new SyntaxDiagnostic());
        }
        // epsilon
        return ParseResult.successfulParse(new LinkedList<>());
    }

    // TODO refactor to reduce nesting
    private ParseResult<ExportsNode> parseExport(TokenQueue queue) {
        ExportsNodeImpl node = astFactory.createExportNode();

        if (TERMINAL_MAP.get("export").symbolMatches(queue.peek())) {
            queue.poll();
            if (TERMINAL_MAP.get("{").symbolMatches(queue.poll())) {
                ParseResult<IdentifierAccessNode> parsedIdentifierAccess = parseIdentifierAccess(queue.branchOff());
                if (parsedIdentifierAccess.isSuccessful()) {
                    queue.mergeBranch();

                    ParseResult<List<IdentifierAccessNode>> parsedAdditionalElements =
                            parseAdditionalExportedElements(queue.branchOff());
                    if (parsedAdditionalElements.isSuccessful()) {
                        queue.mergeBranch();

                        if (TERMINAL_MAP.get("}").symbolMatches(queue.poll())) {
                            List<IdentifierAccessNode> exports = new LinkedList<>();
                            exports.add(parsedIdentifierAccess.getParseResult());
                            exports.addAll(parsedAdditionalElements.getParseResult());

                            node.setExports(exports);
                            return ParseResult.successfulParse(node);
                        }
                    }
                }
            }
            // skip illegal symbols to the next 'closing' separator
            queue.skipOver(TERMINAL_MAP.get("}"));
            return ParseResult.unsuccessfulParse();
        }
        // epsilon
        node.setExports(new LinkedList<>());
        return ParseResult.successfulParse(node);
    }

    /**
     * Parses 2..n exported elements and writes them into the passed node
     *
     * @param queue
     * @param node
     * @return
     */
    private ParseResult<List<IdentifierAccessNode>> parseAdditionalExportedElements(TokenQueue queue) {
        if (TERMINAL_MAP.get(",").symbolMatches(queue.peek())) {
            queue.poll();
            ParseResult<IdentifierAccessNode> parsedIdentifierAccess = parseIdentifierAccess(queue.branchOff());
            if (parsedIdentifierAccess.isSuccessful()) {
                queue.mergeBranch();
                ParseResult<List<IdentifierAccessNode>> parsedAdditionalExports =
                        parseAdditionalExportedElements(queue.branchOff());
                if (parsedAdditionalExports.isSuccessful()) {
                    queue.mergeBranch();

                    List<IdentifierAccessNode> exports = new LinkedList<>();
                    exports.add(parsedIdentifierAccess.getParseResult());
                    exports.addAll(parsedAdditionalExports.getParseResult());

                    return ParseResult.successfulParse(exports);
                }
            }
            return ParseResult.unsuccessfulParse(new SyntaxDiagnostic());
        }
        // epsilon
        return ParseResult.successfulParse(new LinkedList<>());
    }

    @Generated("by BAALL-Parser-Gen")
    public static class TerminalSymbol {
        private final String value;

        private final TerminalType type;

        public TerminalSymbol(TerminalType type, String value) {
            this.type = type;
            this.value = value;
        }

        public boolean symbolMatches(Token symbol) {
            if (!TerminalType.ANY.equals(type)) {
                return value.equals(symbol.value());
            }
            if (type.equals(symbol.type())) {
                return !type.hasExactValueMatching() || value.equals(symbol.type());
            }
            return false;
        }

        public TerminalType getType() {
            return type;
        }

        public String getValue() {
            return value;
        }
    }

}
