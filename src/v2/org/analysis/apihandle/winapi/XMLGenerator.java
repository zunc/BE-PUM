/**
 * Project: BE_PUM_V1
 * Package name: v2.org.analysis.apihandle.be_pum.winapi
 * File name: XMLGenerator.java
 * Created date: Mar 16, 2015
 * Description:
 */
package v2.org.analysis.apihandle.winapi;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author Yen Nguyen
 *
 */
public class XMLGenerator {

	private static BufferedWriter bufferedWriter;
	
	private static void println(String packageName, String funcName, String fileName) throws IOException {
		String str = "\t\t<API funcName=\"" + funcName.toLowerCase() + "\" className=\"" + packageName + fileName
				+ "\" />";
		// System.out.println(str);
//		System.out.print(funcName + ", ");
		bufferedWriter.write(str);
		bufferedWriter.write("\r\n");
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int count = 0;
		String workingDir = System.getProperty("user.dir");
		String path = workingDir + "\\src\\" + XMLGenerator.class.getPackage().getName().replace(".", "\\");
		// System.out.println("Current working directory : " + path);

		try {
			File XMLfile = new File(path + "\\APIMap.xml");

			// if file doesn't exists, then create it
			if (!XMLfile.exists()) {
				XMLfile.createNewFile();
			}

			FileWriter fw = new FileWriter(XMLfile.getAbsoluteFile());
			bufferedWriter = new BufferedWriter(fw);

			bufferedWriter.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n<APIMap>");
			bufferedWriter.write("\r\n");
			File directory = new File(path);
			for (File file : directory.listFiles()) {
				if (file.isDirectory() && !file.getName().equals("structures")) {
					bufferedWriter.write("\t<DLL name=\"" + file.getName() + "\">");
					bufferedWriter.write("\r\n");
					
//					System.out.println();
//					System.out.println();
//					System.out.println(file.getName() + ".dll");

					File subDir = new File(file.getAbsolutePath() + "\\functions");
					for (File api : subDir.listFiles()) {
						String packageName = XMLGenerator.class.getPackage().getName() + "." + file.getName()
								+ ".functions.";
						String funcName = api.getName().replace(".java", "");

						// System.out.println("\t\t" + funcName);

						println(packageName, funcName, funcName);
//						println(packageName, funcName + "A", funcName);
//						println(packageName, funcName + "W", funcName);
						count++;
					}

					bufferedWriter.write("\t</DLL>");
					bufferedWriter.write("\r\n");
				}
			}

			bufferedWriter.write("</APIMap>");
			bufferedWriter.write("\r\n");
			bufferedWriter.close();
			System.out.println("Total: " + count);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
