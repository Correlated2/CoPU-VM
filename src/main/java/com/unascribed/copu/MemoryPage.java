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

import com.unascribed.copu.undefined.VMError;
import com.unascribed.copu.undefined.VMPageFault;

public class MemoryPage {
	public static final int PAGE_SIZE = 2048;
	
	private byte[] data;
	
	public MemoryPage() {
		this.data = new byte[0];
	}
	
	public MemoryPage(byte[] program) {
		this.data = program;
	}

	public int getByte(int addr) throws VMError {
		if (addr<0 || addr>=PAGE_SIZE) {
			throw new VMPageFault("Invalid page access at "+Integer.toHexString(addr));
		}
		
		if (data==null || addr>=data.length) return 0;
		
		return data[addr];
	}
	
	public int get(int addr) throws VMError {
		int a = getByte(addr  ) & 0xFF;
		int b = getByte(addr+1) & 0xFF;
		int c = getByte(addr+2) & 0xFF;
		int d = getByte(addr+3) & 0xFF;
		return (a << 24) | (b << 16) | (c << 8) | (d << 0);
	}
	
	public void setByte(int addr, byte val) {
		if (addr<0 || addr+3>=PAGE_SIZE) {
			throw new VMPageFault("Invalid page access at "+Integer.toHexString(addr));
		} else {
			if (addr>=data.length) {
				//Resize the memory page
				/* Ideally, the page size will double on a resizing access. This will help absorb an
				 * initialize-and-copy-from-0 and avoid hundreds or thousands of consecutive resizes.
				 * If the requested memory region tops out at more than double, resize to that instead.
				 * As always, access is absolutely capped at 2048 bytes in this page.
				 */
				int newSize = Math.min(Math.max(data.length*2, addr+4), PAGE_SIZE);
				byte[] newdata = new byte[newSize];
				System.arraycopy(data, 0, newdata, 0, data.length);
			}

			data[addr] = val;
		}
	}
	
	public void set(int addr, int val) {
		byte a = (byte)((val >> 24) & 0xFF);
		byte b = (byte)((val >> 16) & 0xFF);
		byte c = (byte)((val >>  8) & 0xFF);
		byte d = (byte)((val      ) & 0xFF);
		
		setByte(addr, a);
		setByte(addr+1, b);
		setByte(addr+2, c);
		setByte(addr+3, d);
	}
}
