package de.nikschadowsky.baall.compiler.symbol;

/**
 * An OutOfScopeException occurs when you try to access a scope outside the scope hierarchy. Most often this exception
 * is thrown when you try to access the parent scope, but it does not exist.
 */
public class OutOfScopeException extends IllegalStateException {
    public OutOfScopeException(String message) {
        super(message);
    }
}
