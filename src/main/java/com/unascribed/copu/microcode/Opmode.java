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

import com.unascribed.copu.VirtualMachine;
import com.unascribed.copu.assembler.AssembleError;
import com.unascribed.copu.undefined.VMError;
import com.unascribed.copu.undefined.VMKernelPanic;

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
		new OpmodeDirectWithOffset(),
		new OpmodeDirectWithPostIncrement()
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
	
	public abstract int getCost();
	
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
	
	public boolean canAssemble(Object o) {
		return false;
	}
	
	public long assemble4(Object o) throws AssembleError {
		throw AssembleError.withKey("err.assembler.wrongPacking.4");
	}
	
	public long assemble12(Object o) throws AssembleError {
		throw AssembleError.withKey("err.assembler.wrongPacking.12");
	}
	
	public long assemble32(Object o) throws AssembleError {
		throw AssembleError.withKey("err.assembler.wrongPacking.32");
	}
}
