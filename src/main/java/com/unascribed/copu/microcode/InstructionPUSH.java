package com.unascribed.copu.microcode;

import com.unascribed.copu.VirtualMachine;

public class InstructionPUSH implements Instruction {

	@Override
	public int run(VirtualMachine vm, DecodeFormat format, int high, int low) {
		int data = format.loadA(vm, high, low);
		vm.stack().push(data);
		return format.getCost(high, low);
	}

}
