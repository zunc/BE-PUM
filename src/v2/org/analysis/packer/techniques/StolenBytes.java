package v2.org.analysis.packer.techniques;

import org.jakstab.Program;

import v2.org.analysis.packer.PackerConstants;
import v2.org.analysis.path.BPState;

public class StolenBytes extends PackerTechnique {

	/** 
	 * Using for record stolen bytes-
	 */
	
	public StolenBytes () {
//		num				= 0;
		name = "StolenBytes-Done";
		id = PackerConstants.STOLEN_BYTES;
	}
	
	@Override
	public boolean check (BPState curState, Program prog) {
//		if (curState == null || curState.getInstruction() == null) {
//			return false;
//		}
//		
//		String insName = curState.getInstruction().getName();
//		if (insName.contains("call"))
//		{
//			Operand dest = curState.getInstruction().getOperand(0);
//			Value aVal = PackerHelper.GetOperandValue(curState, dest);
//			if (aVal instanceof LongValue)
//			{
//				String apiName = PackerHelper.GetAPIName(curState, aVal);
//				if (apiName != null)
//				{
//					if (apiName.contains("VirtualAlloc"))
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
		String name = apiName.toLowerCase();
		for (String antiAPI: PackerConstants.STOLENBYTE_APIs) {
			if (name.contains(antiAPI.toLowerCase())) {
				if (!contain(location)) {
					insertLocation(location);
//					num++;
					return true;
				}							
			}
		}
		return false;
	}	
}
