package com.unascribed.copu.microcode;

import com.unascribed.copu.VirtualMachine;
import com.unascribed.copu.undefined.VMError;
import com.unascribed.copu.undefined.VMKernelPanic;

public class OpmodeImmediate extends Opmode {

	@Override
	public int get12(VirtualMachine vm, int operand) throws VMError {
		//iiii'iiii iiii
		return operand & 0b1111_1111_1111;
	}

	@Override
	public int get32(VirtualMachine vm, int operand) throws VMError {
		//iiii iiii'iiii iiii'iiii iiii'iiii iiii
		return operand;
	}

	@Override
	public void put12(VirtualMachine vm, int operand, int data) throws VMError {
		throw new VMKernelPanic("Attempted to set an immediate operand");
	}

	@Override
	public void put32(VirtualMachine vm, int operand, int data) throws VMError {
		throw new VMKernelPanic("Attempted to set an immediate operand");
	}

	
}
