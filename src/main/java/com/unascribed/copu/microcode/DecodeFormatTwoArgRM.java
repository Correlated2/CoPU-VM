package com.unascribed.copu.microcode;

import com.unascribed.copu.VirtualMachine;
import com.unascribed.copu.undefined.VMError;
import com.unascribed.copu.undefined.VMKernelPanic;

public class DecodeFormatTwoArgRM implements DecodeFormat {

	/* [CCCC CCCC .... DDDD dddd dddd dddd AAAA|aaaa aaaa aaaa aaaa aaaa aaaa aaaa aaaa] */
	
	@Override
	public int loadA(VirtualMachine vm, int instructionHigh, int instructionLow) throws VMError {
		Opmode opmode = Opmode.forId(instructionHigh & 0x0F);
		return opmode.get32(vm, instructionLow);
	}

	@Override
	public int loadB(VirtualMachine vm, int instructionHigh, int instructionLow) throws VMError {
		throw new VMKernelPanic("Tried to load nonexistant 'b' operand of a 1-arg instruction");
	}

	@Override
	public void setDest(VirtualMachine vm, int instructionHigh, int instructionLow, int value) throws VMError {
		Opmode opmode = Opmode.forId((instructionHigh >> 16) & 0x0F);
		int operand = (instructionHigh >> 4) & 0b0000_1111_1111_1111;
		opmode.put12(vm, operand, value);
	}

}
