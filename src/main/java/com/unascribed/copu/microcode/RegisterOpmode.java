package com.unascribed.copu.microcode;

import com.unascribed.copu.VirtualMachine;
import com.unascribed.copu.undefined.VMError;

public class RegisterOpmode extends Opmode {

	public int fetch4(VirtualMachine vm, int operand) throws VMError {
		//rrrr
		return vm.getRegister(operand).getAsInt();
	}
	
	@Override
	public int fetch12(VirtualMachine vm, int operand) throws VMError {
		//....'...r rrrr
		return vm.getRegister(operand & 0b0001_1111).getAsInt();
	}

	@Override
	public int fetch32(VirtualMachine vm, int operand) throws VMError {
		//.... ....'.... ....'.... ....'...r rrrr
		return vm.getRegister(operand & 0b0001_1111).getAsInt();
	}

	@Override
	public void put12(VirtualMachine vm, int operand, int data) throws VMError {
		//....'...r rrrr
		vm.getRegister(operand & 0b0001_1111).accept(data);
	}

	@Override
	public void put32(VirtualMachine vm, int operand, int data) throws VMError {
		//.... ....'.... ....'.... ....'...r rrrr
		vm.getRegister(operand & 0b0001_1111).accept(data);
	}

}
