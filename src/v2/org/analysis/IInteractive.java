/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package v2.org.analysis;

import java.util.List;
import v2.org.analysis.path.BPPath;
import v2.org.analysis.transition_rule.X86TransitionRule;

/**
 *
 * @author zunc
 */
public interface IInteractive {
	
	public enum ACTION_OP {
		NOP,
		PASS
	}
	
	ACTION_OP onInstruction(X86TransitionRule rule, BPPath path, List<BPPath> pathList);
	//void onReadMemory(long address);
	
}
