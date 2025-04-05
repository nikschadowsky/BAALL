package de.nikschadowsky.baall.compiler._utility;


import de.nikschadowsky.baall.compiler.symbol.LineInformation;
import de.nikschadowsky.baall.compiler.symbol.Token;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.ASTNodeFactory;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.access.*;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.controlstructures.*;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.expression.*;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.program.*;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.StatementNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.assignment.*;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.controlstatement.BreakStatementNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.controlstatement.ContinueStatementNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.controlstatement.ReturnStatementNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.controlstatement.ReturnStatementNodeImpl;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.controlstatement.exceptionhandling.*;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.typing.*;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.*;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.literal.*;
import de.nikschadowsky.baall.compiler.syntax.tree.util.NodeDiagnosticCollector;
import de.nikschadowsky.baall.compiler.tokenizer.TokenType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

/**
 * @since 03.02.2025
 */
public class AstTestBuilder {

    private static final ASTNodeFactory astNodeFactory = new ASTNodeFactory(new NodeDiagnosticCollector());
    private static final LineInformation UNKNOWN = new LineInformation(-1, -1);

    public static ProgramNode program(ImportsNode imports, StatementsNode statements, ExportsNode exports) {
        ProgramNodeImpl node = astNodeFactory.createProgramNode();
        node.setImports(imports);
        node.setStatements(statements);
        node.setExports(exports);
        return node;
    }

    public static ImportsNode imports(String... imports) {
        ImportsNodeImpl node = astNodeFactory.createImportNode();
        if (imports != null) {
            node.setImports(Arrays.stream(imports)
                                  .map(s -> new Token(TokenType.STRING, s, UNKNOWN))
                                  .toList()
            );
        }
        return node;
    }

    public static StatementsNode statements(StatementNode... statements) {
        StatementsNodeImpl node = astNodeFactory.createStatementsNode();
        if (statements != null) {
            node.setStatements(Arrays.stream(statements).toList());
        }
        return node;
    }

    public static @NotNull ExportsNode exports(@Nullable String namespace, ElementAccessNode... exported) {
        ExportsNodeImpl node = astNodeFactory.createExportNode();
        node.setNamespace(identifier(namespace));
        node.setExports(Arrays.stream(exported).toList());
        return node;
    }

    public static VariableDeclarationNode variableDeclaration(TypeNode type, String name, @Nullable ExpressionNode initialNode) {
        VariableDeclarationNodeImpl node = astNodeFactory.createVariableDeclarationNode();
        node.setType(type);
        node.setIdentifier(identifier(name));
        node.setInitializationValue(initialNode);
        return node;
    }

    public static ConstantDeclarationNode constantDeclaration(TypeNode type, String name, ExpressionNode initialNode) {
        ConstantDeclarationNodeImpl node = astNodeFactory.createConstantDeclarationNode();
        node.setType(type);
        node.setIdentifier(identifier(name));
        node.setInitializationValue(initialNode);
        return node;
    }

    public static VariableReassignmentNode variableReassignment(ElementAccessNode var, String operator, ExpressionNode newValue) {
        VariableReassignmentNodeImpl node = astNodeFactory.createVariableReassignmentNode();
        node.setIdentifier(var);
        node.setOperator(operator(operator));
        node.setValueExpression(newValue);
        return node;
    }

    public static UnaryExpressionNode unaryExpression(ElementAccessNode element, String operator, boolean isPrefix) {
        UnaryExpressionNodeImpl node = astNodeFactory.createUnaryExpressionNode();
        node.setElement(element);
        node.setOperator(operator(operator));
        node.setIsPrefix(isPrefix);
        return node;
    }

    public static ScopeElevationNode scopeElevation(ElementAccessNode inner) {
        ScopeElevationNodeImpl node = astNodeFactory.createScopeElevationNode();
        node.setInner(inner);
        return node;
    }

    public static MemberReferenceNode memberReference(ElementAccessNode self, ElementAccessNode inner) {
        MemberReferenceNodeImpl node = astNodeFactory.createMemberReferenceNode();
        node.setSelf(self);
        node.setInner(inner);
        return node;
    }

    public static IndexedAccessNode indexedAccess(ElementAccessNode inner, ExpressionNode index) {
        IndexedAccessNodeImpl node = astNodeFactory.createIndexedAccessNode();
        node.setInner(inner);
        node.setIndex(index);
        return node;
    }

    public static ListTypeNode listType(TypeNode inner) {
        ListTypeNodeImpl node = astNodeFactory.createListTypeNode();
        node.setInner(inner);
        return node;
    }

    public static FunctionTypeNode functionType(TypeNode returnType, TypeNode... parameters) {
        FunctionTypeNodeImpl node = astNodeFactory.createFunctionTypeNode();
        node.setInnerType(returnType);
        if (parameters != null) {
            node.setParameterTypes(Arrays.stream(parameters).toList());
        }
        return node;
    }

    public static PrimitiveTypeNode primitiveType(PrimitiveTypeNode.Kind kind) {
        PrimitiveTypeNodeImpl node = astNodeFactory.createPrimitiveTypeNode();
        node.setKind(kind);
        return node;
    }

    public static IdentifierTypeNode identifierType(ElementAccessNode element, boolean isNonesafe) {
        IdentifierTypeNodeImpl node = astNodeFactory.createIdentifierTypeNode();
        node.setType(element);
        node.setNoneSafe(isNonesafe);
        return node;
    }

    public static IdentifierNode identifier(String name) {
        IdentifierNodeImpl node = astNodeFactory.createIdentifierNode();
        node.setName(new Token(TokenType.IDENTIFIER, name, UNKNOWN));
        return node;
    }


    public static OperatorNode operator(String operator) {
        OperatorNodeImpl node = astNodeFactory.createOperatorNode();
        node.setOperator(new Token(TokenType.OPERATOR, operator, UNKNOWN));
        return node;
    }

    public static BreakStatementNode breakStatement() {
        return astNodeFactory.createBreakStatementNode();
    }

    public static ContinueStatementNode continueStatement() {
        return astNodeFactory.createContinueStatementNode();
    }

    public static ReturnStatementNode returnStatement(ExpressionNode returnValue) {
        ReturnStatementNodeImpl node = astNodeFactory.createReturnStatementNode();
        node.setReturnExpression(returnValue);
        return node;
    }

    public static FunctionCallNode functionCall(ElementAccessNode function, ExpressionNode... arguments) {
        FunctionCallNodeImpl node = astNodeFactory.createFunctionCallNode();
        node.setFunctionIdentifier(function);
        if (arguments != null) {
            node.setArguments(Arrays.stream(arguments).toList());
        }
        return node;
    }

    public static RaiseStatementNode raiseStatement(ElementAccessNode exception, ExpressionNode... arguments) {
        RaiseStatementNodeImpl node = astNodeFactory.createRaiseStatementNode();
        node.setException(functionCall(exception, arguments));
        return node;
    }

    public static FieldNode field(TypeNode type, String name) {
        FieldNodeImpl node = astNodeFactory.createFieldNode();
        node.setType(type);
        node.setIdentifier(identifier(name));
        return node;
    }

    public static FunctionDefinitionNode functionDefinition(StatementsNode body, FieldNode... parameters) {
        FunctionDefinitionNodeImpl node = astNodeFactory.createFunctionDefinitionNode();
        node.setFunctionBody(body);
        if (parameters != null) {
            node.setParameters(Arrays.stream(parameters).toList());
        }
        return node;
    }

    public static ListLiteralNode listLiteral(ExpressionNode... elements) {
        ListLiteralNodeImpl node = astNodeFactory.createListLiteralNode();
        if (elements != null) {
            node.setElements(Arrays.stream(elements).toList());
        }
        return node;
    }

    public static StringLiteralNode stringLiteral(String value) {
        StringLiteralNodeImpl node = astNodeFactory.createStringLiteralNode();
        node.setValue(new Token(TokenType.STRING, value, UNKNOWN));
        return node;
    }

    public static NumberLiteralNode numberLiteral(String value) {
        NumberLiteralNodeImpl node = astNodeFactory.createNumberLiteralNode();
        node.setValue(new Token(TokenType.NUMBER, value, UNKNOWN));
        return node;
    }

    public static BooleanLiteralNode booleanLiteral(String value) {
        BooleanLiteralNodeImpl node = astNodeFactory.createBooleanLiteralNode();
        node.setValue(new Token(TokenType.BOOLEAN, value, UNKNOWN));
        return node;
    }

    public static StructDefinitionLiteralNode structDefinitionLiteral(FieldNode... fields) {
        StructDefinitionLiteralNodeImpl node = astNodeFactory.createStructDefinitionLiteralNode();
        if (fields != null) {
            node.setFields(Arrays.stream(fields).toList());
        }
        return node;
    }

    public static StructNoneLiteralNode structNoneLiteral() {
        return astNodeFactory.createStructNoneLiteralNode();
    }

    public static BinaryExpressionNode binaryExpression(TermNode left, String operator, ExpressionNode right) {
        BinaryExpressionNodeImpl node = astNodeFactory.createBinaryExpressionNode();
        node.setOperator(operator(operator));
        node.setLeftOperand(left);
        node.setRightOperand(right);
        return node;
    }

    public static ParenthesizedExpressionNode parenthesizedExpression(ExpressionNode inner) {
        ParenthesizedExpressionNodeImpl node = astNodeFactory.createParenthesizedExpressionNode();
        node.setInnerExpression(inner);
        return node;
    }

    public static PrefixOperationNode prefixOperation(String operator, ExpressionNode inner) {
        PrefixOperationNodeImpl node = astNodeFactory.createPrefixOperationNode();
        node.setOperator(operator(operator));
        node.setOperand(inner);
        return node;
    }

    public static ConditionalNode conditional(ExpressionNode condition, StatementsNode body, ConditionalNode elseStatement) {
        ConditionalNodeImpl node = astNodeFactory.createConditionalNode();
        node.setCondition(condition);
        node.setConditionBranch(condition == null ? ConditionalNode.ConditionBranch.ELSE : ConditionalNode.ConditionBranch.IF);
        node.setThenBlock(body);
        node.setElseBranch(elseStatement);
        return node;
    }

    public static ForLoopNode forLoop(String identifier, ExpressionNode start, ExpressionNode end, StatementsNode body, @Nullable ReassignmentNode stepper) {
        ForLoopNodeImpl node = astNodeFactory.createForLoopNode();
        node.setIdentifier(variableDeclaration(primitiveType(PrimitiveTypeNode.Kind.NUMBER), identifier, null));
        node.setStartIndex(start);
        node.setEndIndex(end);
        node.setBody(body);
        node.setOptionalStepperStatement(stepper);
        return node;
    }

    public static WhileLoopNode whileLoop(ExpressionNode condition, StatementsNode body) {
        WhileLoopNodeImpl node = astNodeFactory.createWhileLoopNode();
        node.setCondition(condition);
        node.setBody(body);
        return node;
    }

    public static TryStatementNode tryStatement(StatementsNode body, EnsureStatementNode ensure, InterceptStatementNode... intercepts) {
        TryStatementNodeImpl node = astNodeFactory.createTryStatementNode();
        node.setBody(body);
        node.setEnsureBlock(ensure);
        if (intercepts != null) {
            node.setInterceptBlocks(Arrays.stream(intercepts).toList());
        }
        return node;
    }

    public static InterceptStatementNode interceptStatementNode(String identifier, StatementsNode body, TypeNode interceptedTypes) {
        InterceptStatementNodeImpl node = astNodeFactory.createInterceptStatementNode();
        node.setBody(body);
        node.setCaughtException(variableDeclaration(interceptedTypes, identifier, null));
        return node;
    }

    public static EnsureStatementNode ensureStatement(StatementsNode body) {
        EnsureStatementNodeImpl node = astNodeFactory.createEnsureStatementNode();
        node.setBody(body);
        return node;
    }
}
