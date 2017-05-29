package com.unascribed.copu.microcode;

import com.unascribed.copu.VirtualMachine;
import com.unascribed.copu.undefined.VMUserspaceError;

public class InstructionJSR implements Instruction {

	@Override
	public int run(VirtualMachine vm, DecodeFormat format, int high, int low) {
		vm.stack().push(vm.registers().IP.get());
		if (vm.stack().size()>HardwareLimits.STACK_DEPTH) throw new VMUserspaceError("Stack overflow.");
		vm.registers().IP.accept(format.loadA(vm, high, low));
		return format.getCost(high, low);
	}

}
