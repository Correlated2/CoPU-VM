package com.unascribed.copu.microcode;

import com.unascribed.copu.VirtualMachine;
import com.unascribed.copu.undefined.VMError;

public class DecodeFormatThreeArgDest implements DecodeFormat {

	/* [CCCC CCCC dddd AAAA aaaa aaaa aaaa BBBB|bbbb bbbb bbbb bbbb bbbb bbbb bbbb bbbb] */
	
	@Override
	public int loadA(VirtualMachine vm, int instructionHigh, int instructionLow) throws VMError {
		int opmodeId = (instructionHigh >> 16) & 0x0F;
		int operand = (instructionLow >> 4) & 0b0000_1111_1111_1111;
		Opmode opmode = Opmode.forId(opmodeId);
		return opmode.get12(vm, operand);
	}

	@Override
	public int loadB(VirtualMachine vm, int instructionHigh, int instructionLow) throws VMError {
		Opmode opmode = Opmode.forId(instructionHigh & 0x0F);
		return opmode.get32(vm, instructionLow);
	}

	@Override
	public void setDest(VirtualMachine vm, int instructionHigh, int instructionLow, int value) throws VMError {
		int operand = (instructionHigh >> 20) & 0x0F;
		Opmode.dest().put4(vm, operand, value);
	}

}
