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
import com.unascribed.copu.compiler.CompileError;
import com.unascribed.copu.compiler.Label;
import com.unascribed.copu.undefined.VMError;
import com.unascribed.copu.undefined.VMKernelPanic;

public class DecodeFormatOneArgImm implements DecodeFormat {

	@Override
	public int getCost(int instructionHigh, int instructionLow) throws VMError {
		return 0; //Immediate is folded into the prefetch
	}
	
	@Override
	public int loadA(VirtualMachine vm, int instructionHigh, int instructionLow) throws VMError {
		return instructionLow;
	}

	@Override
	public int loadB(VirtualMachine vm, int instructionHigh, int instructionLow) throws VMError {
		throw new VMKernelPanic("Attempted to access nonexistant operand 'b' in an immediate-mode instruction.");
	}

	@Override
	public void setDest(VirtualMachine vm, int instructionHigh, int instructionLow, int value) throws VMError {
		throw new VMKernelPanic("Attempted to set nonexistant destination of an immediate-mode instruction.");
	}

	@Override
	public long compile(Object[] args, int line) throws CompileError {
		if (args.length>1) throw new CompileError("Too many arguments.", line);
		if (args.length<1) throw new CompileError("Too few arguments.", line);
		
		Object a = args[0];
		long operand = 0L;
		
		if (a instanceof Integer) {
			operand = ((Integer) a).longValue();
		} else if (a instanceof Float) {
			operand = Float.floatToIntBits(((Float)a).floatValue());
		} else if (a instanceof Label) {
			operand = ((Label)a).value;
		}
		
		operand &= 0xFFFFFFFF;
		
		return operand;
	}
}
