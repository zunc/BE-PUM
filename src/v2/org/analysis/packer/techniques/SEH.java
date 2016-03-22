package v2.org.analysis.packer.techniques;

import org.jakstab.Program;

import v2.org.analysis.packer.PackerConstants;
import v2.org.analysis.path.BPState;

public class SEH extends PackerTechnique {

	/** 
	 * Using for record SEH
	 */
	
//	private static boolean setupSEH;
	
	public SEH () {
//		num				= 0;
		id = PackerConstants.SEH;
		name = "SEH-Done";
//		setupSEH 			= false;
	}
	
	@Override
	public boolean check (BPState curState, Program prog) {
//		if (curState == null || curState.getInstruction() == null) {
//			return false;
//		}
//		
//		Environment env = curState.getEnvironement();
//		if (!setupSEH)
//		{
//			int opCount = curState.getInstruction().getOperandCount();
//			if (opCount >= 1)
//			{
//				Operand dest = curState.getInstruction().getOperand(0);
//				if (dest instanceof X86MemoryOperand)
//				{
//					X86MemoryOperand memAddr = (X86MemoryOperand) dest;
//					boolean base = false;
//					if (memAddr.getDisplacement() == 0 && memAddr.getBase() == null) {
//						base = true;
//					} else {
//						if (memAddr.getBase() != null) {
//							Value memVal = env.getRegister().getRegisterValue(memAddr.getBase().toString());
//							if (memVal instanceof LongValue) {
//								base = (((LongValue) memVal).getValue() == 0);
//							}
//						}
//					}
//
//					if (memAddr.getSegmentRegister() != null 
//							&& memAddr.getSegmentRegister().toString() == "%fs" && base) 
//					{
////						num++;
//						long location = curState.getLocation().getValue();
//						if (!contain(location)) {
//							num++;
//							locList.add(location);
//						}
//			
//						return true;
//					}
//				}
//			}
//		}
		return false;
	}

	@Override
	public boolean checkAPIName(String apiName, long location) {
		// TODO Auto-generated method stub
		return false;
	}
}
