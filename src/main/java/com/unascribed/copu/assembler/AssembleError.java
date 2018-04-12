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

package com.unascribed.copu.assembler;

import com.unascribed.copu.Language;

public class AssembleError extends Exception {
	private static final long serialVersionUID = 9069523428324147254L;
	
	private int line = 0;
	
	public AssembleError() {}
	public AssembleError(String msg) {
		super(msg);
	}
	public AssembleError(int line) {
		super("Syntax error on line "+line);
		this.line = line;
	}
	public AssembleError(String msg, int line) {
		super(msg);
		this.line = line;
	}
	public AssembleError(String msg, int line, Throwable cause) {
		super(msg, cause);
		this.line = line;
	}
	
	public int getLine() { return line; }
	public AssembleError setLine(int line) {
		this.line = line;
		return this;
	}
	
	public static AssembleError withKey(String localizationKey) {
		return new AssembleError(Language.getCurrent().localize(localizationKey));
	}
}
