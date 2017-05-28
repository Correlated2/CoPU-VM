package com.unascribed.copu.microcode;

import com.unascribed.copu.VirtualMachine;

public class InstructionJEQ implements Instruction {

	@Override
	public int run(VirtualMachine vm, DecodeFormat format, int high, int low) {
		boolean zf = (format.loadD(vm, high, low) - format.loadA(vm, high, low)) == 0;
		if (zf) {
			vm.registers().IP.accept( format.loadB(vm, high, low) );
		}
		return HardwareLimits.COST_BRANCH_STALL + format.getCost(high, low);
	}
	
}
