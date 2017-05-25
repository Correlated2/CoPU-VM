package com.unascribed.copu;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import com.unascribed.copu.microcode.DecodeFormat;

public class Compiler {
	private static final String[] dataCodes = {
		"dc", "db", "dw", "df", "const"
	};
	
	private ByteArrayOutputStream result = new ByteArrayOutputStream();
	private DataOutputStream output = new DataOutputStream(result);
	private ArrayList<Integer> microcodes = new ArrayList<>();
	private HashMap<String, Integer> namedAddresses = new HashMap<>();
	
	public void parse(String[] input) {
		int lineNum = 1;
		for(String s : input) {
			String[] lines = s.split("\\n");
			for(String t : lines) {
				parseLine(t, lineNum);
				lineNum++;
			}
		}
	}
	
	public void parseLine(String line, int lineNum) {
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
	
	private void parseOpcodeLine(String opName, String rest, int lineNum) {
		for(Opcode opcode : Opcode.values()) {
			if (opcode.name().toLowerCase().equals(opName)) {
				parseOpcodeLine(opcode, rest, lineNum);
				return;
			}
		}
		throw new IllegalArgumentException("Unknown opcode '"+opName+"' at line "+lineNum+".");
	}
	
	private void parseOpcodeLine(Opcode opcode, String rest, int lineNum) {
		//System.out.println("Compile: "+opcode.name());
		
		if (opcode.getDecodeFormat()==DecodeFormat.NO_ARG) {
			if (!rest.isEmpty()) throw new IllegalArgumentException("Opcode "+opcode.name()+" cannot accept arguments on line "+lineNum);
			try {
				//high int
				output.writeByte(opcode.value());
				output.writeByte(0);
				output.writeByte(0);
				output.writeByte(0);
				//low int
				output.writeInt(0);
				System.out.println(opcode.name()+" emitted");
			} catch (IOException ex) {} //never happens in a BAOS
			
		} else if (opcode.getDecodeFormat()==DecodeFormat.ONE_ARG) {
			if (rest.contains(",")) throw new IllegalArgumentException("Invalid comma at line "+lineNum);
			
			//Figure out what this one argument is
			
			
			try {
				//high int
				output.writeByte(opcode.value());
				output.writeByte(0);
				output.writeByte(0);
				output.writeByte(0);
				//low int
				output.writeInt(Integer.valueOf(rest));
				System.out.println(opcode.name()+" emitted");
			} catch (IOException ex) {} //never happens in a BAOS
			
		}
	}
	
	public byte[] toByteArray() {
		try {
			output.flush();
		} catch (IOException e) {}
		
		return result.toByteArray();
	}
}
