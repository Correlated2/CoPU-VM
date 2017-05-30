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

import java.util.HashMap;

import com.unascribed.copu.VirtualMachine;
import com.unascribed.copu.undefined.VMError;
import com.unascribed.copu.undefined.VMUserspaceError;

public final class CallRegistry {
	private static HashMap<String, Integer> symbols = new HashMap<>();
	private static HashMap<Integer, CallHandler> handlers = new HashMap<>();
	private CallRegistry() {}
	
	static {
		register("sys.malloc", 0x05150001, (it)->{});
		
		
		
	}
	
	public static boolean isRegistered(int constant) {
		return handlers.containsKey(constant);
	}
	
	public static void register(String symbol, int constant, CallHandler handler) {
		symbols.put(symbol, constant);
		handlers.put(constant, handler);
	}
	
	public static void execute(VirtualMachine vm, int constant) throws VMError {
		CallHandler handler = handlers.get(constant);
		if (handler!=null) {
			
		} else {
			throw new VMUserspaceError("");
		}
	}
}
