package v2.org.analysis.packer.techniques;

import org.jakstab.Program;
import org.jakstab.asm.Immediate;
import org.jakstab.asm.Instruction;
import org.jakstab.asm.Operand;
import org.jakstab.asm.x86.X86ArithmeticInstruction;

import v2.org.analysis.packer.PackerConstants;
import v2.org.analysis.path.BPState;

public class ObfuscatedConstant extends PackerTechnique {

	/** 
	 * Using for record obfuscated constants
	 */
	
//	private static ArrayList<Long> savedObfuscatedConstState;
	
	public ObfuscatedConstant () {
//		num				= 0;
		id = PackerConstants.OBFUSCATED_CONST;
		name = "ObfuscatedConst-Done";		
//		savedObfuscatedConstState			= new ArrayList<Long>();
	}
	
	@Override
	public boolean check (BPState curState, Program prog)
	{
		if (curState == null || curState.getInstruction() == null) {
			return false;
		}
		long location = curState.getLocation().getValue();
		Instruction ins = curState.getInstruction();
		int opCount = ins.getOperandCount();
		if (ins instanceof X86ArithmeticInstruction
				&& !contain(location)) {
			if (opCount > 1) {
				Operand op1 = curState.getInstruction().getOperand(0);
				Operand op2 = curState.getInstruction().getOperand(1);
				if ((op1 != null && op1 instanceof Immediate) || 
						op2 != null && op2 instanceof Immediate) {					
					locList.add(location);
					return true;
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
