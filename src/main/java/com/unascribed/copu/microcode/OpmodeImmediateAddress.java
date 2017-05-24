package com.unascribed.copu.microcode;

import com.unascribed.copu.MemoryPage;
import com.unascribed.copu.Register;
import com.unascribed.copu.VirtualMachine;
import com.unascribed.copu.undefined.VMError;

public class OpmodeImmediateAddress extends Opmode {

	@Override
	public int get12(VirtualMachine vm, int operand) throws VMError {
		//piii iiii iiii
		int pageID = (operand >> 11) & 0b0000_0001;
		Register pageRegister = (pageID==0) ? vm.registers().PG0 : vm.registers().PG1;
		MemoryPage page = vm.getPage(pageRegister.get());
		int address = operand & 0b0111_1111_1111;
		return page.get(address);
	}

	@Override
	public int get32(VirtualMachine vm, int operand) throws VMError {
		//pppp p...'.... ....'.... .iii'iiii iiii
		int pageId = (operand >> 27) & 0b0001_1111;
		Register pageRegister = vm.getRegister(pageId);
		MemoryPage page = vm.getPage(pageRegister.get());
		int address = operand & 0b0111_1111_1111;
		return page.get(address);
	}

	@Override
	public void put12(VirtualMachine vm, int operand, int data) throws VMError {
		//piii iiii iiii
		int pageID = (operand >> 11) & 0b0000_0001;
		Register pageRegister = (pageID==0) ? vm.registers().PG0 : vm.registers().PG1;
		MemoryPage page = vm.getPage(pageRegister.get());
		int address = operand & 0b0111_1111_1111;
		page.set(address, data);
	}

	@Override
	public void put32(VirtualMachine vm, int operand, int data) throws VMError {
		//pppp p...'.... ....'.... .iii'iiii iiii
		int pageId = (operand >> 27) & 0b0001_1111;
		Register pageRegister = vm.getRegister(pageId);
		MemoryPage page = vm.getPage(pageRegister.get());
		int address = operand & 0b0111_1111_1111;
		page.set(address, data);
	}

}
