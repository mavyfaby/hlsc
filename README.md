# CS-PROGLAN31

A high-level simpletron instructions compiler written in Java.

## Author

- (**@mavyfaby**) Maverick Fabroa

## Co-Author

- Dennis Durano

## Features

- [x] Compile high-level simpletron into low-level instructions.
- [x] Detect whether the variable already exists.
- [x] Detect whether the variable is already declared.
- [x] Detect whether the instruction is valid.
- [x] Single line comment with `>` character.
- [x] Append HALT instruction at the end of the program if not explicitly added.

## In progress

- [ ] Make the branch instructions work.

## Example

```smp
A = 30
B = 40

> This is a comment

LOAD A
ADD B
STORE A
WRITE A
```
will be compiled to:

```sml
30
40
2000
3001
2100
1100
4300
```

Note: `HALT (43)` is added at the end of the program if not explicity added in the high-level instructions.

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