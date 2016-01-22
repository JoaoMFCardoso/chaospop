package testclasses;

import java.io.File;

import properties.PropertiesHandler;
import file.operations.FileOperations;

public class ParsingTester {

	public ParsingTester() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		PropertiesHandler.propertiesLoader();
		String fp = PropertiesHandler.configProperties.getProperty("uploaded.files.path") + File.separator + "family.xml";
		File xml = new File(fp);
		System.out.println(xml.getAbsolutePath());
		try {
			FileOperations.fileProcessor(xml);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
