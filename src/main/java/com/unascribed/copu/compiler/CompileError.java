package com.unascribed.copu.compiler;

import com.unascribed.copu.Language;

public class CompileError extends Exception {
	private static final long serialVersionUID = 9069523428324147254L;
	
	private int line = 0;
	
	public CompileError() {}
	public CompileError(String msg) {
		super(msg);
	}
	public CompileError(int line) {
		super("Syntax error on line "+line);
		this.line = line;
	}
	public CompileError(String msg, int line) {
		super(msg);
		this.line = line;
	}
	
	public int getLine() { return line; }
	public CompileError setLine(int line) {
		this.line = line;
		return this;
	}
	
	public static CompileError withKey(String localizationKey) {
		return new CompileError(Language.getCurrent().localize(localizationKey));
	}
}
