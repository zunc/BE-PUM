package v2.org.analysis.packer.techniques;

import org.jakstab.Program;
import org.jakstab.asm.Operand;
import org.jakstab.asm.x86.X86MemoryOperand;

import v2.org.analysis.environment.Environment;
import v2.org.analysis.path.BPState;
import v2.org.analysis.value.LongValue;
import v2.org.analysis.value.Value;

public class SEH implements PackerTechnique {

	/** 
	 * Using for record SEH
	 */
	
	private static int numOfSEH;
	private static boolean setupSEH;
	
	public SEH ()
	{
		numOfSEH				= 0;
		setupSEH 			= false;
	}
	
	@Override
	public void Count (BPState curState, Program prog)
	{
		if (curState == null || curState.getInstruction() == null) {
			return;
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
						numOfSEH++;
					}
				}
			}
		}
	}
	
	@Override
	public int GetInfo ()
	{
		return numOfSEH;
	}
	
}
