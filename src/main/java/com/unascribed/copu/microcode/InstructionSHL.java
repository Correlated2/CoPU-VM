package com.unascribed.copu.microcode;

import com.unascribed.copu.VirtualMachine;

public class InstructionSHL implements Instruction {

	@Override
	public int run(VirtualMachine vm, DecodeFormat format, int high, int low) {
		int places = format.loadB(vm, high, low);
		if (places==0) return format.getCost(high, low);
		if (places>0) {
			int accumulator = format.loadA(vm, high, low) << places;
			format.setDest(vm, high, low, accumulator);
		} else { //SHL negative is SHR
			int accumulator = format.loadA(vm, high, low) >> -places;
			format.setDest(vm, high, low, accumulator);
		}
		return format.getCost(high, low);
	}

}
