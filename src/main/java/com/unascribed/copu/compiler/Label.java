package com.unascribed.copu.compiler;

import java.util.Map;

public class Label implements Map.Entry<String, Integer> {
	public String key;
	public int value;
	
	public Label(String name, Integer value) {
		this.key = name;
		this.value = value;
	}

	@Override
	public String getKey() {
		return key;
	}

	@Override
	public Integer getValue() {
		return value;
	}

	@Override
	public Integer setValue(Integer i) {
		int result = this.value;
		this.value = i;
		return result;
	}
}
