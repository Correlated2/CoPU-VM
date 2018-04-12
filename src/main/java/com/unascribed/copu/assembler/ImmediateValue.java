/*
 * MIT License
 *
 * Copyright (c) 2017-2018 Isaac Ellingson (Falkreon), Una Thompson (unascribed)
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

package com.unascribed.copu.assembler;

public class ImmediateValue implements Operand {
	public int bitWidth;
	public int value;
	
	public ImmediateValue(int i) {
		this.value = i;
		this.bitWidth = numBitsRequired(value);
	}
	
	public ImmediateValue(float f) {
		this.value = Float.floatToIntBits(f);
		this.bitWidth = 32; //NON-NEGOTIABLE
	}
	
	//NOT COMPREHENSIVE
	private static int numBitsRequired(int i) {
		int bitmask12 = ~0b1111_1111_1111;
		if ((i & bitmask12)==0) return 12;
		
		return 32;
	}

	@Override
	public long as4Bit() throws AssembleError {
		throw AssembleError.withKey("err.assembler.wrongPacking.4");
	}

	@Override
	public long as12Bit() throws AssembleError {
		if (bitWidth>12) throw AssembleError.withKey("err.validate.argTooLarge");
		
		return ((long)value) & 0b1111_1111_1111L; //Opmode.IMMEDATE is zero
	}

	@Override
	public long as32Bit() throws AssembleError {
		return ((long)value) & 0xFFFFFFFFL;
	}
	
	@Override
	public String toString() {
		String result = Integer.toHexString(value);
		while(result.length()<8) result = '0'+result;
		return "0x"+result;
	}
}
