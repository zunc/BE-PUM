/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package v2.org.analysis;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Random;
import org.jakstab.Program;
import org.jakstab.asm.AbsoluteAddress;
import org.jakstab.asm.Instruction;
import org.jakstab.asm.x86.X86Instruction;
import v2.org.analysis.cfg.BPCFG;
import v2.org.analysis.cfg.BPVertex;
import v2.org.analysis.environment.Environment;
import v2.org.analysis.environment.Memory;
import v2.org.analysis.environment.Register;
import v2.org.analysis.environment.Stack;
import v2.org.analysis.path.BPPath;
import v2.org.analysis.path.BPState;
import v2.org.analysis.transition_rule.X86TransitionRule;
import v2.org.analysis.transition_rule.stub.AssemblyInstructionStub;
import v2.org.analysis.value.BooleanValue;
import v2.org.analysis.value.LongValue;

/**
 *
 * @author zunc
 */
public class AnalysisEmdivi implements IInteractive {

	private Program program;
	
	public AnalysisEmdivi(Program prg) {
		program = prg;
	}
	
	@Override
	public ACTION_OP onInstruction(X86TransitionRule rule, BPPath path, List<BPPath> pathList) {
		BPState curState = path.getCurrentState();
		BPCFG cfg = Program.getProgram().getBPCFG();
		Instruction ins = curState.getInstruction();
		BPVertex src = cfg.getVertex(curState.getLocation(), ins);
		AbsoluteAddress location = curState.getLocation();
		long lngAddress = curState.getLocation().getValue();
		
		Register reg = curState.getEnvironement().getRegister();
		Memory memory = curState.getEnvironement().getMemory();
		// Value val = memory.getQWordMemoryValue(0x440dd0);
		// double db = Double.longBitsToDouble(((LongValue)val).getValue());
		// zunc: log to debug
		if (location != null) {
			AbsoluteAddress addr = new AbsoluteAddress(location.getValue());
			String strInst = String.format("0x00%s\t%s",
					Long.toHexString(location.getValue()), program.getInstructionString(addr));
			System.out.println(strInst);
		}

//		if (lngAddress == 0x412044) {
//			//.text:00412044 jnz     short loc_41209A
//			Environment env = curState.getEnvironement();
//			System.out.println(" -> FORCE SET [ZF=1]");
//			env.getFlag().setZFlag(new BooleanValue(true));
//		}

		boolean isHit = false;
		
		if ((lngAddress > 0x412044)
				&& (lngAddress <= 0x412096)) {
			rule.generateNextInstructionForce(ins, path, pathList);
			isHit = true;
		}  else if (lngAddress == 0x41be1a) { // by pass loop trap
			System.out.println(" -> PASS");
			Environment env = curState.getEnvironement();
			Stack stack = env.getStack();
			//0x41be14	pushl	%ebx
			//0x41be15	pushl	%ebx
			//0x41be16	pushl	$0x1<UINT8>
			//0x41be18	pushl	%ebx
			//0x41be19	pushl	%ebx
			//0x41be1a	call	0x0041bfd0			//<!> wtf proc
			// protect environement - ba?o ve^. mo^i truong
			for (int i = 1; i <= 5; i++) {
				stack.pop();
			}
			rule.generateNextInstructionForce(ins, path, pathList);
			isHit = true;
		} else if (lngAddress == 0x4129f0) {
			// .text:004129F0 call    _rand
			Environment env = curState.getEnvironement();
			Random rnd = new Random();
			int randVal = rnd.nextInt();
			randVal = randVal >= 0 ? randVal : -randVal;
			randVal = randVal % 32767;
			System.out.println(" -> randVal: " + randVal);
			env.getRegister().setRegisterValue("eax", new LongValue(randVal));
			rule.generateNextInstructionForce(ins, path, pathList);
			isHit = true;
		} else if (lngAddress == 0x412014
				|| lngAddress == 0x412034) {
			//.text:00412013                 push    eax
			//.text:00412014                 call    procLoop01

			//.text:00412027                 push    ecx             ; void *
			//.text:00412028                 lea     edi, [ebp+var_1B4]
			//.text:0041202E                 lea     ecx, [ebp+var_BC]
			//.text:00412034                 call    sub_403210      ; xxxx pass
			System.out.println(" -> FORCE BY PASS");
			Environment env = curState.getEnvironement();
			env.getStack().pop();
			rule.generateNextInstructionForce(ins, path, pathList);
			isHit = true;
		} else if (lngAddress == 0x41168F
				|| lngAddress == 0x4116C0) {
			System.out.println(" -> PASS");
			Environment env = curState.getEnvironement();
			Stack stack = env.getStack();
			stack.pop();
			rule.generateNextInstructionForce(ins, path, pathList);
			isHit = true;
		}
		
		return isHit ? ACTION_OP.PASS : ACTION_OP.NOP;
	}
	
}
