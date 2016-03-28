package v2.org.analysis.packer;

public class TechniqueSignature {

	private String[] techniqueSignature;
	private String[] techniquePos;
	private String packerName;
	private String packerVersion;
	
	public String getPackerVersion() {
		return packerVersion;
	}

	public void setPackerVersion(String packerVersion) {
		this.packerVersion = packerVersion;
	}

//	public TechniqueSignature (String pName, String pTechSign)	{
//		packerName 		= pName;
//		techniqueSignature	= pTechSign;
//		packerVersion = "";
//	}
//	
//	public TechniqueSignature (String pName, String version, String pTechSign) {
//		packerName 		= pName;
//		techniqueSignature	= pTechSign;
//		packerVersion = version;
//	}
	
	public TechniqueSignature (String pName, String version, String[] pTechSign, 
			String[] pos){
		// TODO Auto-generated constructor stub
		packerName 		= pName;
		techniqueSignature	= pTechSign;
		packerVersion = version;
		techniquePos = pos;
		
		if (techniquePos.length != techniqueSignature.length) {
			System.out.println("Signature " + packerName + " is not wrong");
		}
	}

	public String getPackerName ()
	{
		return packerName;
	}
	
	public String[] getTechiqueSignature() {
		return techniqueSignature;
	}

	public String[] getTechniquePosition() {
		return techniquePos;
	}

	public void setTechniquePosition(String[] techniquePos) {
		this.techniquePos = techniquePos;
	}
	
}
