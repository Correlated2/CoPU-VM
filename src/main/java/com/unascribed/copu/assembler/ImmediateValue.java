package com.unascribed.copu.assembler;

public class ImmediateValue implements Operand {
	public int bitWidth;
	public int value;
	
	public ImmediateValue(int i) {
		this.value = i;
		this.bitWidth = numBitsRequired(value);
	}
	
	public ImmediateValue(float f) {
		this.value = Float.floatToIntBits(f);
		this.bitWidth = 32; //NON-NEGOTIABLE
	}
	
	//NOT COMPREHENSIVE
	private static int numBitsRequired(int i) {
		int bitmask12 = ~0b1111_1111_1111;
		if ((i & bitmask12)==0) return 12;
		
		return 32;
	}

	@Override
	public long as4Bit() throws AssembleError {
		throw AssembleError.withKey("err.assembler.wrongPacking.4");
	}

	@Override
	public long as12Bit() throws AssembleError {
		if (bitWidth>12) throw AssembleError.withKey("err.validate.argTooLarge");
		
		return ((long)value) & 0b1111_1111_1111L; //Opmode.IMMEDATE is zero
	}

	@Override
	public long as32Bit() throws AssembleError {
		return ((long)value) & 0xFFFFFFFFL;
	}
	
	@Override
	public String toString() {
		String result = Integer.toHexString(value);
		while(result.length()<8) result = '0'+result;
		return "0x"+result;
	}
}
