package services;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.FilenameUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.javatuples.Pair;

import com.jcraft.jsch.JSchException;

import database.implementations.DataFileImpl;
import database.implementations.OntologyFileImpl;
import domain.bo.ontologies.OntologyFile;
import domain.bo.parsers.DataFile;
import domain.to.DataFileTO;
import domain.to.wrappers.DataFileTOWrapper;
import exceptions.ChaosPopException;
import exceptions.ErrorMessage;
import exceptions.ErrorMessageHandler;
import file.operations.FileOperations;
import file.sftp.SFTPServerConnectionManager;
import properties.PropertiesHandler;
import utils.FileOperationsUtils;

/**
 * This class implements a jax rs service layer
 * It handles all services related to file management
 * @author João Cardoso
 *
 */
@Path("fileManager")
public class FileManager {

	/** The connection to the database for DataFile objects */
	private DataFileImpl dataFileImpl = new DataFileImpl();

	/**
	 * This method uploads a file from the client.
	 * Processes it and stores it in the database
	 * @param uploadedInputStream The InputStream of the uploaded File
	 * @param fileDetail The File details of the uploaded file
	 * @return Returns a Response
	 */
	@POST
	@Path("/addFile")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces({MediaType.APPLICATION_JSON})
	public Response addFile(@FormDataParam("file") InputStream uploadedInputStream,
			@FormDataParam("file") FormDataContentDisposition fileDetail) {
		Response response;

		/**
		 * Input Checks
		 */
		/* If the file is null */
		if(fileDetail == null) {
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			ErrorMessage nullFile = new ErrorMessage(Response.Status.INTERNAL_SERVER_ERROR, "1", "filemanager"); 

			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.INTERNAL_SERVER_ERROR, nullFile);

			return response;
		}

		/* If it respects the max upload size */
		long maxUploadSize = Long.parseLong(PropertiesHandler.configProperties.getProperty("max.upload.size"));
		if(fileDetail.getSize() > maxUploadSize) {

			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			ErrorMessage maxSize = new ErrorMessage(Response.Status.BAD_REQUEST, "2", "filemanager"); 

			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.BAD_REQUEST, maxSize);

			return response;
		}

		/* Checks if the file extension is supported */
		String[] supportedExtensions = PropertiesHandler.configProperties.getProperty("extensions").split(";");
		String extension = FilenameUtils.getExtension(fileDetail.getFileName());
		if(!Arrays.asList(supportedExtensions).contains(extension)) {

			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			ErrorMessage extensionNotSupported = new ErrorMessage(Response.Status.BAD_REQUEST, "3", "filemanager"); 

			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.BAD_REQUEST, extensionNotSupported);

			return response;
		}


		String uploadedFileName = fileDetail.getFileName();

		File uploadedFile = null;
		File processedFile = null;
		Pair<File, String> processedFileData;
		String processedFileID = "";

		try {
			// save it
			uploadedFile = FileOperationsUtils.writeToFile(uploadedInputStream, uploadedFileName);

			/* Processes the File */
			processedFile = uploadedFile;
			processedFileData = FileOperations.fileProcessor(uploadedFile); 

			processedFile = processedFileData.getValue0();
			processedFileID = processedFileData.getValue1();

			/* Gets the Response */
			response = Response.ok(processedFileID).build();

		}catch(ChaosPopException chaosPopException) {

			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.BAD_REQUEST, chaosPopException.getErrormessage());
			
			/* Deletes the problem file and its structure */
			File errorFile = new File(chaosPopException.getMessage());
			FileOperationsUtils.deleteErrorFile(errorFile);

		}catch(IOException ioException) {
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			ErrorMessage genericError = new ErrorMessage();
			genericError.setMessage(ioException.getMessage());
			genericError.setStatus(Response.Status.BAD_REQUEST.getStatusCode()); 

			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.INTERNAL_SERVER_ERROR, genericError);

			/* Deletes the problem file and its structure */
			FileOperationsUtils.deleteErrorFile(uploadedFile);

			ioException.printStackTrace();

		}catch(Exception exception){

			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			ErrorMessage genericError = new ErrorMessage(Response.Status.INTERNAL_SERVER_ERROR, "4", "filemanager"); 

			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.INTERNAL_SERVER_ERROR, genericError);

			/* Deletes the problem file and its structure */
			FileOperationsUtils.deleteErrorFile(uploadedFile);

			exception.printStackTrace();
		}finally{

			/* Deletes the temporary file but only if it is a data file.
			 * If the temporary file is an ontology it must be kept for use in future populations*/
			if(!FilenameUtils.getExtension(processedFile.getName()).equals("owl")){
				processedFile.delete();
				FileOperationsUtils.deleteDirectoryStructure(processedFile);
			}
		}

		return response;
	}

	/**
	 * Uploads an Ontology file to the database
	 * @param fileName
	 * @return
	 */
	@POST
	@Path("/uploadFileSFTP")
	@Produces({MediaType.APPLICATION_JSON})
	public Response uploadFileSFTP(@FormParam("ontologyFileId") String ontologyFileId){
		File file = null;
		Response response;
		Boolean uploaded = false;
		
		try{
			/* Gets the OntologyFile from the database */
			OntologyFileImpl ontologyFileImpl = new OntologyFileImpl();
			OntologyFile ontologyFile = ontologyFileImpl.get(ontologyFileId);

			/* Checks if the Ontology File was found based on the given ID */
			if(ontologyFile == null) {
				/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
				ErrorMessage ontologyFileNotFoundInDB = new ErrorMessage(Response.Status.BAD_REQUEST, "5", "filemanager"); 

				/* Builds a Response object */
				response = ErrorMessageHandler.toResponse(Response.Status.BAD_REQUEST, ontologyFileNotFoundInDB);

				return response;
			}
			
			/* Checks if the Ontology File is locally stored */
			if(ontologyFile.getPath() == null) {
				
				/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
				ErrorMessage ontologyPathError = new ErrorMessage(Response.Status.BAD_REQUEST, "10", "filemanager"); 

				/* Builds a Response object */
				response = ErrorMessageHandler.toResponse(Response.Status.BAD_REQUEST, ontologyPathError);
				
				return response;
			}

			/* Checks that the namespace is compliant with the sftp server namespace
			 * In case it is not compliant a response with a not accetable status is sent. */
			String ontologyNamespace = ontologyFile.getNamespace().toString();
			if(!FileOperationsUtils.isSFTPServerCompliant(ontologyNamespace)){

				/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
				ErrorMessage fileExists = new ErrorMessage(Response.Status.BAD_REQUEST, "6", "filemanager"); 

				/* Builds a Response object */
				response = ErrorMessageHandler.toResponse(Response.Status.BAD_REQUEST, fileExists);

				return response;
			}

			/* Uploads the file to the SFTP server */
			SFTPServerConnectionManager sftpManager = new SFTPServerConnectionManager();
			
			/* Checks if the file is already in the SFTP Server */
			file = new File(ontologyFile.getPath());
			if(sftpManager.isFileInSFTPServer(file.getName(), ontologyNamespace)) {
				/* The file is already in the SFTP Server.
				 * The user should be notified, and the temporary file should be deleted. */
				
				/* Closes the connection */
				sftpManager.disconnect();
				
				/* Delete file */
				FileOperationsUtils.deleteErrorFile(file);
				
				/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
				ErrorMessage fileExists = new ErrorMessage(Response.Status.BAD_REQUEST, "11", "filemanager"); 

				/* Builds a Response object */
				response = ErrorMessageHandler.toResponse(Response.Status.BAD_REQUEST, fileExists);
				
				return response;
			}
			
			/* Uploads the file */
			uploaded = sftpManager.uploadSFTPFile(ontologyFile.getPath(), ontologyNamespace);
			
			/* Closes the connection */
			sftpManager.disconnect();
			
			/* Updates the OntologyFile */
			ontologyFile.setPath(null);
			ontologyFileImpl.replace(ontologyFileId, ontologyFile);

			/* Gets the Response */
			response = Response.ok().build();

		}catch(NullPointerException | JSchException exception) {

			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			ErrorMessage genericError = new ErrorMessage();
			genericError.setMessage(exception.getMessage());

			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.BAD_REQUEST, genericError);

		}catch(FileNotFoundException fileNotFoundException) {

			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			ErrorMessage fileNotFoundError = new ErrorMessage();
			fileNotFoundError.setMessage(fileNotFoundException.getMessage());

			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.BAD_REQUEST, fileNotFoundError);

		}catch(IllegalArgumentException illegalArgumentException) {

			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			ErrorMessage fileExists = new ErrorMessage(Response.Status.BAD_REQUEST, "7", "filemanager"); 

			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.BAD_REQUEST, fileExists);

		}catch(Exception exception){
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			ErrorMessage genericError = new ErrorMessage(Response.Status.INTERNAL_SERVER_ERROR, "1", "filemanager"); 

			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.INTERNAL_SERVER_ERROR, genericError);

			exception.printStackTrace();

		}finally {
			
			/* Deletes the local created file if created */
			if(file != null && uploaded) {
				FileOperationsUtils.deleteErrorFile(file);
			}
		}

		return response;
	}

	/**
	 * Downloads a file from the SFTP server
	 * @param fileName
	 * @return
	 */
	@POST
	@Path("/downloadFile")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response downloadFile(@FormParam("fileName") String fileName) {
		File file = null;
		Response response;
		Boolean downloaded = false;
		try{

			/* Gets the file from the SFTP server */
			SFTPServerConnectionManager sftpManager = new SFTPServerConnectionManager();
			String localPath = sftpManager.downloadSFTPFile(fileName);
			downloaded = true;
			file = new File(localPath);
			sftpManager.disconnect();

			/* Gets the Response */
			response = Response.ok(file, MediaType.APPLICATION_OCTET_STREAM).header("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"").build();

		}catch(Exception exception){
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			ErrorMessage fileExists = new ErrorMessage(Response.Status.INTERNAL_SERVER_ERROR, "4", "filemanager"); 

			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.INTERNAL_SERVER_ERROR, fileExists);

			exception.printStackTrace();
		}finally{
			/* Deletes the temporarily created file */
			if(downloaded) {
				/* Deletes the problem file and its structure */
				FileOperationsUtils.deleteErrorFile(file);
			}
		}

		return response;
	}

	/**
	 * This method returns all the DataFiles stored in the database
	 * @return An ArrayList with all DataFiles
	 */
	@GET
	@Path("/listDataFiles")
	@Produces(MediaType.APPLICATION_JSON)
	public Response listDataFiles(){
		DataFileTOWrapper dataFilesTOWrapper = new DataFileTOWrapper();
		Response response;

		try {
			/* Get all DataFile objects from the database */
			List<DataFile> dataFiles = this.dataFileImpl.getAll();

			/* Runs the DataFile objects and fills the DataFileTO array  */
			for(DataFile dataFile : dataFiles){
				DataFileTO dataFileTO = dataFile.createTransferObject();
				dataFilesTOWrapper.dataFilesTO.add(dataFileTO);
			}

			/* Builds the response */
			response = Response.ok(dataFilesTOWrapper).build();

		}catch(Exception exception) {
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			ErrorMessage fileExists = new ErrorMessage(Response.Status.INTERNAL_SERVER_ERROR, "4", "filemanager"); 

			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.INTERNAL_SERVER_ERROR, fileExists);

			exception.printStackTrace();
		}

		return response;
	}

	/**
	 * This method gets a DataFile object when given its id
	 * @param dataFileId The DataFile id
	 * @return A DataFile transfer object
	 */
	@POST
	@Path("/getFile")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getDataFile(@FormParam("id") String dataFileId){
		/* Gets the DataFile object from the database and builds the transfer object */
		DataFile dataFile;
		DataFileTO dataFileTO;
		Response response;

		try {
			dataFile = this.dataFileImpl.get(dataFileId);
			dataFileTO = dataFile.createTransferObject();

			/* Builds the response with a filled DataFileTO */
			response = Response.ok(dataFileTO).build();

			/* Any exception leads to an error */
		}catch(NullPointerException nullPointerException) {
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			ErrorMessage fileExists = new ErrorMessage(Response.Status.BAD_REQUEST, "8", "filemanager"); 

			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.BAD_REQUEST, fileExists);


		}catch(IllegalArgumentException illegalArgumentException) {
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			ErrorMessage fileExists = new ErrorMessage(Response.Status.BAD_REQUEST, "7", "filemanager"); 

			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.BAD_REQUEST, fileExists);

		}catch(Exception exception) {
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			ErrorMessage fileExists = new ErrorMessage(Response.Status.INTERNAL_SERVER_ERROR, "4", "filemanager"); 

			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.INTERNAL_SERVER_ERROR, fileExists);

			exception.printStackTrace();
		}

		return response;
	}

	/**
	 * This method removes a list of DataFile objects from the database
	 * @param dataFileIds The DataFile id list. All ids are sepparated by ",".
	 * @return 200 if everything went well, 500 if not.
	 */
	@POST
	@Path("/removeFile")
	public Response removeFile(@FormParam("ids") String dataFileIds){
		Response response;

		try{
			/* Gets the DataFile id's from the dataFileIds string
			 * The id's are sepparated by ","
			 * e.g. 123,2344,455 */
			String[] ids = dataFileIds.split(",");

			/* Runs all ids and fetches the DataFile object  */
			for(String dataFileId : ids){
				/* Removes the DataFile from the database */
				this.dataFileImpl.remove(dataFileId);
			}

			/* Gets the Response */
			response = Response.ok().build();

		}catch(NullPointerException nullPointerException){
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			ErrorMessage fileExists = new ErrorMessage(Response.Status.BAD_REQUEST, "8", "filemanager"); 

			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.BAD_REQUEST, fileExists);

		}catch(IllegalArgumentException illegalArgumentException) {
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			ErrorMessage fileExists = new ErrorMessage(Response.Status.BAD_REQUEST, "7", "filemanager"); 

			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.BAD_REQUEST, fileExists);

		}catch(Exception exception){
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			ErrorMessage fileExists = new ErrorMessage(Response.Status.INTERNAL_SERVER_ERROR, "4", "filemanager"); 

			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.INTERNAL_SERVER_ERROR, fileExists);

			exception.printStackTrace();
		}

		return response;
	}
}
