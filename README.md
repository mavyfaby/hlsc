# CS-PROGLAN31

A high-level simpletron instructions compiler written in Java.

## Author

- (**@mavyfaby**) Maverick Fabroa

## Co-Author

- Dennis Durano

## Features

- [x] Compile high-level simpletron into low-level instructions.
- [x] Declare variables anywhere.
- [x] Include only used variables to improve memory efficiency.
- [x] Show error if variable declared but doesn't have a value. 
- [x] Detect whether the variable already exists.
- [x] Detect whether the variable is already declared.
- [x] Detect whether the instruction is valid.
- [x] Single line comment with `>` character.
- [x] Append HALT instruction at the end of the program if not explicitly added.

## In progress

- [ ] Make the branch instructions work.

## Example

```smp
A = 25
B = 50
C = 75
D = 100

> This is a comment

LOAD A

E = 25
F = 125

ADD B
STORE A
WRITE A
WRITE E
```
will be compiled to:

```sml
2006
3007
2106
1106
1108
4300
25
50
25
```

Note: `HALT (43)` is added at the end of the program if not explicitly added in the high-level instructions.

## Logic

My pseudocode for compiling `high-level` simpletron instructions into `low-level` instructions:


```py
for every line in the program or file:
    get current line 
    disregard line with '>' and empty lines
    
    if the current line is a variable declaration
        get the variable name

        if the variable is declared but doesn't have a value, show error

        if the variable has already been declared, show error
        else add the variable to the variables list with the initial address

    If the current line is NOT a variable declaration
        split the line by space to get the command and operand

        if the command is valid, then get its corresponding opcode

        if the opcode is HALT, then add it to the output and exit the program loop
        else get the initial address of the operand (variable) and assign it to the operand

        if variable doesn't exist, show error
        else put opcode to the `outputs` and put operand (initial address) to the `operands`

    else show unknown command error, and exit program

    if no HALT at the end of the program, then manually add it

    for every variables:
        for every operands (initial address):
            if the variable's address is equal to the operand
                if the variable is not yet added to the output, then add it to the output

                get the variable's new address and assign it to the operand
        
    for every operands (final address):
        set the final address to the output appended to the opcode
```

## List of instructions 

| Instruction | OpCode | Description |
| ----------- | ------- | ----------- |
| READ | 10 | Read a word from the keyboard into a specific location in memory. |
| WRITE | 11 | Write a word from a specific location in memory to the screen. |
| LOAD | 20 | Load a word from a specific location in memory into the accumulator. |
| STORE | 21 | Store a word from the accumulator into a specific location in memory. |
| ADD | 30 | Add a word from a specific location in memory to the word in the accumulator (leave the result in the accumulator). |
| SUBTRACT | 31 | Subtract a word from a specific location in memory from the word in the accumulator (leave the result in the accumulator). |
| BRANCH | 40 | Branch to a specific location in memory. |
| BRANCHNEG | 41 | Branch to a specific location in memory if the accumulator is negative. |
| BRANCHZERO | 42 | Branch to a specific location in memory if the accumulator is zero. |
| HALT | 43 | Halt the program. |

## License

Copyright (C) 2022 Maverick Fabroa <<me@mavyfaby.me>>

This file is part of the High-level Simpletron Compiler project.

The High-level Simpletron Compiler project can not be copied and/or distributed without the express
permission of Maverick Fabroa <<me@mavyfaby.me>>.
