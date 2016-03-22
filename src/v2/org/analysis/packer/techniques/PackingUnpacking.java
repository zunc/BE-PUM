package v2.org.analysis.packer.techniques;

import java.util.HashMap;
import java.util.Map;

import org.jakstab.Program;
import org.jakstab.asm.Instruction;
import org.jakstab.asm.Operand;
import org.jakstab.asm.x86.X86Instruction;
import org.jakstab.asm.x86.X86MemoryOperand;

import v2.org.analysis.packer.PackerConstants;
import v2.org.analysis.path.BPState;
import v2.org.analysis.value.LongValue;
import v2.org.analysis.value.Value;

public class PackingUnpacking extends PackerTechnique {

	/** 
	 * Using for record packing/unpacking
	 */
	private final int DEFAULT_NUMBER = 200; 
	private Map<Long, Integer> tempLocation; 
	public PackingUnpacking () {
		id = PackerConstants.PACKING_UNPACKING;
		name = "PackingUnpacking-Check later";
		tempLocation = new HashMap<Long, Integer> ();
	}
	
//	@Override
//	public boolean check (BPState curState, Program prog) {
//		if (curState == null || curState.getInstruction() == null) {
//			return false;
//		}
//
//		long location = curState.getLocation().getValue();
//		int opCount = curState.getInstruction().getOperandCount();
//		Instruction inst = curState.getInstruction();
//		if (!contain(location) && opCount >= 2 &&  
//				inst != null && PackerConstants.isSpecialInstruction(inst.getName())) {
//			if (tempLocation.containsKey(location)) {
//				int num = tempLocation.get(location);
//				if (tempLocation.get(location) > DEFAULT_NUMBER) {
//					locList.add(location);
//					return true;
//				}
//				tempLocation.put(location, num + 1);
//				return false;
//			}
//			
//			Operand dest = curState.getInstruction().getOperand(0);
//			if (dest != null && dest instanceof X86MemoryOperand) {
//				Value addrVal = curState.getEnvironement().getMemory().calculateAddress((X86MemoryOperand)dest);
//				if (addrVal != null && addrVal instanceof LongValue) {
//					if (inst instanceof X86Instruction) {
//						if (((X86Instruction) inst).hasPrefixREPNZ() || ((X86Instruction) inst).hasPrefixREPZ()) {
//							locList.add(location);
//							return true;
//						}
//					}
//					tempLocation.put(location, 1);
//				}
//			}			
//			if (dest != null && dest instanceof X86MemoryOperand) {
//				Value addrVal = curState.getEnvironement().getMemory().calculateAddress((X86MemoryOperand)dest);
//				if (addrVal != null && addrVal instanceof LongValue) {
//					locList.add(location);
//					return true;
//					
//				}
//			}
//		}
//		return false;
//	}
	
	@Override
	public boolean check (BPState curState, Program prog) {
		if (curState == null || curState.getInstruction() == null) {
			return false;
		}

		long location = curState.getLocation().getValue();
		int opCount = curState.getInstruction().getOperandCount();
		Instruction inst = curState.getInstruction();
		if (!contain(location) && opCount >= 2 &&  
				inst != null && PackerConstants.isSpecialInstruction(inst.getName())) {
			if (tempLocation.containsKey(location)) {
				int num = tempLocation.get(location);
				if (num > DEFAULT_NUMBER) {
					locList.add(location);
					return true;
				}
				tempLocation.put(location, num + 1);
				return false;
			}
			
			Operand dest = curState.getInstruction().getOperand(0);
			Operand src = curState.getInstruction().getOperand(1);
			if (dest != null && dest instanceof X86MemoryOperand
					&& src != null && src instanceof X86MemoryOperand) {
				Value destAddr = curState.getEnvironement().getMemory().calculateAddress((X86MemoryOperand)dest);
				if (destAddr != null && destAddr instanceof LongValue) {
					if (inst instanceof X86Instruction) {
						if (((X86Instruction) inst).hasPrefixREPNZ() || ((X86Instruction) inst).hasPrefixREPZ()) {
							locList.add(location);
							return true;
						}
					}
					tempLocation.put(location, 1);
				}
			}			
		}
		return false;
	}


	@Override
	public boolean checkAPIName(String apiName, long location) {
		// TODO Auto-generated method stub
		return false;
	}
}
