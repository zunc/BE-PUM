package v2.org.analysis.packer;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.jakstab.Program;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import v2.org.analysis.packer.techniques.AntiDebugging;
import v2.org.analysis.packer.techniques.Checksumming;
import v2.org.analysis.packer.techniques.CodeChunking;
import v2.org.analysis.packer.techniques.HardwareBPX;
import v2.org.analysis.packer.techniques.IndirectJump;
import v2.org.analysis.packer.techniques.ObfuscatedConstant;
import v2.org.analysis.packer.techniques.OverlappingBlock;
import v2.org.analysis.packer.techniques.OverlappingFunction;
import v2.org.analysis.packer.techniques.Overwriting;
import v2.org.analysis.packer.techniques.PackerTechnique;
import v2.org.analysis.packer.techniques.PackingUnpacking;
import v2.org.analysis.packer.techniques.SEH;
import v2.org.analysis.packer.techniques.StolenBytes;
import v2.org.analysis.packer.techniques.TimingCheck;
import v2.org.analysis.packer.techniques.TwoSpecialAPIs;
import v2.org.analysis.path.BPState;

public class TechniqueMonitor {
	private ArrayList<PackerTechnique> techList;
	// private PackerPatterns pPattern;
	// private boolean USING_COUNT;
	// private static PackerCounter pCounter;
	private String techOrder;
	private String headerDetectionResult = "", techniqueDetectionResult = "";

	public static final String HEADER_SIGNATURE = "data/data/PackerSignature/header_signature.json";
	public static final String TECHNIQUE_SIGNATURE = "data/data/PackerSignature/technique_signature.json";
	public static final String PACKER_NAME_TAG = "PACKER_NAME";
	public static final String VERSION_TAG = "VERSION";
	public static final String ENTRY_POINT_TAG = "IS_ENTRY_POINT";
	public static final String SIGNATURE_TAG = "SIGNATURE";

	private ArrayList<HeaderSignature> getHeaderSignature() {
		ArrayList<HeaderSignature> hPacker = new ArrayList<HeaderSignature>();

		JSONParser jParser = new JSONParser();
		try {
			JSONArray jArray = (JSONArray) jParser.parse(new FileReader(HEADER_SIGNATURE));
			for (Object o : jArray) {

				JSONObject pSignature = (JSONObject) o;

				String packerName = (String) pSignature.get(PACKER_NAME_TAG);
				String packerVersion = (String) pSignature.get(VERSION_TAG);
				boolean isEntryPoint = ((String) pSignature.get(ENTRY_POINT_TAG)).contains("YES");
				String[] sArr = parseSignature((String) pSignature.get(SIGNATURE_TAG));

				HeaderSignature pHeader = new HeaderSignature(packerName, packerVersion, isEntryPoint, sArr);
				hPacker.add(pHeader);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return hPacker;
	}

	private ArrayList<TechniqueSignature> getTechniqueSignature() {
		ArrayList<TechniqueSignature> tPacker = new ArrayList<TechniqueSignature>();

		JSONParser jParser = new JSONParser();
		try {
			JSONArray jArray = (JSONArray) jParser.parse(new FileReader(TECHNIQUE_SIGNATURE));
			for (Object o : jArray) {

				JSONObject pSignature = (JSONObject) o;

				String packerName = (String) pSignature.get(PACKER_NAME_TAG);
				String packerVersion = (String) pSignature.get(VERSION_TAG);
				// boolean isEntryPoint = ((String)
				// pSignature.get(ENTRY_POINT_TAG)).contains("YES");
				String[] sArr = parseSignature((String) pSignature.get(SIGNATURE_TAG));
//				StringBuilder str = new StringBuilder();
//				str.
				TechniqueSignature pHeader = new TechniqueSignature(packerName, packerVersion, sArr.toString());
				tPacker.add(pHeader);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return tPacker;
	}

	private String[] parseSignature(String signature) {
		String[] sArr = {};
		ArrayList<String> sList = new ArrayList<String>();
		if (signature.length() % 2 != 0) {
			signature += "?";
		}
		for (int i = 0; i < signature.length(); i++) {
			if (i % 2 == 0) {
				sList.add(signature.substring(i, i + 2));
			}
		}
		return sList.toArray(sArr);
	}

	private PackerTechnique getTechnique(int id) {
		for (PackerTechnique pTech : techList) {
			if (pTech.getID() == id) {
				return pTech;
			}
		}
		return null;
	}

	public TechniqueMonitor() {
		techList = new ArrayList<>();
		techList.add(new AntiDebugging());
		techList.add(new Checksumming());
		techList.add(new CodeChunking());
		techList.add(new IndirectJump());
		techList.add(new ObfuscatedConstant());
		techList.add(new OverlappingBlock());
		techList.add(new OverlappingFunction());
		techList.add(new Overwriting());
		techList.add(new PackingUnpacking());
		techList.add(new SEH());
		techList.add(new StolenBytes());
		techList.add(new TimingCheck());
		techList.add(new TwoSpecialAPIs());
		techList.add(new HardwareBPX());
		techOrder = "";
		// USING_COUNT = true;
		// pPattern = new PackerPatterns();
		// pCounter = new PackerCounter();
	}

	public void updateChecking(BPState curState, Program program) {
		if (curState != null) {
			// BEGIN TO STATISTIC
			// pCounter.setCountingState(curState, program);
			// pCounter.Execute(this.USING_COUNT);
			// pPattern.setCheckingState(pCounter);
			// this.checkingState();
			for (PackerTechnique pTech : techList) {
				if (pTech.check(curState, program)) {
					techOrder += pTech.getID()+"_";
					return;
				}
			}
		}
	}

	public String getPackerTechniques() {
		String techniques = "";

		techniques += ((getTechnique(PackerConstants.ANTI_DEBUGGING).hasTechnique()) ? "1" : "0") + "\t";
		techniques += ((getTechnique(PackerConstants.CHECKSUMMING).hasTechnique()) ? "1" : "0") + "\t";
		techniques += ((getTechnique(PackerConstants.CODE_CHUNKING).hasTechnique()) ? "1" : "0") + "\t";
		techniques += ((getTechnique(PackerConstants.INDIRECT_JUMP).hasTechnique()) ? "1" : "0") + "\t";
		techniques += ((getTechnique(PackerConstants.OBFUSCATED_CONST).hasTechnique()) ? "1" : "0") + "\t";
		techniques += ((getTechnique(PackerConstants.OVERLAPPING_BLOCK).hasTechnique()) ? "1" : "0") + "\t";
		techniques += ((getTechnique(PackerConstants.OVERLAPPING_FUNC).hasTechnique()) ? "1" : "0") + "\t";
		techniques += ((getTechnique(PackerConstants.OVERWRITING).hasTechnique()) ? "1" : "0") + "\t";
		techniques += ((getTechnique(PackerConstants.PACKING_UNPACKING).hasTechnique()) ? "1" : "0") + "\t";
		techniques += ((getTechnique(PackerConstants.SEH).hasTechnique()) ? "1" : "0") + "\t";
		techniques += ((getTechnique(PackerConstants.STOLEN_BYTES).hasTechnique()) ? "1" : "0") + "\t";
		techniques += ((getTechnique(PackerConstants.TIMING_CHECK).hasTechnique()) ? "1" : "0") + "\t";
		techniques += ((getTechnique(PackerConstants.TWO_APIS).hasTechnique()) ? "1" : "0") + "\t";
		techniques += (getTechnique(PackerConstants.HARDWARE_BPX).hasTechnique()) ? "1" : "0";

		return techniques;
	}

	// public String outputDetailTechniques () {
	// String techniques = "";
	//
	// techniques +=
	// ((getTechnique(PackerConstants.ANTI_DEBUGGING).hasTechnique())? "1" :
	// "0") + "\t";
	// techniques +=
	// ((getTechnique(PackerConstants.CHECKSUMMING).hasTechnique())? "1" : "0")
	// + "\t";
	// techniques +=
	// ((getTechnique(PackerConstants.CODE_CHUNKING).hasTechnique())? "1" : "0")
	// + "\t";
	// techniques +=
	// ((getTechnique(PackerConstants.INDIRECT_JUMP).hasTechnique())? "1" : "0")
	// + "\t";
	// techniques +=
	// ((getTechnique(PackerConstants.OBFUSCATED_CONST).hasTechnique())? "1" :
	// "0") + "\t";
	// techniques +=
	// ((getTechnique(PackerConstants.OVERLAPPING_BLOCK).hasTechnique())? "1" :
	// "0") + "\t";
	// techniques +=
	// ((getTechnique(PackerConstants.OVERLAPPING_FUNC).hasTechnique())? "1" :
	// "0") + "\t";
	// techniques +=
	// ((getTechnique(PackerConstants.OVERWRITING).hasTechnique())? "1" : "0") +
	// "\t";
	// techniques +=
	// ((getTechnique(PackerConstants.PACKING_UNPACKING).hasTechnique())? "1" :
	// "0") + "\t";
	// techniques += ((getTechnique(PackerConstants.SEH).hasTechnique())? "1" :
	// "0") + "\t";
	// techniques +=
	// ((getTechnique(PackerConstants.STOLEN_BYTES).hasTechnique())? "1" : "0")
	// + "\t";
	// techniques +=
	// ((getTechnique(PackerConstants.TIMING_CHECK).hasTechnique())? "1" : "0")
	// + "\t";
	// techniques += ((getTechnique(PackerConstants.TWO_APIS).hasTechnique())?
	// "1" : "0") + "\t";
	// techniques +=
	// (getTechnique(PackerConstants.HARDWARE_BPX).hasTechnique())? "1" : "0";
	//
	// return techniques;
	// }

	public String getFrequencyTechniques() {
		String techniques = "";

		techniques += getTechnique(PackerConstants.ANTI_DEBUGGING).getFrequency() + "\t";
		techniques += getTechnique(PackerConstants.CHECKSUMMING).getFrequency() + "\t";
		techniques += getTechnique(PackerConstants.CODE_CHUNKING).getFrequency() + "\t";
		techniques += getTechnique(PackerConstants.INDIRECT_JUMP).getFrequency() + "\t";
		techniques += getTechnique(PackerConstants.OBFUSCATED_CONST).getFrequency() + "\t";
		techniques += getTechnique(PackerConstants.OVERLAPPING_BLOCK).getFrequency() + "\t";
		techniques += getTechnique(PackerConstants.OVERLAPPING_FUNC).getFrequency() + "\t";
		techniques += getTechnique(PackerConstants.OVERWRITING).getFrequency() + "\t";
		techniques += getTechnique(PackerConstants.PACKING_UNPACKING).getFrequency() + "\t";
		techniques += getTechnique(PackerConstants.SEH).getFrequency() + "\t";
		techniques += getTechnique(PackerConstants.STOLEN_BYTES).getFrequency() + "\t";
		techniques += getTechnique(PackerConstants.TIMING_CHECK).getFrequency() + "\t";
		techniques += getTechnique(PackerConstants.TWO_APIS).getFrequency() + "\t";
		techniques += getTechnique(PackerConstants.HARDWARE_BPX).getFrequency() + "\t";

		return techniques;
	}

	// public String getTechniquesStatiscial () {
	// return pCounter.getInfo();
	// }
	//
	// public void setCount (boolean isCounting) {
	// this.USING_COUNT = isCounting;
	// }

	public String getTechniqueOrder() {
		return techOrder;
	}

	public void setTechniqueOrder(String techOrder) {
		this.techOrder = techOrder;
	}

	public String getDetectionResult() {
		// TODO Auto-generated method stub
		if (headerDetectionResult == "" || headerDetectionResult == "NONE") {
			headerDetectionResult = detectViaHeaderSignature();
		}

		if (techniqueDetectionResult == "" || headerDetectionResult == "NONE") {
			techniqueDetectionResult = detectViaTechniqueSignature();
		}

		return headerDetectionResult + "\t" + techniqueDetectionResult;
	}

	private String detectViaHeaderSignature() {
		// TODO Auto-generated method stub
		Program prog = Program.getProgram();
		long EP = Long.parseLong(Integer.toBinaryString(prog.getDoubleWordValueMemory(prog.getEntryPoint())), 2);

		// Read file
		String file = prog.getAbsolutePathFile();
		Path path = Paths.get(file);
		String[] dataString = null;
		try {
			byte[] dataByte = Files.readAllBytes(path);
			dataString = new String[dataByte.length];
			for (int i = 0; i < dataByte.length; i++) {
				dataString[i] = String.format("%02x", dataByte[i]);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ArrayList<HeaderSignature> pHeader = getHeaderSignature();

		for (HeaderSignature hP : pHeader) {
			if (detectWithHeaderSignature(dataString, hP, EP)) {
				return hP.getPackerName();
			}
		}

		return "NONE";

	}

	private boolean detectWithHeaderSignature(String[] dataString, HeaderSignature hPacker, long EP) {
		if (dataString == null) {
			return false;
		}

		int beginTracing = 0;
		int endTracing = 0;
		int pivot = 0;
		if (EP != 0x0) {
			for (int i = 0; i < dataString.length - 3; i++) {
				String dword = dataString[i + 3] + dataString[i + 2] + dataString[i + 1] + dataString[i];
				long dwordL = Long.parseLong(dword, 16);
				if (dwordL == EP) {
					pivot = i;
					break;
				}
			}
		}
		if (hPacker.isEntryPoint()) {
			beginTracing = pivot;
			endTracing = pivot + hPacker.getPackerSignature().length + 1;
		} else {
			beginTracing = 0;
			endTracing = pivot;
		}

		boolean trace = true;
		String[] sArr = hPacker.getPackerSignature();
		for (int i = beginTracing; i < endTracing && trace; i++) {
			if (dataString[i].equals(sArr[0].toLowerCase())
					&& (dataString[i + 1].equals(sArr[1].toLowerCase()) || sArr[1].equals("??"))) {
				for (int j = 0; j < sArr.length; j++) {
					if (i + j < dataString.length) {
						if (!sArr[j].toLowerCase().equals(dataString[i + j]) && !sArr[j].equals("??")) {
							break;
						} else if (j == sArr.length - 1) {
							trace = false;
						}
					} else {
						return false;
					}
				}
			} else {
				if (hPacker.isEntryPoint() || i == endTracing - 2) {
					return false;
				}
			}
		}
		return true;
	}

	private String detectViaTechniqueSignature() {
		// TODO Auto-generated method stub
		ArrayList<TechniqueSignature> tSignature = getTechniqueSignature();

		for (TechniqueSignature tP : tSignature) {
//			System.out.println(techOrder);
			String sig = tP.getTechiqueSignature();
			if (techOrder.contains(sig)) {
				return tP.getPackerName() + " v" + tP.getPackerVersion();
		 	}
		}

		return "NONE";

	}

	public String getDetectionDetail() {
		// TODO Auto-generated method stub
		String ret = "";
		
		for (PackerTechnique techSignature : techList) {
//			System.out.println(techOrder);
			ret += techSignature.toString() + "\n"; 
		}

		return ret;
	}

	public void checkAPIName(String api, long value) {
		// TODO Auto-generated method stub
		for (PackerTechnique pTech : techList) {
			if (pTech.checkAPIName(api, value)) {
				techOrder += pTech.getID()+"_";
				return;
			}
		}
	}
	
	public void setOverlapping(String instName, long curAddr, long nextAddr) {
		PackerTechnique p;
		if (instName.equals("call")) {
			p = getTechnique(PackerConstants.OVERLAPPING_FUNC);
			if (p != null) {
				if (!p.contain(nextAddr)) {
					if (((OverlappingFunction)p).checkCallAddr(nextAddr)) {
						techOrder += PackerConstants.OVERLAPPING_FUNC + "_";
						((OverlappingFunction)p).insertLocation(nextAddr);
					}
					((OverlappingFunction)p).insertCallAddr(nextAddr);
				}
			}
		} else if (instName.equals("ret")) {
			p = getTechnique(PackerConstants.OVERLAPPING_FUNC);
			if (p != null) {
				if (!p.contain(curAddr)) {
					if (((OverlappingFunction)p).checkRetAddr(curAddr)) {
						techOrder += PackerConstants.OVERLAPPING_FUNC + "_";
						((OverlappingFunction)p).insertLocation(curAddr);
					}
					((OverlappingFunction)p).insertRetAddr(curAddr);					
				}
			}
		} else if (instName.equals("jmp")) {
			p = getTechnique(PackerConstants.OVERLAPPING_BLOCK);
			if (p != null) {
				if (!p.contain(curAddr)) {
					if (((OverlappingBlock)p).checkJmpAddr(curAddr)) {
						techOrder += PackerConstants.OVERLAPPING_BLOCK + "_";
						((OverlappingBlock)p).insertLocation(curAddr);
					} 
					((OverlappingBlock)p).insertJmpAddr(curAddr, nextAddr);					
				}
			}
		}
	}

	public void setIndirectJumpTechnique(long value) {
		// TODO Auto-generated method stub
		PackerTechnique p = getTechnique(PackerConstants.INDIRECT_JUMP);
		if (p != null) {
			if (!p.contain(value)) {
				((IndirectJump)p).insertLocation(value);
				techOrder += PackerConstants.INDIRECT_JUMP + "_";
			}
		}
	}

	public void setSEHTechnique(long value) {
		// TODO Auto-generated method stub
		PackerTechnique p = getTechnique(PackerConstants.SEH);
		if (p != null) {
			if (!p.contain(value)) {
				((SEH)p).insertLocation(value);
				techOrder += PackerConstants.SEH + "_";
			}
		}
	}

	public void setCodeChunking(String instName, long curAddr, long nextAddr) {
		// TODO Auto-generated method stub
		PackerTechnique p;
		if (instName.equals("jmp")) {
			p = getTechnique(PackerConstants.CODE_CHUNKING);
			if (p != null) {
				if (!p.contain(curAddr)) {
					if (((CodeChunking)p).checkJmpAddr(curAddr)) {
						techOrder += PackerConstants.CODE_CHUNKING + "_";
						((CodeChunking)p).insertLocation(curAddr);
					} 
					((CodeChunking)p).insertJmpAddr(curAddr, nextAddr);					
				}
			}
		}
	}
}
