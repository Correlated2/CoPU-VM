package com.unascribed.copu;

import com.unascribed.copu.undefined.VMError;

public class TestMain {
	
	public static byte[] testProgram = {
			0x02, 0x11, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, //ADD R0, R0, R0
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, //NOP
	 (byte) 0xFF, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, //HALT
			0x07, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, //JMP IMM[0]
	};
	
	public static String[] testPreCompile = {
			"NOP\n    NOP\n\n\tNOP\nJMP 0",
			";;;\n;\nNOP\n;"
	};
	
	public static void main(String[] args) {
		Compiler compiler = new Compiler();
		compiler.parse(testPreCompile);
		byte[] result = compiler.toByteArray();
		compiler = null;
		System.out.println("Compiler result: "+result.length+" bytes.");
		
		/*
		VirtualMachine machine = new VirtualMachine();
		machine.loadProgram(testProgram);
		
		try {
			while(true) {
				machine.runCycle();
			}
		} catch (VMError err) {
			err.printStackTrace();
		}*/
	}
}
