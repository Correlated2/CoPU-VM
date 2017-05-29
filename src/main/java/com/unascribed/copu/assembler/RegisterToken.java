package com.unascribed.copu.assembler;

import com.unascribed.copu.microcode.Opmode;

public enum RegisterToken implements Operand {
	R0, R1, R2, R3, R4, R5, R6, R7,
	X, Y,
	PG0, PG1,
	F0, F1, F2, F3,
	
	CS, IP, SP
	;
	
	public static RegisterToken forName(String s) {
		String t = s.trim().toUpperCase();
		for(RegisterToken token : values()) {
			if(token.name().equals(t)) return token;
		}
		return null;
	}
	
	public boolean isRegister(String s) {
		return forName(s)!=null;
	}

	@Override
	public long as4Bit() throws AssembleError {
		if (this.ordinal()>15) throw AssembleError.withKey("err.validate.destOperandRequired");
		return this.ordinal();
	}

	@Override
	public long as12Bit() throws AssembleError {
		long result = Opmode.REGISTER;
		result = result << 12;
		result |= this.ordinal();
		
		return result;
	}

	@Override
	public long as32Bit() throws AssembleError {
		long result = Opmode.REGISTER;
		result = result << 32;
		result |= this.ordinal();
		
		return result;
	}
}
