package com.unascribed.copu.microcode;

import com.unascribed.copu.VirtualMachine;

public class InstructionMOV implements Instruction {

	@Override
	public int run(VirtualMachine vm, DecodeFormat format, int high, int low) {
		int accumulator = format.loadA(vm, high, low);
		format.setDest(vm, high, low, accumulator);
		return format.getCost(high, low);
	}

}
