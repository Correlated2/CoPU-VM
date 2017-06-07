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

import java.util.ArrayDeque;

import com.unascribed.copu.descriptor.Descriptor;
import com.unascribed.copu.descriptor.DescriptorCharacters;
import com.unascribed.copu.microcode.HardwareLimits;
import com.unascribed.copu.undefined.UndefinedBehavior;
import com.unascribed.copu.undefined.VMError;
import com.unascribed.copu.undefined.VMKernelPanic;
import com.unascribed.copu.undefined.VMPageFault;
import com.unascribed.copu.undefined.VMUserspaceError;

public class VirtualMachine {
	public static final int DESCRIPTOR_STDIN  = 0;
	public static final int DESCRIPTOR_STDOUT = 1;
	public static final int DESCRIPTOR_STDERR = 2;
	
	private RegisterFile registers = new RegisterFile();
	private MemoryPage[] localDescriptorTable = new MemoryPage[16];
	private Descriptor<?>[] fileDescriptorTable = new Descriptor[16];
	private ArrayDeque<Integer> stack = new ArrayDeque<>();
	private int cooldown = HardwareLimits.COST_BRANCH_STALL; //There's been no chance to fill the pipeline.
	private long cycles = 0L;
	private boolean pedantic = false;
	
	private DescriptorCharacters stdout = new DescriptorCharacters();
	private DescriptorCharacters stderr = new DescriptorCharacters();
	
	public VirtualMachine() {
		stdout.setConsumer((it)->System.out.println(it));
		stderr.setConsumer((it)->System.err.println(it));
		fileDescriptorTable[DESCRIPTOR_STDOUT] = stdout;
		fileDescriptorTable[DESCRIPTOR_STDERR] = stderr;
	}
	
	public RegisterFile registers() {
		return registers;
	}
	public Register getRegister(int id) throws VMKernelPanic {
		if (id<0 || id>=registers.table.length) throw new VMKernelPanic("Tried to access invalid register 0x"+Integer.toHexString(id));
		return registers.table[id];
	}
	
	public MemoryPage getPage(int descriptor) throws VMPageFault {
		if (descriptor<0 || descriptor>=localDescriptorTable.length) throw new VMPageFault("Invalid page descriptor 0x"+Integer.toHexString(descriptor));
		
		MemoryPage result = localDescriptorTable[descriptor];
		if (result==null) {
			//TODO: This is the sort of error that typically bricks machines before they can bluescreen. This would be a FANTASTIC opportunity for undefined behavior.
			UndefinedBehavior.engage(this, new VMPageFault("Invalid page descriptor "+Integer.toHexString(descriptor)));
			return localDescriptorTable[0];
		}
		return result;
	}
	
	public Descriptor<?> getDescriptor(int descriptor) {
		if (descriptor<0 || descriptor>=localDescriptorTable.length) throw new VMPageFault("Invalid file descriptor 0x"+Integer.toHexString(descriptor)); 
		return fileDescriptorTable[descriptor];
	}
	
	public ArrayDeque<Integer> stack() { return stack; }
	
	public void loadProgram(byte[] program) {
		MemoryPage programPage = new MemoryPage(program);
		localDescriptorTable[0] = programPage;
	}
	
	public void runCycle() throws VMError {
		cycles++;
		
		if (cooldown<=0) {
			int cs = registers.CS.get();
			int ip = registers.IP.get();
			MemoryPage memory = getPage(cs);
			int high = memory.get(ip);
			int low = memory.get(ip+4);
			registers.IP.accept(ip+8);
			
			int opid = (high >> 24) & 0xFF;
			Opcode opcode = Opcode.forId(opid);
			if (opcode==null) {
				throw new VMUserspaceError("Invalid instruction at MEM[CS:"+
					Integer.toHexString(ip)+ "]");
			} else {
				if (pedantic) System.out.println("MEM["+Integer.toHexString(ip)+"]: "+opcode.name());
				int cost = opcode.exec(this, high, low);
				cooldown += cost;
			}
		}
		cooldown--;
	}
	
	public void cleanup() {
		stdout.flush();
		stderr.flush();
	}
	
	public long getCycleCount() {
		return cycles;
	}
	
	
}
