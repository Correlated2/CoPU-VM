package com.unascribed.copu.assembler;

/**
 * Represents an argument to an instruction that can't be understood as a compile-time constant.
 * "Compile-time constants" could be register names like "R0", fixed locations in memory like
 * "MEM[200]", or raw ints like 0x04B6. If it's not one of these, it's probably a label to an
 * address in memory, and we may not know what its target is yet.
 * 
 * <p>In situations like these, we take note of the label's name and the byte-offset of the
 * instruction that referred to it, so that we can either fill it in on the second pass, or if
 * its destination is still unknown, emit an error including its line number.
 */
public class AssemblyLabel implements Operand {
	public String name;
	public int byteOffset;
	public int line;
	
	/**
	 * Creates a new AssemblyLabel
	 * @param name	the String value of the token
	 * @param offset the offset of the *instruction* at which this label occurred. (label fill-in will typically take place at offset+1)
	 */
	public AssemblyLabel(String name, int offset) {
		this.name = name;
		this.byteOffset = offset;
	}
	
	public AssemblyLabel atLine(int line) {
		this.line = line;
		return this;
	}

	@Override
	public long as4Bit() throws AssembleError {
		throw new AssembleError("Label \""+name+"\" cannot be used as a dest operand.");
	}

	@Override
	public long as12Bit() throws AssembleError {
		throw new AssembleError("Using labels in 12-bit R/M operands is not yet implemented.");
		//return 0;
	}

	@Override
	public long as32Bit() throws AssembleError {
		//Logic outside this will handle the fill-in. Use zero for now.
		return 0;
	}
	
	@Override
	public String toString() {
		return this.name;
	}
}
