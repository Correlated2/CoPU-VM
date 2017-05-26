package com.unascribed.copu.compiler;

public class ZeroPageAddress {
	//public String key;
	public int value;
	
	public ZeroPageAddress(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
}
