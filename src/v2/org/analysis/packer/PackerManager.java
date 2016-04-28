package v2.org.analysis.packer;

import org.jakstab.Program;

import v2.org.analysis.algorithm.OTFThreadManager;
import v2.org.analysis.path.BPState;
import v2.org.analysis.statistics.FileProcess;

public class PackerManager {

	private boolean isPacked;
	private boolean isDetect = false;
	private boolean detectHeaderOnly = false;
	private TechniqueMonitor tMonitor;
//	private String detectViaHeader;
//	private String detectViaTechniques;
//	private String detectViaTechniquesFrequency;
//	private String backupDetectionState = "";
//	private String backupDetectionCountState = "";
	final static String TECHNIQUE_FREQUENCY = "data/data/techniquesFrequency.txt";
	final static String PACKER_SIGNATURE = "data/data/packerSignature.txt";
	final static String PACKER_TECHNIQUE = "data/data/packerTechnique.txt";
	final static String PACKER_DETECION_RESULT = "data/data/detectionResult.txt";
	final static String PACKER_DETECION_DETAIL = "data/data/detectionDetail.txt";
	final static String TECHNIQUE_POSITION = "data/data/positionDetail.txt";

	/**
	 * Singleton instance of {@link OTFThreadManager} class
	 */
	private static volatile PackerManager mInstance = null;

	public PackerManager() {
		isPacked = false;
		tMonitor = new TechniqueMonitor();
	}

	/**
	 * Get singleton instance of this class
	 * 
	 * @return {@link OTFThreadManager} instance
	 */
	public static synchronized PackerManager getInstance() {
		if (mInstance == null) {
			try {
				mInstance = new PackerManager();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return mInstance;
	}

//	private void packedByHeader(String packerName) {
//		String packedby = packerName;
//		System.out.println("HEADER: File is packed by " + packedby);
//		this.detectViaHeader = packedby;
//	}

//	public void packedByTechniques() {
//		String packedByTech = "";
//		String pTech = tMonitor.getPackerTechniques();
//		for (TechniqueSignature pSign : PackerConstants.pTechSign) {
//			if (this.isPackedWithViaTech(pSign.getTechiqueSignature(), pTech)) {
//				packedByTech += pSign.getPackerName();
//				packedByTech += " ";
//			}
//		}
//
//		if (packedByTech.equals("")) {
//			this.isPacked = false;
//			this.detectViaTechniques = pTech + "-" + "NONE";
//		} else {
//			this.isPacked = true;
//			this.detectViaTechniques = pTech + "-" + packedByTech;
//		}
//
//		System.out.println("TECHNIQUES: File is packed by " + packedByTech);
//	}

//	public void packedByTechniquesFrequency() {
//		String packedByTechFrequency = "";
//		String pTechFrequency = tMonitor.getTechniquesStatiscial();
//		for (PackerSign pSign : PackerConstants.pTechCountSign) {
//			if (this.isPackedWithViaTechCount(pSign.getPackerTechSign(), pTechFrequency)) {
//				packedByTechFrequency += pSign.getPackerName();
//				packedByTechFrequency += " ";
//			}
//		}
//
//		if (packedByTechFrequency.equals("")) {
//			this.isPacked = false;
//			this.detectViaTechniquesFrequency = pTechFrequency + "-" + "NONE";
//		} else {
//			this.isPacked = true;
//			this.detectViaTechniquesFrequency = pTechFrequency + "-" + packedByTechFrequency;
//		}
//
//		System.out.println("TECHNIQUES FREQUENCY: File is packed by " + packedByTechFrequency);
//	}

//	public boolean isPackedWithViaTech(String packerStr, String techStr) {
//		for (int i = 0; i < packerStr.length(); i++) {
//			if (packerStr.charAt(i) == '1' && techStr.charAt(i) == '0') {
//				return false;
//			}
//		}
//		return true;
//	}
//
//	public boolean isPackedWithViaTechCount(String packerStr, String techStr) {
//		String[] pStr = packerStr.split(":");
//		String[] tStr = techStr.split("\t");
//		for (int i = 0; i < pStr.length; i++) {
//			if (Integer.parseInt(tStr[i]) < Integer.parseInt(pStr[i])) {
//				return false;
//			}
//		}
//		return true;
//	}
//
//	public void updateBackupDetectionState(Program prog, OTFModelGeneration otfMG) {
//		BPCFG cfg = prog.getBPCFG();
//
//		Kernel32DLL.INSTANCE.SetCurrentDirectory(new WString(System.getProperty("user.dir")));
//
//		String fileName = prog.getFileName();
//		String viaHeader = this.detectViaHeader;
//		String viaTechniques = this.detectViaTechniques;
//		String nodes = String.format("%8d", cfg.getVertexCount());
//		String edges = String.format("%8d", cfg.getEdgeCount());
//		String times = Long.toString(prog.GetAnalyzingTime());
//		String convergence = otfMG.isCompleted() ? "x" : " ";
//
//		String result = fileName + "\t" + viaHeader + "\t";
//		if (viaTechniques == null) {
//			return;
//		}
//
//		for (int i = 0; i < viaTechniques.split("-")[0].length(); i++) {
//			if (viaTechniques.split("-")[0].charAt(i) == '1') {
//				result += ("x" + "\t");
//			} else {
//				result += (" " + "\t");
//			}
//		}
//		result += (viaTechniques.split("-")[1] + "\t" + nodes + "\t" + edges + "\t" + times + "\t" + convergence + "\t");
//
//		this.backupDetectionState = result;
//
//		// Update packer count
//		String viaTechniquesFrequency = this.detectViaTechniquesFrequency;
//		String resultC = fileName + "\t" + viaHeader + "\t";
//		resultC += viaTechniquesFrequency.split("-")[0];
//		resultC += viaTechniquesFrequency.split("-")[1] + "\t";
//		resultC += nodes + "\t" + edges + "\t" + times + "\t" + convergence + "\t";
//
//		this.backupDetectionCountState = resultC;
//	}

	public boolean fileIsPacked() {
		return this.isPacked;
	}

	public TechniqueMonitor getTechniques() {
		return tMonitor;
	}

//	public void setToLogFirst(Program prog) {
//		PackerUtility.setToLogFirst(prog, detectViaHeader);
//	}
//
//	public void setToLog(Program prog) {
//		PackerUtility.setToLog(prog, backupDetectionState, backupDetectionCountState);
//	}

	public void setDetectPacker(boolean b) {
		// TODO Auto-generated method stub
		isDetect = b;
	}

	public boolean isDetected() {
		// TODO Auto-generated method stub
		return isDetect;
	}

	public void updateChecking(BPState curState, Program program) {
		// TODO Auto-generated method stub
		if (isDetect) {
			tMonitor.updateChecking(curState, program);
		}
	}

	public void outputToFile(String fileName) {
		// TODO Auto-generated method stub
		FileProcess frequency = new FileProcess(TECHNIQUE_FREQUENCY);
		frequency.appendFile(fileName + "\t" + tMonitor.getFrequencyTechniques() + "\t" + System.currentTimeMillis());
		
		FileProcess signature = new FileProcess(PACKER_SIGNATURE);
		signature.appendFile(fileName + "\t" + tMonitor.getTechniqueOrder() + "\t" + System.currentTimeMillis());
		
		FileProcess pos = new FileProcess(TECHNIQUE_POSITION);
		pos.appendFile(fileName + "\t" + tMonitor.getPosOrder() + "\t" + System.currentTimeMillis());
		
//		FileProcess technique = new FileProcess(PACKER_TECHNIQUE);
//		technique.appendFile(fileName + "\t" + tMonitor.getPackerTechniques() + "\t" + System.currentTimeMillis());		
		
		FileProcess result = new FileProcess(PACKER_DETECION_RESULT);
		result.appendFile(fileName + "\t" + tMonitor.getDetectionResult() + "\t" + System.currentTimeMillis());
		
		FileProcess detail = new FileProcess(PACKER_DETECION_DETAIL);
		detail.appendFile(fileName + "\n" + tMonitor.getDetectionDetail());
	}
	
	public void getHeaderDetection(String fileName) {
		// TODO Auto-generated method stub		
		FileProcess result = new FileProcess(PACKER_DETECION_RESULT);
		result.appendFile(fileName + "\t" + tMonitor.getDetectionResult() + "\t" + System.currentTimeMillis());
	}

	public void checkAPIName(String api, long value) {
		// TODO Auto-generated method stub
		if (isDetect) {
			tMonitor.checkAPIName(api, value);
		}
	}

	public void setIndirectJumpTechnique(long value) {
		// TODO Auto-generated method stub
		if (isDetect) {
			tMonitor.setIndirectJumpTechnique(value);;
		}
	}

	public void setSEHTechnique(long value) {
		// TODO Auto-generated method stub
		if (isDetect) {
			tMonitor.setSEHTechnique(value);
		}
	}

	public void setOverlappingFuction(String instName, long curAddr, long nextAddr) {
		// TODO Auto-generated method stub
		if (isDetect) {
			tMonitor.setOverlapping(instName, curAddr, nextAddr);
		}
	}

	public void setCodeChunking(String instName, long curAddr, long nextAddr) {
		// TODO Auto-generated method stub
		if (isDetect) {
			tMonitor.setCodeChunking(instName, curAddr, nextAddr);
		}
	}

	public void setDetectHeader(boolean b) {
		// TODO Auto-generated method stub
		detectHeaderOnly = b;
	}

	public boolean isDetectHeaderOnly() {
		return detectHeaderOnly;
	}
}
