package com.unascribed.copu.microcode;

import com.unascribed.copu.VirtualMachine;

public class InstructionFSIN implements Instruction {

	@Override
	public int run(VirtualMachine vm, DecodeFormat format, int high, int low) {
		int rawA = format.loadA(vm, high, low);
		float a = Float.intBitsToFloat(rawA);
		float d = (float) Math.sin(a);
		int rawD = Float.floatToIntBits(d);
		format.setDest(vm, high, low, rawD);
		return HardwareLimits.COST_FPU_TRIG;
	}

}
