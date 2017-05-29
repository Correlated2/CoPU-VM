package com.unascribed.copu.compiler;

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
