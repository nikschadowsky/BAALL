# Glossary

###### **simple type**:

A simple type is either a **primitive type** or a **user defined struct type**.

###### **function type**:

A function type is a type used for functions. It contains the return type and all argument types in diamonds `<>`
after that.

Example:

`number<string<>>: myFunction ...`

A function type that returns a number and expects a parameterless string function.

###### **list type**:

A list type is a type declaring something as a variable size container of some other type. A list type is declared by
appending square brackets `[]` behind a type declaration.

Example:

`boolean[]: myBooleanList ...`

A list type of boolean types. This type declaration can be of any complexity.

###### **primitives:**

There are five primitive types in version 1.0 of BAALL:

1. number — a numeric value that represents a decimal number that can be compared to a double in other programming
   languages.
2. boolean — a truthness value that can either be `true` or `false`, or `1` and `0`.
3. string — a sequence of characters.
4. struct — a user-defined tuple of fields that can be used as a type.
5. exception — a user-defined exception type which shares its structure and syntax with a struct but can be used in a
   raise statement or in an intercept block.

Primitive types are none-safe as standard. This behavior cannot be altered.

###### **user types**

Declaring a struct or exception constant defines a user type. This type can be used in the same way as the other BAALL
types. So you can build complex list or function types with them.

Example:

`MyUserType: myUserTypeField ...`

Since user types do not define a default value, they are inherently not none-safe. See more in section [typing](typing.md). 