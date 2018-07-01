package services;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.bson.types.ObjectId;

import com.google.gson.Gson;

import database.implementations.DataFileImpl;
import database.implementations.MappingImpl;
import domain.bo.mappings.Mapping;
import domain.bo.parsers.DataFile;
import domain.to.DataFileTO;
import domain.to.MappingTO;
import domain.to.wrappers.DataFileTOWrapper;
import domain.to.wrappers.MappingTOWrapper;
import exceptions.ErrorMessage;
import exceptions.ErrorMessageHandler;
import properties.PropertiesHandler;

/**
 * This class implements a jax rs service layer
 * The implemented services have to do with handling Mapping objects
 * @author João Cardoso
 *
 */
@Path("mappingManager")
public class MappingManager {

	/** The connection to the database for Mapping objects */
	private MappingImpl mappingImpl = new MappingImpl();

	/** The connection to the database for DataFile objects */
	private DataFileImpl dataFileImpl = new DataFileImpl();

	/**
	 * This method creates a new Mapping object in the database based
	 * on the transfer object created in the client application.
	 * @param mappingTO The client provided MappingTO transfer object
	 * @return An HTTP response according to the execution of the service.
	 */
	@POST
	@Path("/createMapping")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createMapping(String jsonMappingTO){
		Response response;
		try{
			/* Creates the Mapping from the MappingTO */
			Gson gson = new Gson();
			MappingTO mappingTO = gson.fromJson(jsonMappingTO, MappingTO.class);
			Mapping mapping = new Mapping(mappingTO);

			/* Stores the new mapping in the database */
			String id = this.mappingImpl.save(mapping);

			/* Creates the Response */
			response = Response.ok(gson.toJson(id)).build();

		}catch(NullPointerException nullPointerException){
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			ErrorMessage nullfield = new ErrorMessage(Response.Status.BAD_REQUEST, "2", "messages/mappingmanager"); 
			
			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.BAD_REQUEST, nullfield);
			
		}catch(Exception exception){
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			ErrorMessage error = new ErrorMessage(Response.Status.INTERNAL_SERVER_ERROR, "1", "messages/mappingmanager"); 
			
			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.INTERNAL_SERVER_ERROR, error);
			
			exception.printStackTrace();
		}

		return response;
	}
	
	/**
	 * This method gets a Mapping object when given its id
	 * @param mappingID The Mapping id
	 * @return A Mapping transfer object
	 */
	@POST
	@Path("/getMapping")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getMapping(@FormParam("id") String mappingID){
		/* Initializes the objects */
		Mapping mapping;
		MappingTO mappingTO;
		Response response;

		try {
			/* Gets the IndividualMapping Object from the database and then builds the transfer object */
			mapping = this.mappingImpl.get(mappingID);
			mappingTO = mapping.createTransferObject();

			/* Builds the response with a filled DataFileTO */
			response = Response.ok(mappingTO).build();

			/* Any exception leads to an error */
		}catch(NullPointerException nullPointerException) {
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			ErrorMessage mappingIDnull = new ErrorMessage(Response.Status.BAD_REQUEST, "3", "messages/mappingmanager"); 
			
			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.BAD_REQUEST, mappingIDnull);

		}catch(IllegalArgumentException illegalArgumentException) {
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			ErrorMessage illegalArgumentID = new ErrorMessage(Response.Status.BAD_REQUEST, "4", "messages/mappingmanager"); 
			
			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.BAD_REQUEST, illegalArgumentID);

		}catch(Exception exception) {
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			ErrorMessage error = new ErrorMessage(Response.Status.INTERNAL_SERVER_ERROR, "1", "messages/mappingmanager"); 
			
			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.INTERNAL_SERVER_ERROR, error);
			
			exception.printStackTrace();
		}

		return response;
	}

	/**
	 * This method returns all the Mappings stored in the database
	 * @return An ArrayList with all Mappings transfer objects
	 */
	@GET
	@Path("/getAllMappings")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllMappings(){
		/* Initializes the objects */
		MappingTOWrapper mappingTOWrapper = new MappingTOWrapper();
		Response response;

		try {
			/* Get all Mapping objects from the database */
			List<Mapping> mappings = this.mappingImpl.getAll();

			/* Runs the Mapping objects and fills the MappingTO array  */
			for(Mapping mapping : mappings){
				MappingTO mappingTO = mapping.createTransferObject();
				mappingTOWrapper.mappingsTO.add(mappingTO);
			}

			/* Builds the response */
			response = Response.ok(mappingTOWrapper).build();

		}catch(Exception exception) {
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			ErrorMessage error = new ErrorMessage(Response.Status.INTERNAL_SERVER_ERROR, "1", "messages/mappingmanager"); 
			
			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.INTERNAL_SERVER_ERROR, error);
			
			exception.printStackTrace();
		}

		return response;
	}

	/**
	 * This method removes a list of Mapping objects from the database
	 * @param mappingIds The Mapping id list. All ids are sepparated by ",".
	 * @return An HTTP response according to the execution of the service.
	 */
	@POST
	@Path("/removeMapping")
	@Produces(MediaType.APPLICATION_JSON)
	public Response removeMapping(@FormParam("ids") String mappingsIds){
		Response response;
		try{
			/* Gets the Mappings id's from the mappingsIds string
			 * The id's are a json array [123,456,789] */
			Gson gson = new Gson();
			String[] ids = gson.fromJson(mappingsIds, String[].class);

			/* Runs all ids and fetches the Mapping object  */
			for(String mappingId : ids){
				/* Removes the Mapping from the database */
				this.mappingImpl.remove(mappingId);
			}

			/* Gets the Response */
			/* Gets the Response */
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			String language = PropertiesHandler.configProperties.getProperty("language");
			ResourceBundle resourceBundle = PropertiesHandler.getMessages("messages/mappingmanager", language);
			
			String message = resourceBundle.getString("8") + " " + mappingsIds + " " + resourceBundle.getString("10");
			
			ErrorMessage corectlyRemoved = new ErrorMessage(); 
			corectlyRemoved.setStatus(Response.Status.OK.getStatusCode());
			corectlyRemoved.setMessage(message);

			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.OK, corectlyRemoved);

		}catch(NullPointerException nullPointerException) {
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			ErrorMessage mappingIDnull = new ErrorMessage(Response.Status.BAD_REQUEST, "3", "messages/mappingmanager"); 
			
			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.BAD_REQUEST, mappingIDnull);

		}catch(IllegalArgumentException illegalArgumentException) {
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			ErrorMessage illegalArgumentID = new ErrorMessage(Response.Status.BAD_REQUEST, "4", "messages/mappingmanager"); 
			
			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.BAD_REQUEST, illegalArgumentID);

		}catch(Exception exception){
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			ErrorMessage error = new ErrorMessage(Response.Status.INTERNAL_SERVER_ERROR, "1", "messages/mappingmanager"); 
			
			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.INTERNAL_SERVER_ERROR, error);
			
			exception.printStackTrace();
		}

		return response;
	}

	/**
	 * This method adds a DataFile to an existing Mapping
	 * @param mappingId The Mapping id
	 * @param dataFileId The DataFile id
	 * @return An HTTP response according to the execution of the service.
	 */
	@POST
	@Path("/addDataFileToMapping")
	@Produces(MediaType.APPLICATION_JSON)
	public Response addDataFileToMapping(@FormParam("mappingId") String mappingId, @FormParam("dataFileId") String dataFileId){
		Response response;
		try{
			/* Gets the Mapping from the given id */
			Mapping mapping = this.mappingImpl.get(mappingId);

			/* Gets the Mapping's file list and adds a new id to the list if it doesn't exist already */
			ArrayList<ObjectId> fileList = mapping.getFileList();
			ObjectId newFileId = new ObjectId(dataFileId);

			if(fileList.contains(newFileId)){
				/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
				ErrorMessage alreadyAdded = new ErrorMessage(Response.Status.BAD_REQUEST, "6", "messages/mappingmanager"); 
				
				/* Builds a Response object */
				response = ErrorMessageHandler.toResponse(Response.Status.BAD_REQUEST, alreadyAdded);
				
				return response;
				
			}else{
				/* The Mapping is updated with the new fileList.
				 * And the Database object is updated with the new Mapping*/
				fileList.add(newFileId);
				mapping.setFileList(fileList);

				this.mappingImpl.replace(mappingId, mapping);
			}

			/* Gets the Response */
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			String language = PropertiesHandler.configProperties.getProperty("language");
			ResourceBundle resourceBundle = PropertiesHandler.getMessages("messages/mappingmanager", language);
			
			String message = resourceBundle.getString("11") + " " + dataFileId + " " + resourceBundle.getString("12");
			
			ErrorMessage correctlyAdded = new ErrorMessage(); 
			correctlyAdded.setStatus(Response.Status.OK.getStatusCode());
			correctlyAdded.setMessage(message);

			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.OK, correctlyAdded);
			
		}catch(NullPointerException nullPointerException) {
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			ErrorMessage mappingIDnull = new ErrorMessage(Response.Status.BAD_REQUEST, "7", "messages/mappingmanager"); 
			
			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.BAD_REQUEST, mappingIDnull);

		}catch(IllegalArgumentException illegalArgumentException) {
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			ErrorMessage illegalArgumentID = new ErrorMessage(Response.Status.BAD_REQUEST, "5", "messages/mappingmanager"); 
		
			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.BAD_REQUEST, illegalArgumentID);

		}catch(Exception exception){
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			ErrorMessage error = new ErrorMessage(Response.Status.INTERNAL_SERVER_ERROR, "1", "messages/mappingmanager"); 
			
			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.INTERNAL_SERVER_ERROR, error);
			
			exception.printStackTrace();
		}

		return response;
	}
	
	/**
	 * Gets all the DataFileTO transfer objects from a given Mapping
	 * @param mappingId The Mapping id
	 * @return An Array with all DataFileTO transfer objects that represent the DataFile objects assigned to the Mapping
	 */
	@POST
	@Path("/getAllDataFilesFromMapping")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllDataFilesFromMapping(@FormParam("mappingId") String mappingId){
		/* Initializes the objects */
		DataFileTOWrapper dataFileTOWrapper = new DataFileTOWrapper();
		Response response;

		try {
			/* Gets the Mapping and its DataFile List */
			Mapping mapping = this.mappingImpl.get(mappingId);
			ArrayList<ObjectId> fileList = mapping.getFileList();

			/* Runs the DataFile list and creates DataFileTO objects */
			for(ObjectId dataFileId : fileList){
				DataFile dataFile = this.dataFileImpl.get(dataFileId.toString());
				DataFileTO dataFileTO = dataFile.createTransferObject();
				dataFileTOWrapper.dataFilesTO.add(dataFileTO);
			}
			/* Builds the response */
			response = Response.ok(dataFileTOWrapper).build();

		}catch(NullPointerException nullPointerException) {
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			ErrorMessage mappingIDnull = new ErrorMessage(Response.Status.BAD_REQUEST, "7", "messages/mappingmanager"); 
			
			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.BAD_REQUEST, mappingIDnull);

		}catch(IllegalArgumentException illegalArgumentException) {
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			ErrorMessage illegalArgumentID = new ErrorMessage(Response.Status.BAD_REQUEST, "5", "messages/mappingmanager"); 
			
			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.BAD_REQUEST, illegalArgumentID);

		}catch(Exception exception) {
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			ErrorMessage error = new ErrorMessage(Response.Status.INTERNAL_SERVER_ERROR, "1", "messages/mappingmanager"); 
			
			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.INTERNAL_SERVER_ERROR, error);
			
			exception.printStackTrace();
		}

		return response;

	}

	/**
	 * This method removes a DataFile from an existing Mapping
	 * @param mappingId The Mapping id
	 * @param dataFileId The DataFile id
	 * @return An HTTP response according to the execution of the service.
	 */
	@POST
	@Path("/removeDataFileFromMapping")
	public Response removeDataFileFromMapping(@FormParam("mappingId") String mappingId, @FormParam("dataFileId") String dataFileId){
		Response response;
		try{
			/* Gets the Mapping from the given id */
			Mapping mapping = this.mappingImpl.get(mappingId);

			/* Gets the Mapping's file list and removes the Datafile from the list if it exists*/
			ArrayList<ObjectId> fileList = mapping.getFileList();
			ObjectId newFileId = new ObjectId(dataFileId);

			if(fileList.contains(newFileId)){
				/* The Mapping is updated with the new fileList.
				 * And the Database object is updated with the new Mapping*/
				fileList.remove(newFileId);
				mapping.setFileList(fileList);

				this.mappingImpl.replace(mappingId, mapping);
			}else{
				/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
				ErrorMessage doesNotExist = new ErrorMessage(Response.Status.BAD_REQUEST, "2", "messages/mappingmanager"); 
				
				/* Builds a Response object */
				response = ErrorMessageHandler.toResponse(Response.Status.BAD_REQUEST, doesNotExist);
				
				return response;
			}

			/* Gets the Response */
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			String language = PropertiesHandler.configProperties.getProperty("language");
			ResourceBundle resourceBundle = PropertiesHandler.getMessages("messages/mappingmanager", language);
			
			String message = resourceBundle.getString("11") + " " + dataFileId + " " + resourceBundle.getString("10");
			
			ErrorMessage correctlyRemoved = new ErrorMessage(); 
			correctlyRemoved.setStatus(Response.Status.OK.getStatusCode());
			correctlyRemoved.setMessage(message);

			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.OK, correctlyRemoved);
			
		}catch(NullPointerException nullPointerException) {
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			ErrorMessage mappingIDnull = new ErrorMessage(Response.Status.BAD_REQUEST, "7", "messages/mappingmanager"); 
			
			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.BAD_REQUEST, mappingIDnull);

		}catch(IllegalArgumentException illegalArgumentException) {
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			ErrorMessage illegalArgumentID = new ErrorMessage(Response.Status.BAD_REQUEST, "5", "messages/mappingmanager"); 
		
			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.BAD_REQUEST, illegalArgumentID);

		}catch(Exception exception){
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			ErrorMessage error = new ErrorMessage(Response.Status.INTERNAL_SERVER_ERROR, "1", "messages/mappingmanager"); 
			
			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.INTERNAL_SERVER_ERROR, error);
			
			exception.printStackTrace();
		}

		return response;
	}
}
