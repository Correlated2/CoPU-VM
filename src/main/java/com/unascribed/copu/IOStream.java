package com.unascribed.copu;

/**
 * Since InputStream and OutputStream have no superinterfaces, it's now impossible
 * to implement both of them. So instead, we have here an interface which represents
 * a device capable of both reading and writing octets in their simplest form.
 */
public interface IOStream {
	public int read();
	public void write(int i);
	public void flush();
}
