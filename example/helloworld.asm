; sys.writeln parameters work as follows:
; PG0 - page the string is in
; R1  - offset of the string
; R2  - max string length
; R3  - descriptor to write the string to
; This program just stuffs CS:str into PG0:R1, the length
;   into R2, and a hardcoded descriptor number into R3 (1==stdout),
;   and calls 0x05150006, which is the symbolic constant for
;   sys.writeln

		JMP start
db str  "Hello, world!"
start:
		MOV PG0, CS       ; string is in the same page as code
		MOV R1, str       ; offset of string
		MOV R2, 13        ; string is 13 bytes
		MOV R3, 1         ; stdout descriptor
		CALL sys.println  ; make the call
		MOV R0, 0         ; exit code 0
		RET               ; return to OS control
