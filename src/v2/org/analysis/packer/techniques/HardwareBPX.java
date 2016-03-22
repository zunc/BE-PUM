package v2.org.analysis.packer.techniques;

import org.jakstab.Program;

import v2.org.analysis.packer.PackerConstants;
import v2.org.analysis.path.BPState;

public class HardwareBPX extends PackerTechnique {

	/** 
	 * Using for record hardware breakpoints-
	 */
	
	public HardwareBPX () {
//		num				= 0;
		id = PackerConstants.HARDWARE_BPX;
		name = "HardwareBPX";
	}
	
	@Override
	public boolean check (BPState curState, Program prog)
	{
		if (curState == null || curState.getInstruction() == null) {
			return false;
		}
		//Insert later
//		num = 0;
		return false;
		
	}

	@Override
	public boolean checkAPIName(String apiName, long location) {
		// TODO Auto-generated method stub
		return false;
	}	
}
