package utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.FilenameUtils;
import org.javatuples.Pair;

import properties.PropertiesHandler;

import com.github.junrar.rarfile.FileHeader;

import database.implementations.DataFileImpl;
import domain.bo.parsers.DataFile;
import domain.bo.parsers.Node;

public class FileOperationsUtils {

	/**
	 * This method writes the uploaded file into a temporary local copy
	 * @param uploadedInputStream
	 * @param uploadedFileName
	 * @return
	 */
	public static File writeToFile(InputStream uploadedInputStream, String uploadedFileName) {

		File folder = new File(PropertiesHandler.configProperties.getProperty("uploaded.files.path"));
		String filename = FilenameUtils.getBaseName(uploadedFileName);
		String extension = FilenameUtils.getExtension(uploadedFileName);
		File file;
		
		try{
			file = new File(folder + File.separator + filename + "." + extension);
			/* Uncomment if you want to have temporary files created, with a temporary name
			file = File.createTempFile(filename + "-", "." + extension, folder);
			*/
			OutputStream out = new FileOutputStream(file);
			byte[] buf = new byte[1024];
			int len;
			while ((len = uploadedInputStream.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			uploadedInputStream.close();
			out.close();
			
		}catch (IOException e) {
			return null;
		}
		
		return file;
	}

	/**
	 * This method gets the correct temporary directory for a given filename
	 * @param fileName The file name
	 * @return The correct directory according to the file's extension
	 */
	public static File getCorrectDirectoryForFile(String fileName){
		/* Loads the properties */
		PropertiesHandler.propertiesLoader();

		/* Gets the file extension */
		String extension = FilenameUtils.getExtension(fileName);

		/* If the file is an ontology then it should be placed in the local ontologies directory */
		File directory;
		if(extension.equals("owl")){
			directory = new File(PropertiesHandler.configProperties.getProperty("local.ontologies.path"));
		}else{
			/* Creates the sub directory according to the extension, if needed */
			directory = new File(PropertiesHandler.configProperties.getProperty("uploaded.files.path") + File.separator + extension);
		}

		if(!directory.exists()){
			directory.mkdir();
		}

		return directory;
	}

	/**
	 * This method deletes a directory structure up to the properties specified resource directory
	 * But only if that directory is empty
	 * @param file The file whose directory structure is to be deleted
	 */
	public static void deleteDirectoryStructure(File file){
		/* Loads the properties */
		PropertiesHandler.propertiesLoader();

		/* Sets the reference path, i.e., the directory specified in the properties file that points to the resource directory */
		String referencePath;
		if(FilenameUtils.getExtension(file.getName()).equals("owl")){
			referencePath = PropertiesHandler.configProperties.getProperty("local.ontologies.path");
		}else{
			referencePath = PropertiesHandler.configProperties.getProperty("uploaded.files.path");
		}

		/* Gets the parent file to the given file */
		File current = file.getParentFile();
		File parent;
		/* Deletes the parent file if it's empty until it reaches the reference path */
		while (!current.getAbsolutePath().equals(referencePath) && current.isDirectory()) {

			if(current.list().length > 0){
				break;
			}else{
				parent = current.getParentFile();
				current.delete();
				current = parent;
			}
		}
		return;
	}

	/**
	 * This method creates a file when provided with a FileHeader and a destination directory
	 * @param fh The FileHeader
	 * @param destination The destination directory
	 * @return Returns the created File
	 * @throws Exception
	 */
	public static File createFile(FileHeader fh, File destination) throws Exception{
		File f = null;
		String name = null;
		if (fh.isFileHeader() && fh.isUnicode()) {
			name = fh.getFileNameW();
		} else {
			name = fh.getFileNameString();
		}
		f = new File(destination, name);
		if (!f.exists()) {
			try {
				f = makeFile(destination, name);
			} catch (IOException e) {
				e.printStackTrace();
				throw e;
			}
		}
		return f;
	}

	/**
	 * Checks if a given namespace is compliant with the SFTP server base namespace
	 * @param namespace The given namespace
	 * @return True if it is compliant, false otherwise
	 */
	public static Boolean isSFTPServerCompliant(String namespace){
		Boolean compliant = false;

		/* Gets the base namespace for the SFTP server */
		PropertiesHandler.propertiesLoader();
		String sftpNamespace = PropertiesHandler.configProperties.getProperty("sftp.namespace");

		/* Sets the compliance to true if the given namespace starts with the sftp Namespace */
		if(namespace.startsWith(sftpNamespace)){
			compliant = true;
		}

		return compliant;
	}

	/**
	 * Checks if directory creation is needed by analising a given namespace
	 * @param sftpNamespace The SFTP server base namespace
	 * @param namespace A SFTP Server compliant namespace
	 * @return True if directory creation is needed, false otherwise
	 */
	public static Boolean isDirectoryCreationNeeded(String sftpNamespace, String namespace){
		Boolean create = false;

		/* Eliminate the sftpNamespace from the namespace, i.e
		 * sftpNamespace = http://dev.sysresearch.org/chaos_pop/Ontologies/
		 * namespace = http://dev.sysresearch.org/chaos_pop/Ontologies/directory/ontology.owl
		 * result = directory/ontology.owl */
		namespace = namespace.replace(sftpNamespace, "");

		/* if the result of a parse on the remainder is an array with a size of bigger than one
		 *  then directories need to be created. i.e.
		 *  namespace = directory/ontology.owl
		 *  result = [directory, ontology.owl] -> length = 2 > 1 -> directory creation is needed*/
		if(namespace.split("/").length > 1){
			create = true;
		}

		return create;
	}

	/**
	 * Creates all the necessary directories to store a given namespace in the sftp server
	 * @param sftpNamespace The sftp standard namespace
	 * @param namespace The given namespace
	 * @return A pair containing both a string with the path from the sftp satandard namespace to the file, and the file name
	 */
	public static Pair<String, String> getNamespaceDirectories(String sftpNamespace, String namespace){
		/* Eliminate the sftpNamespace from the namespace, i.e
		 * sftpNamespace = http://dev.sysresearch.org/chaos_pop/Ontologies/
		 * namespace = http://dev.sysresearch.org/chaos_pop/Ontologies/directory/ontology.owl
		 * result = directory/ontology.owl */
			namespace = namespace.replace(sftpNamespace, "");

			/* Get the Array with the directories */
			String[] directories = namespace.split("/");

			/* Create the directories path */
			String directoriesPath = "/";
			int lastPosition = directories.length - 1;
			for (int i = 0; i < lastPosition; i++) {
				directoriesPath += directories[i];
			}

			Pair<String, String> returnPair = new Pair<String, String>(directoriesPath, directories[lastPosition]);

			return returnPair;
	}

	/**
	 * This method gets a DataFile object and stores it in the database
	 * @param dataFileImpl The database implementation for data files
	 * @param dataFile The DataFile Object
	 * @param file The file
	 * @param node The node
	 * @return The ID of the DataFile
	 */
	public static String storeDataFile(DataFileImpl dataFileImpl, DataFile dataFile, File file, Node node){

		/* Sets the DataFile object attributes */
		dataFile.setName(file.getName());
		dataFile.setNodeID(node.getID());

		/* Saves the DataFile object */
		String dataFileID = dataFileImpl.save(dataFile);
		
		return dataFileID;
	}


	/**
	 * This method creates a File
	 * @param destination The destination directory
	 * @param name The name of the File
	 * @return The created file
	 * @throws IOException
	 */
	private static File makeFile(File destination, String name)
			throws IOException {
		String[] dirs = name.split("\\\\");
		if (dirs == null) {
			return null;
		}
		int size = dirs.length;
		if (size == 1) {
			return new File(destination, name);
		} else if (size > 1) {
			name = dirs[dirs.length - 1];
			File f = new File(destination, name);
			f.createNewFile();
			return f;
		} else {
			return null;
		}
	}
}
