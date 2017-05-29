package com.unascribed.copu.compiler;

public class DirectAddress implements Operand {
	public RegisterToken page;
	public RegisterToken offset;
	
	public DirectAddress(RegisterToken page, RegisterToken offset) {
		this.page = page;
		this.offset = offset;
	}
	
	public RegisterToken getPage() { return page; }
	public RegisterToken getOffset() { return offset; }

	@Override
	public long as4Bit() throws AssembleError {
		throw AssembleError.withKey("err.assembler.wrongPacking.4");
	}

	@Override
	public long as12Bit() throws AssembleError {
		long result = 1L << 12;
		result |= ((long)page.ordinal()) << 7;
		result |= ((long)offset.ordinal());
		
		return result;
	}

	@Override
	public long as32Bit() throws AssembleError {
		long result = 1L << 32;
		result |= ((long)page.ordinal()) << 27;
		result |= ((long)offset.ordinal());
		
		return result;
	}
	
	@Override
	public String toString() {
		return "MEM["+page.name()+":"+offset.name()+"]";
	}
}
