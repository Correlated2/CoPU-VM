package com.unascribed.copu.microcode;

import com.unascribed.copu.VirtualMachine;
import com.unascribed.copu.undefined.VMUserspaceError;

public class InstructionHALT implements Instruction {

	@Override
	public int run(VirtualMachine vm, DecodeFormat format, int high, int low) {
		int code = vm.registers().R0.get();
		throw new VMUserspaceError("Program halted with exit code "+code);
	}
}
