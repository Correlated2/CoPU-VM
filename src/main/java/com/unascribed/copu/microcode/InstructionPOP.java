package com.unascribed.copu.microcode;

import com.unascribed.copu.VirtualMachine;

public class InstructionPOP implements Instruction {

	@Override
	public int run(VirtualMachine vm, DecodeFormat format, int high, int low) {
		int data = vm.stack().pop();
		format.setDest(vm, high, low, data);
		return format.getCost(high, low);
	}
	
}
