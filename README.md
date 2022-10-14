# CS-PROGLAN31

A high-level simpletron instructions compiler written in Java.

## Author

- (**@mavyfaby**) Maverick Fabroa

## Co-Author

- Dennis Durano

## Features

- [x] Compile high-level simpletron into low-level instructions.
- [x] Dynamic branching with `@branch_name` anywhere in the program.
- [x] Declare variables anywhere.
- [x] Include only used variables to improve memory efficiency.
- [x] Show error if variable declared but doesn't have a value. 
- [x] Detect whether the variable already exists.
- [x] Detect whether the variable is already declared.
- [x] Detect whether the instruction is valid.
- [x] Single line comment with `>` character.
- [x] Append HALT instruction at the end of the program if not explicitly added.

## Todo

- [x] Make the branch instructions work.

## Example

```smp
A = 6
B = 5
C = 1
D = 0

@A1

LOAD D
ADD A
STORE D
LOAD B
SUBTRACT C
STORE B
BRANCHZERO @B1
BRANCH @A1

@B1
WRITE D
```
will be compiled to:

```sml
2013
3010
2113
2011
3112
2111
4208
4000
1113
4300
6
5
1
0

```

Note: `HALT (43)` is added at the end of the program if not explicitly added in the high-level instructions.

## Logic

My pseudocode for compiling `high-level` simpletron instructions into `low-level` instructions:


```txt
for every line in the program:
    get current line and trim

    if current line is a comment or empty:
        continue to next line

    if current line is a variable declaration:
        if variable is declared but doesn't have a value:
            throw error
        else if variable is already declared:
            throw error
        else:
            add variable to the list of variables
            continue to next line

    if current line is a branch:
        if branch is already declared:
            throw error
        else:
            add branch to the list of branches
            continue to next line

    if the command is incomplete:
        throw error

    if the command is valid:
        if the command is HALT:
            add HALT instruction to the list of instructions
            break the loop

        if the command is a branch instruction:
            find the branch in the list of branches

            if found:
                add branch instruction together with the branch's address to the output
                continue to next line
            
            else if not found:
                find the branch after the current line

                if found:
                    add branch instruction together with the branch's address to the output
                    continue to next line

                else if not found:
                    throw error

        convert the variable operand to its initial address

        if variable is not declared:
            throw error

        add opcode to output
        add operand to list of operands (to be used later)
    
    if the command is invalid:
        throw error

    if no halt instruction is found in the program:
        add HALT instruction automatically to the output (to prevent infinite loop)

    add variables to the output only if they're used in the program (to improve memory efficiency)
    update the addresses of the variables in the list of operands

    for every operand in the list of operands:
        if opcode is a branch instruction:
            continue to next loop

        append the operand to the opcode in the output
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