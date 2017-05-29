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
import com.unascribed.copu.assembler.Operand;
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
	public int loadD(VirtualMachine vm, int high, int low) throws VMError {
		int operand = (high >> 20) & 0x0F;
		return Opmode.dest().get4(vm, operand);
	}
	
	@Override
	public void setDest(VirtualMachine vm, int high, int low, int value) throws VMError {
		int operand = (high >> 20) & 0x0F;
		Opmode.dest().put4(vm, operand, value);
	}

	@Override
	public long compile(Operand[] args) throws AssembleError {
		if (args.length>1) throw AssembleError.withKey("err.validate.tooManyArgs");
		if (args.length<1) throw AssembleError.withKey("err.validate.notEnoughArgs");
		
		return args[0].as4Bit() << 52;
	}
}
