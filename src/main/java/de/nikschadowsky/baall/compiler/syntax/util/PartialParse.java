package de.nikschadowsky.baall.compiler.syntax.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that a parser method returns either a complete and valid node, a partially parsed node, or an unsuccessful
 * parse. A method should be a partially parsing method if any of its components is partially parsed.
 *
 * @since 05.08.2024
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface PartialParse {
}
