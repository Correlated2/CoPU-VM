package com.unascribed.copu.compiler;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;

import com.unascribed.copu.Opcode;

public class Assembler {
	private static final String[] dataCodes = {
		"dc", "db", "dw", "df", "const"
	};
	
	private ByteArrayOutputStream result = new ByteArrayOutputStream();
	private DataOutputStream output = new DataOutputStream(result);
	private HashMap<String, Integer> namedAddresses = new HashMap<>();
	
	public void parse(String[] input) throws CompileError {
		int lineNum = 1;
		for(String s : input) {
			String[] lines = s.split("\\n");
			for(String t : lines) {
				parseLine(t, lineNum);
				lineNum++;
			}
		}
	}
	
	public void parseLine(String line, int lineNum) throws CompileError {
		line = line.trim(); //Kill leading and trailing whitespace
		if (line.contains("\n")) line = line.split("\\n")[0]; //Strip any additional lines
		if (line.contains(";")) { //Strip comments
			String[] lineArray = line.split(";");
			if (lineArray.length==0) return;
			line = lineArray[0];
		}
		
		String opName = line;
		String rest = "";
		
		int opEnd = line.indexOf(' '); //Opcode ends at the first whitespace character
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
	
	private void parseOpcodeLine(String opName, String rest, int lineNum) throws CompileError {
		for(Opcode opcode : Opcode.values()) {
			if (opcode.name().toLowerCase().equals(opName)) {
				parseOpcodeLine(opcode, rest, lineNum);
				return;
			}
		}
		throw new IllegalArgumentException("Unknown opcode '"+opName+"' at line "+lineNum+".");
	}
	
	private void parseOpcodeLine(Opcode opcode, String rest, int lineNum) throws CompileError {
		rest = rest.trim();
		
		String[] args = new String[0];
		if (!rest.isEmpty()) args = rest.split(",");
		Operand[] arguments = new Operand[args.length];
		for(int i=0; i<args.length; i++) {
			arguments[i] = parseArgument(args[i]);
		}
		
		String parsedLine = opcode.name();
		for(Operand o : arguments) {
			parsedLine += ' ';
			parsedLine += o.toString();
		}
		
		System.out.println("Parsed: "+parsedLine);
		
		long filledOpcode = opcode.getDecodeFormat().compile(arguments);
		filledOpcode |= ((long)opcode.value()) << 56;
		
		try {
			System.out.println("Emitting opcode "+longToHex(filledOpcode));
			
			output.writeLong(filledOpcode);
		} catch (IOException ex) {
			//Never happens in a byteArrayOutputStream
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
		
		RegisterToken reg = RegisterToken.forName(arg);
		if (reg!=null) return reg;
		
		
		return null;
	}
	
	public byte[] toByteArray() {
		try {
			output.flush();
		} catch (IOException e) {}
		
		return result.toByteArray();
	}
}
