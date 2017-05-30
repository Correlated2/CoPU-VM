package com.unascribed.copu.microcode;

import com.unascribed.copu.VirtualMachine;

public class InstructionXOR implements Instruction {

	@Override
	public int run(VirtualMachine vm, DecodeFormat format, int high, int low) {
		int a = format.loadA(vm, high, low);
		int b = format.loadB(vm, high, low);
		
		format.setDest(vm, high, low, a ^ b);
		
		return format.getCost(high, low);
	}

}
