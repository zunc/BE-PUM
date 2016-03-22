package v2.org.analysis.packer.techniques;

import org.jakstab.Program;
import org.jakstab.asm.AbsoluteAddress;
import org.jakstab.asm.Operand;
import org.jakstab.asm.x86.X86MemoryOperand;

import v2.org.analysis.packer.PackerConstants;
import v2.org.analysis.path.BPState;
import v2.org.analysis.value.LongValue;
import v2.org.analysis.value.Value;

public class Overwriting extends PackerTechnique {

	/**
	 * Using for record overwriting
	 */

//	private static ArrayList<Long> savedSMCState;

	public Overwriting() {
		// num = 0;
		name = "Overwriting-Done";
		id = PackerConstants.OVERWRITING;
//		savedSMCState = new ArrayList<Long>();
	}

	@Override
	public boolean check(BPState curState, Program prog) {
		if (curState == null || curState.getInstruction() == null) {
			return false;
		}

		long location = curState.getLocation().getValue();
		int opCount = curState.getInstruction().getOperandCount();
		if (opCount >= 2 && !contain(location) && 
				PackerConstants.isSpecialInstruction(curState.getInstruction().getName())) {
			Operand dest = curState.getInstruction().getOperand(0);
			if (dest != null && dest instanceof X86MemoryOperand) {
				Value addrVal = curState.getEnvironement().getMemory().calculateAddress((X86MemoryOperand)dest);
				if (addrVal != null && addrVal instanceof LongValue) {
					AbsoluteAddress aAddr = new AbsoluteAddress(((LongValue) addrVal).getValue());
					// if (PackerHelper.IsI nCodeSection(prog, aAddr)) {
					if (prog.getMainModule().isCodeArea(aAddr)) {
						locList.add(location);
						return true;
					}
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
