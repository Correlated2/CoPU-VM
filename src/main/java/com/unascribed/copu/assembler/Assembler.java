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

package com.unascribed.copu.assembler;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.unascribed.copu.Opcode;
import com.unascribed.copu.microcode.CallRegistry;

public class Assembler {
	private static final String[] dataCodes = {
		"dc", "db", "dw", "df", "const"
	};
	
	private ByteArrayOutputStream result = new ByteArrayOutputStream();
	private DataOutputStream output = new DataOutputStream(result);
	private HashMap<String, Integer> namedAddresses = new HashMap<>();
	private ArrayList<AssemblyLabel> labelFillIns = new ArrayList<>();
	
	public void parse(String[] input) throws AssembleError {
		int lineNum = 1;
		for(String s : input) {
			String[] lines = s.split("\\n");
			for(String t : lines) {
				parseLine(t, lineNum);
				lineNum++;
			}
		}
	}
	
	public static int firstWhitespaceIndex(String s) {
		int space = s.indexOf(' ');
		int tab = s.indexOf('\t');
		if (space==-1 && tab==-1) return -1;
		if (space==-1) return tab;
		if (tab==-1) return space;
		return Math.min(space, tab);
	}
	
	public void parseLine(String line, int lineNum) throws AssembleError {
		line = line.trim(); //Kill leading and trailing whitespace
		if (line.contains("\n")) line = line.split("\\n")[0]; //Strip any additional lines
		if (line.contains(";")) { //Strip comments
			String[] lineArray = line.split(";");
			if (lineArray.length==0) return;
			line = lineArray[0];
		}
		
		String opName = line;
		String rest = "";
		
		int opEnd = firstWhitespaceIndex(line); //Opcode ends at the first whitespace character
		
		if (opEnd>0) {
			opName = line.substring(0, opEnd);
			if (line.length()>opEnd+1) {
				rest = line.substring(opEnd+1);
			}
		}
		
		opName = opName.trim().toLowerCase();
		rest = rest.trim();
		if (opName.isEmpty()) return;
		
		if (opName.equals("const")) {
			//Do const things
		} else if (isDataLine(opName)){
			parseDataLine(opName, rest, lineNum);
		} else if (opName.endsWith(":")) {
			namedAddresses.put(opName.substring(0,opName.length()-1), output.size());
			if (!rest.isEmpty()) parseLine(rest, lineNum);
		} else {
			parseOpcodeLine(opName, rest, lineNum);
		}
	}
	
	private boolean isDataLine(String opName) {
		for(String s : dataCodes) {
			if (s.equals(opName)) return true;
		}
		return false;
	}
		
	private void parseDataLine(String opName, String rest, int lineNum) {
		//grab the label name
		int labelEnd = rest.indexOf(' ');
		String label = rest.substring(0, labelEnd);
		String data = "";
		if (rest.length()> labelEnd+1) data = rest.substring(labelEnd+1).trim();
		namedAddresses.put(label, output.size()); //output.size() is the current page offset(!)
		
		try {
			if (opName.equals("db") || opName.equals("dc")) {
				//Character constant
				if (data.startsWith("\"")) data = data.substring(1,data.length());
				for(int i=0; i<data.length(); i++) {
					output.writeByte(data.charAt(i) & 0xFF);
				}
			} else if (opName.equals("dw")) {
				int w = 0;
				if (data.startsWith("0x")) {
					w = Integer.parseInt(data.substring(2),16);
				} else if (data.startsWith("0b")) {
					w = Integer.parseInt(data.substring(2),2);
				} else {
					w = Integer.parseInt(data);
				}
				
				output.writeInt(w);
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
	
	private void parseOpcodeLine(String opName, String rest, int lineNum) throws AssembleError {
		for(Opcode opcode : Opcode.values()) {
			if (opcode.name().toLowerCase().equals(opName)) {
				parseOpcodeLine(opcode, rest, lineNum);
				return;
			}
		}
		throw new IllegalArgumentException("Unknown opcode '"+opName+"' at line "+lineNum+".");
	}
	
	private void parseOpcodeLine(Opcode opcode, String rest, int lineNum) throws AssembleError {
		rest = rest.trim();
		
		String[] args = new String[0];
		if (!rest.isEmpty()) args = rest.split(",");
		Operand[] arguments = new Operand[args.length];
		for(int i=0; i<args.length; i++) {
			arguments[i] = parseArgument(args[i]);
			if (arguments[i] instanceof AssemblyLabel) {
				if (i==args.length-1) {
					labelFillIns.add(((AssemblyLabel)arguments[i]).atLine(lineNum));
				} else {
					throw new AssembleError("Cannot assemble label \""+arguments[i].toString()+"\". A label should be the last argument in an instruction.");
				}
			}
		}
		
		String parsedLine = opcode.name();
		for(Operand o : arguments) {
			parsedLine += '\t';
			parsedLine += o;
		}
		
		try {
			
			long filledOpcode = opcode.getDecodeFormat().compile(arguments);
			filledOpcode |= ((long)opcode.value()) << 56;
		
		
			try {
				System.out.println(longToHex(filledOpcode)+"\t"+parsedLine);
				
				output.writeLong(filledOpcode);
			} catch (IOException ex) {
				//Never happens in a byteArrayOutputStream
			}
		} catch (AssembleError ex) {
			ex.setLine(lineNum);
			throw new AssembleError(ex.getMessage(), lineNum, ex);
		}
	}
	
	private static String longToHex(long l) {
		String result = Long.toHexString(l);
		while (result.length()<16) result = "0"+result;
		return result;
	}
	
	public Operand parseArgument(String a) throws IllegalArgumentException {
		if (a==null) return null;
		String arg = a.trim();
		if (arg.isEmpty()) return null;
		
		Character firstLetter = arg.charAt(0);
		if (Character.isDigit(firstLetter) || firstLetter.equals('-')) {
			//if it contains a period it's a float, otherwise int.
			try {
				if (arg.contains(".")) {
					return  new ImmediateValue(Float.valueOf(arg));
				} else {
					//Negative signs will screw with detecting other radixes. Detect them early and strip them
					int mul = 1;
					if (arg.startsWith("-")) {
						arg = arg.substring(1);
						mul = -1;
					}
					
					if (arg.startsWith("0x")) {
						arg = arg.substring(2);
						return  new ImmediateValue(Integer.parseInt(arg,16)*mul);
					} else if (arg.startsWith("0b")) {
						return new ImmediateValue(Integer.parseInt(arg,2)*mul);
					} else {
						return new ImmediateValue(Integer.parseInt(arg)*mul);
					}
				}
			} catch (Throwable t) {
				throw new IllegalArgumentException("Cannot parse numeric argument '"+a+"'.", t);
			}
		}
		
		if (namedAddresses.containsKey(arg)) {
			return new ZeroPageAddress(namedAddresses.get(arg));
		}
		
		int symbol = CallRegistry.getSymbol(arg);
		if (symbol!=0) {
			return new ImmediateValue(symbol);
		}
		
		RegisterToken reg = RegisterToken.forName(arg);
		if (reg!=null) return reg;
		
		if (arg.toLowerCase().startsWith("mem[")) {
			//System.out.println("Assembling memory argument "+arg);
			String addrString = arg.substring(4);
			if (addrString.endsWith("]")) {
				addrString = addrString.substring(0, addrString.length()-1);
				//System.out.println("Trying to parse memory argument: "+addrString);
				try {
					Operand o = parseArgument(addrString);
					
					if (o instanceof ImmediateValue) {
						//System.out.println("Immediate Address");
						return new ZeroPageAddress(((ImmediateValue)o).value);
					} else if (o instanceof RegisterToken) {
						return new DirectAddress(RegisterToken.CS, (RegisterToken)o);
					}
				} catch (IllegalArgumentException ex) {
					//Argument is probably in the form MEM[S:R]
					//This will get a LOT crazier when syntax like MEM[S:(R)] and MEM[S:R+4] are implemented
					if (arg.contains(":")) {
						String[] pieces = arg.split(":");
						if (pieces.length!=2) throw new IllegalArgumentException("Invalid syntax in memory address parameter '"+arg+"'.");
						Operand s = parseArgument(pieces[0]);
						Operand r = parseArgument(pieces[1]);
						if (!(s instanceof RegisterToken)) throw new IllegalArgumentException("first parameter in a multi-param memory address must be a segment register");
						if (r instanceof ImmediateValue) {
							//like MEM[S:42]
						} else if (r instanceof RegisterToken) {
							//Like MEM[S:R]
						}
					} else {
						//For now we can't parse it.
						
					}
				}
				
				
			} else throw new IllegalArgumentException("Invalid syntax in memory parameter '"+arg+"'.");
		}
		
		//It's probably a label we haven't encountered yet
		return new AssemblyLabel(arg, result.size());
		//labelFillIns.add(new LabelFillIn(arg, result.size()+1));
		//return new ZeroPageAddress(0);
		
		//return null;
	}
	
	public byte[] toByteArray() {
		try {
			output.flush();
		} catch (IOException e) {}
		
		return result.toByteArray();
	}
}
