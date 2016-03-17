package v2.org.analysis.packer.techniques;

import org.jakstab.Program;

import v2.org.analysis.packer.PackerConstants;
import v2.org.analysis.path.BPState;

public class HardwareBPX extends TechniqueAbstract {

	/** 
	 * Using for record hardware breakpoints-
	 */
	
	public HardwareBPX () {
		num				= 0;
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
		num = 0;
		return false;
		
	}	
}
