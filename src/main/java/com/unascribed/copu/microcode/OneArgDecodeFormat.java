package com.unascribed.copu.microcode;

import com.unascribed.copu.VirtualMachine;
import com.unascribed.copu.undefined.VMError;
import com.unascribed.copu.undefined.VMKernelPanic;

public class OneArgDecodeFormat implements DecodeFormat {

	/*
	 * [CCCC CCCC .... .... .... .... .... AAAA|aaaa aaaa aaaa aaaa aaaa aaaa aaaa aaaa]
	 * One-arg has a special case: A and D are both the same operand, as some instructions read and other instructions write.
	 */
	
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
		Opmode opmode = Opmode.forId(instructionHigh & 0x0F);
		opmode.put32(vm, instructionLow, value);
	}
}
