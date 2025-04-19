# Scoping

Every element inside a BAALL program is assigned a scope. This scope defines where this element can be referenced in
code. This is important since BAALL does not support declaration lifting. Variables and constants can only be used in
the declaring or any inner scopes after the element was declared. Functions, structs, and exceptions are an exception to
this behavior. They support use-before-declare. See the section [use-before-declare](typing.md#use-before-declare) for
more
information.

###### Scoping model

BAALL uses a two-tiered scoping model built around the concepts of major and minor scopes. Every language construct that
introduces a new structural block — such as a function, loop, or conditional — defines a major scope. Within each of
these
major scopes, minor scopes are used to structure more granular visibility, such as within function bodies, branches, or
loop blocks.

Each minor scope is strictly nested within its enclosing major scope and always has access to declarations made in that
major scope without restriction. This ensures a predictable and hierarchical model where inner logic can freely depend
on outer context, while still supporting structured nesting and variable shadowing when needed.

Because every language element is assigned a major scope, the scoping hierarchy remains clear and consistent, making it
easier to reason about variable visibility and name resolution at all levels of the program.

*todo: element resolution chain*

###### Scope elevation

If you want to reference an element from a parent scope relative to the current one, you can use the scope elevation
operator `$`. This operator applies only to identifiers.

A single `$` looks for a matching name one major scope above the current one. Each additional `$` moves the search one
level higher. The compiler raises an error if you attempt to elevate beyond the root of your program.

```baall
number: copyFromElevated := $$myNumber; // looks for myNumber two major scopes higher.
```

When using the `$` operator, the compiler will only search in that specific elevated scope level, and not continue
searching elsewhere. This means you cannot use `$` to search multiple ancestor scopes at once — the elevation is exact
and
not recursive beyond the level specified. 