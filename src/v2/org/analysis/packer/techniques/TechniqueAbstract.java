package v2.org.analysis.packer.techniques;

import org.jakstab.Program;

import v2.org.analysis.path.BPState;

public abstract class TechniqueAbstract {
	protected String name = ""; 
	protected int num = 0;
	protected int id; 
	
	public abstract boolean check(BPState curState, Program prog);	
	
	public int getFrequency() {
		return num;
	}
	public boolean hasTechnique() {
		return num > 0;
	}
	
	public String getName() {
		return name;
	}
	
	public int getID() {
		return id;
	}
}
