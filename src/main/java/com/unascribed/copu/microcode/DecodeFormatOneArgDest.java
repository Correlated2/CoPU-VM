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
import com.unascribed.copu.compiler.RegisterToken;
import com.unascribed.copu.undefined.VMError;
import com.unascribed.copu.undefined.VMKernelPanic;

public class DecodeFormatOneArgDest implements DecodeFormat {

	/*
	 * [CCCC CCCC dddd .... .... .... .... ....|.... .... .... .... .... .... .... ....]
	 * One-arg has a special case: A and D are both the same operand, as some instructions read and other instructions write.
	 */
	
	@Override
	public int getCost(int instructionHigh, int instructionLow) throws VMError {
		return 0; //Dest is always zero-cost
	}
	
	@Override
	public int loadA(VirtualMachine vm, int instructionHigh, int instructionLow) throws VMError {
		int regId = (instructionHigh >> 20) & 0x0F;
		return vm.getRegister(regId).get();
	}

	@Override
	public int loadB(VirtualMachine vm, int instructionHigh, int instructionLow) throws VMError {
		throw new VMKernelPanic("Tried to load nonexistant 'b' operand of a 1-arg instruction");
	}

	@Override
	public void setDest(VirtualMachine vm, int instructionHigh, int instructionLow, int value) throws VMError {
		int regId = (instructionHigh >> 20) & 0x0F;
		vm.getRegister(regId).accept(value);
	}

	@Override
	public long compile(Object[] args, int line) throws CompileError {
		if (args.length>1) throw new CompileError("Too many arguments.", line);
		if (args.length<1) throw new CompileError("Too few arguments.", line);
		
		Object a = args[0];
		if (!(a instanceof RegisterToken)) throw new CompileError("This instruction can only take a register argument.", line);
		
		long result = 0L;
		long operand = ((RegisterToken)a).ordinal();
		if (operand>=16) throw new CompileError("This instruction is ineligible for register "+a.toString(), line);
		result |= (operand << (32+20));
		
		return result;
	}

}
