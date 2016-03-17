package v2.org.analysis.packer.techniques;

import java.util.ArrayList;

import org.jakstab.Program;
import org.jakstab.asm.AbsoluteAddress;
import org.jakstab.asm.Operand;

import v2.org.analysis.packer.PackerConstants;
import v2.org.analysis.packer.PackerHelper;
import v2.org.analysis.path.BPState;
import v2.org.analysis.value.LongValue;
import v2.org.analysis.value.Value;

public class Overwriting extends TechniqueAbstract {

	/** 
	 * Using for record overwriting
	 */
	
	private static ArrayList<Long> savedSMCState;
	
	public Overwriting ()
	{
		num				= 0;
		name = "Overwriting";
		id = PackerConstants.OVERWRITING;
		savedSMCState			= new ArrayList<Long>();
	}
	
	@Override
	public boolean check (BPState curState, Program prog)
	{
		if (curState == null || curState.getInstruction() == null) {
			return false;
		}
		
		int opCount = curState.getInstruction().getOperandCount();
		if (opCount >= 2
				&& !PackerHelper.IsExisted(savedSMCState, curState.getLocation().getValue()))
		{
			Operand dest = curState.getInstruction().getOperand(0);
			Value aVal = PackerHelper.GetOperandValue(curState, dest);
			if (aVal instanceof LongValue)
			{
				AbsoluteAddress aAddr = new AbsoluteAddress(((LongValue) aVal).getValue());
				if (PackerHelper.IsInCodeSection(prog, aAddr))
				{
					num++;
					savedSMCState.add(new Long(curState.getLocation().getValue()));
					return true;
				}
			}
		}
		return false;
	}
}
