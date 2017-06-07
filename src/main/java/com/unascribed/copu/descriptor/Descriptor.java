package com.unascribed.copu.descriptor;

import java.util.function.Consumer;

public interface Descriptor<T> {
	public void write(int i);
	public void flush();
	public void setConsumer(Consumer<T> consumer);
}
