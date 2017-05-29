package com.unascribed.copu.microcode;

import com.unascribed.copu.VirtualMachine;
import com.unascribed.copu.undefined.VMUserspaceError;

public class InstructionPOP implements Instruction {

	@Override
	public int run(VirtualMachine vm, DecodeFormat format, int high, int low) {
		if (vm.stack().isEmpty()) throw new VMUserspaceError("Stack underflow.");
		int data = vm.stack().pop();
		format.setDest(vm, high, low, data);
		return format.getCost(high, low);
	}
	
}