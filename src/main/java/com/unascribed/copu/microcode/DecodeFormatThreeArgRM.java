package com.unascribed.copu.microcode;

import com.unascribed.copu.VirtualMachine;
import com.unascribed.copu.undefined.VMError;

public class DecodeFormatThreeArgRM implements DecodeFormat {

	/* [CCCC CCCC .... DDDD dddd dddd dddd ....|AAAA aaaa aaaa aaaa BBBB bbbb bbbb bbbb] */
	
	@Override
	public int loadA(VirtualMachine vm, int instructionHigh, int instructionLow) throws VMError {
		int opmodeId = (instructionLow >> 28) & 0x0F;
		int operand = (instructionLow >> 16) & 0b0000_1111_1111_1111;
		Opmode opmode = Opmode.forId(opmodeId);
		return opmode.get12(vm, operand);
	}

	@Override
	public int loadB(VirtualMachine vm, int instructionHigh, int instructionLow) throws VMError {
		int opmodeId = (instructionLow >> 12) & 0x0F;
		int operand = (instructionLow) & 0b0000_1111_1111_1111;
		Opmode opmode = Opmode.forId(opmodeId);
		return opmode.get12(vm, operand);
	}

	@Override
	public void setDest(VirtualMachine vm, int instructionHigh, int instructionLow, int value) throws VMError {
		int opmodeId = (instructionHigh >> 16) & 0x0F;
		int operand = (instructionLow >> 4) & 0b0000_1111_1111_1111;
		Opmode opmode = Opmode.forId(opmodeId);
		opmode.put12(vm, operand, value);
	}

}
