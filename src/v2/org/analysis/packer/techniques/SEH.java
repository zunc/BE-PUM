package v2.org.analysis.packer.techniques;

import org.jakstab.Program;
import org.jakstab.asm.Operand;
import org.jakstab.asm.x86.X86MemoryOperand;

import v2.org.analysis.environment.Environment;
import v2.org.analysis.packer.PackerConstants;
import v2.org.analysis.path.BPState;
import v2.org.analysis.value.LongValue;
import v2.org.analysis.value.Value;

public class SEH extends TechniqueAbstract {

	/** 
	 * Using for record SEH
	 */
	
	private static boolean setupSEH;
	
	public SEH () {
		num				= 0;
		id = PackerConstants.SEH;
		name = "SEH";
		setupSEH 			= false;
	}
	
	@Override
	public boolean check (BPState curState, Program prog) {
		if (curState == null || curState.getInstruction() == null) {
			return false;
		}
		
		Environment env = curState.getEnvironement();
		if (!setupSEH)
		{
			int opCount = curState.getInstruction().getOperandCount();
			if (opCount >= 1)
			{
				Operand dest = curState.getInstruction().getOperand(0);
				if (dest instanceof X86MemoryOperand)
				{
					X86MemoryOperand memAddr = (X86MemoryOperand) dest;
					boolean base = false;
					if (memAddr.getDisplacement() == 0 && memAddr.getBase() == null) {
						base = true;
					} else {
						if (memAddr.getBase() != null) {
							Value memVal = env.getRegister().getRegisterValue(memAddr.getBase().toString());
							if (memVal instanceof LongValue) {
								base = (((LongValue) memVal).getValue() == 0);
							}
						}
					}

					if (memAddr.getSegmentRegister() != null 
							&& memAddr.getSegmentRegister().toString() == "%fs" && base) 
					{
						num++;
						return true;
					}
				}
			}
		}
		return false;
	}
}
