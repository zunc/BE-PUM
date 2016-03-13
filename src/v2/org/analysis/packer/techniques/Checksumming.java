package v2.org.analysis.packer.techniques;

import java.util.ArrayList;

import org.jakstab.Program;
import org.jakstab.asm.AbsoluteAddress;
import org.jakstab.asm.Instruction;
import org.jakstab.asm.Operand;
import org.jakstab.asm.x86.X86ArithmeticInstruction;
import org.jakstab.asm.x86.X86CondJmpInstruction;
import org.jakstab.asm.x86.X86JmpInstruction;

import v2.org.analysis.packer.PackerConstants;
import v2.org.analysis.packer.PackerHelper;
import v2.org.analysis.packer.PackerRecord;
import v2.org.analysis.packer.PackerSavedState;
import v2.org.analysis.path.BPState;
import v2.org.analysis.value.LongValue;
import v2.org.analysis.value.Value;

public class Checksumming implements PackerTechnique {

	/** 
	 * Using for record checksum-
	 */
	
	private static int numOfChecksumming;
	private static boolean useCheckingLoop;
	private static PackerSavedState checksumState;
	private static ArrayList<PackerSavedState> savedCheckSumState;
	
	public Checksumming ()
	{
		numOfChecksumming				= 0;
		useCheckingLoop					= false;
		checksumState					= new PackerSavedState();
		savedCheckSumState				= new ArrayList<PackerSavedState>();
	}
	
	@Override
	public void Count (BPState curState, Program prog)
	{
		if (curState == null || curState.getInstruction() == null) {
			return;
		}
		
		Instruction ins = curState.getInstruction();
		
		if (!useCheckingLoop)
		{
			if (ins instanceof X86ArithmeticInstruction)
			{
				int opCount = curState.getInstruction().getOperandCount();
				if (opCount >= 2)
				{
					Operand dest = curState.getInstruction().getOperand(0);
					Value aVal = PackerHelper.GetOperandValue(curState, dest);
					if (aVal instanceof LongValue)
					{
						AbsoluteAddress aAddr = new AbsoluteAddress(((LongValue) aVal).getValue());
						if (!PackerHelper.IsInCodeSection(prog, aAddr))
						{
							long insLoc = curState.getLocation().getValue();
							checksumState.setStates(insLoc, ins.getName());
						}
					}
				}
			}
			
			if (checksumState.getInsLoc() != 0x0
					&& !PackerHelper.IsExistedState(savedCheckSumState, checksumState))
			{
				if (ins instanceof X86JmpInstruction || ins instanceof X86CondJmpInstruction)
				{
					Operand op = curState.getInstruction().getOperand(0);
					Value aVal = PackerHelper.GetOperandValue(curState, op);
					if (aVal instanceof LongValue)
					{
						if (((LongValue) aVal).getValue() <= checksumState.getInsLoc())
						{
							useCheckingLoop = true;
							savedCheckSumState.add(checksumState);
						}
					}
				}
			}
		}
		else
		{
			if (ins.getName().contains("cmp"))
			{
				Operand op = curState.getInstruction().getOperand(1);
				Value aVal = PackerHelper.GetOperandValue(curState, op);
				if (aVal instanceof LongValue)
				{
					long nextInsLoc = curState.getLocation().getValue() + ins.getSize();
					Instruction nextIns = prog.getInstruction(new AbsoluteAddress(nextInsLoc), curState.getEnvironement());
					if (nextIns instanceof X86CondJmpInstruction)
					{
						numOfChecksumming++;
						PackerRecord.getInstance().updatePackerTechniqueRecord(String.valueOf(PackerConstants.CHECKSUMMING));
						checksumState.reset();
						useCheckingLoop = false;
					}
				}
			}
		}
	}
	
	@Override
	public int GetInfo ()
	{
		return numOfChecksumming;
	}
	
}
