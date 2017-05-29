package com.unascribed.copu.microcode;

import com.unascribed.copu.VirtualMachine;
import com.unascribed.copu.undefined.VMError;

@FunctionalInterface
public interface CallHandler {
	public void doCall(VirtualMachine vm) throws VMError;
}
