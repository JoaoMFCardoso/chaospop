package services;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
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

import utils.FileOperationsUtils;
import database.implementations.DataFileImpl;
import database.implementations.OntologyFileImpl;
import domain.bo.ontologies.OntologyFile;
import domain.bo.parsers.DataFile;
import domain.to.DataFileTO;
import file.operations.FileOperations;
import file.sftp.SFTPServerConnectionManager;

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

		// save it
		String uploadedFileName = fileDetail.getFileName();
		File uploadedFile = FileOperationsUtils.writeToFile(uploadedInputStream, uploadedFileName);

		Response response;
		Pair<File, String> processedFileData;
		File processedFile = uploadedFile;
		String processedFileID = "";
		
		try{
			/* Processes the File */
			processedFileData = FileOperations.fileProcessor(uploadedFile); 
			
			processedFile = processedFileData.getValue0();
			processedFileID = processedFileData.getValue1();

			/* Gets the Response */
			response = Response.ok(processedFileID).build();
		}catch(Exception exception){
			/* Sends a response that is not ok */
			response = Response.status(500).build();
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
	public Response uploadFileSFTP(@FormParam("ontologyFileId") String ontologyFileId){
		File file = null;
		Response response;
		try{
			/* Gets the OntologyFile from the database */
			OntologyFileImpl ontologyFileImpl = new OntologyFileImpl();
			OntologyFile ontologyFile = ontologyFileImpl.get(ontologyFileId);

			/* Checks that the namespace is compliant with the sftp server namespace
			 * In case it is not compliant an exception is thrown */
			String ontologyNamespace = ontologyFile.getNamespace().toString();
			if(!FileOperationsUtils.isSFTPServerCompliant(ontologyNamespace)){
				throw new Exception("Upload Failed - Namespace is not compliant with the SFTP Server Base Namespace");
			}

			/* Uploads the file to the SFTP server */
			SFTPServerConnectionManager sftpManager = new SFTPServerConnectionManager();
			file = new File(ontologyFile.getPath());
			sftpManager.uploadSFTPFile(ontologyFile.getPath(), ontologyNamespace);

			/* Updates the OntologyFile */
			ontologyFile.setPath(null);
			ontologyFileImpl.replace(ontologyFileId, ontologyFile);

			/* Gets the Response */
			response = Response.status(200).build();
		}catch(Exception exception){
			exception.printStackTrace();
			/* Sends a response that is not ok */
			response = Response.status(500).build();
		}finally{
			/* Deletes the local created file */
			file.delete();
			FileOperationsUtils.deleteDirectoryStructure(file);
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
		try{

		/* Gets the file from the SFTP server */
		SFTPServerConnectionManager sftpManager = new SFTPServerConnectionManager();
		String localPath = sftpManager.downloadSFTPFile(fileName);
		file = new File(localPath);
		sftpManager.disconnect();

		/* Gets the Response */
		response = Response.ok(file, MediaType.APPLICATION_OCTET_STREAM)
        .header("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"")
        .build();
	}catch(Exception exception){
		exception.printStackTrace();
		/* Sends a response that is not ok */
		response = Response.status(500).build();
	}finally{
		/* Deletes the temporarily created file */
		file.delete();
		FileOperationsUtils.deleteDirectoryStructure(file);
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
	public ArrayList<DataFileTO> listDataFiles(){
		ArrayList<DataFileTO> dataFilesTO = new ArrayList<DataFileTO>();

		/* Get all DataFile objects from the database */
		List<DataFile> dataFiles = this.dataFileImpl.getAll();

		/* Runs the DataFile objects and fills the DataFileTO array  */
		for(DataFile dataFile : dataFiles){
			DataFileTO dataFileTO = dataFile.createTransferObject();
			dataFilesTO.add(dataFileTO);
		}

		return dataFilesTO;
	}

	/**
	 * This method gets a DataFile object when given its id
	 * @param dataFileId The DataFile id
	 * @return A DataFile transfer object
	 */
	@POST
	@Path("/getFile")
	@Produces(MediaType.APPLICATION_JSON)
	public DataFileTO getDataFile(@FormParam("id") String dataFileId){
		/* Gets the DataFile object from the database and builds the transfer object */
		DataFile dataFile = this.dataFileImpl.get(dataFileId);
		DataFileTO dataFileTO = dataFile.createTransferObject();

		return dataFileTO;
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
			response = Response.status(200).build();
		}catch(Exception exception){
			exception.printStackTrace();
			/* Sends a response that is not ok */
			response = Response.status(500).build();
		}

		return response;
	}
}
