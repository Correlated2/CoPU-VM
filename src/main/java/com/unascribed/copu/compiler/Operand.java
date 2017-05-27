package com.unascribed.copu.compiler;

public interface Operand {
	public long as4Bit() throws CompileError;
	public long as12Bit() throws CompileError;
	public long as32Bit() throws CompileError;
}
