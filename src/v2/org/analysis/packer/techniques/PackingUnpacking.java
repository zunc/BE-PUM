package v2.org.analysis.packer.techniques;

import java.util.ArrayList;

import org.jakstab.Program;
import org.jakstab.asm.AbsoluteAddress;
import org.jakstab.asm.Instruction;
import org.jakstab.asm.Operand;
import org.jakstab.asm.x86.X86CondJmpInstruction;
import org.jakstab.asm.x86.X86JmpInstruction;

import v2.org.analysis.packer.PackerConstants;
import v2.org.analysis.packer.PackerHelper;
import v2.org.analysis.packer.PackerRecord;
import v2.org.analysis.packer.PackerSavedState;
import v2.org.analysis.path.BPState;
import v2.org.analysis.value.LongValue;
import v2.org.analysis.value.Value;

public class PackingUnpacking implements PackerTechnique {

	/** 
	 * Using for record packing/unpacking
	 */
	
	private static int numOfPackingUnpacking;
	private static PackerSavedState decryptionState;
	private static ArrayList<PackerSavedState> savedDecryptionStates;
	
	public PackingUnpacking ()
	{
		numOfPackingUnpacking				= 0;
		
		decryptionState						= new PackerSavedState();
		savedDecryptionStates				= new ArrayList<PackerSavedState>();
	}
	
	@Override
	public void Count (BPState curState, Program prog)
	{
		if (curState == null || curState.getInstruction() == null) {
			return;
		}
		
		Instruction ins = curState.getInstruction();
		
		int opCount = curState.getInstruction().getOperandCount();
		if (opCount >= 2)
		{
			Operand dest = curState.getInstruction().getOperand(0);
			Value aVal = PackerHelper.GetOperandValue(curState, dest);
			if (aVal instanceof LongValue)
			{
				AbsoluteAddress aAddr = new AbsoluteAddress(((LongValue) aVal).getValue());
				if (PackerHelper.IsInCodeSection(prog, aAddr))
				{
					long insLoc = curState.getLocation().getValue();
					decryptionState.setStates(insLoc, ins.getName());
				}
			}
		}
		
		if (decryptionState.getInsLoc() != 0x0
				&& !PackerHelper.IsExistedState(savedDecryptionStates, decryptionState))
		{
			if (ins instanceof X86JmpInstruction
					|| ins instanceof X86CondJmpInstruction)
			{
				Operand opD = ins.getOperand(0);
				Value aValD = PackerHelper.GetOperandValue(curState, opD);
				if (aValD instanceof LongValue)
				{
					if (((LongValue) aValD).getValue() <= decryptionState.getInsLoc())
					{
						numOfPackingUnpacking++;
						PackerRecord.getInstance().updatePackerTechniqueRecord(String.valueOf(PackerConstants.PACKING_UNPACKING));
						savedDecryptionStates.add(decryptionState);
						decryptionState.reset();
					}
				}
			}
		}
	}
	
	@Override
	public int GetInfo ()
	{
		return numOfPackingUnpacking;
	}
	
}
