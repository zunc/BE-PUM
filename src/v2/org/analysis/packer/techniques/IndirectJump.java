package v2.org.analysis.packer.techniques;

import java.util.ArrayList;

import org.jakstab.Program;
import org.jakstab.asm.Instruction;
import org.jakstab.asm.Operand;
import org.jakstab.asm.x86.X86CondJmpInstruction;
import org.jakstab.asm.x86.X86JmpInstruction;
import org.jakstab.asm.x86.X86MemoryOperand;
import org.jakstab.asm.x86.X86Register;

import v2.org.analysis.packer.PackerHelper;
import v2.org.analysis.path.BPState;

public class IndirectJump implements PackerTechnique {

	/** 
	 * Using for record indirect jump
	 */
	
	private static int numOfIndirectCall;
	private static int numOfIndirectJump;
	private static ArrayList<Long> savedIndirectState;
	
	public IndirectJump ()
	{
		numOfIndirectCall 		= 0;
		numOfIndirectJump		= 0;
		
		savedIndirectState		= new ArrayList<Long>();
	}
	
	@Override
	public void Count (BPState curState, Program prog)
	{
		if (curState == null || curState.getInstruction() == null) {
			return;
		}
		
		Instruction ins = curState.getInstruction();
		String insName = ins.getName();
		if (PackerHelper.IsExisted(savedIndirectState, new Long(curState.getLocation().getValue())))
		{
			return;
		}
		if (insName.contains("call"))
		{
			Operand dest = curState.getInstruction().getOperand(0);
			if (dest instanceof X86Register || dest instanceof X86MemoryOperand)
			{
				numOfIndirectCall++;
			}
		}
		else if (ins instanceof X86JmpInstruction || ins instanceof X86CondJmpInstruction)
		{
			Operand dest = curState.getInstruction().getOperand(0);
			if (dest instanceof X86Register || dest instanceof X86MemoryOperand)
			{
				numOfIndirectJump++;
			}
		}
		savedIndirectState.add(new Long(curState.getLocation().getValue()));
	}
	
	@Override
	public int GetInfo ()
	{
		return numOfIndirectCall + numOfIndirectJump;
	}
	
}
