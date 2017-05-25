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
import com.unascribed.copu.undefined.VMError;

public class OpmodeRegister extends Opmode {

	@Override
	public int getCost() {
		return 0;
	}
	
	public int get4(VirtualMachine vm, int operand) throws VMError {
		//rrrr
		return vm.getRegister(operand).get();
	}
	
	@Override
	public int get12(VirtualMachine vm, int operand) throws VMError {
		//....'...r rrrr
		return vm.getRegister(operand & 0b0001_1111).get();
	}

	@Override
	public int get32(VirtualMachine vm, int operand) throws VMError {
		//.... ....'.... ....'.... ....'...r rrrr
		return vm.getRegister(operand & 0b0001_1111).get();
	}

	@Override
	public void put4(VirtualMachine vm, int operand, int data) throws VMError {
		vm.getRegister(operand & 0b0000_1111).accept(data);
	}
	
	@Override
	public void put12(VirtualMachine vm, int operand, int data) throws VMError {
		//....'...r rrrr
		vm.getRegister(operand & 0b0001_1111).accept(data);
	}

	@Override
	public void put32(VirtualMachine vm, int operand, int data) throws VMError {
		//.... ....'.... ....'.... ....'...r rrrr
		vm.getRegister(operand & 0b0001_1111).accept(data);
	}

}
