package utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.FilenameUtils;

import properties.PropertiesHandler;

import com.github.junrar.rarfile.FileHeader;

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

		try{
			File file = File.createTempFile(filename + "-", "." + extension, folder);
			OutputStream out = new FileOutputStream(file);
			byte[] buf = new byte[1024];
			int len;
			while ((len = uploadedInputStream.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			uploadedInputStream.close();
			out.close();
			//		    Files.copy(uploadedInputStream, file.toPath(), REPLACE_EXISTING);
			return file;
		}catch (IOException e) {
			e.printStackTrace();
		}
		return null;
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

		/* Creates the sub directory according to the extension, if needed */
		File directory = new File(PropertiesHandler.configProperties.getProperty("uploaded.files.path") + File.separator + extension);
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
		String referencePath = PropertiesHandler.configProperties.getProperty("uploaded.files.path");

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
