package com.unascribed.copu.call;

import com.unascribed.copu.MemoryPage;
import com.unascribed.copu.VirtualMachine;
import com.unascribed.copu.descriptor.Descriptor;
import com.unascribed.copu.undefined.VMError;

public class Sys {
	
	public static void print(VirtualMachine vm) {
		MemoryPage page = vm.getPage(vm.registers().PG0.get());
		int addr = vm.registers().R1.get();
		int limit = vm.registers().R2.get();
		Descriptor<?> descriptor = vm.getDescriptor(vm.registers().R3.get());
		
		page.readNullTerminated(addr, limit, descriptor::write);
	}
	
	public static void println(VirtualMachine vm) {
		MemoryPage page = vm.getPage(vm.registers().PG0.get());
		int addr = vm.registers().R1.get();
		int limit = vm.registers().R2.get();
		Descriptor<?> descriptor = vm.getDescriptor(vm.registers().R3.get());
		if (descriptor==null) throw new VMError("Attempted to write to invalid descriptor 0x"+Integer.toHexString(vm.registers().R3.get())+"");
		
		page.readNullTerminated(addr, limit, descriptor::write);
		descriptor.write(0xD); //If there were unfinished surrogate sets before this, they'll get clobbered and the newline will get written instead.
	}
	
	public static void pipe(VirtualMachine vm) {
		MemoryPage page = vm.getPage(vm.registers().PG0.get());
		int addr = vm.registers().R1.get();
		
		String s = page.readString(addr, 16);
		if (s.equals("stdout")) {
			vm.registers().R0.accept(VirtualMachine.DESCRIPTOR_STDOUT);
		} else if (s.equals("stderr")) {
			vm.registers().R0.accept(VirtualMachine.DESCRIPTOR_STDERR);
		} else {
			vm.registers().R0.accept(-1);
		}
	}
}
