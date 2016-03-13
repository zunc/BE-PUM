package v2.org.analysis.packer.techniques;

import org.jakstab.Program;
import org.jakstab.asm.Operand;

import v2.org.analysis.packer.PackerConstants;
import v2.org.analysis.packer.PackerHelper;
import v2.org.analysis.packer.PackerRecord;
import v2.org.analysis.path.BPState;
import v2.org.analysis.value.LongValue;
import v2.org.analysis.value.Value;

public class StolenBytes implements PackerTechnique {

	/** 
	 * Using for record stolen bytes-
	 */
	
	private static int numOfStolenBytes;
	
	public StolenBytes ()
	{
		numOfStolenBytes				= 0;
	}
	
	@Override
	public void Count (BPState curState, Program prog)
	{
		if (curState == null || curState.getInstruction() == null) {
			return;
		}
		
		String insName = curState.getInstruction().getName();
		if (insName.contains("call"))
		{
			Operand dest = curState.getInstruction().getOperand(0);
			Value aVal = PackerHelper.GetOperandValue(curState, dest);
			if (aVal instanceof LongValue)
			{
				String apiName = PackerHelper.GetAPIName(curState, aVal);
				if (apiName != null)
				{
					if (apiName.contains("VirtualAlloc"))
					{
						numOfStolenBytes++;
						PackerRecord.getInstance().updatePackerTechniqueRecord(String.valueOf(PackerConstants.STOLEN_BYTES));
					}
				}
			}
		}
	}
	
	@Override
	public int GetInfo ()
	{
		return numOfStolenBytes;
	}
	
}
