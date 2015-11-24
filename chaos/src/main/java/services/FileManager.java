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

import org.bson.types.ObjectId;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import utils.FileOperationsUtils;
import database.implementations.DataFileImpl;
import domain.bo.parsers.DataFile;
import domain.to.DataFileTO;
import file.operations.FileOperations;

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
	public Response addFile(@FormDataParam("file") InputStream uploadedInputStream,
			@FormDataParam("file") FormDataContentDisposition fileDetail) {

		// save it
		String uploadedFileName = fileDetail.getFileName();
		File uploadedFile = FileOperationsUtils.writeToFile(uploadedInputStream, uploadedFileName);

		Response response;
		File processedFile = uploadedFile;
		try{
			/* Processes the File */
			processedFile = FileOperations.fileProcessor(uploadedFile);

			/* Gets the Response */
			response = Response.status(200).build();
		}catch(Exception exception){
			/* Sends a response that is not ok */
			response = Response.status(500).build();
		}finally{

			/* Deletes the temporary file */
			processedFile.delete();
			FileOperationsUtils.deleteDirectoryStructure(processedFile);
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
	 * This method removes a DataFile from the database
	 * @param dataFileId The DataFile id
	 * @return 200 if everything went well, 500 if not.
	 */
	@POST
	@Path("/removeFile")
	public Response removeFile(@FormParam("id") String dataFileId){
		Response response;
		try{
			/* Removes the DataFile from the database */
			this.dataFileImpl.remove(new ObjectId(dataFileId));

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
