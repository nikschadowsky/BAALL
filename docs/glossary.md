# Glossary

###### **primitive type**:

A primitive type is one of the following BAALL types:

- number
- string
- boolean
- struct

###### **simple type**:

A simple type is either a **primitive type** or a **user defined struct type**.

###### **function type**:

A function type is a type used for functions. It contains the return type and all argument types in diamonds `<>`
after that.

Example:

`number<string<>>`

A function type that returns a number and expects a parameterless string function.

###### **list type**:

A list type is a type declaring something as a variable size container of some other type. A list type is declared by 
appending square brackets `[]` behind a type declaration.

Example:

`boolean[]`

A list type of boolean types. This type declaration can be of any complexity.