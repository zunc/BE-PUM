package v2.org.analysis.transition_rule.x86instruction;

import org.jakstab.asm.Immediate;
import org.jakstab.asm.x86.X86MemoryOperand;
import v2.org.analysis.complement.Convert;

import v2.org.analysis.path.BPState;
import v2.org.analysis.transition_rule.stub.X86InstructionStub;
import v2.org.analysis.value.DoubleValue;
import v2.org.analysis.value.LongValue;
import v2.org.analysis.value.Value;

public class fistpll extends X86InstructionStub {

	@Override
	public BPState execute() {
		if (dest == null) {
			return curState;
		}
		DoubleValue val = (DoubleValue) env.getRegister().popFPU();
		LongValue lngVal = new LongValue(val.getValue());
		if (dest.getClass().getSimpleName().equals("X86Register")
				|| dest.getClass().getSimpleName().equals("X86RegisterPart")
				|| dest.getClass().getSimpleName().equals("X86SegmentRegister")) {
			
			env.getRegister().setRegisterValue(dest.toString(), lngVal);
		} else if (dest.getClass().getSimpleName().equals("Immediate")) {
			;
		} else if (dest.getClass().getSimpleName().equals("X86MemoryOperand")) {
			env.getMemory().setMemoryValue((X86MemoryOperand) dest, lngVal, inst);
		}
		return null;
	}

}
