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

public class DecodeFormatThreeArgMultiDest implements DecodeFormat {
	/* [CCCC CCCC dddd aaaa .... .... .... BBBB|bbbb bbbb bbbb bbbb bbbb bbbb bbbb bbbb] */
	
	@Override
	public int getCost(int instructionHigh, int instructionLow) throws VMError {
		Opmode opmode = Opmode.forId(instructionHigh & 0x0F);
		return opmode.getCost();
	}
	
	@Override
	public int loadA(VirtualMachine vm, int instructionHigh, int instructionLow) throws VMError {
		int operand = (instructionHigh >> 16) & 0x0F;
		return Opmode.dest().get4(vm, operand);
	}

	@Override
	public int loadB(VirtualMachine vm, int instructionHigh, int instructionLow) throws VMError  {
		int modeId = instructionHigh & 0x0F;
		Opmode opmode = Opmode.forId(modeId);
		return opmode.get32(vm, instructionLow);
	}

	@Override
	public int loadD(VirtualMachine vm, int high, int low) throws VMError {
		int operand = (high >> 20) & 0x0F;
		return Opmode.dest().get4(vm, operand);
	}
	
	@Override
	public void setDest(VirtualMachine vm, int instructionHigh, int instructionLow, int value) throws VMError  {
		int operand = (instructionHigh >> 20) & 0x0F;
		Opmode.dest().put4(vm, operand, value);
	}

	@Override
	public long compile(Operand[] args) throws AssembleError {
		if (args.length<2) throw AssembleError.withKey("err.validate.notEnoughArgs");
		if (args.length>3) throw AssembleError.withKey("err.validate.tooManyArgs");
		
		Operand d = args[0];
		Operand a = (args.length==3) ? args[1] : d;
		Operand b = (args.length==3) ? args[2] : args[1];
		
		return (d.as4Bit() << 52) | (a.as4Bit() << 48) | (b.as32Bit());
	}
}
