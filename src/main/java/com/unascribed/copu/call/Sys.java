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

package com.unascribed.copu.call;

import com.unascribed.copu.MemoryPage;
import com.unascribed.copu.VirtualMachine;
import com.unascribed.copu.descriptor.Descriptor;
import com.unascribed.copu.undefined.VMError;

public class Sys {
	
	public static void print(VirtualMachine vm) {
		MemoryPage page = vm.getPage(vm.registers().PG0.get());
		int addr = vm.registers().R1.get();
		int limit = vm.registers().R2.get();
		Descriptor<?> descriptor = vm.getDescriptor(vm.registers().R3.get());
		
		page.readNullTerminated(addr, limit, descriptor::write);
	}
	
	public static void println(VirtualMachine vm) {
		MemoryPage page = vm.getPage(vm.registers().PG0.get());
		int addr = vm.registers().R1.get();
		int limit = vm.registers().R2.get();
		Descriptor<?> descriptor = vm.getDescriptor(vm.registers().R3.get());
		if (descriptor==null) throw new VMError("Attempted to write to invalid descriptor 0x"+Integer.toHexString(vm.registers().R3.get())+"");
		
		page.readNullTerminated(addr, limit, descriptor::write);
		descriptor.write(0xD); //If there were unfinished surrogate sets before this, they'll get clobbered and the newline will get written instead.
	}
	
	public static void pipe(VirtualMachine vm) {
		MemoryPage page = vm.getPage(vm.registers().PG0.get());
		int addr = vm.registers().R1.get();
		
		String s = page.readString(addr, 16);
		if (s.equals("stdout")) {
			vm.registers().R0.accept(VirtualMachine.DESCRIPTOR_STDOUT);
		} else if (s.equals("stderr")) {
			vm.registers().R0.accept(VirtualMachine.DESCRIPTOR_STDERR);
		} else {
			vm.registers().R0.accept(-1);
		}
	}
}
