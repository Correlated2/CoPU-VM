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

public interface DecodeFormat {
	public static final DecodeFormat NO_ARG  = new NullDecodeFormat();
	public static final DecodeFormat ONE_ARG = new DecodeFormatOneArg();
	public static final DecodeFormat ONE_ARG_IMM = new DecodeFormatOneArg();
	public static final DecodeFormat ONE_ARG_DEST = new DecodeFormatOneArgDest();
	public static final DecodeFormat TWO_ARG_RM = new DecodeFormatTwoArgRM();
	public static final DecodeFormat TWO_ARG_DEST = new DecodeFormatTwoArgDest();
	public static final DecodeFormat THREE_ARG_RM = new DecodeFormatThreeArgRM(); //RARE / UNUSED
	public static final DecodeFormat THREE_ARG_DEST = new DecodeFormatThreeArgDest();
	public static final DecodeFormat THREE_ARG_MULTI_DEST = new DecodeFormatThreeArgMultiDest();
	
	public int getCost(int high, int low);
	public int loadA(VirtualMachine vm, int high, int low) throws VMError;
	public int loadB(VirtualMachine vm, int high, int low) throws VMError;
	public int loadD(VirtualMachine vm, int high, int low) throws VMError;
	public void setDest(VirtualMachine vm, int high, int low, int value) throws VMError;
	public long compile(Operand[] args) throws CompileError;
	
	public static class NullDecodeFormat implements DecodeFormat {
		@Override
		public int getCost(int instructionHigh, int instructionLow) throws VMError {
			return 0;
		}
		
		@Override
		public int loadA(VirtualMachine vm, int high, int low) {
			throw new VMKernelPanic("Attempted to load 'a' arg for no-arg instruction!");
		}
		@Override
		public int loadB(VirtualMachine vm, int high, int low) {
			throw new VMKernelPanic("Attempted to load 'b' arg for no-arg instruction!");
		}
		@Override
		public int loadD(VirtualMachine vm, int high, int low) throws VMError {
			throw new VMKernelPanic("Attempted to load 'd' arg for no-arg instruction!");
		}
		
		@Override
		public void setDest(VirtualMachine vm, int high, int low, int value) {
			throw new VMKernelPanic("Attempted to set dest for no-arg instruction!");
		}

		@Override
		public long compile(Operand[] args) throws CompileError {
			if (args.length>0) throw CompileError.withKey("err.validate.tooManyArgs");
			return 0L;
		}

		
	}
}
