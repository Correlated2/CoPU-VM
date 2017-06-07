		JMP start
db str  "Hello, world!"
start:
		MOV PG0, CS
		MOV R1, str
		MOV R2, 13
		MOV R3, 1         ; stdout descriptor
		CALL 0x05150006   ; std.writeln
		MOV R0, 0
		RET
