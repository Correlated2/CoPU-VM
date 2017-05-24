package com.unascribed.copu.microcode;

import com.unascribed.copu.VirtualMachine;
import com.unascribed.copu.undefined.VMError;
import com.unascribed.copu.undefined.VMKernelPanic;

public class DecodeFormatTwoArgDest implements DecodeFormat {

	/* [CCCC CCCC dddd .... .... .... .... AAAA|aaaa aaaa aaaa aaaa aaaa aaaa aaaa aaaa] */
	
	@Override
	public int loadA(VirtualMachine vm, int instructionHigh, int instructionLow) throws VMError {
		Opmode opmode = Opmode.forId(instructionHigh & 0x0F);
		return opmode.get32(vm, instructionLow);
	}

	@Override
	public int loadB(VirtualMachine vm, int instructionHigh, int instructionLow) throws VMError {
		throw new VMKernelPanic("Tried to load nonexistant 'b' operand of a 2-arg-dest instruction");
	}

	@Override
	public void setDest(VirtualMachine vm, int instructionHigh, int instructionLow, int value) throws VMError {
		int operand = (instructionHigh >> 20) & 0x0F;
		Opmode.dest().put4(vm, operand, value);
	}

}
