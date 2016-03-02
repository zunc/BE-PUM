package v2.org.analysis.packer.techniques;

import java.util.ArrayList;

import org.jakstab.Program;
import org.jakstab.asm.AbsoluteAddress;
import org.jakstab.asm.Instruction;
import org.jakstab.asm.Operand;

import v2.org.analysis.packer.PackerHelper;
import v2.org.analysis.packer.PackerSavedBlock;
import v2.org.analysis.path.BPState;
import v2.org.analysis.value.LongValue;
import v2.org.analysis.value.Value;

public class OverlappingFunction implements PackerTechnique {

	/** 
	 * Using for record overlapping function
	 */
	
	private static int numOfOverlappingFunction;
	private static ArrayList<PackerSavedBlock> funcs;
	private static long tracingFuncLoc;
	private static ArrayList<Long> savedListFunc;
	
	private static boolean isOverlap;
	
	public OverlappingFunction ()
	{
		numOfOverlappingFunction		= 0;
		
		tracingFuncLoc					= 0x0;
		funcs							= new ArrayList<PackerSavedBlock>();
		
		savedListFunc					= new ArrayList<Long>();
		
		isOverlap						= false;
	}
	
	@Override
	public void Count (BPState curState, Program prog)
	{
		if (curState == null || curState.getInstruction() == null) {
			return;
		}
		
		if (curState.getInstruction().getName().contains("call"))
		{
			Operand op = curState.getInstruction().getOperand(0);
			Value aVal = PackerHelper.GetOperandValue(curState, op);
			if (aVal instanceof LongValue)
			{
				AbsoluteAddress aAddr = new AbsoluteAddress(((LongValue) aVal).getValue());
				tracingFuncLoc = aAddr.getValue();
			}
		}
		
		Instruction ins = curState.getInstruction();
		String insName = ins.getName();
		if (insName.contains("ret"))
		{
			PackerSavedBlock savedFunc = new PackerSavedBlock(tracingFuncLoc
					 										, curState.getLocation().getValue());
			funcs.add(savedFunc);
			int size = funcs.size();
			if (size > 1)
			{
				for (PackerSavedBlock func: funcs)
				{
					if (PackerHelper.CheckOverlapping(func, savedFunc)
							&& !PackerHelper.IsExisted(savedListFunc, new Long(func.getBeginBlock()))
							&& !PackerHelper.IsExisted(savedListFunc, new Long(savedFunc.getBeginBlock())))
					{
						numOfOverlappingFunction++;
						isOverlap = true;
						savedListFunc.add(new Long(func.getBeginBlock()));
						savedListFunc.add(new Long(savedFunc.getBeginBlock()));
						break;
					}
				}
			}
			
			if (isOverlap)
			{
				funcs = PackerHelper.ClearStates(funcs);
				isOverlap = false;
			}
		}
	}
	
	@Override
	public int GetInfo ()
	{
		return numOfOverlappingFunction;
	}
	
}
