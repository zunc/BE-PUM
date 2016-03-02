package v2.org.analysis.packer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.jakstab.Program;

import com.sun.jna.WString;

import v2.org.analysis.algorithm.OTFModelGeneration;
import v2.org.analysis.apihandle.winapi.kernel32.Kernel32DLL;
import v2.org.analysis.cfg.BPCFG;

public class PackerDetection {

	private boolean isPacked;
	
	private PackerTechniques techniques;
	
	private String detectViaHeader;
	private String detectViaTechniques;
	private String detectViaTechniquesFrequency;
	
	private String backupDetectionState = "";
	private String backupDetectionCountState = "";
	
	public PackerDetection ()
	{
		isPacked = false;
		techniques 	= new PackerTechniques();
	}
	
	public void detectViaHeader (Program prog)
	{	
		// Entry point
		long EP = Long.parseLong(Integer.toBinaryString(prog.getDoubleWordValueMemory(prog.getEntryPoint())),2);
		
		// Read file
		String file = prog.getAbsolutePathFile();
		Path path = Paths.get(file);
		String[] dataString = null;
		try {
			byte[] dataByte = Files.readAllBytes(path);
			dataString = new String[dataByte.length];
		    for(int i = 0; i < dataByte.length; i++) {
		        dataString[i] = String.format("%02x", dataByte[i]);
		    }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ArrayList<PackerHeader> pHeader = PackerConstants.GetData();
		
		for (PackerHeader hP: pHeader)
		{
			if (detectWithHeaderSignature(dataString, hP, EP))
			{
				packedByHeader(hP.getPackerName() + " - version: " + hP.getPackerVersion());
				return;
			}
		}
		
		this.detectViaHeader = "NONE";
	}
	
	private boolean detectWithHeaderSignature (String[] dataString, PackerHeader hPacker, long EP)
	{
		if (dataString == null) {
			return false;
		}
		
		int beginTracing = 0;
		int endTracing = 0;
		int pivot = 0;
		if (EP != 0x0)
		{
			for (int i = 0; i < dataString.length - 3; i++)
			{
				String dword = dataString[i+3] + dataString[i+2] + dataString[i+1] + dataString[i];
				long dwordL = Long.parseLong(dword, 16);
				if (dwordL == EP)
				{
					pivot = i;
					break;
				}
			}
		}
		if (hPacker.isEntryPoint())
		{	
			beginTracing = pivot;
			endTracing = pivot + hPacker.getPackerSignature().length + 1;
		}
		else
		{
			beginTracing = 0;
			endTracing = pivot;
		}
		
		boolean trace = true;
		String[] sArr = hPacker.getPackerSignature();
		for (int i = beginTracing; i < endTracing && trace; i++)
		{	
			if (dataString[i].equals(sArr[0].toLowerCase()) 
				&& (dataString[i+1].equals(sArr[1].toLowerCase())
				|| sArr[1].equals("??")))
			{
				for (int j = 0; j < sArr.length; j++)
				{
					if (i + j < dataString.length)
					{
						if (!sArr[j].toLowerCase().equals(dataString[i + j]) 
							&& !sArr[j].equals("??"))
						{
							break;
						}
						else if (j == sArr.length - 1)
						{
							trace = false;
						}
					}
					else
					{
						return false;
					}
				}
			}
			else
			{
				if (hPacker.isEntryPoint() 
						|| i == endTracing - 2)
				{
					return false;
				}
			}
		}
		return true;
	}
	
	private void packedByHeader(String packerName)
	{
		String packedby = packerName;
		System.out.println("HEADER: File is packed by " + packedby);
		this.detectViaHeader = packedby;
	}
	
	public void packedByTechniques ()
	{
		String packedByTech = "";
		String pTech = techniques.getDetailTechniques();
		for (PackerSign pSign: PackerConstants.pTechSign)
		{
			if (this.isPackedWithViaTech(pSign.getPackerTechSign(), pTech))
			{
				packedByTech += pSign.getPackerName();
				packedByTech += " ";
			}
		}
		
		if (packedByTech.equals(""))
		{
			this.isPacked = false;
			this.detectViaTechniques = pTech + "-" + "NONE";
		}
		else 
		{
			this.isPacked = true;
			this.detectViaTechniques = pTech + "-" + packedByTech;
		}
		
		System.out.println("TECHNIQUES: File is packed by " + packedByTech);
	}
	
	public void packedByTechniquesFrequency ()
	{
		String packedByTechFrequency = "";
		String pTechFrequency = techniques.getTechniquesStatiscial();
		for (PackerSign pSign: PackerConstants.pTechCountSign)
		{
			if (this.isPackedWithViaTechCount(pSign.getPackerTechSign(), pTechFrequency))
			{
				packedByTechFrequency +=  pSign.getPackerName();
				packedByTechFrequency +=  " ";
			}
		}
		
		if (packedByTechFrequency.equals(""))
		{
			this.isPacked = false;
			this.detectViaTechniquesFrequency = pTechFrequency + "-" + "NONE";
		}
		else 
		{
			this.isPacked = true;
			this.detectViaTechniquesFrequency = pTechFrequency + "-" + packedByTechFrequency;
		}
		
		System.out.println("TECHNIQUES FREQUENCY: File is packed by " + packedByTechFrequency);
	}
	
	public boolean isPackedWithViaTech(String packerStr, String techStr)
	{
		for (int i = 0; i < packerStr.length(); i++)
		{
			if (packerStr.charAt(i) == '1' && techStr.charAt(i) == '0')
			{
				return false;
			}
		}
		return true;
	}

	public boolean isPackedWithViaTechCount(String packerStr, String techStr)
	{
		String[] pStr = packerStr.split(":");
		String[] tStr = techStr.split("\t");
		for (int i = 0; i < pStr.length; i++)
		{
			if (Integer.parseInt(tStr[i]) < Integer.parseInt(pStr[i]))
			{
				return false;
			}
		}
		return true;
	}
	
	public void updateBackupDetectionState (Program prog, OTFModelGeneration otfMG)
	{
		BPCFG cfg = prog.getBPCFG();

		Kernel32DLL.INSTANCE.SetCurrentDirectory(new WString(System.getProperty("user.dir")));
		
		String fileName = prog.getFileName();
		String viaHeader = this.detectViaHeader;
		String viaTechniques = this.detectViaTechniques;
		String nodes = String.format("%8d", cfg.getVertexCount());
		String edges = String.format("%8d", cfg.getEdgeCount());
		String times = Long.toString(prog.GetAnalyzingTime());
		String convergence = otfMG.isCompleted() ? "x": " ";
		
		String result = fileName + "\t" + viaHeader + "\t";	
		if (viaTechniques == null) {
			return;
		}
		
		for(int i = 0; i < viaTechniques.split("-")[0].length(); i++)
		{
			if (viaTechniques.split("-")[0].charAt(i) == '1') {
				result += ("x" + "\t");
			} else {
				result += (" " + "\t");
			}
		}
		result += (viaTechniques.split("-")[1] + "\t"
				+ nodes + "\t"
				+ edges + "\t"
				+ times + "\t"
				+ convergence + "\t");

		this.backupDetectionState = result;
		
		// Update packer count
		String viaTechniquesFrequency = this.detectViaTechniquesFrequency;
		String resultC = fileName + "\t" + viaHeader + "\t";
		resultC += viaTechniquesFrequency.split("-")[0];
		resultC += viaTechniquesFrequency.split("-")[1] + "\t";
		resultC += nodes + "\t" + edges + "\t" + times + "\t" + convergence + "\t";
		
		this.backupDetectionCountState = resultC;	
	}
	
	public boolean fileIsPacked ()
	{
		return this.isPacked;
	}
	
	public PackerTechniques getTechniques ()
	{
		return techniques;
	}
	
	public void setToLogFirst(Program prog)
	{
		PackerUtility.setToLogFirst(prog, detectViaHeader);
	}
	
	public void setToLog(Program prog)
	{
		PackerUtility.setToLog(prog, backupDetectionState, backupDetectionCountState);
	}
}
