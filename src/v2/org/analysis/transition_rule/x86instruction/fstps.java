package v2.org.analysis.transition_rule.x86instruction;

import org.jakstab.asm.DataType;
import org.jakstab.asm.Immediate;
import org.jakstab.asm.x86.X86MemoryOperand;
import v2.org.analysis.complement.Convert;

import v2.org.analysis.path.BPState;
import v2.org.analysis.transition_rule.stub.X86InstructionStub;
import v2.org.analysis.value.LongValue;
import v2.org.analysis.value.Value;

public class fstps extends X86InstructionStub {

	@Override
	public BPState execute() {
		if (dest == null) {
			return curState;
		}
		
		Value val = env.getRegister().popFPU();
		if (dest.getClass().getSimpleName().equals("X86Register")
				|| dest.getClass().getSimpleName().equals("X86RegisterPart")
				|| dest.getClass().getSimpleName().equals("X86SegmentRegister")) {
			env.getRegister().setRegisterValue(dest.toString(), val);
		} else if (dest.getClass().getSimpleName().equals("Immediate")) {
			;
		} else if (dest.getClass().getSimpleName().equals("X86MemoryOperand")) {
			env.getMemory().setMemoryValue((X86MemoryOperand) dest, val, inst);
		}
		return null;
	}

}
