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
import com.unascribed.copu.compiler.Operand;
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
	public long compile(Operand[] args) throws CompileError {
		if (args.length==0) throw CompileError.withKey("err.validate.NotEnoughArgs");
		if (args.length >2) throw CompileError.withKey("err.validate.tooManyArgs");
		
		Operand d = (Operand)args[0];
		Operand a = (args.length==2) ? (Operand)args[1] : (Operand)args[0];
		
		return (d.as4Bit() << 52) | a.as32Bit();
	}

}
