package de.nikschadowsky.baall.compiler._utility;


import de.nikschadowsky.baall.compiler.symbol.Token;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.ASTNodeFactory;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.access.ElementAccessNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.access.IdentifierNodeImpl;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.expression.ExpressionNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.program.*;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.StatementNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.assignment.ConstantDeclarationNodeImpl;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.assignment.DeclarationNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.typing.TypeNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.FieldNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.literal.FunctionDefinitionNodeImpl;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.literal.PrimitiveLiteralNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.literal.PrimitiveLiteralNodeImpl;
import de.nikschadowsky.baall.compiler.syntax.tree.util.NodeDiagnosticCollector;
import de.nikschadowsky.baall.compiler.tokenizer.TokenType;

import java.util.Arrays;
import java.util.List;

/**
 * @since 03.02.2025
 */
public class AstTestBuilder {

    private static final ASTNodeFactory astNodeFactory = new ASTNodeFactory(new NodeDiagnosticCollector());


    public static ProgramNode newProgramNode(ImportsNode imports, StatementsNode statements, ExportsNode exports) {
        ProgramNodeImpl node = astNodeFactory.createProgramNode();
        node.setImports(imports);
        node.setStatements(statements);
        node.setExports(exports);
        return node;
    }

    public static ImportsNode newImportsNode(String... imports) {
        ImportsNodeImpl node = astNodeFactory.createImportNode();
        node.setImports(Arrays.stream(imports).map(s -> new Token(TokenType.STRING, s, 0, 0)).toList());
        return node;
    }

    public static StatementsNode newStatementsNode(StatementNode... statements) {
        StatementsNodeImpl node = astNodeFactory.createStatementsNode();
        node.setStatements(Arrays.stream(statements).toList());
        return node;
    }

    public static DeclarationNode newFunctionDefinition(TypeNode returnType, String functionName, List<FieldNode> parameters, StatementsNode statements) {
        ConstantDeclarationNodeImpl declarationNode = astNodeFactory.createConstantDeclarationNode();
        declarationNode.setType(returnType);
        IdentifierNodeImpl functionNameNode = astNodeFactory.createIdentifierNode();
        functionNameNode.setName(new Token(TokenType.IDENTIFIER, functionName, 0, 0));
        declarationNode.setIdentifier(functionNameNode);

        FunctionDefinitionNodeImpl functionDefinitionNode = astNodeFactory.createFunctionDefinitionNode();
        functionDefinitionNode.setParameters(parameters);
        functionDefinitionNode.setFunctionBody(statements);

        declarationNode.setInitializationValue(functionDefinitionNode);
        return declarationNode;
    }

    public static ExportsNode newExportsNode(String namespace, ElementAccessNode... exports) {
        ExportsNodeImpl node = astNodeFactory.createExportNode();
        IdentifierNodeImpl namespaceNode = astNodeFactory.createIdentifierNode();
        namespaceNode.setName(new Token(TokenType.STRING, namespace, 0, 0));
        node.setNamespace(namespaceNode);
        node.setExports(Arrays.stream(exports).toList());
        return node;
    }

    public static ExpressionNode newNumberExpression(int number) {
        PrimitiveLiteralNodeImpl node = astNodeFactory.createPrimitiveLiteralNode();
        node.setPrimitiveType(PrimitiveLiteralNode.PrimitiveType.NUMBER);
        node.setPrimitiveValue(new Token(TokenType.NUMBER, String.valueOf(number), 0, 0));

        return node;
    }

}
