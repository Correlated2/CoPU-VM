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
import com.unascribed.copu.compiler.RegisterToken;
import com.unascribed.copu.undefined.VMError;
import com.unascribed.copu.undefined.VMKernelPanic;

public class DecodeFormatOneArg implements DecodeFormat {

	/*
	 * [CCCC CCCC .... .... .... .... .... AAAA|aaaa aaaa aaaa aaaa aaaa aaaa aaaa aaaa]
	 * One-arg has a special case: A and D are both the same operand, as some instructions read and other instructions write.
	 */
	
	@Override
	public int getCost(int instructionHigh, int instructionLow) throws VMError {
		Opmode opmode = Opmode.forId(instructionHigh & 0x0F);
		return opmode.getCost();
	}
	
	@Override
	public int loadA(VirtualMachine vm, int instructionHigh, int instructionLow) throws VMError {
		Opmode opmode = Opmode.forId(instructionHigh & 0x0F);
		return opmode.get32(vm, instructionLow);
	}

	@Override
	public int loadB(VirtualMachine vm, int instructionHigh, int instructionLow) throws VMError {
		throw new VMKernelPanic("Tried to load nonexistant 'b' operand of a 1-arg instruction");
	}

	@Override
	public void setDest(VirtualMachine vm, int instructionHigh, int instructionLow, int value) throws VMError {
		Opmode opmode = Opmode.forId(instructionHigh & 0x0F);
		opmode.put32(vm, instructionLow, value);
	}

	@Override
	public long compile(Object[] args, int line) throws CompileError {
		if (args.length>1) throw new CompileError("Too many arguments.", line);
		if (args.length<1) throw new CompileError("Too few arguments.", line);
		
		Object a = args[0];
		
		long result = 0L;
		long opmode = 0L;
		long operand = 0L;
		
		if (a instanceof RegisterToken) {
			opmode = Opmode.REGISTER;
			operand = ((RegisterToken) a).ordinal();
		} else if (a instanceof Integer) {
			opmode = Opmode.IMMEDIATE;
			operand = ((Integer)a).longValue();
		} else if (a instanceof Float) {
			opmode = Opmode.IMMEDIATE;
			operand = Float.floatToIntBits(((Float)a).floatValue());
		} else if (a instanceof Label) {
			opmode = Opmode.IMMEDIATE_ADDRESS;
			operand = ((Label)a).value;
		}
		
		//Just to be safe, make sure we can't clobber bits we don't own.
		opmode &= 0x0F;
		operand &= 0xFFFFFFFF;
		
		//Stuff them into the A param
		result |= (opmode << 32);
		result |= (operand);
		
		return result;
	}
}
