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

import com.unascribed.copu.microcode.Opmode;

public enum RegisterToken implements Operand {
	R0, R1, R2, R3, R4, R5, R6, R7,
	X, Y,
	PG0, PG1,
	F0, F1, F2, F3,
	
	CS, IP, SP
	;
	
	public static RegisterToken forName(String s) {
		String t = s.trim().toUpperCase();
		for(RegisterToken token : values()) {
			if(token.name().equals(t)) return token;
		}
		return null;
	}
	
	public boolean isRegister(String s) {
		return forName(s)!=null;
	}

	@Override
	public long as4Bit() throws AssembleError {
		if (this.ordinal()>15) throw AssembleError.withKey("err.validate.destOperandRequired");
		return this.ordinal();
	}

	@Override
	public long as12Bit() throws AssembleError {
		long result = Opmode.REGISTER;
		result = result << 12;
		result |= this.ordinal();
		
		return result;
	}

	@Override
	public long as32Bit() throws AssembleError {
		long result = Opmode.REGISTER;
		result = result << 32;
		result |= this.ordinal();
		
		return result;
	}
}
