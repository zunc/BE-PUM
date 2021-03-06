package v2.org.analysis.transition_rule.x86move;

import v2.org.analysis.path.BPState;
import v2.org.analysis.transition_rule.stub.X86MoveStub;
import v2.org.analysis.value.BooleanValue;

public class cmovp extends X86MoveStub {

	@Override
	public BPState execute() {
		if (env.getFlag().getPFlag().equal(new BooleanValue(1))) {
			isSet_CMOVcc = true;
		}
		return this.cmov();
	}

}
