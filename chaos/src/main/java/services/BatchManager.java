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

import database.implementations.BatchImpl;
import database.implementations.MappingImpl;
import domain.bo.mappings.Mapping;
import domain.bo.population.Batch;
import domain.to.BatchTO;
import domain.to.MappingTO;
import domain.to.wrappers.BatchTOWrapper;
import domain.to.wrappers.MappingTOWrapper;
import exceptions.ErrorMessage;
import exceptions.ErrorMessageHandler;
import properties.PropertiesHandler;

/**
 * This class implements a jax rs service layer
 * The implemented services have to do with handling Batch objects
 * @author João Cardoso
 *
 */
@Path("batchManager")
public class BatchManager {

	/** The connection to the database for Batch objects */
	private BatchImpl batchImpl = new BatchImpl();
	
	/** The connection to the database for Mapping objects */
	private MappingImpl mappingImpl = new MappingImpl();
	
	/**
	 * This method creates a new Batch object in the database based
	 * on the transfer object created in the client application.
	 * @param batchTO The client provided BatchTO transfer object
	 * @return An HTTP response according to the execution of the service.
	 */
	@POST
	@Path("/createBatch")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({MediaType.APPLICATION_JSON})
	public Response createMapping(String jsonBatch){
		Response response;
		try{
			/* Creates the Batch from the BatchTO */
			Gson gson = new Gson();
			BatchTO batchTO = gson.fromJson(jsonBatch, BatchTO.class);
			Batch batch = new Batch(batchTO);

			/* Stores the new Batch in the database */
			String batchId = this.batchImpl.save(batch);

			/* Creates the Response */
			response = Response.ok(gson.toJson(batchId)).build();

		}catch(NullPointerException nullPointerException){
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			ErrorMessage noBatchInputed = new ErrorMessage(Response.Status.BAD_REQUEST, "8", "messages.batchmanager"); 
			
			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.BAD_REQUEST, noBatchInputed);
			
		}catch(IllegalArgumentException illegalArgumentException){
		/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
		ErrorMessage badBatchJson = new ErrorMessage(Response.Status.BAD_REQUEST, "9", "messages.batchmanager"); 
		
		/* Builds a Response object */
		response = ErrorMessageHandler.toResponse(Response.Status.BAD_REQUEST, badBatchJson);
		
	}catch(Exception exception){
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			ErrorMessage error = new ErrorMessage(Response.Status.INTERNAL_SERVER_ERROR, "1", "messages.batchmanager"); 
			
			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.INTERNAL_SERVER_ERROR, error);
			
			exception.printStackTrace();
		}

		return response;
	}
	
	/**
	 * This method gets a Batch object when given its id
	 * @param batchID The Batch id
	 * @return An appropriate HTTP response
	 */
	@POST
	@Path("/getBatch")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getBatch(@FormParam("id") String batchID){
		/* Initializes the objects */
		Batch batch;
		BatchTO batchTO;
		Response response;

		try {
			/* Gets the Batch Object from the database and then builds the transfer object */
			batch = this.batchImpl.get(batchID);
			
			/* Checks if the Batch was found based on the given ID */
			if(batch == null) {
				/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
				ErrorMessage batchNotFound = new ErrorMessage(Response.Status.NOT_FOUND, "10", "messages.batchmanager"); 

				/* Builds a Response object */
				response = ErrorMessageHandler.toResponse(Response.Status.NOT_FOUND, batchNotFound);

				return response;
			}
			
			batchTO = batch.createTransferObject();

			/* Builds the response with a filled DataFileTO */
			response = Response.ok(batchTO).build();

			/* Any exception leads to an error */
		}catch(NullPointerException nullPointerException) {
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			ErrorMessage noBatchId = new ErrorMessage(Response.Status.BAD_REQUEST, "3", "messages.batchmanager"); 
			
			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.BAD_REQUEST, noBatchId);

		}catch(IllegalArgumentException illegalArgumentException) {
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			ErrorMessage illegalArgumentID = new ErrorMessage(Response.Status.BAD_REQUEST, "4", "messages.batchmanager"); 
			
			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.BAD_REQUEST, illegalArgumentID);

		}catch(Exception exception) {
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			ErrorMessage error = new ErrorMessage(Response.Status.INTERNAL_SERVER_ERROR, "1", "messages.batchmanager"); 
			
			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.INTERNAL_SERVER_ERROR, error);
			
			exception.printStackTrace();
		}

		return response;
	}

	/**
	 * This method returns all the Batches stored in the database
	 * @return An ArrayList with all Batch transfer objects
	 */
	@GET
	@Path("/getAllBatches")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllBatches(){
		/* Initializes the objects */
		BatchTOWrapper batchTOWrapper = new BatchTOWrapper();
		Response response;

		try {
			/* Get all Batch objects from the database */
			List<Batch> batches = this.batchImpl.getAll();

			/* Runs the Batch objects and fills the BatchTO array  */
			for(Batch batch : batches){
				BatchTO batchTO = batch.createTransferObject();
				batchTOWrapper.batchesTO.add(batchTO);
			}

			/* Builds the response */
			response = Response.ok(batchTOWrapper).build();

		}catch(Exception exception) {
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			ErrorMessage error = new ErrorMessage(Response.Status.INTERNAL_SERVER_ERROR, "1", "messages.batchmanager"); 
			
			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.INTERNAL_SERVER_ERROR, error);
			
			exception.printStackTrace();
		}

		return response;
	}
	
	/**
	 * This method removes a list of Batch objects from the database
	 * @param batchIds The Batch id list. All ids are sepparated by ",".
	 * @return An HTTP response according to the execution of the service.
	 */
	@POST
	@Path("/removeBatch")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response removeBatch(String batchIds){
		Response response;
		try{
			/* Gets the DataFile id's from the dataFileIds string
			 * The id's are a json array [123,456,789] */
			Gson gson = new Gson();
			String[] ids = gson.fromJson(batchIds, String[].class);

			/* Runs all ids and fetches the Batch object  */
			for(String batchID : ids){
				/* Removes the Batch from the database */
				this.batchImpl.remove(batchID);
			}

			/* Gets the Response */
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			String language = PropertiesHandler.configProperties.getProperty("language");
			ResourceBundle resourceBundle = PropertiesHandler.getMessages("messages.batchmanager", language);
			
			String message = resourceBundle.getString("11") + " " + batchIds + " " + resourceBundle.getString("12");
			
			ErrorMessage corectlyRemoved = new ErrorMessage(); 
			corectlyRemoved.setStatus(Response.Status.OK.getStatusCode());
			corectlyRemoved.setMessage(message);

			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.OK, corectlyRemoved);

		}catch(NullPointerException nullPointerException) {
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			ErrorMessage mappingIDnull = new ErrorMessage(Response.Status.BAD_REQUEST, "3", "messages.batchmanager"); 
			
			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.BAD_REQUEST, mappingIDnull);

		}catch(IllegalArgumentException illegalArgumentException) {
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			ErrorMessage illegalArgumentID = new ErrorMessage(Response.Status.BAD_REQUEST, "4", "messages.batchmanager"); 
			
			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.BAD_REQUEST, illegalArgumentID);

		}catch(Exception exception){
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			ErrorMessage error = new ErrorMessage(Response.Status.INTERNAL_SERVER_ERROR, "1", "messages.batchmanager"); 
			
			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.INTERNAL_SERVER_ERROR, error);
			
			exception.printStackTrace();
		}

		return response;
	}
	
	/**
	 * This method adds a Mapping to an existing Batch
	 * @param mappingId The Mapping id
	 * @param batchId The Batch id
	 * @return An HTTP response according to the execution of the service.
	 */
	@POST
	@Path("/addMappingToBatch")
	@Produces(MediaType.APPLICATION_JSON)
	public Response addMappingToBatch(@FormParam("mappingId") String mappingId, @FormParam("batchId") String batchId){
		Response response;
		try{
			/* Gets the Batch from the given id */
			Batch batch = this.batchImpl.get(batchId);	
			
			/* Gets the Batch's Mapping list and adds a new id to the list if it doesn't exist already */
			ArrayList<ObjectId> mappingList = batch.getMappings();
			ObjectId newMappingId = new ObjectId(mappingId);

			if(mappingList.contains(newMappingId)){
				/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
				ErrorMessage alreadyAdded = new ErrorMessage(Response.Status.BAD_REQUEST, "6", "messages.batchmanager"); 
				
				/* Builds a Response object */
				response = ErrorMessageHandler.toResponse(Response.Status.BAD_REQUEST, alreadyAdded);
				
				return response;
				
			}else{
				/* The Batch is updated with the new Mapping List.
				 * And the Database object is updated with the new Batch*/
				mappingList.add(newMappingId);
				batch.setMappings(mappingList);

				this.batchImpl.replace(batchId, batch);
			}

			
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			String language = PropertiesHandler.configProperties.getProperty("language");
			ResourceBundle resourceBundle = PropertiesHandler.getMessages("messages.batchmanager", language);
			
			String message = resourceBundle.getString("13") + " " + mappingId + " " + resourceBundle.getString("14");
			
			ErrorMessage correctlyAdded = new ErrorMessage(); 
			correctlyAdded.setStatus(Response.Status.OK.getStatusCode());
			correctlyAdded.setMessage(message);

			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.OK, correctlyAdded);

			
		}catch(NullPointerException nullPointerException) {
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			ErrorMessage mappingIDnull = new ErrorMessage(Response.Status.NOT_FOUND, "7", "messages.batchmanager"); 
			
			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.NOT_FOUND, mappingIDnull);

		}catch(IllegalArgumentException illegalArgumentException) {
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			ErrorMessage illegalArgumentID = new ErrorMessage(Response.Status.BAD_REQUEST, "5", "messages.batchmanager"); 
		
			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.BAD_REQUEST, illegalArgumentID);

		}catch(Exception exception){
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			ErrorMessage error = new ErrorMessage(Response.Status.INTERNAL_SERVER_ERROR, "1", "messages.batchmanager"); 
			
			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.INTERNAL_SERVER_ERROR, error);
			
			exception.printStackTrace();
		}

		return response;
	}
	
	/**
	 * Gets all the MappingTO transfer objects from a given Batch
	 * @param batchId The Batch id
	 * @return An Array with all MappingTO transfer objects that represent the Mapping objects assigned to the Batch
	 */
	@POST
	@Path("/getAllMappingsFromBatch")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllMappingsFromBatch(@FormParam("batchId") String batchId){
		/* Initializes the objects */
		MappingTOWrapper mappingTOWrapper = new MappingTOWrapper();
		Response response;

		try {
			/* Gets the Batch and its Mapping List */
			Batch batch = this.batchImpl.get(batchId);
			ArrayList<ObjectId> mappingList = batch.getMappings();

			/* Runs the Mappings list and creates MappingTO objects */
			for(ObjectId mappingId : mappingList){
				Mapping mapping = this.mappingImpl.get(mappingId.toString());
				MappingTO mappingTO = mapping.createTransferObject();
				mappingTOWrapper.mappingsTO.add(mappingTO);
			}
			/* Builds the response */
			response = Response.ok(mappingTOWrapper).build();

		}catch(NullPointerException nullPointerException) {
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			ErrorMessage mappingIDnull = new ErrorMessage(Response.Status.BAD_REQUEST, "3", "messages.batchmanager"); 
			
			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.BAD_REQUEST, mappingIDnull);

		}catch(IllegalArgumentException illegalArgumentException) {
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			ErrorMessage illegalArgumentID = new ErrorMessage(Response.Status.BAD_REQUEST, "4", "messages.batchmanager"); 
			
			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.BAD_REQUEST, illegalArgumentID);

		}catch(Exception exception) {
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			ErrorMessage error = new ErrorMessage(Response.Status.INTERNAL_SERVER_ERROR, "1", "messages.batchmanager"); 
			
			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.INTERNAL_SERVER_ERROR, error);
			
			exception.printStackTrace();
		}

		return response;
	}
	
	/**
	 * This method removes a Mapping from an existing Batch
	 * @param batchId The Batch id
	 * @param mappingId The Mapping id
	 * @return An HTTP response according to the execution of the service.
	 */
	@POST
	@Path("/removeMappingFromBatch")
	public Response removeMappingFromBatch(@FormParam("batchId") String batchId, @FormParam("mappingId") String mappingId){
		Response response;
		try{
			/* Gets the Batch from the given id */
			Batch batch = this.batchImpl.get(batchId);

			/* Gets the Batche's Mapping list and removes the Mapping from the list if it exists*/
			ArrayList<ObjectId> mappingsList = batch.getMappings();
			ObjectId newMappingId = new ObjectId(mappingId);

			if(mappingsList.contains(newMappingId)){
				/* The Batch is updated with the new Mapping List.
				 * And the Database object is updated with the new Batch*/
				mappingsList.remove(newMappingId);
				batch.setMappings(mappingsList);

				this.batchImpl.replace(batchId, batch);
			}else{
				/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
				ErrorMessage doesNotExist = new ErrorMessage(Response.Status.BAD_REQUEST, "2", "messages.batchmanager"); 
				
				/* Builds a Response object */
				response = ErrorMessageHandler.toResponse(Response.Status.BAD_REQUEST, doesNotExist);
				
				return response;
			}

			/* Gets the Response */
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			String language = PropertiesHandler.configProperties.getProperty("language");
			ResourceBundle resourceBundle = PropertiesHandler.getMessages("messages.batchmanager", language);
			
			String message = resourceBundle.getString("15") + " " + mappingId + " " + resourceBundle.getString("12");
			
			ErrorMessage corectlyRemoved = new ErrorMessage(); 
			corectlyRemoved.setStatus(Response.Status.OK.getStatusCode());
			corectlyRemoved.setMessage(message);
			
			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.OK, corectlyRemoved);
			
		}catch(NullPointerException nullPointerException) {
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			ErrorMessage mappingIDnull = new ErrorMessage(Response.Status.BAD_REQUEST, "7", "messages.batchmanager"); 
			
			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.BAD_REQUEST, mappingIDnull);

		}catch(IllegalArgumentException illegalArgumentException) {
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			ErrorMessage illegalArgumentID = new ErrorMessage(Response.Status.BAD_REQUEST, "5", "messages.batchmanager"); 
		
			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.BAD_REQUEST, illegalArgumentID);

		}catch(Exception exception){
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			ErrorMessage error = new ErrorMessage(Response.Status.INTERNAL_SERVER_ERROR, "1", "messages.mappingmanager"); 
			
			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.INTERNAL_SERVER_ERROR, error);
			
			exception.printStackTrace();
		}

		return response;
	}
}
