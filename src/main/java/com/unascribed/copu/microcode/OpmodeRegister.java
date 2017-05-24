package com.unascribed.copu.microcode;

import com.unascribed.copu.VirtualMachine;
import com.unascribed.copu.undefined.VMError;

public class OpmodeRegister extends Opmode {

	public int get4(VirtualMachine vm, int operand) throws VMError {
		//rrrr
		return vm.getRegister(operand).get();
	}
	
	@Override
	public int get12(VirtualMachine vm, int operand) throws VMError {
		//....'...r rrrr
		return vm.getRegister(operand & 0b0001_1111).get();
	}

	@Override
	public int get32(VirtualMachine vm, int operand) throws VMError {
		//.... ....'.... ....'.... ....'...r rrrr
		return vm.getRegister(operand & 0b0001_1111).get();
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
