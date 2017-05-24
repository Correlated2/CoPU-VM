# CoPU-VM
Reference implementation of the CoPU, which powers robots and computers in the Correlated minecraft mod.


The CoPU is a strange and complex fusion of new and old ISAs. It carries a lot of inspiration from MIPS,
as well as Z80 and 6502 chips that powered most of our favorite 1980s machines. Several design choices
have been made, however, to make the machine language approachable and easy to read/encode. This means a
positively gigantic 64-bit instruction size with more uniform locations and formats for operands.


## Data
    "byte" or "char": 8-bit byte value
    "short": 16-bit half-integer. Basically never used.
    "word" or "int": a signed 32-bit two's complement integer
    "dword" or "long": 64-bit double machine word. Basically never used.
    "float": A single-precision 32-bit IEEE 754 floating point number.
    
Additional data keywords

    dest:  Certain operands can only target one of the first 16 registers in the machine, used typically
           as a write-only destination operand.
    const: An IDE feature rather than a machine code feature, indicates that a value will be inlined
           into the instructions as an immediate value where possible.

## Registers

The CoPU has 8 general-purpose registers, R0-R8. It shares 4 general-purpose registers with the
floating-point unit, F0-F3. It has three page-selector registers, PG0, PG1, and CS. It has Stack
Pointer (SP) and Instruction Pointer (IP) registers. Most registers are indexed by either a 4-bit or
5-bit value in machine code. The 4-bit mode intentionally renders certain registers (like CS, SP, and
IP) inaccessible.

## Optimizing Programs

The best strategy to optimize a program is to keep memory access to a minimum. Do calculations using
only registers when possible. Occasionally a bogged-down program will use the F0-F3 registers for
more integer data instead of floating-point data. This is completely valid and has no additonal cost.


If you must do memory access, keep tabs on the cost of your instruction and consider which is smaller:
An explicit load-to-register using the MOV instruction, or an implicit load (which will create a
pipeline bubble) in the 'b' operand of an ALU instruction.


Consider using fixed-point / integer math instead of floating point math. The floating point
coprocessor is much slower than the on-die ALU. Use lookup tables for trigonometric functions. The
memory access is much faster than the function.


The CoPU has no branch predictor, so after streamlining memory access and minimizing program complexity,
consider reducing the amount of nondeterministic branching. The savings here tend to be very small
(unlike VLIW machines), so optimizing here should be a last resort.
