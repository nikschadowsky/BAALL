package de.nikschadowsky.baall.compiler.syntax.error;

import de.nikschadowsky.baall.compiler.output.error.CompileException;

public class SyntaxException extends CompileException {

    public SyntaxException(SyntaxDiagnostic diagnostic) {
        super(diagnostic);
    }
}
