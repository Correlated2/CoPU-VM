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

public class OpmodeImmediate extends Opmode {

	@Override
	public int getCost() {
		return 0;
	}
	
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

	@Override
	public boolean canAssemble(Object o) {
		return
				o instanceof Integer ||
				o instanceof Float;
	}
	
	@Override
	public long assemble12(Object o) throws AssembleError {
		if (o instanceof Integer) {
			long operand = ((Integer) o).intValue();
			if (operand>0xFF_FFFF) throw AssembleError.withKey("err.validate.argTooLarge");
			operand |= (((long)Opmode.IMMEDIATE) << 12);
			return operand;
		}
		if (o instanceof Float) {
			//Can never fit immediate float in 12-bit fields.
			throw AssembleError.withKey("err.validate.argTooLarge");
		}
		
		throw AssembleError.withKey("err.assembler.wrongType");
	}
	
	@Override
	public long assemble32(Object o) throws AssembleError {
		if (o instanceof Integer) {
			long operand = ((Integer) o).intValue();
			operand &= 0xFFFF_FFFFL;
			operand |= (((long)Opmode.IMMEDIATE) << 32);
			return operand;
		}
		if (o instanceof Float) {
			long operand = Float.floatToIntBits(((Float) o).floatValue());
			operand &= 0xFFFF_FFFFL;
			operand |= (((long)Opmode.IMMEDIATE) << 32);
			return operand;
		}
		
		throw AssembleError.withKey("err.assembler.wrongType");
	}
}
