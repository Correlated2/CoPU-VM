package com.unascribed.copu.microcode;

import com.unascribed.copu.VirtualMachine;
import com.unascribed.copu.undefined.VMUserspaceError;

public class InstructionRET implements Instruction {

	@Override
	public int run(VirtualMachine vm, DecodeFormat format, int high, int low) {
		if (vm.stack().isEmpty()) {
			int code = vm.registers().R0.get();
			throw new VMUserspaceError("Program halted with exit code "+code);
		} else {
			int ipNext = vm.stack().pop();
			vm.registers().IP.accept(ipNext);
		}
		return 0;
	}

}
