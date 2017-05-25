package com.unascribed.copu.microcode;

import com.unascribed.copu.VirtualMachine;

public class InstructionJMP implements Instruction {

	@Override
	public int run(VirtualMachine vm, DecodeFormat format, int high, int low) {
		int addr = format.loadA(vm, high, low);
		System.out.println("IP: "+Integer.toHexString(vm.registers().IP.get())+"->"+Integer.toHexString(addr));
		vm.registers().IP.accept(addr);
		return format.getCost(high, low); //Unconditional jump has no branch cost
	}

}
