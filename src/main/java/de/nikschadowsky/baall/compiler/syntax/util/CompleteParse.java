package de.nikschadowsky.baall.compiler.syntax.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that a parser method returns either a complete and valid node, or an unsuccessful parse.
 *
 * @since 05.08.2024
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface CompleteParse {
}
