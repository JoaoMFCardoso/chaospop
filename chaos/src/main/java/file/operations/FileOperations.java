package file.operations;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.ws.rs.core.Response;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.javatuples.Pair;

import exceptions.ChaosPopException;
import exceptions.ErrorMessage;
import parsing.ParserInterface;
import parsing.parsers.CSVParserImpl;
import parsing.parsers.JSONParserImpl;
import parsing.parsers.OWLParserImpl;
import parsing.parsers.XMLParserImpl;
import utils.FileOperationsUtils;

public class FileOperations {

	public FileOperations() {
		super();
	}

	/**
	 * This method processes a given file according to its file format.
	 * May it be calling a parser or other pre processing that is necessary before parsing
	 * @param file The file to be processed
	 * @return The sorted file
	 * @throws ChaosPopException 
	 */
	public static Pair<File, String> fileProcessor(File unsortedFile) throws ChaosPopException{
		/* Gets the extension */
		String extension = FilenameUtils.getExtension(unsortedFile.getName());
		ParserInterface parserInterface;

		/* Creates the correct directory to place the file and moves it there*/
		File directory = FileOperationsUtils.getCorrectDirectoryForFile(unsortedFile.getName());

		File file;
		try {
			if(!unsortedFile.getParent().equals(directory.getAbsolutePath())){
				FileUtils.moveFileToDirectory(unsortedFile, directory, false);

				String filePath = directory.getAbsolutePath() + File.separator + unsortedFile.getName();
				file = new File(filePath);
			}else{
				file = unsortedFile;
			}
		}catch(IOException ioException) {
			ErrorMessage ioError = new ErrorMessage();
			ioError.setMessage(ioException.getMessage());
			ioError.setStatus(Response.Status.BAD_REQUEST.getStatusCode());
			
			ChaosPopException chaosPopException = new ChaosPopException(ioException.getMessage());
			chaosPopException.setErrormessage(ioError);
			
			throw chaosPopException;
		}

		/* Deletes any directory structure if the file is not under the resource directory
		 * But this is only valid if the directory structure in which the unsorted file
		 * is placed is empty. */
		FileOperationsUtils.deleteDirectoryStructure(unsortedFile);

		CompressedFilesExtractor compressedFilesExtractor;
		ArrayList<File> extractedFiles;
		String dataFileID = "";

		/* Processes the file according to its file extension */
		switch (extension) {
		case "xml": /* It's an xml file */
			parserInterface = new XMLParserImpl();
			dataFileID = parserInterface.parseFile(file);
			break;
		case "bpmn": /* It's an bpmn file */
			parserInterface = new XMLParserImpl();
			dataFileID = parserInterface.parseFile(file);
			break;
		case "epml": /* It's an epml file */
			parserInterface = new XMLParserImpl();
			dataFileID = parserInterface.parseFile(file);
			break;
		case "json": /* It's an json file */
			parserInterface = new JSONParserImpl();
			dataFileID = parserInterface.parseFile(file);
			break;
		case "csv": /* It's an json file */
			parserInterface = new CSVParserImpl();
			dataFileID = parserInterface.parseFile(file);
			break;
		case "zip": /* It's an zip file */
			/* Extracts the zip file and gets the extracted files */
			compressedFilesExtractor = new CompressedFilesExtractor();
			extractedFiles = compressedFilesExtractor.unZipFile(file);

			/* Processes the extracted files */
			for(File extractedFile : extractedFiles){
				extractedFile = fileProcessor(extractedFile).getValue0();
				extractedFile.delete();
			}
			break;
		case "rar": /* It's an rar file */
			/* Extracts the rar file and gets the extracted files */
			compressedFilesExtractor = new CompressedFilesExtractor();
			extractedFiles = compressedFilesExtractor.unRarFile(file);

			/* Processes the extracted files */
			for(File extractedFile : extractedFiles){
				extractedFile = fileProcessor(extractedFile).getValue0();
				extractedFile.delete();
			}
			break;
		case "owl": /* It's an Ontology file */
			parserInterface = new OWLParserImpl();
			dataFileID = parserInterface.parseFile(file);
			break;
		default:
			break;
		}

		Pair<File, String> returnPair = new Pair<File, String>(file, dataFileID);
		return returnPair;
	}

	public static void createRemoteOutputFile(File outputFile){
		//TODO this needs to be defined
	}
}
