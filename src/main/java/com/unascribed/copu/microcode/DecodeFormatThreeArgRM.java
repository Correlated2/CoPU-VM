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
import com.unascribed.copu.undefined.VMError;

public class DecodeFormatThreeArgRM implements DecodeFormat {

	/* [CCCC CCCC .... DDDD dddd dddd dddd ....|AAAA aaaa aaaa aaaa BBBB bbbb bbbb bbbb] */
	
	@Override
	public int getCost(int instructionHigh, int instructionLow) throws VMError {
		Opmode opmodeD = Opmode.forId((instructionHigh >> 16) & 0x0F);
		Opmode opmodeA = Opmode.forId((instructionLow >> 28) & 0x0F);
		Opmode opmodeB = Opmode.forId((instructionLow >> 12) & 0x0F);
		return opmodeD.getCost() + opmodeA.getCost() + opmodeB.getCost();
	}
	
	@Override
	public int loadA(VirtualMachine vm, int instructionHigh, int instructionLow) throws VMError {
		int opmodeId = (instructionLow >> 28) & 0x0F;
		int operand = (instructionLow >> 16) & 0b0000_1111_1111_1111;
		Opmode opmode = Opmode.forId(opmodeId);
		return opmode.get12(vm, operand);
	}

	@Override
	public int loadB(VirtualMachine vm, int instructionHigh, int instructionLow) throws VMError {
		int opmodeId = (instructionLow >> 12) & 0x0F;
		int operand = (instructionLow) & 0b0000_1111_1111_1111;
		Opmode opmode = Opmode.forId(opmodeId);
		return opmode.get12(vm, operand);
	}

	@Override
	public void setDest(VirtualMachine vm, int instructionHigh, int instructionLow, int value) throws VMError {
		int opmodeId = (instructionHigh >> 16) & 0x0F;
		int operand = (instructionLow >> 4) & 0b0000_1111_1111_1111;
		Opmode opmode = Opmode.forId(opmodeId);
		opmode.put12(vm, operand, value);
	}

	@Override
	public long compile(Object[] args, int line) throws CompileError {
		throw new CompileError("Not yet implemented.", line);
	}

}
