package com.unascribed.copu.compiler;

public interface Operand {
	public long as4Bit() throws AssembleError;
	public long as12Bit() throws AssembleError;
	public long as32Bit() throws AssembleError;
}
