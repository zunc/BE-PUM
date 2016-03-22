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

public class Checksumming extends PackerTechnique {

	/**
	 * Using for record checksum
	 */
	private final int DEFAULT_NUMBER = 100;
	private Map<Long, Integer> tempLocation;
//	private Map<Long, String> destValue;

	public Checksumming() {
		id = PackerConstants.CHECKSUMMING;
		name = "Checksumming-Check later";
		tempLocation = new HashMap<Long, Integer>();
//		destValue = new HashMap<Long, String>();
	}

	// @Override
	// public boolean check (BPState curState, Program prog) {
	// if (curState == null || curState.getInstruction() == null) {
	// return false;
	// }
	//
	// Operand src = curState.getInstruction().getOperand(1);
	// Operand dest = curState.getInstruction().getOperand(0);
	// if (src == null || dest == null) {
	// return false;
	// }
	//
	// String destStringValue = getValue(curState, dest);
	// long location = curState.getLocation().getValue();
	// int opCount = curState.getInstruction().getOperandCount();
	// Instruction inst = curState.getInstruction();
	// if (!contain(location) && opCount >= 2 &&
	// inst != null && PackerConstants.isSpecialInstruction(inst.getName())) {
	// if (tempLocation.containsKey(location)) {
	// int num = tempLocation.get(location);
	// if (tempLocation.get(location) > DEFAULT_NUMBER) {
	// locList.add(location);
	// return true;
	// }
	//
	// if (destValue.get(location).equals(destStringValue)) {
	// tempLocation.put(location, num + 1);
	// } else {
	// tempLocation.remove(location);
	// }
	// return false;
	// }
	//
	//
	// if (src != null && src instanceof X86MemoryOperand) {
	// Value addrVal =
	// curState.getEnvironement().getMemory().calculateAddress((X86MemoryOperand)src);
	// if (addrVal != null && addrVal instanceof LongValue) {
	// // AbsoluteAddress aAddr = new AbsoluteAddress(((LongValue)
	// addrVal).getValue());
	// // if (PackerHelper.IsI nCodeSection(prog, aAddr)) {
	// if (inst instanceof X86Instruction) {
	// if (((X86Instruction) inst).hasPrefixREPNZ() || ((X86Instruction)
	// inst).hasPrefixREPZ()) {
	// locList.add(location);
	// return true;
	// }
	// }
	// destValue.put(location, destStringValue);
	// tempLocation.put(location, 1);
	// }
	// }
	// if (src != null && src instanceof X86MemoryOperand) {
	// Value addrVal =
	// curState.getEnvironement().getMemory().calculateAddress((X86MemoryOperand)src);
	// if (addrVal != null && addrVal instanceof LongValue) {
	// locList.add(location);
	// return true;
	// }
	// }
	// }
	// return false;
	// }

	@Override
	public boolean check(BPState curState, Program prog) {
		if (curState == null || curState.getInstruction() == null) {
//				|| curState.getInstruction().getName().startsWith("mov")) {
			return false;
		}

		Operand src = curState.getInstruction().getOperand(1);
		Operand dest = curState.getInstruction().getOperand(0);
		if (src == null || dest == null || !(src instanceof X86MemoryOperand) || dest instanceof X86MemoryOperand) {
			return false;
		}

		long location = curState.getLocation().getValue();
		Instruction inst = curState.getInstruction();
		if (!contain(location) && PackerConstants.isSpecialInstruction(inst.getName())) {
			if (tempLocation.containsKey(location)) {
				int num = tempLocation.get(location);
				if (num > DEFAULT_NUMBER) {
					locList.add(location);
					return true;
				}
				tempLocation.put(location, num + 1);
				return false;
			}

			Value addrVal = curState.getEnvironement().getMemory().calculateAddress((X86MemoryOperand) src);
			if (addrVal != null && addrVal instanceof LongValue) {
				// AbsoluteAddress aAddr = new AbsoluteAddress(((LongValue)
				// addrVal).getValue());
				// if (PackerHelper.IsI nCodeSection(prog, aAddr)) {
				if (inst instanceof X86Instruction) {
					if (((X86Instruction) inst).hasPrefixREPNZ() || ((X86Instruction) inst).hasPrefixREPZ()) {
						locList.add(location);
						return true;
					}
				}				
				tempLocation.put(location, 1);
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
