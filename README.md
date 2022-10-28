# CS-PROGLAN31

A high-level simpletron instructions compiler written in Java.

## Author

- (**@mavyfaby**) Maverick Fabroa

## Co-Author

- Dennis Durano

## Features

- [x] Compile high-level simpletron into low-level instructions.
- [x] Dynamic branching with `@branch_name` anywhere in the program.
- [x] Evaluate arithmetic expressions. 
- [x] Declare variables anywhere.
- [x] Include only used variables to improve memory efficiency.
- [x] Show error if variable declared but doesn't have a value. 
- [x] Detect whether the variable already exists.
- [x] Detect whether the variable is already declared.
- [x] Detect whether the instruction is valid.
- [x] Single line comment with `>` character.
- [x] Append HALT instruction at the end of the program if not explicitly added.

## Example

```smp
A = 10
B = 10
C = 10

D = A + B + C

WRITE D
HALT
```
will be compiled to:

```sml
2008
3009
2111
2011
3010
2111
1111
4300
10
10
10
0
```

Note: `HALT (43)` is added at the end of the program if not explicitly added in the high-level instructions.

## License

Copyright (C) 2022 Maverick Fabroa <<me@mavyfaby.me>>

This file is part of the High-level Simpletron Compiler project.

The High-level Simpletron Compiler project can **NOT** be copied and/or distributed without the express
permission of Maverick Fabroa <<me@mavyfaby.me>>.

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

## Pseudocode

My pseudocode for compiling `high-level` simpletron instructions into `low-level` instructions:


```txt
if program is empty
    return error

for each line in program
    get current line and trim

    if current line is a comment or empty
        continue

    if current line is has an equal sign (e.g. A = 10)
        if it has a plus or minus operator (e.g. A = B + C - D)
            Split the line by equal sign (e.g [ A, B + C - D ])
            Split the 2nd value by plus and minus operator (e.g [ B, C, D ])

            for each element in the 2nd value
                if element is empty (e.g [ A, , C ])
                    then it's not an expression
                    break

            if it's an expression (e.g A = B + C - D)
                remove all spaces (e.g A=B+C-D)
                split the expression by equal sign (e.g [ A, B+C-D ])

                get the variable name (A)
                get the expression (B+C-D)

                declare result expression 
                declare current variable as empty string

                for every character in the expression
                    if character is a plus or minus operator
                        add the variable to the result expression
                        add the operator to the result expression
                        continue

                    otherwise, add the character to the current variable

                add the last variable to the result expression

                if the expression's variable name doesn't exist
                    add the expression's variable name to the variable list

                for every element in the expression (e.g. [B+C-D])
                    if element is not an operator
                        get the 1st variable name (e.g. B)
                        get the operator (e.g. +)
                        get the 2nd variable name (e.g. C)

                        get the 1st variable's address
                        get the 2nd variable's address

                        if the 1st variable doesn't exist
                            show error

                        if the 2nd variable doesn't exist
                            show error

                        add LOAD opcode to output
                        add operator's opcode (e.g ADD or SUBTRACT) to output
                        add STORE opcode to output

                        if first evaluation
                            add 1st variable's address to operand,
                        else
                            add the expressions variable's address

                        add 2nd variable's address to operand
                        add the expressions variable's address to operand

            continue

        otherwise, it's variable declaration (e.g. A = 10)
            Split the line by equal sign (e.g [ A, 10 ])
            get the variable name (e.g. A)
            get the variable value (e.g. 10)

            if the variable value is empty
                return error

            if the variable already exists
                return error

            add the variable to the variable list

    if current line starts with @ (e.g. @branch_name)
        remove all spaces
        get the branch name (e.g. branch_name)

        if the branch name already exists
            return error

        add the branch name to the branch list

    split the current line by space (e.g. [ WRITE, 10 ])
    get the instruction (e.g. WRITE)

    if the instruction is incomplete
        if command not found
            return incomplete command error

        return unknown command error

    if the instruction is valid
        get instruction name
        get instruction operand if not empty

        get instruction's opcode

        if command is HALT
            add HALT opcode to output
            break

        set OPERAND = remove all spaces in the operand

        if command is a BRANCH
            get branch name

            if branch has no identifier name
                show branch name is missing error

            if branch name exist in the branch list
                get branch name's address
                add the opcode and operand to the output
                add -1 to the operand
                continue

            declare flag for branch name after the branch callee

            for each line after the branch callee's line
                get current line
                remove all spaces in the line

                if line starts with @ and the branch name is the same as the branch callee
                    set address to output length + difference in lines
                    add opcode and address to output
                    add -1 to operand
                    set flag to true
                    break

            if not found
                show branch name not found error
            
            continue

        for every variable in the variable list
            if variable name is the same as the OPERAND
                get variable's address
                set OPERAND to the variable's address

        if operand is same as the processed operand (OPERAND)
            show variable not found error

        add the opcode and operand to the output
        continue

    otherwise, show unknown command error

if HALT opcode is not found
    add HALT opcode to output


for every variable in the variable list
    for every operand in the operand list
        if current operand's value is same as the variable's address
            if the variable isn't in the output yet
                add the variable to the output

            set new address of the variable

for every operand in the list of operands
    get opcode 
    get operand

    if opcode is a branch instruction
        continue

    otherwise, add opcode and operand to the output

```

## License

Copyright (C) 2022 Maverick Fabroa <<me@mavyfaby.me>>

This file is part of the High-level Simpletron Compiler project.

The High-level Simpletron Compiler project can **NOT** be copied and/or distributed without the express
permission of Maverick Fabroa <<me@mavyfaby.me>>.