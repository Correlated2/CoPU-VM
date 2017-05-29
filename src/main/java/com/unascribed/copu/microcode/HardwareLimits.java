package com.unascribed.copu.microcode;

public class HardwareLimits {
	public static final int PIPELINE_DEPTH = 4;
	
	//These values are fairly sane and realistic, and also quite subject to change.
	public static final int COST_FPU = 5;
	public static final int COST_FPU_TRIG = 80;
	public static final int COST_MODULE_LOAD = 32; //Assuming it's from ROM. A fast disk would be in the thousands of cycles.
	public static final int COST_BRANCH_STALL = PIPELINE_DEPTH;
	public static final int COST_MEMORY_ACCESS_BYTE = 1;
	public static final int COST_MEMORY_ACCESS_WORD = 1;
	
	public static final int STACK_DEPTH = 64;
}
