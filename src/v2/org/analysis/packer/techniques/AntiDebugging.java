package v2.org.analysis.packer.techniques;

import org.jakstab.Program;
import org.jakstab.asm.Operand;

import v2.org.analysis.packer.PackerHelper;
import v2.org.analysis.path.BPState;
import v2.org.analysis.value.LongValue;
import v2.org.analysis.value.Value;

public class AntiDebugging implements PackerTechnique {

	/** 
	 * Using for record anti-debugging
	 */
	
	private static int numOfAntiDebugging;
	private final static String[] ANTI_APIs = {"IsDebuggerPresent"
			 								, "CheckRemoteDebuggerPresent"
			 								, "NtQueryInformationProcess"
			 								, "NtQuerySystemInformation"
			 								, "NtQueryObject"};
	
	public AntiDebugging ()
	{
		numOfAntiDebugging				= 0;
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
					for (String antiAPI: ANTI_APIs)
					{
						if (apiName.contains(antiAPI))
						{
							numOfAntiDebugging++;
						}
					}
				}
			}	
		}
	}
	
	@Override
	public int GetInfo ()
	{
		return numOfAntiDebugging;
	}
	
}
