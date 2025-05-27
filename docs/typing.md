# Typing system

BAALL follows a strict, static and explicit typing model. The language features builtin types. While primitive types are
the basis of the typesystem, the definition of composition types allows for very flexible typing. Every field or
function needs to declare its type.

The BAALL typesystem differentiates between simple types and composition types. A simple type is either
a [primitive type](glossary.md#user-types) or a [user type](glossary.md#user-types). Composition types are
either [function types](glossary.md#function-type) or [list types](glossary.md#list-type).

BAALL embraces a no-surprises philosophy when it comes to typing. This means that BAALL does not feature type coercion —
values will never be converted between types.

```baall
number: myNumber := 5;
string: myString := "The number field has the value:" + num_to_str(myNumber);
```

###### User types are constants

User types can only be declared as constants. This makes sense because the compiler enforces a strict type model.
Declaring user types as variable fields will result in a compilation error.

```baall
struct: myValidType := (...);  // ✅ this is correct
struct: myInvalidType = (...); // ❌ compilation error
```

###### Use-before-declare

Functions and user types support use-before-declare. This is typesafe and none-safe behavior on user types because
structs and exceptions are constants and therefore are none-safe.

With functions there needs to be a restriction. Only
constant function declarations are use-before-declare because they are known at compile time and functions are none-safe
by nature. Function variables are runtime assignments and are not known before they are actually assigned.

```baall
struct: myType := (...);                 // ✅ supports use-before-declare
number<>: myConstantFunction := () {...} // ✅ supports use-before-declare
number<>: myVariableFunction = () {...}  // ❌ does not support use-before-declare
```

The compiler enforces this behavior and will raise a compilation exception if you try to use a variable function before
it is declared.

###### Of scopes and types

As mentioned in [Use-before-declare](#use-before-declare) BAALL types can be used before they appear in your code. This
behavior extends across scope borders. BAALL's scoping model disallows the declaration of elements with identical
identifiers within a major scope. This forces all use-before-declare types to be unambiguous within their major scope
meaning there cannot be an element with the same identifier in this major scope.
