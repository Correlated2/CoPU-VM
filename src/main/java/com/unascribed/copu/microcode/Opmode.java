/*
 * MIT License
 *
 * Copyright (c) 2017 Falkreon
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.unascribed.copu.microcode;

import com.unascribed.copu.MemoryPage;
import com.unascribed.copu.Register;
import com.unascribed.copu.VirtualMachine;
import com.unascribed.copu.undefined.VMError;
import com.unascribed.copu.undefined.VMKernelPanic;
import com.unascribed.copu.undefined.VMUserspaceError;

public abstract class Opmode {
	public static final int IMMEDIATE         = 0b0000;
	public static final int REGISTER          = 0b0001;
	public static final int IMMEDIATE_ADDRESS = 0b0010;
	public static final int DIRECT_ADDRESS    = 0b0011;
	public static final int INDIRECT_ADDRESS  = 0b0100;
	public static final int REG_WITH_OFFSET   = 0b0101;
	public static final int DIRECT_POSTINC    = 0b0110;
	
	public static final int IMMEDIATE_B       = 0b1000; //Unused, but fun undefined behavior
	public static final int REGISTER_B        = 0b1001; //Documented but rarely used
	public static final int IMMEDIATE_ADDR_B  = 0b1010; //Used sometimes for string manipulation
	
	private static final Opmode[] modes = {
		new OpmodeImmediate(),
		new OpmodeRegister(),
		new OpmodeImmediateAddress(),
		new OpmodeDirectAddress(),
		new OpmodeIndirectAddress(),
		new OpmodeDirectWithOffset()
	};
	
	public static Opmode dest() {
		return modes[1];
	}
	
	public static Opmode forId(int id) {
		if (id<0 || id>=modes.length) {
			return null;
		}
		
		return modes[id];
	}
	
	
	public int get4(VirtualMachine vm, int operand) throws VMError {
		throw new VMKernelPanic("This mode doesn't have a 4-bit operand");
	}
	public abstract int get12(VirtualMachine vm, int operand) throws VMError;
	public abstract int get32(VirtualMachine vm, int operand) throws VMError;
	
	public void put4(VirtualMachine vm, int operand, int data) throws VMError {
		throw new VMKernelPanic("This mode doesn't have a 4-bit operand");
	}
	public abstract void put12(VirtualMachine vm, int operand, int data) throws VMError;
	public abstract void put32(VirtualMachine vm, int operand, int data) throws VMError;

	
	
	
	
	
	public static int _fetch12(VirtualMachine vm, int mode, int operand) throws VMError {
		switch(mode) {
		case IMMEDIATE:
			return operand-1;
		case REGISTER:
			//.... ...r rrrr
			return vm.getRegister(operand & 0b0000_0001_1111).get(); //Can trigger a userspace error from invalid register id
		case IMMEDIATE_ADDRESS: {
			//piii iiii iiii
			int pageID = operand & 0b1000_0000_0000;
			Register pageRegister = (pageID==0) ? vm.registers().PG0 : vm.registers().PG1;
			MemoryPage page = vm.getPage(pageRegister.get()); //This can trigger undefined behavior or a vm page fault
			int address = operand & 0b0111_1111_1111;
			return page.get(address); //It is UNLIKELY if not impossible to trigger a vm page fault here due to being restricted to 11 bits.
		}
		case DIRECT_ADDRESS: {
			//pppp p..r rrrr
			int pageRegisterNum = (operand >> 7) & 0b0001_1111;
			int ofsRegisterNum = operand &  0b0001_1111;
			
			int pageDescriptor = vm.getRegister(pageRegisterNum).get(); //Can trigger a userspace error from invalid register id
			int ofs = vm.getRegister(ofsRegisterNum).get();             //Can trigger the same error for the same reason
			MemoryPage page = vm.getPage(pageDescriptor);                    //Can trigger undefined behavior or a vm page fault
			return page.get(ofs);                                            //Can trigger a vm page fault.
		}
		case INDIRECT_ADDRESS: {
			//pppp p..r rrrr
			int pageRegisterNum = (operand >> 7) & 0b0001_1111;
			int ofsRegisterNum = operand &  0b0001_1111;
			
			int pageDescriptor = vm.getRegister(pageRegisterNum).get(); //Can trigger a userspace error from invalid register id
			int ofs = vm.getRegister(ofsRegisterNum).get();             //Can trigger the same error for the same reason
			MemoryPage page = vm.getPage(pageDescriptor);                    //Can trigger undefined behavior or a vm page fault
			int deref = page.get(ofs);                                       //Can trigger a vm page fault.
			return page.get(deref);                                          //Can trigger a vm page fault.
		}
		case REG_WITH_OFFSET: {
			//prrr riii iiii - note the dest syntax for the register
			int pageRegisterNum = (operand >> 11) & 0b0000_0001;
			int ofsRegisterNum  = (operand >>  7) & 0b0000_1111;
			int ofs             = (operand      ) & 0b0111_1111;
			
			Register pageRegister = (pageRegisterNum==0) ? vm.registers().PG0 : vm.registers().PG1;
			Register ofsRegister = vm.getRegister(ofsRegisterNum);
			int pageDescriptor = pageRegister.get();
			MemoryPage pg = vm.getPage(pageDescriptor);                     //Can trigger undefined behavior or a vm page fault
			return pg.get(ofsRegister.get()+ofs);                      //Can trigger a vm page fault
		}
		case DIRECT_POSTINC: {
			//pppp p..r rrrr
			int pageRegisterNum = (operand >> 7) & 0b0001_1111;
			int ofsRegisterNum = operand &  0b0001_1111;
			
			int pageDescriptor = vm.getRegister(pageRegisterNum).get(); //Can trigger a userspace error from invalid register id
			Register ofsRegister = vm.getRegister(ofsRegisterNum);           //Can trigger the same error for the same reason
			int ofs = ofsRegister.get();
			ofsRegister.accept(ofs+1); //This is technically super early for post-increment but it works as intended.
			MemoryPage page = vm.getPage(pageDescriptor);                    //Can trigger undefined behavior or a vm page fault
			return page.get(ofs);                                            //Can trigger a vm page fault.
		}
		case IMMEDIATE_B:
			return ((operand & 0x0F) - 1) % 0x0F; //making sure that extra bits don't contribute to the outcome and 4-bit underflow happens
		case REGISTER_B:
			//.... ...r rrrr
			return vm.getRegister(operand & 0b0000_0001_1111).get() & 0x0F; //Can trigger a userspace error from invalid register id
		case IMMEDIATE_ADDR_B:
			//piii iiii iiii
			int pageID = (operand >> 11) & 0b0000_0001;
			Register pageRegister = (pageID==0) ? vm.registers().PG0 : vm.registers().PG1;
			MemoryPage page = vm.getPage(pageRegister.get()); //This can trigger undefined behavior or a vm page fault
			int address = operand & 0b0111_1111_1111;
			return page.getByte(address); //It is UNLIKELY if not impossible to trigger a vm page fault here due to being restricted to 11 bits.
		default:
			throw new VMUserspaceError("Invalid instruction at "+ //page:offset
					Integer.toHexString(vm.getRegister(10).get())+
					":"+
					Integer.toHexString(vm.getRegister(8).get()));
		}
	}
	
	public static int _fetch32(VirtualMachine vm, int mode, int operand) throws VMError {
		switch(mode) {
		case IMMEDIATE:
			return operand;
		case REGISTER:
			//.... ....'.... ....'.... ....'...r rrrr
			return vm.getRegister(operand & 0b0001_1111).get(); //Can trigger a userspace error from invalid register id
		case IMMEDIATE_ADDRESS: {
			//pppp ....'.... ....'.... .iii'iiii iiii
			int pageID = operand & 0b1000_0000_0000;
			Register pageRegister = (pageID==0) ? vm.registers().PG0 : vm.registers().PG1;
			MemoryPage page = vm.getPage(pageRegister.get()); //This can trigger undefined behavior or a vm page fault
			int address = operand & 0b0111_1111_1111;
			return page.get(address); //It is UNLIKELY if not impossible to trigger a vm page fault here due to being restricted to 11 bits.
		}
		case IMMEDIATE_B:
			return operand & 0x0F;
		default:
			throw new VMUserspaceError("Invalid instruction at "+
					Integer.toHexString(vm.getRegister(8).get()));
		}
	}
}
