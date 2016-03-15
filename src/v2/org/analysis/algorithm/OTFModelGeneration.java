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
import v2.org.analysis.path.BPPath;
import v2.org.analysis.path.BPState;
import v2.org.analysis.path.PathList;
import v2.org.analysis.transition_rule.X86TransitionRule;
import v2.org.analysis.value.Formulas;

public class OTFModelGeneration implements Algorithm {

	// private static final Logger logger =
	// Logger.getLogger(CPAAlgorithm.class);
	//private static long maxTimeProgam = 3600000;
	//private static long maxTimePath = 1500000;
	//private static long bkTime = 2700000;
	private static long outTime = 180000;
	// For Debug
//	private int num = 1, loopCount = 1;
//	private boolean isCompareOlly = true, isChecked = false;
	//isRestored = true;
//	private long count = 1;
//	private AbsoluteAddress checkedAddr = new AbsoluteAddress(0);
//	private AbsoluteAddress endAddr = new AbsoluteAddress(0);
//	private String fileName = "";
//	private FileProcess compareOllyResult = null;
//	private OllyComparisonV2 ollyCompare = null;

	private final Program program;

	private static boolean detectPacker = false;
	private long overallStartTime;
//	private int countOEP = 0;
	
	public OTFModelGeneration(Program program) {
		super();
		this.program = program;
	}

	@Override
	public void run() {
		// --------------------------
		//FileProcess fileState = new FileProcess("data/data/stateValue.txt");
		//FileProcess bkFile = new FileProcess("data/data/restore.txt");
//		fileName = "out_" + Program.getProgram().getFileName() + "_";
//		int numAddStop = 0;
		//fileName = "out_themida_";

		//fileState.clearContentFile();
		//bkFile.clearContentFile();
		overallStartTime = System.currentTimeMillis();
//		long overallStartTemp = overallStartTime;
		// BE-PUM algorithm
		System.out.println("Starting On-the-fly Model Generation algorithm.");
		program.getResultFileTemp().appendInLine('\n' + program.getFileName() + '\t');
		
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

		/*if (Program.getProgram().getFileName().equals("api_test_v2.3_lvl1.exe") 
				&& isRestored) {
			System.out.println("Restore State from File.");
			FileProcess reFile = new FileProcess("data/data/restoreState.txt");
			pathList = restoreState(reFile);
			// bkFile.clearContentFile();
			System.out.println("Finished restoring state!");
		}*/

		// Update at first -----------------------------
		TIB.setBeUpdated(true);
		TIB.updateTIB(curState);
		
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
		
		synchronized (OTFThreadManager.getInstance()) {
			try {
				OTFThreadManager.getInstance().check(this, pathList);
				OTFThreadManager.getInstance().wait();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
//		while (!pathList.isEmpty()) {
//
//			path = pathList.remove(pathList.size() - 1);
//			curState = path.getCurrentState();
//			
//			// long overallStartTimePath = System.currentTimeMillis();
//			while (true) {
//				
//				/*
//				if (curState != null && curState.getLocation() != null)
//				{
//					if (curState.getLocation().toString().contains("401000"))
//					{
//						countOEP++;
//						//if (countOEP >= 2)
//						{
//							program.getDetection().packedBy();
//							this.detectPacker = false;
//						}
//					}
//				}
//				*/
//			
//				////////////////////////////////// VIA OTF ////////////////////////////////////////
//				if (this.detectPacker)
//				{
//					program.getDetection().getTechniques().updateChecking(curState, program);
//				}
//				///////////////////////////////////////////////////////////////////////////////////
//				 
//				long overallEndTimeTemp = System.currentTimeMillis();
//				// Output file each 60s
//				if (overallEndTimeTemp - overallStartTemp > outTime) {
//
//					backupState(curState);
//					overallStartTemp = overallEndTimeTemp;
//					
//					////////////////////////////////////////////////////
//					// Write to packer result file after each 60s
//					if (this.detectPacker)
//					{
//						program.SetAnalyzingTime(System.currentTimeMillis()
//						- overallStartTime); 
//						program.getDetection().packedByTechniques();
//						program.getDetection().packedByTechniquesFrequency();
//						program.getDetection().updateBackupDetectionState(program, this);
//						program.getDetection().setToLog(program);
//					}					
//					
//					if (inst != null && inst.getName().contains("addb")
//							&& inst.getOperand(0) != null && inst.getOperand(0).toString().contains("eax")
//							&& inst.getOperand(1) != null && inst.getOperand(1).toString().contains("al")) {
//						numAddStop ++;
//					}
//					
//					if (numAddStop > 1) {
//						program.getStopFile().appendFile(program.getFileName());
//						break;				
//					}
//					////////////////////////////////////////////////////
//				}
//
//				if (path.isStop()) {
//					break;
//				}
//
//				inst = curState.getInstruction();
//				location = curState.getLocation();	
//				
////				compareOlly(curState);
////				if (location != null && location.toString().contains("404091")) {
////					System.out.println("Debug");
////				}
//								
//				// PHONG: 20150506 - Update TIB
//				// --------------------------------------
//				TIB.updateTIB(curState);
////				TIB.updateChecking(curState);
//				// --------------------------------------
//				
//				if (inst == null || location == null) {
//					break;
//				}
//				path.addTrace(curState.getLocation());
//
//				if (inst instanceof X86CondJmpInstruction) {
//					rule.getNewState((X86CondJmpInstruction) inst, path, pathList);
//					if (!curState.checkFeasiblePath()) {
//						path.destroy();
//						break;
//					}
//				} else {
//					rule.getNewState(path, pathList, true);
//				}
//				
//				if (this.detectPacker && isOEP(curState.getLocation(), program.getFileName())) {
//					program.SetAnalyzingTime(System.currentTimeMillis()
//							- overallStartTime); 
//					program.getDetection().packedByTechniques();
//					program.getDetection().packedByTechniquesFrequency();
//					program.getDetection().updateBackupDetectionState(program, this);
//					program.getDetection().setToLog(program);
//					
//					this.detectPacker = false;
//				}
//			}
//		}
		// PHONG - 20150724
//		System.out.println("================PACKER DETECTION VIA OTF======================");
//		program.getDetection().packedByTechniques();
//		program.getDetection().packedByTechniquesFrequency();
//		System.out.println("==============================================================");
	}

	public class OTFThread extends OTFThreadBase {
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

			while (!pathList.isEmpty()) {
				path = pathList.remove(pathList.size() - 1);
				curState = path.getCurrentState();
				
				// long overallStartTimePath = System.currentTimeMillis();
				while (true) {
					if (!curState.checkFeasiblePath()) {
						path.destroy();
						break;
					}
					/*
					if (curState != null && curState.getLocation() != null)
					{
						if (curState.getLocation().toString().contains("401000"))
						{
							countOEP++;
							//if (countOEP >= 2)
							{
								program.getDetection().packedBy();
								this.detectPacker = false;
							}
						}
					}
					*/
				
//					////////////////////////////////// VIA OTF ////////////////////////////////////////
//					if (detectPacker)
//					{
//						program.getDetection().getTechniques().updateChecking(curState, program);
//					}
//					///////////////////////////////////////////////////////////////////////////////////
					 
					long overallEndTimeTemp = System.currentTimeMillis();
					// Output file each 60s
					if (overallEndTimeTemp - overallStartTemp > outTime) {
	
						backupState(curState);
						overallStartTemp = overallEndTimeTemp;
						
						////////////////////////////////////////////////////
						// Write to packer result file after each 60s
//						if (detectPacker)
//						{
//							program.SetAnalyzingTime(System.currentTimeMillis()
//							- overallStartTime); 
//							program.getDetection().packedByTechniques();
//							program.getDetection().packedByTechniquesFrequency();
//							program.getDetection().updateBackupDetectionState(program, OTFModelGeneration.this);
//							program.getDetection().setToLog(program);
//						}
						
//						if (inst != null && inst.getName().contains("addb")
//								&& inst.getOperand(0) != null && inst.getOperand(0).toString().contains("eax")
//								&& inst.getOperand(1) != null && inst.getOperand(1).toString().contains("al")) {
//							program.getStopFile().appendFile(program.getFileName());
//							break;
//						}
						
//						if (numAddStop > 1) {
//							program.getStopFile().appendFile(program.getFileName());
//							break;				
//						}
						////////////////////////////////////////////////////
					}
	
					if (path.isStop()) {
						break;
					}
	
					inst = curState.getInstruction();
					location = curState.getLocation();					
					
					if (inst != null && inst.getName().contains("addb")
							&& inst.getOperand(0) != null && inst.getOperand(0).toString().contains("eax")
							&& inst.getOperand(1) != null && inst.getOperand(1).toString().contains("al")) {
						program.getStopFile().appendFile(program.getFileName());
						break;
					}

//					compareOlly(curState);
//					if (location != null && location.toString().contains("408ac6")) {
//							System.out.println("Debug instruction at " + location + ": " + inst.getName() + " " 
//									+ inst.getOperand(0).toString());
//							System.out.println(curState.getEnvironement().getRegister().toString());
//							System.out.println(path.getTrace().toString());
//					}
									
					// PHONG: 20150506 - Update TIB
					// --------------------------------------
					TIB.updateTIB(curState);
//							TIB.updateChecking(curState);
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
					
//					if (OTFModelGeneration.detectPacker && isOEP(curState.getLocation(), program.getFileName())) {
//						program.SetAnalyzingTime(System.currentTimeMillis()
//								- overallStartTime); 
//						program.getDetection().packedByTechniques();
//						program.getDetection().packedByTechniquesFrequency();
//						program.getDetection().updateBackupDetectionState(program, OTFModelGeneration.this);
//						program.getDetection().setToLog(program);
//						
//						OTFModelGeneration.detectPacker = false;
//					}
					
					
					///////// AFTER LOOP ///////////
					this.afterLoop(OTFModelGeneration.this, pathList);
					if (this.isStopCurrentPath(curState)) {
						path.destroy();
						break;
					}
				}
			}
		}
	}
	
//	private boolean isOEP(AbsoluteAddress location, String fileName) {
//		// TODO Auto-generated method stub
//		return (location != null) && (fileName.contains("api_test") && location.toString().contains("401000")
//				|| fileName.contains("bof") && location.toString().contains("401000")
//				|| fileName.contains("demo1") && location.toString().contains("401000")
//				|| fileName.contains("demo2") && location.toString().contains("401000")
//				|| fileName.contains("Aztec") && location.toString().contains("401000")
//				|| fileName.contains("Benny") && location.toString().contains("401000")
//				|| fileName.contains("Cabanas") && location.toString().contains("401000")
//				|| fileName.contains("Adson") && location.toString().contains("401000")
//				|| fileName.contains("api_testv2") && location.toString().contains("401131")
//				);
//	}

	private void backupState(BPState curState) {
		// TODO Auto-generated method stub
		program.generageCFG("/asm/cfg/" + program.getFileName() + "_test");
		program.getResultFileTemp().appendInLine("\t"
						+ String.format("%8dms", (System.currentTimeMillis() - overallStartTime)) + "\t"
						+ String.format("%8d", program.getBPCFG().getVertexCount()) + "\t" + String.format("%8d", program.getBPCFG().getEdgeCount())
				);
	}

//	private void backupStateAll(BPState curState, FileProcess bkFile) {
//		// TODO Auto-generated method stub
//		String result = "Address=" + curState.getLocation().toString() + "\n";
//		result += "Register:" + curState.getEnvironement().getRegister() + "\n";
//		result += "Flag:" + curState.getEnvironement().getFlag() + "\n";
//		result += "Memory:" + curState.getEnvironement().getMemory() + "\n";
//		result += "SEH:" + curState.getEnvironement().getSystem().getSEHHandler().toString() + "\n";
//		result += "*************************************************************";
//
//		bkFile.appendFile(result);
//	}

//	private List<BPPath> restoreState(FileProcess bkFile) {
//		// TODO Auto-generated method stub
//		Environment env = new Environment();
//		AbsoluteAddress location = getContinuosPoint(bkFile);
//		getEnvironment(env, bkFile);		
//		Instruction inst = Program.getProgram().getInstruction(location, env);
//		List<BPPath> pathList = new ArrayList<BPPath>();
//		// BPVertex startNode = null;
//		// BPState curState = null;
//		// BPPath path = null;
//		// startNode = new BPVertex(location, inst);
//		// startNode.setType(0);
//		// cfg.insertVertex(startNode);
//		BPState curState = new BPState(env, location, inst);
//		BPPath path = new BPPath(curState, new PathList(), new Formulas());
//		path.setCurrentState(curState);
//
//		BPCFG cfg = Program.getProgram().getBPCFG();
//		BPVertex startNode = null;
//		startNode = new BPVertex(location, inst);
//		startNode.setType(0);
//		cfg.removeVertex(0);
//		cfg.insertVertex(startNode);
//		pathList.add(path);
//
//		return pathList;
//	}

//	private AbsoluteAddress getContinuosPoint(FileProcess file) {
//		// TODO Auto-generated method stub
//		int t = 0;
//		while (true) {
//			String temp = file.getLineAt(t);
//
//			if (temp == null || temp == "") {
//				return null;
//			}
//
//			if (temp.contains("Address")) {
//				temp = temp.substring(temp.indexOf("x") + 1, temp.length());
//				return new AbsoluteAddress(Long.parseLong(temp, 16));
//			}
//			t++;
//		}
//	}
//
//	private void getEnvironment(Environment env, FileProcess file) {
//		// TODO Auto-generated method stub
//		int t = 0;
//		boolean setReg = false, setFlag = false, setMem = false;
//		while (true) {
//			String temp = file.getLineAt(t);
//
//			if (temp == null || temp == "") {
//				return;
//			} else if (temp.contains("Register")) {
//				temp = temp.substring(temp.indexOf(":") + 1, temp.length());
//				String[] reg = temp.split(",");
//				for (int i = 0; i < reg.length; i++) {
//					reg[i] = reg[i].replace(" ", "");
//					String r[] = reg[i].split("=");
//
//					env.getRegister().setRegisterValue(r[0], new LongValue(Long.parseLong(r[1], 16)));					
//				}
//				setReg = true;
//			} else if (temp.contains("Flag")) {
//				temp = temp.substring(temp.indexOf(":") + 1, temp.length());
//				String[] reg = temp.split(",");
//				for (int i = 0; i < reg.length; i++) {
//					reg[i] = reg[i].replace(" ", "");
//					String r[] = reg[i].split("=");
//
//					if (r[1].toLowerCase().contains("true")) {
//						env.getFlag().setFlagValue(r[0], new BooleanValue(true));
//					} else {
//						env.getFlag().setFlagValue(r[0], new BooleanValue(false));
//					}
//				}
//				setFlag = true;
//			} else if (temp.contains("Memory")) {
//				temp = temp.substring(temp.indexOf(":") + 1, temp.length());
//				String[] reg = temp.split(",");
//				for (int i = 0; i < reg.length; i++) {
//					reg[i] = reg[i].replace(" ", "");
//					String r[] = reg[i].split("=");
//					String addr = r[0].replace("0x", "");
//					long x = Long.parseLong(addr, 16);
//					byte y = (byte) Long.parseLong(reduce(r[1], 8), 16);
//					env.getMemory().setByteMemoryValue(x, new LongValue(y));
//				}
//				setMem = true;
//			}  
//			t++;
//			
//			if (setReg && setFlag && setMem) {
//				return;
//			}
//		}
//	}

//	private String reduce(String str, int i) {
//		// TODO Auto-generated method stub
//		String ret = str;
//		if (i == 8) {
//			if (str.length() > 2) {
//				ret = str.substring(str.length()-2);
//			}
//		}
//		
//		return ret;
//	}

//	private void backupState(BPState curState, FileProcess fileState) {
//		// TODO Auto-generated method stub
//		/*String result = "Address=" + curState.getLocation() + "\n";
//		result += "Register: " + curState.getEnvironement().getRegister() + "\n";
//		result += "Flag: " + curState.getEnvironement().getFlag() + "\n";
//		result += "Stack: " + ((StackV2) curState.getEnvironement().getStack()).toString() + "\n";
//		result += "SEH: " + curState.getEnvironement().getSystem().getSEHHandler().toString() + "\n";
//		result += "*************************************************************";
//
//		// bkFile.appendFile(result);
//		fileState.appendFile(result);*/
//
//		program.generageCFG("/asm/cfg/" + program.getFileName() + "_test");
//		program.getResultFileTemp().appendInLine(
//				program.getDetailTechnique() + " Nodes:" + program.getBPCFG().getVertexCount() + " Edges:"
//						+ program.getBPCFG().getEdgeCount() + " ");
//		//System.out.println();
//	}

//	private void compareOlly(BPState state) {
//		// TODO Auto-generated method stub
//		// -------------------------------------------------------------------
//		// OLLY DEBUG HERE
//		// checkedAddr = new AbsoluteAddress(0x40818C);
//		// endAddr = new AbsoluteAddress(0x4085B2);
//		if (isCompareOlly) {
//			AbsoluteAddress location = state.getLocation();
//			Environment env = state.getEnvironement();
//			if (ollyCompare == null) {
//				long memoryStartAddr = 0x12FFAC;
//				long memoryEndAddr = 0x12FFBC;
//				long stackIndex = 0xc;
//				System.out.println("Read file Olly " + "data/data/" + fileName + "" + num + ".txt");
//				ollyCompare = new OllyComparisonV2("data/data/" + fileName + "" + num + ".txt", memoryStartAddr,
//						memoryEndAddr, stackIndex);
//				// ollyCompare = new OllyCompare("asm/olly/" + fileName +
//				// ".txt", memoryStartAddr,
//				// memoryEndAddr, stackIndex);
//				ollyCompare.importOllyData(checkedAddr, endAddr);
//				count = ollyCompare.getFirstCount();
//				System.out.println("Finish reading!");
//			}
//
//			if (compareOllyResult == null) {
//				compareOllyResult = new FileProcess("data/data/compareWithOlly_" + fileName + "" + num + ".txt");
//				compareOllyResult.clearContentFile();
//			}
//
//			if (location != null && location.getValue() == checkedAddr.getValue()) {
//				isChecked = true;
//			}
//
//			if (isChecked & location != null && !ollyCompare.isFinished()) {
//					//&& (location.getValue() != endAddr.getValue() || loopCount != ollyCompare.getLoopCount())) {
//				// System.out.println("Loop = " + count + " , Address = " +
//				// location.toString() + ":");
//				compareOllyResult.appendFile("Loop = " + count + " , Address = " + location.toString() + ":");
//				// COMPARE HERE
//				String result = ollyCompare.compareBEPUM(env, count, location);
//				count = ollyCompare.getNextCheck();
//				// if (result.contains("Unequal")) {
//				// System.out.println("Bug");
//				// }
//				compareOllyResult.appendFile(result);
//				// System.out.println("*************************************************************");
//				compareOllyResult.appendFile("*************************************************************");
//				loopCount++;
//			}
//
//			if (isChecked && location != null 
//					&& ollyCompare.isFinished()) {
//					//&& location.getValue() == endAddr.getValue()
//					//&& loopCount == ollyCompare.getLoopCount()) {
//				System.out.println("Stop Check: " + fileName + "" + num + ".txt");
//				ollyCompare = null;
//				loopCount = 1;
//				compareOllyResult = null;
//				num++;
//				isChecked = false;
//				count = 1;
//
//				if (num >= 2) {
//					isCompareOlly = false;
//					System.out.println("Finish Checking");
//				}
//			}
//		}
//	}
//
//	private void debugProgram(AbsoluteAddress location, BPState curState, FileProcess fileState, FileProcess bkFile) {
//		// TODO Auto-generated method stub
//		String fileName = Program.getProgram().getFileName();
//		if (location != null && (location.toString().contains("FFFFFFFFF")
//		// ******************************************
//		// Virus.Win32.HLLO.Momac.a
//				|| (fileName.equals("Virus.Win32.HLLO.Momac.a") && (location.toString().contains("40130c")))
//				// ******************************************
//				// Email-Worm.Win32.Atak.e
//				|| (fileName.equals("Email-Worm.Win32.Atak.e") && (location.toString().contains("404cf0")))
//				// ******************************************
//				// Virus.Win32.ZMist
//				|| (fileName.equals("Virus.Win32.ZMist") && (location.toString().contains("402d01")))
//				// ******************************************
//				// api_test_pespin.exe
//				|| (fileName.equals("api_test_pespin.exe") && (location.toString().contains("40669e")))
//				// ******************************************
//				// api_test_yoda.exe
//				|| (fileName.equals("api_test_yoda.exe") && (location.toString().contains("4045fb")))
//				// ******************************************
//				// api_test_vmprotect.exe
//				|| (fileName.equals("api_test_vmprotect.exe") && (
//				// location.toString().contains("4c11b0")
//				location.toString().contains("4b9da5")))
//				// ******************************************
//				// api_test_yc1.2.exe
//				|| (fileName.equals("api_test_v2.3_lvl1.exe") && 
//				(location.toString().contains("00000000001")
//				|| location.toString().contains("424a41") // API GetLocalTime
//				|| location.toString().contains("436daf") // API CreateFile
//				|| location.toString().contains("436ef8") // API CreateFile				
//				|| location.toString().contains("436bc7") // RET
//				|| location.toString().contains("437b16") // After STI
//				|| location.toString().contains("43ce7c") // After STI 
//				|| location.toString().contains("43f722") // API GetVersionExA
//				|| location.toString().contains("43d397") // API GetCommandLine				
//				
//				|| location.toString().contains("44228a") // Target
//				))
//				// ******************************************
//				// api_test_aspack.exe
//				|| (fileName.equals("api_test_aspack.exe") && (location.toString().contains("4043c2")
//				// || location.toString().contains("408184")
//				))
//				// ******************************************
//				// api_test_aspack.exe
//				|| (fileName.equals("Virus.Win32.Cabanas.2999") && (location.toString().contains("40497b")
//				// || location.toString().contains("408184")
//				))
//				// ******************************************
//				// Email-Worm.Win32.Apbost.c
//				|| (fileName.equals("Email-Worm.Win32.Apbost.c") && (location.toString().contains("4046e8")
//				// || location.toString().contains("408184")
//				))
//		// ******************************************
//				// ******************************************
//				// Email-Worm.Win32.Navidad.b
//				|| (fileName.equals("Email-Worm.Win32.Navidad.b") && 
//						(location.toString().contains("409239")
//				|| location.toString().contains("409227")
//				))
//				
//				|| (fileName.equals("Virus.Win32.Adson.1559") && 
//						(location.toString().contains("4094b3")
//						//|| location.toString().contains("402058")
//				// || location.toString().contains("408184")
//				))
//				
//				|| (fileName.equals("Virus.Win32.HLLP.Delf.d") && 
//						(location.toString().contains("401324")
//						//|| location.toString().contains("402058")
//				// || location.toString().contains("408184")
//				))
//				
//				|| (fileName.equals("Virus.Win32.HLLC.Asive") && 
//				(location.toString().contains("401130")
//						//|| location.toString().contains("402058")
//				// || location.toString().contains("408184")
//				))
//
//		// Virus.Win32.Aztec.01
//				|| (fileName.equals("Virus.Win32.Aztec.01") && 
//				(location.toString().contains("40134e")
//				|| location.toString().contains("401312")
//				|| location.toString().contains("40106c")
//				)))) {
//			//if (curState.getEnvironement().getRegister().getRegisterValue("eax").toString().equals("7c800c00"))			
//			backupState(curState, fileState);
//			//backupStateAll(curState, bkFile);
//			//program.generageCFG(program.getAbsolutePathFile() + "_test");
//			//program.generageCFG("/asm/cfg/" + program.getFileName() + "_test");
//			System.out.println("Debug at:" + location.toString());
//		}
//		/*
//		 * if (location != null && location.toString().contains("0040481b") &&
//		 * Program.getProgram().getFileName()
//		 * .equals("Virus.Win32.Cabanas.2999")) { // Value ecx =
//		 * env.getRegister().getRegisterValue("ecx"); String s1 =
//		 * curState.getEnvironement().getMemory() .getText(this, 4215362, 650);
//		 * System.out.println("Decrypted String: " + s1); }
//		 */
//	}

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
}