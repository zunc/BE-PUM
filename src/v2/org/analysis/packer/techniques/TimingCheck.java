package v2.org.analysis.packer.techniques;

import org.jakstab.Program;
import org.jakstab.asm.Operand;

import v2.org.analysis.packer.PackerHelper;
import v2.org.analysis.path.BPState;
import v2.org.analysis.value.LongValue;
import v2.org.analysis.value.Value;

public class TimingCheck implements PackerTechnique {

	/** 
	 * Using for record timing check-
	 */
	
	private static int numOfTimingCheck;
	
	public TimingCheck ()
	{
		numOfTimingCheck				= 0;
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
					if (apiName.contains("GetTickCount"))
					{
						numOfTimingCheck++;
					}
				}
			}	
		}
		else if (insName.contains("RDTSC"))
		{
			numOfTimingCheck++;
		}
	}
	
	@Override
	public int GetInfo ()
	{
		return numOfTimingCheck;
	}
	
}
