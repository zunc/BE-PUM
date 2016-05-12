/**
 * 
 */
package v2.org.analysis.algorithm;

import java.util.ArrayList;
import java.util.List;

import org.jakstab.Algorithm;
import org.jakstab.Program;
import org.jakstab.asm.AbsoluteAddress;
import org.jakstab.asm.Instruction;
/**
 * @author NMHai
 * 
 * The main algorithm of On-the-fly Model Generation
 * 
 */
import org.jakstab.asm.x86.X86CondJmpInstruction;

import v2.org.analysis.algorithm.OTFThreadManager.OTFThreadBase;
import v2.org.analysis.cfg.BPCFG;
import v2.org.analysis.cfg.BPVertex;
import v2.org.analysis.environment.Environment;
import v2.org.analysis.environment.processthread.TIB;
import v2.org.analysis.olly.OllyComparisonV2;
import v2.org.analysis.packer.PackerManager;
import v2.org.analysis.path.BPPath;
import v2.org.analysis.path.BPState;
import v2.org.analysis.path.PathList;
import v2.org.analysis.statistics.FileProcess;
import v2.org.analysis.transition_rule.X86TransitionRule;
import v2.org.analysis.value.Formulas;
import v2.org.analysis.value.LongValue;
import v2.org.analysis.value.Value;

public class OTFModelGeneration implements Algorithm {
	public static long DEFAULT_OUT_TIME = 180000;
	private final Program program;
	private long overallStartTime;
	
	//For compare with OllyDbg
	private long count = 1;
	private boolean isCompareOlly = true, isChecked = false;
	private OllyComparisonV2 ollyCompare = null;
	private AbsoluteAddress checkedAddr = new AbsoluteAddress(0);
	private AbsoluteAddress endAddr = new AbsoluteAddress(0);
	private String fileName = "";
	private FileProcess compareOllyResult = null;
	private int num = 1, loopCount = 1;

	public OTFModelGeneration(Program program) {
		super();
		this.program = program;
	}

	@Override
	public void run() {
		overallStartTime = System.currentTimeMillis();
		// BE-PUM algorithm
		System.out.println("Starting On-the-fly Model Generation algorithm.");
		program.getResultFileTemp().appendInLine('\n' + program.getFileName() + '\t');
		fileName = "out_" + Program.getProgram().getFileName() + "_";
		
		// Set up initial context
//		X86TransitionRule rule = new X86TransitionRule();
		BPCFG cfg = Program.getProgram().getBPCFG();
		Environment env = new Environment();
		env.getMemory().resetImportTable(program);
		AbsoluteAddress location = Program.getProgram().getEntryPoint();
		Instruction inst = Program.getProgram().getInstruction(location, env);
		List<BPPath> pathList = new ArrayList<BPPath>();
		
		// Insert start node
		BPVertex startNode = null;
		startNode = new BPVertex(location, inst);
		startNode.setType(0);
		cfg.insertVertex(startNode);

		BPState curState = null;
		BPPath path = null;
		curState = new BPState(env, location, inst);
		path = new BPPath(curState, new PathList(), new Formulas());
		path.setCurrentState(curState);
		pathList.add(path);

		// Update at first -----------------------------
		TIB.setBeUpdated(true);
		TIB.updateTIB(curState);
		
		if (PackerManager.getInstance().isDetectHeaderOnly()) {
			PackerManager.getInstance().getHeaderDetection(fileName);
			return;
		}
		
		// ---------------------------------------------

		// PHONG - 20150801 /////////////////////////////
		// Packer Detection via Header
//		System.out.println("================PACKER DETECTION VIA HEADER ======================");
//		if (OTFModelGeneration.detectPacker)
//		{
//			program.getDetection().detectViaHeader(program);
//			program.getDetection().setToLogFirst(program);
//		}
//		System.out.println("==================================================================");
		/////////////////////////////////////////////////
//		PackerManager.getInstance().setDetectPacker(true);
//		OTFThreadManager.getInstance().setMultiThread(true);
		synchronized (OTFThreadManager.getInstance()) {
			try {
				OTFThreadManager.getInstance().setOtfModelGeneration(this);
				OTFThreadManager.getInstance().check(this, pathList);
				OTFThreadManager.getInstance().wait();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public class OTFThread extends OTFThreadBase {
		private static final int MAX_NUMBER_ADDB = 100;
		X86TransitionRule rule = new X86TransitionRule();
		List<BPPath> pathList = new ArrayList<BPPath>();
		BPPath path = null;
		BPState curState = null;
		Instruction inst = null;
		AbsoluteAddress location = null;
		int numAddStop = 0;
		
		public OTFThread(BPPath bpPath) {
			this.pathList.add(bpPath);
			this.path = bpPath;
			this.curState = path.getCurrentState();
			this.inst = this.curState.getInstruction();
			this.location = this.curState.getLocation();
		}

		@Override
		public void execute() {
			long overallStartTemp = overallStartTime;
			PackerManager pManager = PackerManager.getInstance();
			
			while (!pathList.isEmpty()) {
				path = pathList.remove(pathList.size() - 1);
				curState = path.getCurrentState();
				int numADDB = 0;
				// long overallStartTimePath = System.currentTimeMillis();
				while (true) {
					if (!curState.checkFeasiblePath()) {
						path.destroy();
						break;
					}
					
//					////////////////////////////////// OTF Packer Detection////////////////////////////////////////
					pManager.updateChecking(curState, program);
//					///////////////////////////////////////////////////////////////////////////////////
					 
					long overallEndTimeTemp = System.currentTimeMillis();
					// Output file each 60s
					if (overallEndTimeTemp - overallStartTemp > DEFAULT_OUT_TIME) {
	
						backupState(curState);
						overallStartTemp = overallEndTimeTemp;
					}
	
					if (path.isStop()) {
						break;
					}
	
					inst = curState.getInstruction();
					location = curState.getLocation();		
					
//					debugProg(location, curState);
					
					if (inst != null && inst.getName().contains("addb")
							&& inst.getOperand(0) != null && inst.getOperand(0).toString().contains("eax")
							&& inst.getOperand(1) != null && inst.getOperand(1).toString().contains("al")) {
						if (numADDB > MAX_NUMBER_ADDB) {
							program.getStopFile().appendFile(program.getFileName());						
							break;
						}
						
						numADDB ++;
					}
					
//					compareOlly(curState);
					// PHONG: 20150506 - Update TIB
					// --------------------------------------
					TIB.updateTIB(curState);
//					TIB.updateChecking(curState);
					// --------------------------------------
					
					if (inst == null || location == null) {
						break;
					}
					path.addTrace(curState.getLocation());
	
					if (inst instanceof X86CondJmpInstruction) {
						rule.getNewState((X86CondJmpInstruction) inst, path, pathList);
						if (!curState.checkFeasiblePath()) {
							path.destroy();
							break;
						}
					} else {
						rule.getNewState(path, pathList, true);
					}
					
					if (pManager.isDetected() && isOEP(curState.getLocation(), program.getFileName())) {
						pManager.outputToFile(program.getFileName());
						pManager.setDetectPacker(false);
					}
					
					
					///////// AFTER LOOP ///////////
					this.afterLoop(OTFModelGeneration.this, pathList);
					if (this.isStopCurrentPath(curState)) {
						path.destroy();
						break;
					}
				}
			}
		}
		
		private boolean b = false;

		private void debugProg(AbsoluteAddress loc, BPState curState2) {
			// TODO Auto-generated method stub			
			if (b) {
				System.out.print(loc.toString() + " ");
			}
			
			if (loc != null && loc.toString().contains("a31c64")) {
				Value v = curState.getEnvironement().getMemory().getDoubleWordMemoryValue(1229032);
				if (!b && v != null && v instanceof LongValue && 
						 ((LongValue)v).getValue() > 4000000) {
					b = true;
				}				
				
				if (b) {
					System.out.println();
					System.out.println(curState.getEnvironement().getMemory().getDoubleWordMemoryValue(1229032) + " " +
							curState.getEnvironement().getRegister().toString() + " " + 
							curState.getEnvironement().getFlag().toString() + " ");
				}
			}
		}
	}
	
	private boolean isOEP(AbsoluteAddress location, String fileName) {
		// TODO Auto-generated method stub
		return (location != null) && (fileName.contains("api_test") && location.toString().contains("401000")
				|| fileName.contains("bof") && location.toString().contains("401000")
				|| fileName.contains("demo1") && location.toString().contains("401000")
				|| fileName.contains("demo2") && location.toString().contains("401000")
				|| fileName.contains("Aztec") && location.toString().contains("401000")
				|| fileName.contains("Benny") && location.toString().contains("401000")
				|| fileName.contains("Cabanas") && location.toString().contains("401000")
				|| fileName.contains("Adson") && location.toString().contains("401000")
				|| fileName.contains("api_testv2") && location.toString().contains("401131")
				);
	}

	private void backupState(BPState curState) {
		// TODO Auto-generated method stub
		program.generageCFG("/asm/cfg/" + program.getFileName() + "_test");
		program.getResultFileTemp().appendInLine("\t"
						+ String.format("%8dms", (System.currentTimeMillis() - overallStartTime)) + "\t"
						+ String.format("%8d", program.getBPCFG().getVertexCount()) + "\t" + String.format("%8d", program.getBPCFG().getEdgeCount()));
		////////////////////////////////////////////////////
		// Write to packer result file after each 60s
		if (PackerManager.getInstance().isDetected()) {
			PackerManager.getInstance().outputToFile(program.getFileName());
		}
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
	}

	public boolean isCompleted() {
		// TODO Auto-generated method stub
		return program.getBPCFG().isCompleted();
	}

	public boolean isSound() {
		// TODO Auto-generated method stub
		return true;
	}	
	
	private void compareOlly(BPState state) {
		// TODO Auto-generated method stub
		// -------------------------------------------------------------------
		// OLLY DEBUG HERE
		// checkedAddr = new AbsoluteAddress(0x40818C);
		// endAddr = new AbsoluteAddress(0x4085B2);
		if (isCompareOlly) {
			AbsoluteAddress location = state.getLocation();
			Environment env = state.getEnvironement();
			if (ollyCompare == null) {
				long memoryStartAddr = 0x407000;
				long memoryEndAddr = 0x407004;
				long stackIndex = 0x4;
				System.out.println("Read file Olly " + "data/data/" + fileName + "" + num + ".txt");
				ollyCompare = new OllyComparisonV2("data/data/" + fileName + "" + num + ".txt", memoryStartAddr,
						memoryEndAddr, stackIndex);
				// ollyCompare = new OllyCompare("asm/olly/" + fileName +
				// ".txt", memoryStartAddr,
				// memoryEndAddr, stackIndex);
				ollyCompare.importOllyData(checkedAddr, endAddr);
				count = ollyCompare.getFirstCount();
				System.out.println("Finish reading!");
			}

			if (compareOllyResult == null) {
				compareOllyResult = new FileProcess("data/data/compareWithOlly_" + fileName + "" + num + ".txt");
				compareOllyResult.clearContentFile();
			}

			if (location != null && location.getValue() == checkedAddr.getValue()) {
				isChecked = true;
			}

			if (isChecked & location != null && !ollyCompare.isFinished()) {
					//&& (location.getValue() != endAddr.getValue() || loopCount != ollyCompare.getLoopCount())) {
				// System.out.println("Loop = " + count + " , Address = " +
				// location.toString() + ":");
				compareOllyResult.appendFile("Loop = " + Long.toHexString(count) + " , Address = " + location.toString() + ":");
				// COMPARE HERE
				String result = ollyCompare.compareBEPUM(env, count, location);
				count = ollyCompare.getNextCheck();
				// if (result.contains("Unequal")) {
				// System.out.println("Bug");
				// }
				compareOllyResult.appendFile(result);
				// System.out.println("*************************************************************");
				compareOllyResult.appendFile("*************************************************************");
				loopCount++;
			}

			if (isChecked && location != null 
					&& ollyCompare.isFinished()) {
					//&& location.getValue() == endAddr.getValue()
					//&& loopCount == ollyCompare.getLoopCount()) {
				System.out.println("Stop Check: " + fileName + "" + num + ".txt");
				ollyCompare = null;
				loopCount = 1;
				compareOllyResult = null;
				num++;
				isChecked = false;
				count = 1;

				if (num >= 2) {
					isCompareOlly = false;
					System.out.println("Finish Checking");
				}
			}
		}
	}
}