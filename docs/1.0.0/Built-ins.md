# LScript Built-in Functions

[abs](#abs)  
[append](#append)  
[ciel](#ciel)  
[cls](#cls)  
[contains](#contains)  
[eval](#eval)  
[floor](#floor)  
[format](#format)  
[getbytes](#getbytes)  
[indexof](#indexof)  
[input](#input)  
[join](#join)  
[lastindexof](#lastindexof)  
[len](#len)  
[max](#max)  
[min](#min)  
[numinput](#numinput)  
[open](#open)  
[pop](#pop)  
[print](#print)  
[quit](#quit-cli-only)  
[rand](#rand)  
[randint](#randint)  
[readfile](#readfile)  
[remove](#remove)  
[root](#root)  
[round](#round)  
[split](#split)  
[sqrt](#sqrt)  
[tostring](#tostring)  
[typeof](#typeof)  
[writefile](#writefile)

&nbsp;

# abs

### Returns the absolute value of a number.

## Usage
- ### `num` abs(`num` val)  
## Parameters
- val: The `num` to evaluate to absolute value of.
## Returns
- A `num` conatining the absolute value of val.

&nbsp;

# append

### Appends a value to a list and returns the updated value.

## Usage
- ### `list` append(`list` container, `var` toAppend)
## Parameters
- container: The `list` to add to.
- toAppend: The value to append to the list.
## Returns
- A new `list` containing the old lists's values as well as the appended value.

&nbsp;

# ciel

### Rounds a number value to the lowest integer value greater than or equal to it.

## Usage
- ### `int` ciel(`num` value)
## Parameters
- value: Any number value to round up.
## Returns
- An `int` containing the smallest integer value greater than or equal to `value`.

&nbsp;

# cls

### Clears the console.

## Usage
- ### `void` cls()

&nbsp;

# contains

### Tests whether a container contains a value.

## Usage
- ### `bool` contains(`str` container, `str` element)
- ### `bool` contains(`list` container, `var` element)
- ### `bool` contains(`map` container, `var` element)
## Parameters
- container: A `str`, `list`, or `map` to check inside of.
- element: A value to check against the container.
    - If `container` is a `str`, `element` is a substring to check for inside of `container`
## Returns
- A `bool` - `true` if the `container` contains the `element`; otherwise, `false`.

# eval

### Evaluates a string as LScript code.

`eval` will run the provided code, producing an error as normal if it cannot interpret it.  
All variables created inside this evaluated expression are then accessible from the original script.

## Usage
- ### `void` eval(`str` expression)
## Parameters
- expression: A `str` containing LScript code to interpret during runtime.
## Throws
- Can throw any error reached when interpreting the input.

&nbsp;

# floor

### Rounds a number value to the highest integer value less than or equal to it.

## Usage
- ### `int` floor(`num` value)
## Parameters
- value: Any number value to round down.
## Returns
- An `int` containing the largest integer value less than or equal to `value`.

&nbsp;

# format

### Formats values into a `str`.

## Usage
- ### `str` format(`str` text, `list` args)
## Parameters
- text: A `str` to format values into. The `str` must use `{}` as a placeholder wherever a value should be inserted.
- args: A `list` of arguments to replace every instance of `{}` in `text`
## Returns
- A formatted `str` containing the value of `text`, with every `{}` replaced by an argument.
## Throws
- `ArgumentError` if the number of arguments is not equa to the instances of `{}` in `text`.

&nbsp;

# getbytes

### Returns the byte value of a value.

## Usage
- ### `list` getbytes(`str` value)
- ### `list` getbytes(`num` value)
- ### `list` getbytes(`list` value)
## Parameters
- value: A `str`, `num`, or `list` to return the byte value of.
## Returns
- A `list` of `bytes` representing `value`. If `value` is a `list`, this function will return nested lists for each element in the list.
## Throws
- `ArgumentError` if `value` is a `list` containing values that are neither a `str` nor a `num`.

&nbsp;

# indexof

### Returns the first index of a value in a container.
This function will return -1 if the value is not present in the container from the given index.  
The start index defaults to 0, but can be set.

## Usages
- ### `int` indexof(`str` toIndex, `str` value)
- ### `int` indexof(`list` toIndex, `var` value)
- ### `int` indexof(`map` toIndex, `var` value)
- ### `int` indexof(`str` toIndex, `str` value, `int` startIndex)
- ### `int` indexof(`list` toIndex, `var` value, `int` startIndex)
## Parameters
- toIndex: A container (`str`, `list`, or `map`) to check inside of.
- value: The value to search `toIndex` for the index of.
- startIndex (optional): The 0-indexed position in `toIndex` to start searching at. Defaults to 0.
## Returns
- The 0-based integer index of `value` in `toIndex` starting from `startIndex`, or -1 if it is not found.
- If `toIndex` is a `map`, this function will return the key associated with `value` in the `map`.
## Throws
- `ArgumentError` if `startIndex` is equal to null.
- `IndexOutOfBoundsError` if `startIndex` is less than 0 or greater than the size of `toIndex`.

&nbsp;

# input

### Creates an stdin input prompt.

## Usages
- ### `str` input()
- ### `str` input(`str` prompt)
## Parameters
- prompt (optional): A `str` to output before input is accepted. Defaults to '', or empty.
## Returns
- A `str` containing the user's input.

&nbsp;

# join

### Joins a `list` together into a `str`, sparated by a delimiter.

## Usages
- ### `str` join(`list` toJoin, `str` delimiter)
## Parameters
- toJoin: The `list` to be joined into a `str`.
- delimiter: A `str` to repeat between each list element in the result.
## Returns
- A string representation of `toJoin`, with every element separated with `delimiter`.

&nbsp;

# lastindexof

### Returns the last index of a value in a `str` or `list`.
This function will return -1 if the value is not present in the container.  

## Usages
- ### `int` lastindexof(`str` toIndex, `str` value)
- ### `int` lastindexof(`list` toIndex, `var` value)
## Parameters
- toIndex: A `str` or `list` to check inside of.
- value: The value to search `toIndex` for the index of.
## Returns
- The last 0-based integer index of `value` in `toIndex`, or -1 if it is not found.

&nbsp;

# len

### Returns the length of a container or `str`.

## Usages
- ### `int` len(`str` container)
- ### `int` len(`list` container)
- ### `int` len(`map` container)
## Parameters
- container: A `str`, `len`, or `map` to return the length of.
## Returns
- The `int` length of `container`. 
    - For an `str`, this is the number of characters present.
    - For a `list`, this is the number of elements.
    - For a `map`, this is the number of key-value pairs in the `map`.

&nbsp;

# max

### Returns the maximum of two numbers.

## Usages
- ### `int` max(`int` val1, `int` val2)
- ### `num` max(`num` val1, `num` val2)
## Parameters
- val1: The first `num` to compare.
- val2: The second `num` to compare.
## Returns
- A `num` equal to the maximum of `val1` and `val2`.
    - If both `val1` and `val2` are `ints`, this will return in `int`.

&nbsp;

# min

### Returns the minimum of two numbers.

## Usages
- ### `int` main(`int` val1, `int` val2)
- ### `num` min(`num` val1, `num` val2)
## Parameters
- val1: The first `num` to compare.
- val2: The second `num` to compare.
## Returns
- A `num` equal to the minimum of `val1` and `val2`.
    - If both `val1` and `val2` are `ints`, this will return in `int`.

&nbsp;

# numinput

### Creates an stdin input prompt, and returns an input number, if that is what is input.

## Usages
- ### `num` numinput()
- ### `num` numinput(`str` prompt)
## Parameters
- prompt (optional): A `str` to output before input is accepted. Defaults to '', or empty.
## Returns
- A `num` containing the user's input as a number. Returns `null` if they did not input a number.

&nbsp;

# open

### Opens an external file for access by the program.

## Usages
- ### `file` open(`str` relativePath, `str` openModes)
## Parameters
- relativePath: A `str` representing the relative path of the file to open to this file (or CLI instance).
- openModes: A `str` specifying how to open the file. The rules for openModes are found in [the main documentation](./DOCS.md#files).  
## Returns
- A  `file` with the specified access modifiers, which can be read from/written to, depending on access.
## Throws
- `InvalidSyntaxError` if the mode string is not valid.
- `FileAccessError` if the specified path cannot be found and the access modes do not include creation.
    - A `FileAccessError` is also thrown if the specified file cannot be created even with create access.

&nbsp;

# pop

### Removes a value from a list at a specified index.

## Usage
- ### `var` pop(`list` container, `int` index)
## Parameters
- container: A `list` to remove a value from.
- index: The index to remove from `container`. Supports reverse indexing.
## Returns
- A `var` containing the value removed from the list.
## Throws
- `IndexOutOfBoundsError` if `index` is greater than the length of `container`.

&nbsp;

# print

### Outputs a line to stdout.

## Usage
- ### `void` print(`str` text)
## Parameters
- text: The text to print.

&nbsp;

# quit (CLI only)

### Exits the commandline interface.

## Usage
- ### `void` quit()

&nbsp;

# rand

### Returns a random `float` between 0 (incluive) and 1 (exclusive).

## Usage
- `float` rand()
## Returns
- A pseudorandom `float` value, uniformly distributed between 0.0 and 1.0.

&nbsp;

# randint

### Returns a random `int` value between lower and upper bounds.

## Usage
- `int` randint()
- `int` randint(`int` upperBound)
- `int` randInt(`int` lowerBound, `int` upperBound)
## Parameters
- lowerBound (optional): The lower limit for the random value, inclusive. Defaults to 0 if an upperBound is given; otherwise, the lower integer limit -(2^32).
- upperBound (optional): The upper limit for the random value, exclusive. Defaults to the upper integer limit 2^32.
## Returns
- A pseudorandom `int` value, uniformly distributed between `lowerBound` and `upperBound`.

&nbsp;

# readfile

### Reads the contents of a `file` as either a list of `bytes` or a `str`, depending on the file's [open mode](./DOCS.md#files).

## Usage
- `list` readfile(`file` f)
- `str` readfile(`file` f)
## Parameters
- f: A file to read the contents of.
## Returns
- If `f` has binary access, this function will return a list of bytes read from the file.
- Otherwise, this function will return `f` cread as a string.
## Throws
- `FileAccessError` if `f` does not have read access, or if the file cannot be read.

&nbsp;

# remove

### Removes the first instance of a value in a `list` or the value associated with a key in a `map`.

## Usage
- ### `bool` remove(`list` container, `var` value)
- ### `var` remove(`map` container, `var` value)
## Parameters
- container: The container (`list` or `map`) to remove an element from.
- value: The element to remove from `container`.
## Returns
- If `container` is a `list`, this function will return a `bool` signifying whether the removal was successful.
- If `container` is a map, this function will instead return the value in the `map` associated with a key of `value`, or `null` if there was no key in `container` of `value`.

&nbsp;

# root

### Takes a value to a specified root (aka 1/n) and returns it.
This function is equivalent to calling
```
val^(1/rootNum);
```

## Usage
- ### `num` root(`num` val, `num` rootNum)
## Parameters
- val: The value to take the root of.
- rootNum: The value to take `val` to the root of.
```
root(36, 2); // returns the square root of 36, or 6.
```
## Returns
- The value equated when `val` is taken to the root of `rootNum` (or the power of 1/`rootNum`)

&nbsp;

# round

### Rounds a number value to the nearest integer value.

## Usage
- ### `int` round(`num` value)
## Parameters
- value: Any number value to round.
## Returns
- An `int` containing `value`, rounded to the nearest integer.

&nbsp;

# split

### Splits a `str` by a regular expression and returns a `list` of `strs` produced by the split.

## Usage
- ### `list` split(`str` toSplit)
- ### `list` split(`str` toSplit, `str` regex)
## Parameters
- toSplit: The `str` to split on the regex.
- regex (optional): A Regular Expression to split `toSplit` by. Default "" (empty `str`).
## Returns
- A `list` of `strs` resulting from the split operation on `toSplit` by `regex`.

&nbsp;

# sqrt

### Returns the square root of a value. (Equivalent to `root(val, 2)`)

## Usage
- ### `num` sqrt(`num` val)
## Parameters
- val: The value to take the square root of.
## Returns
- The square root of `val`.

&nbsp;

# tostring

### Returns the string representation of a value.

## Usage
- ### `str` tostring(`var` value)
## Parameters
- value: The value to convert to a `str`.
## Returns
- A `str` representation of `value`.

&nbsp;

# typeof

### Returns the variable type of a value as a `str`.

## Usage
- ### `str` typeof(`var`value)
## Parameters
- value: The value to find the type of.
## Returns
- A `str` containing the [type](./DOCS.md#types) of `value`.

&nbsp;

# writefile

### Writes content to a file. Can write in either binary or text mode, depending on the [open mode](./DOCS.md#files).

## Usage
- ### `void` writefile(`file` f, `list` bytes)
    - Version used in binary mode
- ### `void` writefile(`file` f, `str` text)
    - Version used in text mode\
## Parameters
- f: The `file` to write to.
- bytes (binary mode): A `list` of bytes to write to the file.
- text (text mode): A `str` containing text to write to the file.
## Throws
- `FileAccessError` if `f` does not have write access or cannot be found.
    - A `FileAccessError` is also thrown if the wrong parameters are used for `f`'s open mode.