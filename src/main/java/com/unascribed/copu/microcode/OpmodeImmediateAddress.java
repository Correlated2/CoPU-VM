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

import com.unascribed.copu.MemoryPage;
import com.unascribed.copu.Register;
import com.unascribed.copu.VirtualMachine;
import com.unascribed.copu.undefined.VMError;

public class OpmodeImmediateAddress extends Opmode {

	@Override
	public int getCost() {
		return 1;
	}
	
	@Override
	public int get12(VirtualMachine vm, int operand) throws VMError {
		//piii iiii iiii
		int pageID = (operand >> 11) & 0b0000_0001;
		Register pageRegister = (pageID==0) ? vm.registers().PG0 : vm.registers().PG1;
		MemoryPage page = vm.getPage(pageRegister.get());
		int address = operand & 0b0111_1111_1111;
		return page.get(address);
	}

	@Override
	public int get32(VirtualMachine vm, int operand) throws VMError {
		//pppp p...'.... ....'.... .iii'iiii iiii
		int pageId = (operand >> 27) & 0b0001_1111;
		Register pageRegister = vm.getRegister(pageId);
		MemoryPage page = vm.getPage(pageRegister.get());
		int address = operand & 0b0111_1111_1111;
		return page.get(address);
	}

	@Override
	public void put12(VirtualMachine vm, int operand, int data) throws VMError {
		//piii iiii iiii
		int pageID = (operand >> 11) & 0b0000_0001;
		Register pageRegister = (pageID==0) ? vm.registers().PG0 : vm.registers().PG1;
		MemoryPage page = vm.getPage(pageRegister.get());
		int address = operand & 0b0111_1111_1111;
		page.set(address, data);
	}

	@Override
	public void put32(VirtualMachine vm, int operand, int data) throws VMError {
		//pppp p...'.... ....'.... .iii'iiii iiii
		int pageId = (operand >> 27) & 0b0001_1111;
		Register pageRegister = vm.getRegister(pageId);
		MemoryPage page = vm.getPage(pageRegister.get());
		int address = operand & 0b0111_1111_1111;
		page.set(address, data);
	}

}
