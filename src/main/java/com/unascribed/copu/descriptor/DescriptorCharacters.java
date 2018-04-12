/*
 * MIT License
 *
 * Copyright (c) 2017-2018 Isaac Ellingson (Falkreon), Una Thompson (unascribed)
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
