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

public class DirectAddress implements Operand {
	public RegisterToken page;
	public RegisterToken offset;
	
	public DirectAddress(RegisterToken page, RegisterToken offset) {
		this.page = page;
		this.offset = offset;
	}
	
	public RegisterToken getPage() { return page; }
	public RegisterToken getOffset() { return offset; }

	@Override
	public long as4Bit() throws AssembleError {
		throw AssembleError.withKey("err.assembler.wrongPacking.4");
	}

	@Override
	public long as12Bit() throws AssembleError {
		long result = 1L << 12;
		result |= ((long)page.ordinal()) << 7;
		result |= ((long)offset.ordinal());
		
		return result;
	}

	@Override
	public long as32Bit() throws AssembleError {
		long result = 1L << 32;
		result |= ((long)page.ordinal()) << 27;
		result |= ((long)offset.ordinal());
		
		return result;
	}
	
	@Override
	public String toString() {
		return "MEM["+page.name()+":"+offset.name()+"]";
	}
}
