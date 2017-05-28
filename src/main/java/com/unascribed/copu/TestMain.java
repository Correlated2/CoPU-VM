package com.unascribed.copu;

import com.unascribed.copu.compiler.CompileError;
import com.unascribed.copu.compiler.Assembler;
import com.unascribed.copu.undefined.VMError;

public class TestMain {
	/*
	public static byte[] testProgram = {
			0x02, 0x11, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, //ADD R0, R0, R0
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, //NOP
	 (byte) 0xFF, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, //HALT
			0x07, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, //JMP IMM[0]
	};*/
	
	public static String[] testPreCompile = {
			"MOV R0, 1",
			"ADD R1, R0",
			"ADD R0, 1",
			"JL R0, 11, MEM[8]",
			"MOV R0, 0",
			"HALT"
	};
	
	public static void main(String[] args) {
		byte[] program = new byte[0];
		
		Assembler compiler = new Assembler();
		try {
			compiler.parse(testPreCompile);
			program = compiler.toByteArray();
			compiler = null;
			System.out.println("Compiler result: "+program.length+" bytes.");
		} catch (CompileError error) {
			System.out.println("Error on line "+error.getLine()+":");
			error.printStackTrace();
		}
		
		VirtualMachine machine = new VirtualMachine();
		machine.loadProgram(program);
		
		try {
			while(true) {
				machine.runCycle();
			}
		} catch (VMError err) {
			System.out.println(err.getMessage());
		}
		
		System.out.println("REGISTER FILE DUMP");
		for(Register reg : machine.registers().table) {
			System.out.print(Integer.toHexString(reg.get())+"\t");
		}
		System.out.println();
	}
}
