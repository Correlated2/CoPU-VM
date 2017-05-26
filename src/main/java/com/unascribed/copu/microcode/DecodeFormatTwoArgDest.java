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

import com.unascribed.copu.Language;
import com.unascribed.copu.VirtualMachine;
import com.unascribed.copu.compiler.CompileError;
import com.unascribed.copu.compiler.RegisterToken;
import com.unascribed.copu.undefined.VMError;
import com.unascribed.copu.undefined.VMKernelPanic;

public class DecodeFormatTwoArgDest implements DecodeFormat {

	/* [CCCC CCCC dddd .... .... .... .... AAAA|aaaa aaaa aaaa aaaa aaaa aaaa aaaa aaaa] */
	
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
		throw new VMKernelPanic("Tried to load nonexistant 'b' operand of a 2-arg-dest instruction");
	}

	@Override
	public void setDest(VirtualMachine vm, int instructionHigh, int instructionLow, int value) throws VMError {
		int operand = (instructionHigh >> 20) & 0x0F;
		Opmode.dest().put4(vm, operand, value);
	}

	@Override
	public long compile(Object[] args, int line) throws CompileError {
		if (args.length==0) throw new CompileError(Language.getCurrent().localize("err.validate.NotEnoughArgs"), line);
		if (args.length >2) throw new CompileError(Language.getCurrent().localize("err.validate.tooManyArgs"), line);
		
		if (args.length==1) {
			//try d == a
			//This is actually a common case, as with FSIN F0 or ABS R4
			if (args[0] instanceof RegisterToken) {
				long operand = ((RegisterToken) args[0]).ordinal();
				if (operand>15) {
					throw new CompileError(Language.getCurrent().localize("err.validate.destOperandRequired"), line);
				}
				long result = 0L;
				result |= ((long)operand) << 52;
				result |= ((long)Opmode.REGISTER) << 32;
				result |= ((long)operand);
				return result;
			} else {
				throw new CompileError(Language.getCurrent().localize("err.validate.destOperandRequired"), line);
			}
			
		} else if (args.length==2) {
			//Most proper case.
			
			//d param
			if (!(args[0] instanceof RegisterToken)) throw new CompileError(Language.getCurrent().localize("err.validate.destOperandRequired"), line);
			long d = ((RegisterToken)args[0]).ordinal();
			if (d>15) throw new CompileError(Language.getCurrent().localize("err.validate.destOperandRequired"), line);
			long result = 0L;
			result |= ((long)d) << 52;
			
			//a param
			if (args[1] instanceof RegisterToken) {
				long a = ((RegisterToken)args[1]).ordinal();
				result |= ((long)Opmode.REGISTER) << 32;
				result |= a;
			} else if (args[1] instanceof Integer) {
				long a = ((Integer) args[1]).intValue();
				result |= ((long)Opmode.IMMEDIATE) << 32;
				result |= a;
			} else if (args[1] instanceof Float) {
				long a = Float.floatToIntBits(((Float) args[1]).floatValue());
				result |= ((long)Opmode.IMMEDIATE) << 32;
				result |= a;
			}
			
			return result;
		}
		
		throw new IllegalStateException("Compiler validation error!");
	}

}
