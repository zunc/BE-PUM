package v2.org.analysis.packer.techniques;

import java.util.HashSet;
import java.util.Set;

import org.jakstab.Program;

import v2.org.analysis.packer.PackerConstants;
import v2.org.analysis.path.BPState;

public class CodeChunking extends PackerTechnique {

	/** 
	 * Using for record code chunk-
	 */
	
//	private static ArrayList<PackerSavedState> chunkCodeStates;
//	private static ArrayList<Long> savedChunkCodeStates;	
	private static final long CHUNKING_THRESHOLD = 10;
//	private Map<Long, Long> blockList; 
	private long startAddr; 
	private Set<String> detailedList;

	
	public CodeChunking () { 
		id = PackerConstants.CODE_CHUNKING;
//		chunkCodeStates 				= new ArrayList<PackerSavedState>();
//		savedChunkCodeStates			= new ArrayList<Long>();
		name = "CodeChunking-Done";
//		blockList = new HashMap<Long, Long>();
		startAddr = -1;
		detailedList = new HashSet<String> ();
	}
	
	@Override
	public String toString() {
		String ret = "Name:" + name + ", ID:" + id + ", Location:";
		for (String location: detailedList) {
			ret += location + ", ";
		}
		
		return ret;
	}
	
	@Override
	public boolean check (BPState curState, Program prog)
	{
//		if (curState == null || curState.getInstruction() == null) {
//			return false;
//		}
//		
//		Instruction ins = curState.getInstruction();
//		if (ins instanceof X86JmpInstruction)
//		{
//			long insLoc = curState.getLocation().getValue();
//			PackerSavedState jmpState = new PackerSavedState(insLoc, ins.getName());
//			boolean isExisted = false;
//			for (PackerSavedState jState: chunkCodeStates)
//			{
//				if (jState.getInsLoc() == jmpState.getInsLoc())
//				{
//					isExisted = true;
//					break;
//				}
//			}
//			if (!isExisted)
//			{
//				chunkCodeStates.add(jmpState);
//			}
//		}
//		int size = chunkCodeStates.size();
//		if (chunkCodeStates.size() > 2)
//		{
//			PackerSavedState jmpStateA = chunkCodeStates.get(size - 1);
//			PackerSavedState jmpStateB = chunkCodeStates.get(size - 2);
//			PackerSavedState jmpStateC = chunkCodeStates.get(size - 3);
//			if (Math.abs(jmpStateA.getInsLoc() - jmpStateB.getInsLoc()) <= CHUNKING_THRESHOLD
//				&& Math.abs(jmpStateB.getInsLoc() - jmpStateC.getInsLoc()) <= CHUNKING_THRESHOLD)
//			{
//				if (!PackerHelper.IsExisted(savedChunkCodeStates, jmpStateA.getInsLoc())
//						|| !PackerHelper.IsExisted(savedChunkCodeStates, jmpStateB.getInsLoc())
//						|| !PackerHelper.IsExisted(savedChunkCodeStates, jmpStateC.getInsLoc()))
//				{
////					num++;
//					long location = curState.getLocation().getValue();
//					if (!contain(location)) {
////						num++;
//						locList.add(location);
//					}
//					chunkCodeStates = PackerHelper.ClearStates(chunkCodeStates);
//					savedChunkCodeStates.add(jmpStateA.getInsLoc());
//					savedChunkCodeStates.add(jmpStateB.getInsLoc());
//					savedChunkCodeStates.add(jmpStateC.getInsLoc());
//					return true;
//				}
//			}
//		}		
		return false;
	}

	@Override
	public boolean checkAPIName(String apiName, long location) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean checkJmpAddr(long curAddr) {
		// TODO Auto-generated method stub
		if (startAddr > 0) {
			if (curAddr - startAddr >= 0 && curAddr - startAddr < CHUNKING_THRESHOLD) {
				detailedList.add("0x"+ Long.toHexString(startAddr) + "-0x" + Long.toHexString(curAddr));
				return true;
			}
		}
		return false;
	}

	public void insertJmpAddr(long curAddr, long nextAddr) {
		// TODO Auto-generated method stub					
		startAddr = nextAddr;
	}	
}
