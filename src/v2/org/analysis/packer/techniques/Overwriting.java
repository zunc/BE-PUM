package v2.org.analysis.packer.techniques;

import java.util.ArrayList;

import org.jakstab.Program;
import org.jakstab.asm.AbsoluteAddress;
import org.jakstab.asm.Operand;

import v2.org.analysis.packer.PackerConstants;
import v2.org.analysis.packer.PackerHelper;
import v2.org.analysis.packer.PackerRecord;
import v2.org.analysis.path.BPState;
import v2.org.analysis.value.LongValue;
import v2.org.analysis.value.Value;

public class Overwriting implements PackerTechnique {

	/** 
	 * Using for record overwriting
	 */
	
	private static int numOfSMC;
	private static ArrayList<Long> savedSMCState;
	
	public Overwriting ()
	{
		numOfSMC				= 0;
		savedSMCState			= new ArrayList<Long>();
	}
	
	@Override
	public void Count (BPState curState, Program prog)
	{
		if (curState == null || curState.getInstruction() == null) {
			return;
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
					numOfSMC++;
					PackerRecord.getInstance().updatePackerTechniqueRecord(String.valueOf(PackerConstants.OVERWRITING));
					savedSMCState.add(new Long(curState.getLocation().getValue()));
				}
			}
		}
	}
	
	@Override
	public int GetInfo ()
	{
		return numOfSMC;
	}
	
}
