package com.unascribed.copu.microcode;

import com.unascribed.copu.MemoryPage;
import com.unascribed.copu.Register;
import com.unascribed.copu.VirtualMachine;
import com.unascribed.copu.undefined.VMError;

public class OpmodeDirectWithOffset extends Opmode {

	@Override
	public int get12(VirtualMachine vm, int operand) throws VMError {
		//prrr rrii iiii
		int pageRegNum = (operand >> 11) & 0b0000_0001;
		int ofsRegNum = (operand >> 6) & 0b0001_1111;
		int ofsImm = operand & 0b0011_1111;
		//Sign-extend the offset to 32 bits so that java sees it as negative if it's negative.
		if ((ofsImm & 0b0010_0000) != 0) ofsImm |= 0b1111_1111__1111_1111__1111_1111__1100_0000;
		
		Register pageReg = (pageRegNum==0) ? vm.registers().PG0 : vm.registers().PG1;
		Register ofsReg = vm.getRegister(ofsRegNum);
		MemoryPage page = vm.getPage(pageReg.get());
		return page.get(ofsReg.get() + ofsImm);
	}

	@Override
	public int get32(VirtualMachine vm, int operand) throws VMError {
		//pppp prrr rr.. .... .... iiii iiii iiii
		int pageRegNum = (operand >> 27) & 0b0001_1111;
		int ofsRegNum = (operand >> 22) & 0b0001_1111;
		int ofsImm = operand & 0b0000_1111_1111_1111;
		//Sign-extend the offset to 32 bits so that java sees it as negative if it's negative.
		if ((ofsImm & 0b0000_1000_0000_0000) != 0) ofsImm |= 0b1111_1111__1111_1111__1111_0000__0000_0000;
		
		Register pageReg = vm.getRegister(pageRegNum);
		Register ofsReg = vm.getRegister(ofsRegNum);
		MemoryPage page = vm.getPage(pageReg.get());
		return page.get(ofsReg.get() + ofsImm);
	}

	@Override
	public void put12(VirtualMachine vm, int operand, int data) throws VMError {
		//prrr rrii iiii
		int pageRegNum = (operand >> 11) & 0b0000_0001;
		int ofsRegNum = (operand >> 6) & 0b0001_1111;
		int ofsImm = operand & 0b0011_1111;
		//Sign-extend the offset to 32 bits so that java sees it as negative if it's negative.
		if ((ofsImm & 0b0010_0000) != 0) ofsImm |= 0b1111_1111__1111_1111__1111_1111__1100_0000;
		
		Register pageReg = (pageRegNum==0) ? vm.registers().PG0 : vm.registers().PG1;
		Register ofsReg = vm.getRegister(ofsRegNum);
		MemoryPage page = vm.getPage(pageReg.get());
		page.set(ofsReg.get() + ofsImm, data);
	}

	@Override
	public void put32(VirtualMachine vm, int operand, int data) throws VMError {
		//pppp prrr rr.. .... .... iiii iiii iiii
		int pageRegNum = (operand >> 27) & 0b0001_1111;
		int ofsRegNum = (operand >> 22) & 0b0001_1111;
		int ofsImm = operand & 0b0000_1111_1111_1111;
		//Sign-extend the offset to 32 bits so that java sees it as negative if it's negative.
		if ((ofsImm & 0b0000_1000_0000_0000) != 0) ofsImm |= 0b1111_1111__1111_1111__1111_0000__0000_0000;
		
		Register pageReg = vm.getRegister(pageRegNum);
		Register ofsReg = vm.getRegister(ofsRegNum);
		MemoryPage page = vm.getPage(pageReg.get());
		page.set(ofsReg.get() + ofsImm, data);
	}

}
