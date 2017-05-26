package com.unascribed.copu.compiler;

public enum RegisterToken {
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
}
