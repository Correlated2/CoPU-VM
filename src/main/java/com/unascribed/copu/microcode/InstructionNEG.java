package com.unascribed.copu.microcode;

import com.unascribed.copu.VirtualMachine;

public class InstructionNEG implements Instruction {

	@Override
	public int run(VirtualMachine vm, DecodeFormat format, int high, int low) {
		int a = format.loadA(vm, high, low);
		format.setDest(vm, high, low, -a);
		return format.getCost(high, low);
	}

}
