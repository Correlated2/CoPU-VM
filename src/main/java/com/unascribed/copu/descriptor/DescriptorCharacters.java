package com.unascribed.copu.descriptor;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

/**
 * A file descriptor which represents a character stream, such as the default pipes "stdout" and "stderr".
 */
public class DescriptorCharacters implements Descriptor<String> {
	private Consumer<String> consumer;
	ByteArrayOutputStream stream = new ByteArrayOutputStream();
	
	@Override
	public void write(int i) {
		if (i==0x0D) {
			flush();
		} else {
			stream.write(i);
		}
	}
	
	/**
	 * Write a String out to this character stream
	 * @param s
	 */
	public void write(String s) {
		byte[] b = s.getBytes(StandardCharsets.UTF_8);
		for(int i=0; i<b.length; i++) {
			write(b[i]);
		}
	}
	
	/**
	 * Write a String out to this character stream, followed by a newline.
	 * @param s
	 */
	public void writeln(String s) {
		write(s);
		write(0x0D);
	}
	
	@Override
	public void flush() {
		String result = new String(stream.toByteArray(), StandardCharsets.UTF_8);
		stream.reset();
		if (consumer!=null) consumer.accept(result);
	}

	@Override
	public void setConsumer(Consumer<String> consumer) {
		this.consumer = consumer;
	}
}
