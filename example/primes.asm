	

		MOV R0, 1
start:	ADD R0, 1
    	JL R0, 8, start
		MOV R1, R0
		MOV R0, 0
		RET
db str  "This is a test  "
; Argument passed in in R1. If it's prime, sets R2 to 0. If not-prime, sets it to 1.
isPrime:
	