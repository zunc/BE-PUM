package v2.org.analysis.packer.techniques;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jakstab.Program;

import v2.org.analysis.packer.PackerConstants;
import v2.org.analysis.path.BPState;

public class OverlappingBlock extends PackerTechnique {

	/** 
	 * Using for record overlapping function
	 */
	
//	private static boolean firstCheck;
//	private static long tracingBlockLoc;
//	private static ArrayList<PackerSavedBlock> blocks;	
//	private static ArrayList<Long> savedListBlock;	
//	private static boolean isOverlap;
	private Map<Long, Long> blockList; 
	private long startAddr; 
	private Set<String> detailedList;
	
	public OverlappingBlock () {
//		num			= 0;
		name = "OverlappingBlock-Check later";
		id = PackerConstants.OVERLAPPING_BLOCK;
		blockList = new HashMap<Long, Long>();
		startAddr = -1;
		detailedList = new HashSet<String> ();
//		firstCheck						= true;
//		tracingBlockLoc					= 0x0;
//		blocks 							= new ArrayList<PackerSavedBlock>();		
//		savedListBlock					= new ArrayList<Long>();		
//		isOverlap						= false;
	}
	
	@Override
	public boolean check (BPState curState, Program prog) {
//		if (curState == null || curState.getInstruction() == null) {
//			return false;
//		}
//		boolean ret = false;
//		if (firstCheck)
//		{
//			tracingBlockLoc = curState.getLocation().getValue();
//			firstCheck = false;
//		}
//		
//		Instruction ins = curState.getInstruction();
//		if (ins instanceof X86JmpInstruction)
//		{
//			Operand op = ins.getOperand(0); 
//			Value aVal = PackerHelper.GetOperandValue(curState, op);
//			if (aVal instanceof LongValue)
//			{
//				PackerSavedBlock savedBlock = new PackerSavedBlock(tracingBlockLoc
//																 , curState.getLocation().getValue());
//				int size = blocks.size();
//				if (size > 1)
//				{
//					for (PackerSavedBlock block: blocks)
//					{
//						if (PackerHelper.CheckOverlapping(block, savedBlock)
//								&& !PackerHelper.IsExisted(savedListBlock, new Long(block.getBeginBlock()))
//								&& !PackerHelper.IsExisted(savedListBlock, new Long(savedBlock.getBeginBlock())))
//						{
////							num++;
//							long location = curState.getLocation().getValue();
//							if (!contain(location)) {
////								num++;
//								locList.add(location);
//							}
//				
//							isOverlap = true;
//							savedListBlock.add(new Long(block.getBeginBlock()));
//							savedListBlock.add(new Long(savedBlock.getBeginBlock()));	
//							ret = true;
//							break;
//						}
//					}
//				}
//				blocks.add(savedBlock);
//				tracingBlockLoc = ((LongValue) aVal).getValue();
//				if (isOverlap)
//				{
//					blocks = PackerHelper.ClearStates(blocks);
//					isOverlap = false;
//				}
//				
//			}
//		}
//		return ret;
		return false;
	}

	@Override
	public boolean checkAPIName(String apiName, long location) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean checkJmpAddr(long value) {
		// TODO Auto-generated method stub
		return checkAddrInside(value);
	}
	
	private boolean checkAddrInside(long value) {
		for (Map.Entry<Long, Long> pair : blockList.entrySet()) {
			 if (pair.getKey() < value && pair.getValue()> value){  
//					 && !Program.getProgram().getBPCFG().containVertex(value)) {
				detailedList.add("0x"+ Long.toHexString(pair.getKey()) + "-0x" + Long.toHexString(pair.getValue()) + " ; "
						+ "0x" + Long.toHexString(value)); 
				return true;
			}
		} 
	    
	    return false;
	}
	
	@Override
	public String toString() {
		String ret = "Name:" + name + ", ID:" + id + ", Location:";
		for (String location: detailedList) {
			ret += location + ", ";
		}
		
		return ret;
	}

	public void insertJmpAddr(long curAddr, long nextAddr) {
		// TODO Auto-generated method stub
		if (startAddr > 0) {
			blockList.put(startAddr, curAddr);			
		}
		startAddr = nextAddr;
		blockList.put(nextAddr, (long)-1);
	}	
}
