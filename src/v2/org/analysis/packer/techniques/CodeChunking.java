package v2.org.analysis.packer.techniques;

import java.util.ArrayList;

import org.jakstab.Program;
import org.jakstab.asm.Instruction;
import org.jakstab.asm.x86.X86JmpInstruction;

import v2.org.analysis.packer.PackerHelper;
import v2.org.analysis.packer.PackerSavedState;
import v2.org.analysis.path.BPState;

public class CodeChunking implements PackerTechnique {

	/** 
	 * Using for record code chunk-
	 */
	
	private static int numOfCodeChunking;
	private static ArrayList<PackerSavedState> chunkCodeStates;
	private static ArrayList<Long> savedChunkCodeStates;
	
	private static final long CHUNKING_THRESHOLD = 10;
	
	public CodeChunking ()
	{
		numOfCodeChunking				= 0;
		chunkCodeStates 				= new ArrayList<PackerSavedState>();
		savedChunkCodeStates			= new ArrayList<Long>();
	}
	
	@Override
	public void Count (BPState curState, Program prog)
	{
		if (curState == null || curState.getInstruction() == null) {
			return;
		}
		
		Instruction ins = curState.getInstruction();
		if (ins instanceof X86JmpInstruction)
		{
			long insLoc = curState.getLocation().getValue();
			PackerSavedState jmpState = new PackerSavedState(insLoc, ins.getName());
			boolean isExisted = false;
			for (PackerSavedState jState: chunkCodeStates)
			{
				if (jState.getInsLoc() == jmpState.getInsLoc())
				{
					isExisted = true;
					break;
				}
			}
			if (!isExisted)
			{
				chunkCodeStates.add(jmpState);
			}
		}
		int size = chunkCodeStates.size();
		if (chunkCodeStates.size() > 2)
		{
			PackerSavedState jmpStateA = chunkCodeStates.get(size - 1);
			PackerSavedState jmpStateB = chunkCodeStates.get(size - 2);
			PackerSavedState jmpStateC = chunkCodeStates.get(size - 3);
			if (Math.abs(jmpStateA.getInsLoc() - jmpStateB.getInsLoc()) <= CHUNKING_THRESHOLD
				&& Math.abs(jmpStateB.getInsLoc() - jmpStateC.getInsLoc()) <= CHUNKING_THRESHOLD)
			{
				if (!PackerHelper.IsExisted(savedChunkCodeStates, jmpStateA.getInsLoc())
						|| !PackerHelper.IsExisted(savedChunkCodeStates, jmpStateB.getInsLoc())
						|| !PackerHelper.IsExisted(savedChunkCodeStates, jmpStateC.getInsLoc()))
				{
					numOfCodeChunking++;
					chunkCodeStates = PackerHelper.ClearStates(chunkCodeStates);
					savedChunkCodeStates.add(jmpStateA.getInsLoc());
					savedChunkCodeStates.add(jmpStateB.getInsLoc());
					savedChunkCodeStates.add(jmpStateC.getInsLoc());
				}
			}
		}
	}
	
	@Override
	public int GetInfo ()
	{
		return numOfCodeChunking;
	}
	
}
