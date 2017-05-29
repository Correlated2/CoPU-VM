package com.unascribed.copu.microcode;

import com.unascribed.copu.VirtualMachine;
import com.unascribed.copu.undefined.VMUserspaceError;

public class InstructionPUSH implements Instruction {

	@Override
	public int run(VirtualMachine vm, DecodeFormat format, int high, int low) {
		int data = format.loadA(vm, high, low);
		vm.stack().push(data);
		if (vm.stack().size()>HardwareLimits.STACK_DEPTH) throw new VMUserspaceError("Stack overflow.");
		return format.getCost(high, low);
	}

}
