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
import com.unascribed.copu.undefined.VMUserspaceError;

public class AddressingMode {
	public static final int IMMEDIATE         = 0b0000;
	public static final int REGISTER          = 0b0001;
	public static final int IMMEDIATE_ADDRESS = 0b0010;
	public static final int DIRECT_ADDRESS    = 0b0011;
	public static final int INDIRECT_ADDRESS  = 0b0100;
	public static final int REG_WITH_OFFSET   = 0b0101;
	public static final int DIRECT_POSTINC    = 0b0110;
	
	public static final int IMMEDIATE_B       = 0b1000; //Unused, but fun undefined behavior
	public static final int REGISTER_B        = 0b1001;
	public static final int IMMEDIATE_ADDR_B  = 0b1010;
	
	public static int fetch4(VirtualMachine vm, int operand) throws VMError {
		return vm.getRegister(operand).getAsInt();
	}
	
	public static int fetch12(VirtualMachine vm, int mode, int operand) throws VMError {
		switch(mode) {
		case IMMEDIATE:
			return operand;
		case REGISTER:
			//.... ...r rrrr
			return vm.getRegister(operand & 0b0000_0001_1111).getAsInt(); //Can trigger a userspace error from invalid register id
		case IMMEDIATE_ADDRESS:
			//prrr rrrr rrrr
			int pageID = operand & 0b1000_0000_0000;
			Register pageRegister = (pageID==0) ? vm.registers().PG0 : vm.registers().PG1;
			MemoryPage page = vm.getPage(pageRegister.getAsInt()); //This can trigger undefined behavior or a vm page fault
			int address = operand & 0b0111_1111_1111;
			return page.get(address); //It is UNLIKELY if not impossible to trigger a vm page fault here due to being restricted to 11 bits.
		case DIRECT_ADDRESS:
			//pppp p..r rrrr
			int pageRegisterNum = (operand >> 7) & 0b0001_1111;
			int ofsRegisterNum = operand &  0b0001_1111;
		case IMMEDIATE_B:
			return operand & 0x0F;
		default:
			throw new VMUserspaceError("Invalid instruction at "+ //page:offset
					Integer.toHexString(vm.getRegister(10).getAsInt())+
					":"+
					Integer.toHexString(vm.getRegister(8).getAsInt()));
		}
	}
	
	public static int fetch20(VirtualMachine vm, int mode, int operand) throws VMError {
		throw new VMUserspaceError("20-bit operands are only in 3-arg r/m instructions which are not implemented.");
	}
	
	public static int fetch32(VirtualMachine vm, int mode, int operand) throws VMError {
		switch(mode) {
		case IMMEDIATE:
			return operand;
		case REGISTER:
			return vm.getRegister(operand & 0x0F).getAsInt();
		case IMMEDIATE_B:
			return operand & 0x0F;
		default:
			throw new VMUserspaceError("Invalid instruction at "+
					Integer.toHexString(vm.getRegister(8).getAsInt()));
		}
	}
}
