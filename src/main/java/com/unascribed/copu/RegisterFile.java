/*
 * MIT License
 *
 * Copyright (c) 2017 Falkreon
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

package com.unascribed.copu;

public class RegisterFile {
	public final Register R0 = new Register();
	public final Register R1 = new Register();
	public final Register R2 = new Register();
	public final Register R3 = new Register();
	public final Register R4 = new Register();
	public final Register R5 = new Register();
	public final Register R6 = new Register();
	public final Register R7 = new Register();
	public final Register X  = new Register();
	public final Register Y  = new Register();
	public final Register PG0= new Register();
	public final Register PG1= new Register();
	public final Register F0 = new Register();
	public final Register F1 = new Register();
	public final Register F2 = new Register();
	public final Register F3 = new Register();
	
	public final Register CS = new Register();
	public final Register IP = new Register();
	public final Register SP = new Register(); //Synthetic, somewhat unreliable to tamper with or query.
	
	public final Register[] table = {
			R0, R1, R2, R3, R4, R5, R6, R7,
			X,  Y,  PG0,PG1,F0, F1, F2, F3,
			CS, IP, SP
	};
}
