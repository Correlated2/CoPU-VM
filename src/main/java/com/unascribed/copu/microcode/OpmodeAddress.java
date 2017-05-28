package com.unascribed.copu.microcode;

import com.unascribed.copu.VirtualMachine;
import com.unascribed.copu.undefined.VMError;

public abstract class OpmodeAddress extends Opmode {
	public abstract int getPointer12(VirtualMachine vm, int operand) throws VMError;
}
