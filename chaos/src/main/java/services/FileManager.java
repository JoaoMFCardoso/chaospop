package services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.apache.commons.io.FilenameUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.javatuples.Pair;

import com.google.gson.Gson;
import com.jcraft.jsch.JSchException;

import database.implementations.DataFileImpl;
import database.implementations.OntologyFileImpl;
import domain.bo.ontologies.OntologyFile;
import domain.bo.parsers.DataFile;
import domain.to.DataFileTO;
import domain.to.wrappers.DataFileTOWrapper;
import exceptions.ChaosPopException;
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
//			ErrorMessage nullFile = new ErrorMessage(Response.Status.INTERNAL_SERVER_ERROR, "1", "messages.filemanager"); 

			/* Builds a Response object */
//			response = ErrorMessageHandler.toResponse(Response.Status.INTERNAL_SERVER_ERROR, nullFile);

			response = Response.serverError().build();
			
			return response;
		}

		/* If it respects the max upload size */
		long maxUploadSize = Long.parseLong(PropertiesHandler.configProperties.getProperty("max.upload.size"));
		if(fileDetail.getSize() > maxUploadSize) {

			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
//			ErrorMessage maxSize = new ErrorMessage(Response.Status.REQUEST_ENTITY_TOO_LARGE, "2", "messages.filemanager"); 

			/* Builds a Response object */
//			response = ErrorMessageHandler.toResponse(Response.Status.REQUEST_ENTITY_TOO_LARGE, maxSize);

			response = Response.status(Response.Status.REQUEST_ENTITY_TOO_LARGE).build();
			
			return response;
		}

		/* Checks if the file extension is supported */
		String[] supportedExtensions = PropertiesHandler.configProperties.getProperty("extensions").split(";");
		String extension = FilenameUtils.getExtension(fileDetail.getFileName());
		if(!Arrays.asList(supportedExtensions).contains(extension)) {

			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
//			ErrorMessage extensionNotSupported = new ErrorMessage(Response.Status.UNSUPPORTED_MEDIA_TYPE, "3", "messages.filemanager"); 

			/* Builds a Response object */
//			response = ErrorMessageHandler.toResponse(Response.Status.UNSUPPORTED_MEDIA_TYPE, extensionNotSupported);

			response = Response.status(Response.Status.UNSUPPORTED_MEDIA_TYPE).build();
			
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
//			response = ErrorMessageHandler.toResponse(Response.Status.BAD_REQUEST, chaosPopException.getErrormessage());
			
			response = Response.status(Response.Status.BAD_REQUEST).build();
			
			/* Deletes the problem file and its structure */
			File errorFile = new File(chaosPopException.getMessage());
			FileOperationsUtils.deleteErrorFile(errorFile);

		}catch(IOException ioException) {
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
//			ErrorMessage genericError = new ErrorMessage();
//			genericError.setMessage(ioException.getMessage());
//			genericError.setStatus(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()); 

			/* Builds a Response object */
//			response = ErrorMessageHandler.toResponse(Response.Status.INTERNAL_SERVER_ERROR, genericError);

			response = Response.serverError().build();
			
			/* Deletes the problem file and its structure */
			FileOperationsUtils.deleteErrorFile(uploadedFile);

			ioException.printStackTrace();

		}catch(Exception exception){

			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
//			ErrorMessage genericError = new ErrorMessage(Response.Status.INTERNAL_SERVER_ERROR, "4", "messages.filemanager"); 
			
			/* Builds a Response object */
//			response = ErrorMessageHandler.toResponse(Response.Status.INTERNAL_SERVER_ERROR, genericError);

			response = Response.serverError().build();
			
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
//				ErrorMessage ontologyFileNotFoundInDB = new ErrorMessage(Response.Status.NOT_FOUND, "5", "messages.filemanager"); 

				/* Builds a Response object */
//				response = ErrorMessageHandler.toResponse(Response.Status.NOT_FOUND, ontologyFileNotFoundInDB);

				response = Response.status(Response.Status.NOT_FOUND).build();
				
				return response;
			}
			
			/* Checks if the Ontology File is locally stored */
			if(ontologyFile.getPath() == null) {
				
				/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
//				ErrorMessage ontologyPathError = new ErrorMessage(Response.Status.NOT_FOUND, "10", "messages.filemanager"); 

				/* Builds a Response object */
//				response = ErrorMessageHandler.toResponse(Response.Status.NOT_FOUND, ontologyPathError);
				
				response = Response.status(Response.Status.NOT_FOUND).build();
				
				return response;
			}

			/* Checks that the namespace is compliant with the sftp server namespace
			 * In case it is not compliant a response with a not accetable status is sent. */
			String ontologyNamespace = ontologyFile.getNamespace().toString();
			if(!FileOperationsUtils.isSFTPServerCompliant(ontologyNamespace)){

				/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
//				ErrorMessage namespaceNotCompliant = new ErrorMessage(Response.Status.BAD_REQUEST, "6", "messages.filemanager"); 

				/* Builds a Response object */
//				response = ErrorMessageHandler.toResponse(Response.Status.BAD_REQUEST, namespaceNotCompliant);

				response = Response.status(Response.Status.BAD_REQUEST).build();
				
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
//				ErrorMessage fileExists = new ErrorMessage(Response.Status.BAD_REQUEST, "11", "messages.filemanager"); 

				/* Builds a Response object */
//				response = ErrorMessageHandler.toResponse(Response.Status.BAD_REQUEST, fileExists);
				
				response = Response.status(Response.Status.BAD_REQUEST).build();
				
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
			response = Response.noContent().build();

		}catch(NullPointerException | JSchException exception) {

			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
//			ErrorMessage genericError = new ErrorMessage();
//			genericError.setMessage(exception.getMessage());

			/* Builds a Response object */
//			response = ErrorMessageHandler.toResponse(Response.Status.BAD_REQUEST, genericError);

			response = Response.status(Response.Status.BAD_REQUEST).build();
			
		}catch(FileNotFoundException fileNotFoundException) {

			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
//			ErrorMessage fileNotFoundError = new ErrorMessage();
//			fileNotFoundError.setMessage(fileNotFoundException.getMessage());

			/* Builds a Response object */
//			response = ErrorMessageHandler.toResponse(Response.Status.NOT_FOUND, fileNotFoundError);
			
			response = Response.status(Response.Status.BAD_REQUEST).build();

		}catch(IllegalArgumentException illegalArgumentException) {

			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
//			ErrorMessage fileExists = new ErrorMessage(Response.Status.BAD_REQUEST, "7", "messages.filemanager"); 

			/* Builds a Response object */
//			response = ErrorMessageHandler.toResponse(Response.Status.BAD_REQUEST, fileExists);

			response = Response.status(Response.Status.BAD_REQUEST).build();
			
		}catch(Exception exception){
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
//			ErrorMessage genericError = new ErrorMessage(Response.Status.INTERNAL_SERVER_ERROR, "1", "messages.filemanager"); 

			/* Builds a Response object */
//			response = ErrorMessageHandler.toResponse(Response.Status.INTERNAL_SERVER_ERROR, genericError);

			response = Response.serverError().build();
			
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
	@Produces({MediaType.APPLICATION_OCTET_STREAM, MediaType.APPLICATION_JSON})
	public Response downloadFile(@FormParam("fileName") String fileName) {
		Response response;
		try{
			/* Connects to the SFTP Server */
			SFTPServerConnectionManager sftpManager = new SFTPServerConnectionManager();
			
			/* Checks that the file is in the SFTP Server */
			if(!sftpManager.isFileInSFTPServer(fileName, "")) {
				/* The file is does not exist in the SFTP Server.
				 * The user should be notified, because it might be a typo. */
				
				/* Closes the connection */
				sftpManager.disconnect();
				
				/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
//				ErrorMessage notFound = new ErrorMessage(Response.Status.NOT_FOUND, "12", "messages.filemanager"); 

				/* Builds a Response object */
//				response = ErrorMessageHandler.toResponse(Response.Status.NOT_FOUND, notFound);
				
				response = Response.status(Response.Status.NOT_FOUND).build();
				
				return response;
			}
			
			/* Gets the file from the SFTP server */
			String localPath = sftpManager.downloadSFTPFile(fileName);
			final File file = new File(localPath);
			
			/* Closes the connection */
			sftpManager.disconnect();

			final InputStream responseStream = new FileInputStream(file);
			
			StreamingOutput output = new StreamingOutput() {
	            @Override
	            public void write(OutputStream out) throws IOException, WebApplicationException {  
	                int length;
	                byte[] buffer = new byte[1024];
	                while((length = responseStream.read(buffer)) != -1) {
	                    out.write(buffer, 0, length);
	                }
	                out.flush();
	                responseStream.close();
	                file.delete();             
	            }   
	        };
			
			/* Gets the Response */
			response = Response.ok(output, MediaType.APPLICATION_OCTET_STREAM).header("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"").build();

		}catch(Exception exception){
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
//			ErrorMessage genericError = new ErrorMessage(Response.Status.INTERNAL_SERVER_ERROR, "1", "messages.filemanager"); 

			/* Builds a Response object */
//			response = ErrorMessageHandler.toResponse(Response.Status.INTERNAL_SERVER_ERROR, genericError);

			response = Response.serverError().build();
			
			exception.printStackTrace();
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
//			ErrorMessage fileExists = new ErrorMessage(Response.Status.INTERNAL_SERVER_ERROR, "1", "messages.filemanager"); 

			/* Builds a Response object */
//			response = ErrorMessageHandler.toResponse(Response.Status.INTERNAL_SERVER_ERROR, fileExists);

			response = Response.serverError().build();
			
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
			
			/* Checks if the Ontology File was found based on the given ID */
			if(dataFile == null) {
				/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
//				ErrorMessage dataFileNotFound = new ErrorMessage(Response.Status.NOT_FOUND, "15", "messages.filemanager"); 

				/* Builds a Response object */
//				response = ErrorMessageHandler.toResponse(Response.Status.NOT_FOUND, dataFileNotFound);

				response = Response.status(Response.Status.NOT_FOUND).build();
				
				return response;
			}
			
			dataFileTO = dataFile.createTransferObject();
			
			/* Builds the response with a filled DataFileTO */
			response = Response.ok(dataFileTO).build();

			/* Any exception leads to an error */
		}catch(NullPointerException nullPointerException) {
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
//			ErrorMessage noIdGiven = new ErrorMessage(Response.Status.BAD_REQUEST, "8", "messages.filemanager"); 

			/* Builds a Response object */
//			response = ErrorMessageHandler.toResponse(Response.Status.BAD_REQUEST, noIdGiven);

			response = Response.status(Response.Status.BAD_REQUEST).build();

		}catch(IllegalArgumentException illegalArgumentException) {
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
//			ErrorMessage sintaxError = new ErrorMessage(Response.Status.BAD_REQUEST, "16", "messages.filemanager"); 

			/* Builds a Response object */
//			response = ErrorMessageHandler.toResponse(Response.Status.BAD_REQUEST, sintaxError);

			response = Response.status(Response.Status.BAD_REQUEST).build();
			
		}catch(Exception exception) {
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
//			ErrorMessage fileExists = new ErrorMessage(Response.Status.INTERNAL_SERVER_ERROR, "1", "messages.filemanager"); 

			/* Builds a Response object */
//			response = ErrorMessageHandler.toResponse(Response.Status.INTERNAL_SERVER_ERROR, fileExists);

			response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
			
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
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response removeFile(String dataFileIds){
		Response response;

		try{
			/* Gets the DataFile id's from the dataFileIds string
			 * The id's are a json array [123,456,789] */
			Gson gson = new Gson();
			String[] ids = gson.fromJson(dataFileIds, String[].class);

			/* Runs all ids and fetches the DataFile object  */
			for(String dataFileId : ids){
				/* Removes the DataFile from the database */
				this.dataFileImpl.remove(dataFileId);
			}

			/* Gets the Response */
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
//			String language = PropertiesHandler.configProperties.getProperty("language");
//			ResourceBundle resourceBundle = PropertiesHandler.getMessages("messages.filemanager", language);
//			
//			String message = resourceBundle.getString("13") + " " + dataFileIds + " " + resourceBundle.getString("14");
//			
//			ErrorMessage corectlyRemoved = new ErrorMessage(); 
//			corectlyRemoved.setStatus(Response.Status.OK.getStatusCode());
//			corectlyRemoved.setMessage(message);

			/* Builds a Response object */
//			response = ErrorMessageHandler.toResponse(Response.Status.OK, corectlyRemoved);
			
			response = Response.status(Response.Status.OK).build();

		}catch(NullPointerException nullPointerException){
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
//			ErrorMessage noIDProvided = new ErrorMessage(Response.Status.BAD_REQUEST, "8", "messages.filemanager"); 

			/* Builds a Response object */
//			response = ErrorMessageHandler.toResponse(Response.Status.BAD_REQUEST, noIDProvided);

			response = Response.status(Response.Status.BAD_REQUEST).build();
			
		}catch(IllegalArgumentException illegalArgumentException) {
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
//			ErrorMessage illegalArgumentError = new ErrorMessage(Response.Status.BAD_REQUEST, "16", "messages.filemanager"); 

			/* Builds a Response object */
//			response = ErrorMessageHandler.toResponse(Response.Status.BAD_REQUEST, illegalArgumentError);

			response = Response.status(Response.Status.BAD_REQUEST).build();
			
		}catch(Exception exception){
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
//			ErrorMessage genericError = new ErrorMessage(Response.Status.INTERNAL_SERVER_ERROR, "1", "messages.filemanager"); 

			/* Builds a Response object */
//			response = ErrorMessageHandler.toResponse(Response.Status.INTERNAL_SERVER_ERROR, genericError);

			response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
			
			exception.printStackTrace();
		}

		return response;
	}
}
