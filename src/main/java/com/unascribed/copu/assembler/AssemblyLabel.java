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

package com.unascribed.copu.assembler;

/**
 * Represents an argument to an instruction that can't be understood as a compile-time constant.
 * "Compile-time constants" could be register names like "R0", fixed locations in memory like
 * "MEM[200]", or raw ints like 0x04B6. If it's not one of these, it's probably a label to an
 * address in memory, and we may not know what its target is yet.
 * 
 * <p>In situations like these, we take note of the label's name and the byte-offset of the
 * instruction that referred to it, so that we can either fill it in on the second pass, or if
 * its destination is still unknown, emit an error including its line number.
 */
public class AssemblyLabel implements Operand {
	public String name;
	public int byteOffset;
	public int line;
	
	/**
	 * Creates a new AssemblyLabel
	 * @param name	the String value of the token
	 * @param offset the offset of the *instruction* at which this label occurred. (label fill-in will typically take place at offset+1)
	 */
	public AssemblyLabel(String name, int offset) {
		this.name = name;
		this.byteOffset = offset;
	}
	
	public AssemblyLabel atLine(int line) {
		this.line = line;
		return this;
	}

	@Override
	public long as4Bit() throws AssembleError {
		throw new AssembleError("Label \""+name+"\" cannot be used as a dest operand.");
	}

	@Override
	public long as12Bit() throws AssembleError {
		throw new AssembleError("Using labels in 12-bit R/M operands is not yet implemented.");
		//return 0;
	}

	@Override
	public long as32Bit() throws AssembleError {
		//Logic outside this will handle the fill-in. Use zero for now.
		return 0;
	}
	
	@Override
	public String toString() {
		return this.name;
	}
}
