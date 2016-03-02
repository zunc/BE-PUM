package v2.org.analysis.packer;

import org.jakstab.Program;

import v2.org.analysis.path.BPState;

public class PackerTechniques {
	
	private PackerPatterns pPattern;
	
	private boolean anti_debugging;	
	private boolean checksumming;	
	private boolean code_chunking;
	private boolean indirect_jump; 
	private boolean obfuscated_const;
	private boolean overlapping_block;
	private boolean overlapping_func;
	private boolean overwriting;
	private boolean packing_unpacking;	
	private boolean SEHs; 
	private boolean stolen_bytes;	
	private boolean timing_check;
	private boolean two_APIs;
	private boolean hardware_bpx;
	
	private boolean USING_COUNT;
	private static PackerCounter pCounter;
	
	public PackerTechniques ()
	{
		USING_COUNT			= true;
		
		pPattern 			= new PackerPatterns();
		
		anti_debugging		= false;
		checksumming		= false;
		code_chunking		= false;
		indirect_jump 		= false; 
		obfuscated_const	= false;
		overlapping_block	= false;
		overlapping_func	= false;
		overwriting 		= false;
		packing_unpacking 	= false; 
		SEHs				= false;
		stolen_bytes		= false; 
		timing_check		= false;
		two_APIs			= false; 
		hardware_bpx		= false;

		pCounter			= new PackerCounter();
	}
	
	public void updateChecking (BPState curState, Program program)
	{
		if (curState != null)
		{
			// BEGIN TO STATISTIC
			pCounter.setCountingState(curState, program);
			pCounter.Execute(this.USING_COUNT);
			pPattern.setCheckingState(pCounter);
			this.checkingState();
		}
	}
	
	private void checkingState ()
	{
		// Check packing/unpacking
		if (!this.packing_unpacking)
		{
			if (pPattern.PackAndUnpack()) {
				isPackingUnpacking();
			}
		}
		
		// Check SMC
		if (!this.packing_unpacking)
		{
			if (pPattern.Overwriting()) {
				isOverwriting();
			}
		}
		
		// Check indirect - jump
		if (!this.indirect_jump)
		{
			if (pPattern.IndirectJump()) {
				isIndirectJump();
			}
		}
		
		// Check obfuscated constant
		if (!this.obfuscated_const)
		{
			if (pPattern.ObfuscatedConst()) {
				isObfuscatedConst();
			}
		}
		
		// Check overlapping function
		if (!this.overlapping_func)
		{
			if (pPattern.OverlappingFunction()) {
				isOverlappingFunc();
			}
		}
		
		// Check overlapping block
		if (!this.overlapping_block)
		{
			if (pPattern.OverlappingBlock()) {
				isOverlappingBlock();
			}
		}
		
		// Check code chunk
		if (!this.code_chunking)
		{
			if (pPattern.CodeChunking()) {
				isCodeChunking();
			}
		}
		
		// Check stolen bytes
		if (!this.stolen_bytes)
		{
			if (pPattern.StolenBytes()) {
				isStolenBytes();
			}
		}
		
		// Check checksum
		if (!this.checksumming)
		{
			if (pPattern.Checksumming()) {
				isChecksumming();
			}
		}
		
		// Check SEH
		if (!this.SEHs)
		{
			if (pPattern.SEHs()) {
				isSEH();
			}
		}
		
		// Check 2 special APIs
		if (!this.two_APIs)
		{
			if (pPattern.TwoAPIs()) {
				isTwoAPIs();
			}
		}
		
		// Check anti-debugging
		if (!this.anti_debugging)
		{
			if (pPattern.AntiDebugging()) {
				isAntiDebugging();
			}
		}
		
		// Check timing-check
		if (!this.timing_check)
		{
			if (pPattern.TimingCheck()) {
				isTimingCheck();
			}
		}
		
		// Check hardware breakpoints
		if (!this.hardware_bpx)
		{
			if (pPattern.HardwareBPX()) {
				isHardwareBPX();
			}
		}
	}
	
	public void isPackingUnpacking ()
	{
		this.packing_unpacking = true;
	}
	
	public void isOverwriting ()
	{
		this.overwriting = true;
	}
	
	public void isIndirectJump ()
	{
		this.indirect_jump = true;
	}
	
	public void isObfuscatedConst ()
	{
		this.obfuscated_const = true;
	}
	
	public void isOverlappingFunc ()
	{
		this.overlapping_func = true;
	}
	
	public void isOverlappingBlock ()
	{
		this.overlapping_block = true;
	}
	
	public void isCodeChunking ()
	{
		this.code_chunking = true;
	}
	
	public void isStolenBytes ()
	{
		this.stolen_bytes = true;
	}
	
	public void isChecksumming ()
	{
		this.checksumming = true;
	}
	
	public void isSEH ()
	{
		this.SEHs = true;
	}
	
	public void isTwoAPIs ()
	{
		this.two_APIs = true;
	}
	
	public void isAntiDebugging ()
	{
		this.anti_debugging = true;
	}
	
	public void isTimingCheck ()
	{
		this.timing_check = true;
	}
	
	public void isHardwareBPX ()
	{
		this.hardware_bpx = true;
	}

	public String getDetailTechniques ()
	{
		String techniques = "";
		
		techniques += (anti_debugging) 		? "1" : "0";
		techniques += (checksumming) 		? "1" : "0"; 
		techniques += (code_chunking) 		? "1" : "0";
		techniques += (indirect_jump) 		? "1" : "0"; 
		techniques += (obfuscated_const) 	? "1" : "0";
		techniques += (overlapping_block)	? "1" : "0";
		techniques += (overlapping_func)	? "1" : "0";
		techniques += (overwriting) 		? "1" : "0"; 
		techniques += (packing_unpacking) 	? "1" : "0"; 
		techniques += (SEHs) 				? "1" : "0"; 
		techniques += (stolen_bytes) 		? "1" : "0"; 
		techniques += (timing_check)		? "1" : "0";
		techniques += (two_APIs) 			? "1" : "0"; 
		techniques += (hardware_bpx)		? "1" : "0";	
		
		return techniques;
	}
	
	public String getTechniquesStatiscial ()
	{
		return pCounter.getInfo();
	}
	
	public void setCount (boolean isCounting)
	{
		this.USING_COUNT = isCounting;
	}
}
