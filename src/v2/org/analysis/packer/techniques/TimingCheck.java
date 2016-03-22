package v2.org.analysis.packer.techniques;

import org.jakstab.Program;

import v2.org.analysis.packer.PackerConstants;
import v2.org.analysis.path.BPState;

public class TimingCheck extends PackerTechnique {

	/** 
	 * Using for record timing check-
	 */
	
	public TimingCheck ()
	{
//		num				= 0;
		id = PackerConstants.TIMING_CHECK;
		name = "TimingCheck-Done";
	}
	
	@Override
	public boolean check (BPState curState, Program prog) {
		if (curState == null || curState.getInstruction() == null) {
			return false;
		}
		
		String insName = curState.getInstruction().getName();
		if (insName.contains("RDTSC"))
		{
			long location = curState.getLocation().getValue();
			if (!contain(location)) {
//				num++;
				locList.add(location);
			}

			return true;
		}
		return false;
	}

	@Override
	public boolean checkAPIName(String apiName, long location) {
		// TODO Auto-generated method stub
		String name = apiName.toLowerCase();
		for (String antiAPI: PackerConstants.TIMINGCHECK_APIs) {
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
