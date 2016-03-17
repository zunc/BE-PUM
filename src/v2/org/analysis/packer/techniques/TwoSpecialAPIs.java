package v2.org.analysis.packer.techniques;

import org.jakstab.Program;
import org.jakstab.asm.Operand;

import v2.org.analysis.packer.PackerConstants;
import v2.org.analysis.packer.PackerHelper;
import v2.org.analysis.path.BPState;
import v2.org.analysis.value.LongValue;
import v2.org.analysis.value.Value;

public class TwoSpecialAPIs extends TechniqueAbstract {

	/** 
	 * Using for record two APIs using
	 */
	
	private static boolean useLoadLibrary;
	private static boolean useGetProcAddress;
	
	public TwoSpecialAPIs ()
	{
		num				= 0;
		name = "TwoSpecialAPIs";
		id = PackerConstants.TWO_APIS;
		useLoadLibrary				= false;
		useGetProcAddress			= false;
	}
	
	@Override
	public boolean check (BPState curState, Program prog)
	{
		if (curState == null || curState.getInstruction() == null) {
			return false;
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
					if (apiName.contains("LoadLibrary"))
					{
						useLoadLibrary = true;
					}
					else if (apiName.contains("GetProcAddress"))
					{
						useGetProcAddress = true;
					}
				}
			}	
		}
		if (useLoadLibrary && useGetProcAddress)
		{
			num++;
			useLoadLibrary = false;
			useGetProcAddress = false;
			return true;
		}
		return false;
	}
}
