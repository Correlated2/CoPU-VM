package com.unascribed.copu.compiler;

import com.unascribed.copu.microcode.Opmode;

public class ZeroPageAddress implements Operand {
	//public String key;
	public int value;
	
	public ZeroPageAddress(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
	
	@Override
	public long as4Bit() throws AssembleError {
		throw AssembleError.withKey("err.assembler.wrongPacking.4");
	}
	
	@Override
	public long as12Bit() throws AssembleError {
		long result = Opmode.IMMEDIATE_ADDRESS;
		result = result << 12;
		result |= ((long)value) & 0b0111_1111_1111L;
		
		return result;
	}
	
	@Override
	public long as32Bit() throws AssembleError {
		long result = Opmode.IMMEDIATE_ADDRESS;
		result = result << 32;
		result |= ((long)value) & 0b0111_1111_1111L;
		
		return result;
	}
	
	@Override
	public String toString() {
		return "MEM["+Integer.toHexString(value)+"]";
	}
}
