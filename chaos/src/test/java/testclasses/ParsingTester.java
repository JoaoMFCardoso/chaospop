package testclasses;

import java.io.File;

import file.operations.FileOperations;
import parsing.parsers.XMLParserImpl;
import properties.PropertiesHandler;
import services.FileManager;

public class ParsingTester {

	public ParsingTester() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		PropertiesHandler.propertiesLoader();
		String fp = PropertiesHandler.configProperties.getProperty("uploaded.files.path") + File.separator + "hard.json";
		File xml = new File(fp);
		System.out.println(xml.getAbsolutePath());
		try {
			FileOperations.fileProcessor(xml);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
