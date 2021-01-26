# LScript Documentation

LScript is a high-level interpreted programming language, with a similar syntax to typed languages such as Java and C#, while keeping a smoother feel.

[Syntax Rules](#Syntax-Rules)  
&nbsp; &nbsp; &nbsp; [Language Operators](#language-operators-are-as-follows)  
[Types](#Types)  
&nbsp; &nbsp; &nbsp; [Dynamic Types](#dynamic-types)  
&nbsp; &nbsp; &nbsp; [Numbers](#Numbers)  
&nbsp; &nbsp; &nbsp; [Collections](#Collections)  
[Default Values](#Default-Values)  
[Conditionals](#Conditionals)  
&nbsp; &nbsp; &nbsp; [Syntax](#conditional-syntax)  
[Defining Functions](#Function-Definitions)  
&nbsp; &nbsp; &nbsp; [Syntax](#function-definition-syntax)  
&nbsp; &nbsp; &nbsp; [Signature](#declaration-signature)  
&nbsp; &nbsp; &nbsp; [Contents](#function-contents)  
[Calling Functions](#function-calling)  
&nbsp; &nbsp; &nbsp; [Syntax](#call-syntax)  
[Loops](#Loops)  
&nbsp; &nbsp; &nbsp; [Syntax](#loop-syntax)  
&nbsp; &nbsp; &nbsp; [For Loops](#for-loops)  
&nbsp; &nbsp; &nbsp; [While Loops](#while-loops)  
[Comments](#Comments)  
[Importing](#Importing)  
&nbsp; &nbsp; &nbsp; [Syntax](#import-syntax)  
[Built-in Functions](./Built-ins.md)  
[Indexing](#indexing)  
&nbsp; &nbsp; &nbsp; [Syntax](#index-syntax)  
&nbsp; &nbsp; &nbsp; [Basic Indexing](#basic-indexing)  
&nbsp; &nbsp; &nbsp; [Reverse Indexing](#reverse-indexing)  
&nbsp; &nbsp; &nbsp; [Slicing](#slicing)  
[Files](#files)

&nbsp;

## Syntax Rules:
- Conditionals, loops, and function declarations must all be surrounded with braces `{}`.
    - All other lines, excluding [comments](#Comments), must end with a semicolon `;`.
- To format a string inline, you can either use the [format](./Built-ins.md#format) built-in function, or place a `%` symbol before a variable name to insert its string value.
    - Example:
    ```
    str language = "LScript";
    print("My favorite programming language is %language!");
    ```
- Strings may start with either single or double quotes, but must end with the same symbol they started with.
    - The escape character `\` may also be used in strings to signify line breaks (`\n`), tabs(`\t`), or to display the following character, regardless of what that character is.
    - Example:
    ```
    str myFirstString = "This string starts with single quotes!";
    str mySecondString = 'This one has single quotes.';
    str escapeString = "And this one has a \n newline.";
    ```
- When used in a conditional or as a boolean, any non-boolean value will return `true` unless it is equal to `null`.
- ### Language operators are as follows:
    - ### `+` : Addition
        - Used for appending to lists/maps [see also: [append](./Built-ins.md#append)], string manipulation, and arithmetic
    - ### `-` : Subtraction
        - Used for removing from lists/maps [see also: [pop](./Built-ins.md#pop), [remove](./Built-ins.md#remove)], string manipulation, and arithmetic
    - ### `*` : Multiplication
        - Used for both string multiplication and arithmetic
    - ### `/` : Division
        - Used for both string multiplication and arithmetic
    - ### `%` : Modulo
        - Used for arithmetic remainders
    - ### `^` : Power
        - Used for arithmetic powers
    - ### Boolean Operators (return `true` or `false`)
        - ### `==` : Boolean Equality
            - A check for equality between two values
        - ### `!` : Boolean Negator
            - Reverses the subsequent boolean value
        - ### `!=` : Boolean Inequality
            - A check for inequality between two values (opposite of boolean equality)
        - ### `<` : Less Than
            - Returns `true` if the value to the left of the operator is less than the value on the right
        - ### `>` : Greater Than
            - Returns `true` if the value to the left of the operator is greater than the value on the right
        - ### `<=` : Less Than or Equal
            - Returns `true` if the value to the left of the operator is less than or equal to the value on the right
        - ### `>=` : Greater Than or Equal
            - Returns `true` if the value to the left of the operator is greater than or equal to the value on the right
        - ### `&` : Logical And
            - Returns `true` if the values to either side of the operator return `true`
        - ### `|` : Logical Or
            - Returns `true` if the either or both of the values next to the operator return `true`

## Types
The recognized types in LScript are as follows:
- ### Dynamic Types 
    - #### `var`
        - a basic type for dynamic values 
    - #### `const`
        - a constant variable


- ### Numbers
    - #### `int`
        - an integer value, ranging from -2,147,483,648 to 2,147,483,647
    - #### `float`
        - a floating-point number
    - #### `num`
        - represents an int or float
    - #### `byte`
        - a single byte of data, ranging from `0x00` to `0xff`


- ### Collections
    - #### `list`
        - a collection of values of any type, surrounded by [ and ].
    - #### `map`
        - a collection that maps keys of any type to values of any type, surrounded by { and }.
            - Keys and values are separated by colons `:`
            - Key-value pairs are separated by commas `,`


-  #### `str`
    - a string of characters
-  #### `bool`
    - a boolean representing either `true` or `false`
-  #### `file`
    - an external file, opened using one or more [open modes](#files), for writing to and/or reading from
-  #### `void`
    - represents the absence of a value
-  #### `nullType`
    - only used by `null`; represents an empty value
-  #### `function`
    - a function that can be called

&nbsp;

## Default Values
When a variable is initialized, most of the time, it will default to `null`.  
However, certain types will default to values:
- `ints`, `nums`, `floats`, and `bytes` will all default to 0.
- `strs` will default to "", or an empty string.
- `bools` will default to `false`.
- `lists` and `maps` will default to `[]` and `{}`, respectively (empty lists/maps).

&nbsp;

## Conditionals
LScript currently has support for conditionals through if, elif, and else statements.
### Conditional Syntax
```
int i = 1;
if (i == 0) {
    print("i equals 0");
} elif (i == 1) {
    print("i equals 1");
} else {
    print("i does not equal 0 or 1");
}
```
```
Output: i equals 1
```

If and elif statements require a boolean expression (or other value for a null-check), wrapped in parentheses. Additionally, all three statements require the use of braces `{}` around internal statements.

## Function Definitions
LScript currently has support for defining functions, both anonymously and with identifiers.
### Function Definition Syntax
```
// This function adds two numbers and returns the result.
func add (int a, int b) : int {
    return a + b;
}

// This function is created anonymously and stored in a variable.
function printHi = func () {
    print("Hello!");
}
```

&nbsp;

### Declaration Signature

- To define a function in LScript, the `func` keyword is required.
- Optionally, after `func`, an identifier can be used to [call the function](#function-calling) in the future.
- Following this, all functions need a pair of parentheses `()`,containing all arguments for the function (if it has any).
    - All arguments must be defined with types (can be var). Constant values are not allowed as arguments.
- The final piece of a function definition is return types.
    - If no return types are desired, this is not required; the default return type is void.
    - Any number of types can be declared after a colon `:`, separated by commas - However, they must all be returned in every return statement.

### Function Contents
- Function contents must be wrapped in braces `{}`.
- Functions can run any code, including calling other functions, setting variables, or doing operations - but all variable inside of a function are lost after it is finished unless the are returned.
- At any point in a function, value(s) can be returned to the call by using:  
    `return [value(s)];`
    - every return statement in a function must return the same value type(s) as are declared in the [signature](#declaration-signature).


## Function Calling
Any defined function, including [those built into LScript](#built-in-function), can be easily called using parentheses `()`.

### Call Syntax
```
// This function adds two numbers and returns the result.
func add (int a, int b) : int {
    return a + b;
}

print(add(2, 3));
```
```
Output: 5
```
To call a function, use the function's identifier followed by parentheses.
- Inside the parenthese, you can place arguments matching the types defined in the function's [definition](#function-definitions).
- After the function has finished, the call will be evaluated as its return. (of the type(s) defined in its declaration)
    - If the function returns multiple values, they will be returned as a list, which can easily be assigned inline to multiple variables
- Anonymous functions stored in variables can be called the same way, by simply putting parameters in parentheses after the variable's name.

## Loops
LScript has support for both for and while loops.
### Loop Syntax
```
// Basic for loop
for (int i = 0, 5) {
    print(i);
}
print();

// Basic while loop
bool running = true;
int x = 0;
while (running) {
    x++;
    if (x % 3 == 0) {
        continue;
    }
    print(x);
    if (x >= 10) {
        break;
    }
}
```
```
Output:
0
1
2
3
4

1
2
4
5
7
8
10
```
For a more complete example of while loop syntax check out [this example](../examples/numberguessgame.ls).

### For Loops
- For loops iterate through some amount of code a number of times, as a iterator variable increments.
- In LScript, for loops are defined using the syntax:  
 `for (int [name] = [start position], [end position(, [step size (default 1)])`
    - The whole expression must be wrapped in parentheses `()`.
    - The iterator variable must be of type int, num, or var, and the start position, end position, and step must all be integers.
- The rest of the loop is wraped in braces `{}`.

### While Loops
- While loops continue to iterate until a defined boolean condition evaluates to false.
- In LScript, while loops are defined using the syntax:  
`while ([boolean expression])`
    - The expression must be wrapped in parentheses `()`.
- The rest of the loop is wraped in braces `{}`.



All variables created in both types of loops are temporary; they will reset during each iteration  unless they are defined beforehand.

Either type of loop can use 'break' or 'continue' to break out of the loop of continue to the next iteration, respectively.
## Comments
Both single-line and block comments are supported in LScript. Single-line comments are acheived using double forward slash `//`. These comments persist until a `\n` character is reached.

Block comments use `/*` to signify the beginning of a comment, and `*/` for the end.  
Example:
```
// I'm a single-line comment! My last character is the period.
/* Block comments
    will not
    end until the
    end signifier. */
```

## Importing
LScript supports importing code from local .ls files.
### Import Syntax
```
// This statement imports all symbols from local file 'test1.ls', making all variables in 'test' accessible from here
from 'test1' import *;

// This statement imports the file 'test2' as a group of symbols, which can now be accessed through the object 't'
import 'test2' as t;

// If 'test2.ls' defined a variable x equal to 10, this would print 10
print(t.x);
```

There are two types of import statements in LScript: object-based and symbol-based.

Object based importing, which uses the syntax  
`import [filename] (as [object name]);`  
imports the entire file's contents, including functions and variables. This is then accessible through the object name if specified, or the simple name of the imported file.

Symbol-based importing, which uses the syntax  
`from [filename] import [comma-separated tokens] (as [comma-separated names]);`
imports only specific symbols from a file, which can then be accessed. If new names are specified for these tokens, they will be accessible by these, but otherwise they will keep their names from the imported file.

## Built-in Functions
LScript comes builti-in with various built-in functions for ease of use and utility. These are specified and explained in [Built-ins.md](./Built-ins.md)

## Indexing
LSript supports indexing and slicing of `strs`, `lists`, and `maps`.
### Index Syntax
```
str myString = "I love LScript!";

// This will return the third letter in myString
str atIdx2 = myString[2];

// This will return a substring of myString from the second to the second to last letter.
str slice = myString[1:-2];
```

### Basic Indexing
Indexing can be done by using brackets `[]` after a `map`, `list`, or `str`.
- Indexing a `map` at a key will return the value at that key, if it exists, or null if not.
- Indexing a `list` at an `int` will return the value of the list at that index.
- Indexing a `str` at an `int` will return the letter of the string at that index.

### Reverse Indexing
Indexes at the end of a container can be acheived by using negative numbers.
- -1 corresponds to the last index, -2 to the second-to-last, etc.

### Slicing
`strs` and `lists` can also be sliced, by similar syntax to indexing. To slice a container, separate a beginning and end index with a colon. These indices can be reverse indices, as long as the second appears after the first in the string.
- The start index is inclusive, while the end index is exclusive.
- To slice with only a start index or end index, simpl only use a colon and the existing indes. Example:
    ```
    "hello"[:2]; // returns he
    "hello"[1:]; // returns ello
    ```

### Setting Indices
You can also set indices of `strs`, `lists`, and `maps` by simply using the syntax:
```
container[index] = value;
```

## Files

LScript supports [opening](./Built-ins.md#open) files for [reading](./Built-ins.md#readfile) and [writing](./Built-ins.md#writefile). There are several types of file access, specified when a file is opened:

| Open Mode 	| Access                                    	|  
|---------------|-----------------------------------------------|  
| r         	| read access                               	|  
| w         	| write/create access                       	|  
| a         	| append access                             	|  
| +         	| read and write access (not file creation) 	|  
| t         	| text access (default)                     	|  
| b         	| binary access                             	|  

&nbsp;

- An open mode string must contain at least 'r', 'w', 'a', or '+'.
- Only one out of 'r', 'w', and 'a' is allowed, but '+' can be combined with any other mode.
- The default content access is 't', or text. You may use 'b' or 't' in your open mode string to specify, but only one is allowed.