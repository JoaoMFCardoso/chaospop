package testclasses;

import java.io.File;

import properties.PropertiesHandler;
import file.sftp.SFTPServerConnectionManager;

public class SFTPTester {

	// Main method to invoke the above methods
    public static void main(String[] args) {
        try {
        	PropertiesHandler.propertiesLoader();

        	SFTPServerConnectionManager sftp = new SFTPServerConnectionManager();

        	String filePath = PropertiesHandler.configProperties.getProperty("uploaded.files.path") + File.separator + "BPMN.owl";

        	for(String name : sftp.listSFTPFiles()){
        		System.out.println(name);
        	}

        	sftp.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
