package de.nikschadowsky.baall.compiler.syntax.analysis;

import de.nikschadowsky.baall.compiler._utility.TokenQueueTestBuilder;
import de.nikschadowsky.baall.compiler.symbol.TokenQueue;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.ASTNodeFactory;
import de.nikschadowsky.baall.compiler.syntaxtree.util.NodeDiagnosticCollector;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @since 13.08.2024
 */
class AuxiliaryParserTest {

    private ASTNodeFactory astFactory = new ASTNodeFactory(new NodeDiagnosticCollector());

    @Test
    void parseFieldDeclarations() {
    }

    @Test
    void parseFieldDeclaration() {
    }

    @Test
    void parseArgumentList() {
    }

    @Test
    void parseBinaryOperator() {
    }

    @Test
    void parseUnaryOperator() {
    }

    @Test
    void parseShorthandOperator() {
    }

    @Test
    void parseIdentifier() {
        TokenQueue queue = new TokenQueueTestBuilder().identifier("MyIdentifier").number("1234").build();

        // parsing identifier
        assertTrue(AuxiliaryParser.parseIdentifier(queue, astFactory).isSuccessful());
        // parsing number
        assertTrue(AuxiliaryParser.parseIdentifier(queue, astFactory).isUnsuccessful());

    }

    @Test
    void parseType() {
    }

    @Test
    void parseSimpleType() {
    }

    @Test
    void parseArrayTypeDefinitions() {
    }

    @Test
    void parseIdentifierAccess() {
    }

    @Test
    void parseOptionalArrayIndex() {
    }

    @Test
    void parseIdentifierAccesses() {
    }

    @Test
    void parseCodeBlock() {
    }
}