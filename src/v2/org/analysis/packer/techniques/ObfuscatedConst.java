package v2.org.analysis.packer.techniques;

import java.util.ArrayList;

import org.jakstab.Program;
import org.jakstab.asm.Immediate;
import org.jakstab.asm.Instruction;
import org.jakstab.asm.Operand;
import org.jakstab.asm.x86.X86ArithmeticInstruction;

import v2.org.analysis.packer.PackerHelper;
import v2.org.analysis.path.BPState;

public class ObfuscatedConst implements PackerTechnique {

	/** 
	 * Using for record obfuscated constants
	 */
	
	private static int numOfObfuscatedConst;
	private static ArrayList<Long> savedObfuscatedConstState;
	
	public ObfuscatedConst ()
	{
		numOfObfuscatedConst				= 0;
		savedObfuscatedConstState			= new ArrayList<Long>();
	}
	
	@Override
	public void Count (BPState curState, Program prog)
	{
		if (curState == null || curState.getInstruction() == null) {
			return;
		}
		
		Instruction ins = curState.getInstruction();
		int opCount = ins.getOperandCount();
		if (ins instanceof X86ArithmeticInstruction
				&& !PackerHelper.IsExisted(savedObfuscatedConstState, new Long(curState.getLocation().getValue())))
		{
			if (opCount >= 1)
			{
				Operand op1 = curState.getInstruction().getOperand(0);
				Operand op2 = null;
				if (opCount >= 2)
				{
					op2 = curState.getInstruction().getOperand(1);
				}
				if (op1 instanceof Immediate || op2 instanceof Immediate)
				{
					numOfObfuscatedConst++;
					savedObfuscatedConstState.add(new Long(curState.getLocation().getValue()));
				}
			}
		}
	}
	
	@Override
	public int GetInfo ()
	{
		return numOfObfuscatedConst;
	}
	
}
