package v2.org.analysis.packer;

public class TechniqueSignature {

	private String techniqueSignature;
	private String packerName;
	private String packerVersion;
	
	public String getPackerVersion() {
		return packerVersion;
	}

	public void setPackerVersion(String packerVersion) {
		this.packerVersion = packerVersion;
	}

	public TechniqueSignature (String pName, String pTechSign)	{
		packerName 		= pName;
		techniqueSignature	= pTechSign;
		packerVersion = "";
	}
	
	public TechniqueSignature (String pName, String version, String pTechSign) {
		packerName 		= pName;
		techniqueSignature	= pTechSign;
		packerVersion = version;
	}
	
	public String getPackerName ()
	{
		return packerName;
	}
	
	public String getTechiqueSignature()
	{
		return techniqueSignature;
	}
	
}
