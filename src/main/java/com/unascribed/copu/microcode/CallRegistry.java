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
