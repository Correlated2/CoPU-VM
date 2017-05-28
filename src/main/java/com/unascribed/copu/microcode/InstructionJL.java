package com.unascribed.copu.microcode;

import com.unascribed.copu.VirtualMachine;

public class InstructionJL implements Instruction {

	@Override
	public int run(VirtualMachine vm, DecodeFormat format, int high, int low) {
		System.out.println("JL: "+format.loadD(vm, high, low)+ " < " +format.loadA(vm, high, low));
		boolean lt = (format.loadD(vm, high, low) < format.loadA(vm, high, low));
		System.out.println("LessThan: "+lt);
		if (lt) {
			System.out.println("Branching to: ");
			System.out.println("\t\t"+format.loadB(vm, high, low));
			vm.registers().IP.accept( format.loadB(vm, high, low) );
			System.out.println("Jumped successfully.");
		} else {
		}
		return HardwareLimits.COST_BRANCH_STALL + format.getCost(high, low);
	}

}
