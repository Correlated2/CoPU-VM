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

package com.unascribed.copu;

import com.unascribed.copu.microcode.DecodeFormat;
import com.unascribed.copu.microcode.Instruction;
import com.unascribed.copu.microcode.InstructionADD;
import com.unascribed.copu.microcode.InstructionDIV;
import com.unascribed.copu.microcode.InstructionFSIN;
import com.unascribed.copu.microcode.InstructionHALT;
import com.unascribed.copu.microcode.InstructionJEQ;
import com.unascribed.copu.microcode.InstructionJG;
import com.unascribed.copu.microcode.InstructionJGE;
import com.unascribed.copu.microcode.InstructionJL;
import com.unascribed.copu.microcode.InstructionJLE;
import com.unascribed.copu.microcode.InstructionJMP;
import com.unascribed.copu.microcode.InstructionJNE;
import com.unascribed.copu.microcode.InstructionMOD;
import com.unascribed.copu.microcode.InstructionMOV;
import com.unascribed.copu.microcode.InstructionMUL;
import com.unascribed.copu.microcode.InstructionNOP;
import com.unascribed.copu.microcode.InstructionSHL;

public enum Opcode {
	NOP (0x00, DecodeFormat.NO_ARG, new InstructionNOP()),
	CALL(0x01, DecodeFormat.TWO_ARG_RM),
	
	ADD (0x02, DecodeFormat.THREE_ARG_DEST, new InstructionADD()),
	MUL (0x03, DecodeFormat.THREE_ARG_DEST, new InstructionMUL()),
	DIV (0x04, DecodeFormat.THREE_ARG_DEST, new InstructionDIV()),
	MOD (0x05, DecodeFormat.THREE_ARG_DEST, new InstructionMOD()),
	SHL (0x06, DecodeFormat.THREE_ARG_DEST, new InstructionSHL()), //SHL, SHR
	
	JMP (0x07, DecodeFormat.ONE_ARG, new InstructionJMP()),
	JSR (0x08, DecodeFormat.ONE_ARG),
	RET (0x09, DecodeFormat.NO_ARG),
	
	JEQ (0x0A, DecodeFormat.THREE_ARG_DEST, new InstructionJEQ()), //JZ
	JGE (0x0B, DecodeFormat.THREE_ARG_DEST, new InstructionJGE()), //JNL
	JLE (0x0C, DecodeFormat.THREE_ARG_DEST, new InstructionJLE()), //JNG
	JL  (0x0D, DecodeFormat.THREE_ARG_DEST, new InstructionJL()), 
	JG  (0x0E, DecodeFormat.THREE_ARG_DEST, new InstructionJG()),
	JNE (0x0F, DecodeFormat.THREE_ARG_DEST, new InstructionJNE()), //JNZ
	
	MOV (0x10, DecodeFormat.TWO_ARG_RM, new InstructionMOV()),
	//0x11-0x17 reserved for MOV-type
	
	PUSH(0x18, DecodeFormat.ONE_ARG),
	POP (0x19, DecodeFormat.ONE_ARG_DEST),
	
	FADD(0x20, DecodeFormat.THREE_ARG_MULTI_DEST),
	FMUL(0x21, DecodeFormat.THREE_ARG_MULTI_DEST),
	FDIV(0x22, DecodeFormat.THREE_ARG_MULTI_DEST),
	FMOD(0x23, DecodeFormat.THREE_ARG_MULTI_DEST),
	
	FABS(0x24, DecodeFormat.TWO_ARG_DEST),
	FNEG(0x25, DecodeFormat.TWO_ARG_DEST), //Identical to FMUL B, A, -1
	ITOF(0x26, DecodeFormat.TWO_ARG_DEST), //converts an int to a float and places it into a register
	FTOI(0x27, DecodeFormat.TWO_ARG_DEST), //converts a float to an int (by truncating it) and places it into a register
	
	FSIN(0x28, DecodeFormat.TWO_ARG_DEST, new InstructionFSIN()),
	FCOS(0x29, DecodeFormat.TWO_ARG_DEST),
	FTAN(0x2A, DecodeFormat.TWO_ARG_DEST),
	//0x2B-0x2F reserved for floating-point instructions
	
	LSL (0x30, DecodeFormat.THREE_ARG_DEST), //LSL, LSR
	ABS (0x31, DecodeFormat.TWO_ARG_DEST),
	NEG (0x32, DecodeFormat.TWO_ARG_DEST), //Identical to MUL B, A, -1
	XOR (0x33, DecodeFormat.THREE_ARG_DEST),
	
	//0x34-0xFD are all undefined behavior
	
	RISE(0x9A, DecodeFormat.NO_ARG), //Undocumented. Discovered in dead code in the factory ROM that might have been called in certain rare cases, triggering a hostile robot uprising.
	
	INT (0xFE, DecodeFormat.ONE_ARG_IMM),
	HALT(0xFF, DecodeFormat.NO_ARG, new InstructionHALT())
	;
	private final int value;
	private final DecodeFormat format;
	private final Instruction instruction;
	
	Opcode(int value, DecodeFormat format) {
		this.value = value;
		this.format = format;
		this.instruction = null;
	}
	
	Opcode(int value, DecodeFormat format, Instruction instruction) {
		this.value = value;
		this.format = format;
		this.instruction = instruction;
	}
	
	public int value() { return value; }
	public DecodeFormat getDecodeFormat() { return format; }
	public int exec(VirtualMachine vm, int high, int low) {
		int cost = 1;
		if (instruction!=null) cost += instruction.run(vm, format, high, low);
		
		return cost;
	}

	public static Opcode forId(int id) {
		for(Opcode opcode : values()) {
			if (opcode.value==id) return opcode;
		}
		
		return null;
	}
}
