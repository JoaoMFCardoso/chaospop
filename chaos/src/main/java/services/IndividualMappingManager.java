package services;

import java.util.List;
import java.util.ResourceBundle;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;

import database.implementations.IndividualMappingsImpl;
import domain.bo.mappings.IndividualMapping;
import domain.to.IndividualMappingTO;
import domain.to.wrappers.IndividualMappingTOWrapper;
import exceptions.ErrorMessage;
import exceptions.ErrorMessageHandler;
import properties.PropertiesHandler;

/**
 * This class implements a jax rs service layer
 * The implemented services have to do with handling Mapping objects
 * @author João Cardoso
 *
 */
@Path("individualMappingManager")
public class IndividualMappingManager {

	/** The connection to the database for IndividualMapping objects */
	private IndividualMappingsImpl individualMappingsImpl = new IndividualMappingsImpl();

	/**
	 * Creates a new IndividualMapping in the Database
	 * @param individualMappingTO The individual Mapping transfer object
	 * @return An HTTP response according to the execution of the service.
	 */
	@POST
	@Path("/createIndividualMapping")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createIndividualMapping(String jsonResponse){
		Response response;
		try{
			Gson gson = new Gson();
			IndividualMappingTO individualMappingTO = gson.fromJson(jsonResponse, IndividualMappingTO.class);
			
			/* Creates the individual mapping from the individual mapping transfer object */
			IndividualMapping individualMapping = new IndividualMapping(individualMappingTO);

			/* Stores the new individual mapping in the database */
			String individualMappingID = this.individualMappingsImpl.save(individualMapping);

			/* Creates the Response */
			response = Response.ok(gson.toJson(individualMappingID)).build();
			
		}catch(NullPointerException nullPointerException){
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			ErrorMessage requiredFieldNull = new ErrorMessage(Response.Status.BAD_REQUEST, "2", "messages.individualmappingmanager"); 
			
			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.BAD_REQUEST, requiredFieldNull);
			
		}catch(Exception exception){
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			ErrorMessage error = new ErrorMessage(Response.Status.INTERNAL_SERVER_ERROR, "1", "messages.individualmappingmanager"); 
			
			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.INTERNAL_SERVER_ERROR, error);
			
			exception.printStackTrace();
		}

		return response;
	}

	/**
	 * This method gets a IndividualMapping object when given its id
	 * @param individualMappingID The IndividualMapping id
	 * @return A IndividualMapping transfer object
	 */
	@POST
	@Path("/getIndividualMapping")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getIndividualMapping(@FormParam("id") String individualMappingID){
		/* Initializes the variables */
		IndividualMapping individualMapping;
		IndividualMappingTO individualMappingTO;
		Response response;
		
		try {
		/* Gets the IndividualMapping Object from the database and then builds the transfer object */
		individualMapping = this.individualMappingsImpl.get(individualMappingID);
		
		/* Checks if the Individual Mapping was found based on the given ID */
		if(individualMapping == null) {
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			ErrorMessage individualMappingNotFound = new ErrorMessage(Response.Status.NOT_FOUND, "9", "messages.individualmappingmanager"); 

			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.NOT_FOUND, individualMappingNotFound);

			return response;
		}
		
		individualMappingTO = individualMapping.createTransferObject();

		Gson gson = new Gson();
		String jsonResponse = gson.toJson(individualMappingTO);
		
		response = Response.ok(jsonResponse).build();
		
		/* Any exception leads to an error */
		}catch(NullPointerException nullPointerException) {
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			ErrorMessage requiredFieldNull = new ErrorMessage(Response.Status.BAD_REQUEST, "3", "messages.individualmappingmanager"); 
			
			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.BAD_REQUEST, requiredFieldNull);

		}catch(IllegalArgumentException illegalArgumentException) {
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			ErrorMessage illegalArgument = new ErrorMessage(Response.Status.BAD_REQUEST, "4", "messages.individualmappingmanager"); 
			
			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.BAD_REQUEST, illegalArgument);

		}catch(Exception exception) {
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			ErrorMessage error = new ErrorMessage(Response.Status.INTERNAL_SERVER_ERROR, "1", "messages.individualmappingmanager"); 
			
			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.INTERNAL_SERVER_ERROR, error);
			
			exception.printStackTrace();
		}
		
		return response;
	}
	
	/**
	 * Replaces as existing IndividualMapping in the Database
	 * @param individualMappingTO The individual Mapping transfer object
	 * @return An appropriate status according to the execution
	 */
	@POST
	@Path("/replaceIndividualMapping")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response replaceIndividualMapping(String jsonResponse){
		Response response;
		try{
			/* Creates the individual mapping from the individual mapping transfer object */
			Gson gson = new Gson();
			IndividualMappingTO individualMappingTO = gson.fromJson(jsonResponse, IndividualMappingTO.class);
			
			IndividualMapping individualMapping = new IndividualMapping(individualMappingTO);

			/* Stores the new individual mapping in the database */
			this.individualMappingsImpl.replace(individualMappingTO.get_id(), individualMapping);

			/* Creates the Response */
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			String language = PropertiesHandler.configProperties.getProperty("language");
			ResourceBundle resourceBundle = PropertiesHandler.getMessages("messages.individualmappingmanager", language);
			
			String message = resourceBundle.getString("7") + " " + individualMappingTO.get_id() + " " + resourceBundle.getString("10");
			
			ErrorMessage corectlyReplaced = new ErrorMessage(); 
			corectlyReplaced.setStatus(Response.Status.OK.getStatusCode());
			corectlyReplaced.setMessage(message);

			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.OK, corectlyReplaced);
			
		}catch(NullPointerException nullPointerException) {
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			ErrorMessage individualMappingToNull = new ErrorMessage(Response.Status.BAD_REQUEST, "5", "messages.individualmappingmanager"); 
			
			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.BAD_REQUEST, individualMappingToNull);

		}catch(IllegalArgumentException illegalArgumentException) {
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			ErrorMessage illegalArgument = new ErrorMessage(Response.Status.BAD_REQUEST, "6", "messages.individualmappingmanager"); 
			
			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.BAD_REQUEST, illegalArgument);

		}catch(Exception exception){
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			ErrorMessage error = new ErrorMessage(Response.Status.INTERNAL_SERVER_ERROR, "1", "messages.individualmappingmanager"); 
			
			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.INTERNAL_SERVER_ERROR, error);
			
			exception.printStackTrace();
		}

		return response;
	}

	/**
	 * This method removes a list of IndividualMapping objects from the database
	 * @param individualMappingIds The IndividualMapping id list. All ids are sepparated by ",".
	 * @return An appropriate status according to the execution
	 */
	@POST
	@Path("/removeIndividualMapping")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response removeIndividualMapping(String individualMappingIds){
		Response response;
		try{
			/* Gets the Individual Mapping id's from the individualMappingIds string
			 * The id's are a json array [123,456,789] */
			Gson gson = new Gson();
			String[] ids = gson.fromJson(individualMappingIds, String[].class);

			/* Runs all ids and fetches the IndividualMapping object  */
			for(String individualMappingId : ids){
				/* Removes the IndividualMapping from the database */
				this.individualMappingsImpl.remove(individualMappingId);
			}

			/* Gets the Response */
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			String language = PropertiesHandler.configProperties.getProperty("language");
			ResourceBundle resourceBundle = PropertiesHandler.getMessages("messages.individualmappingmanager", language);
			
			String message = resourceBundle.getString("7") + " " + individualMappingIds + " " + resourceBundle.getString("8");
			
			ErrorMessage corectlyRemoved = new ErrorMessage(); 
			corectlyRemoved.setStatus(Response.Status.OK.getStatusCode());
			corectlyRemoved.setMessage(message);

			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.OK, corectlyRemoved);
			
		}catch(NullPointerException nullPointerException) {
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			ErrorMessage requiredFieldNull = new ErrorMessage(Response.Status.BAD_REQUEST, "3", "messages.individualmappingmanager"); 
			
			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.BAD_REQUEST, requiredFieldNull);

		}catch(IllegalArgumentException illegalArgumentException) {
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			ErrorMessage illegalArgument = new ErrorMessage(Response.Status.BAD_REQUEST, "4", "messages.individualmappingmanager"); 
			
			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.BAD_REQUEST, illegalArgument);
		}catch(Exception exception){
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			ErrorMessage error = new ErrorMessage(Response.Status.INTERNAL_SERVER_ERROR, "1", "messages.individualmappingmanager"); 
			
			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.INTERNAL_SERVER_ERROR, error);
			
			exception.printStackTrace();
		}

		return response;
	}

	/**
	 * This method returns all the IndividualMappings stored in the database
	 * @return An ArrayList with all IndividualMappings transfer objects
	 */
	@GET
	@Path("/getAllIndividualMappings")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllIndividualMappings(){
		IndividualMappingTOWrapper individualMappingTOWrapper = new IndividualMappingTOWrapper();
		Response response;
		
		try {
			/* Get all IndividualMapping objects from the database */
			List<IndividualMapping> individualMappings = this.individualMappingsImpl.getAll();

			/* Runs the IndividualMapping objects and fills the IndividualMappingTO array  */
			for(IndividualMapping individualMapping : individualMappings){
				IndividualMappingTO individualMappingTO = individualMapping.createTransferObject();
				individualMappingTOWrapper.individualMappingsTO.add(individualMappingTO);
			}
			
			/* Builds the response */
			Gson gson = new Gson();
			String jsonResponse = gson.toJson(individualMappingTOWrapper);
			
			
			response = Response.ok(jsonResponse).build();
			
		}catch(Exception exception) {
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			ErrorMessage error = new ErrorMessage(Response.Status.INTERNAL_SERVER_ERROR, "1", "messages.individualmappingmanager"); 
			
			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.INTERNAL_SERVER_ERROR, error);
			
			exception.printStackTrace();
		}

		return response;

	}
	
	/**
	 * Compares a given IndividualMappingTO with an existing IndividualMapping
	 * @param individualMappingID the ID of an existing IndividualMapping
 	 * @param jsonResponse An individual Mapping transfer object
	 * @return An HTTP response according to the execution of the service.
	 */
	@POST
	@Path("/compareIndividualMappings")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response compareIndividualMappings(@HeaderParam("id") String individualMappingID, String jsonResponse){
		Response response;
		try{
			Gson gson = new Gson();
			IndividualMappingTO individualMappingTO = gson.fromJson(jsonResponse, IndividualMappingTO.class);
			
			/* Creates the individual mapping from the individual mapping transfer object */
			IndividualMapping newIndividualMapping = new IndividualMapping(individualMappingTO);

			/* Gets the existing individual mapping from the database */
			IndividualMapping existingIndividualMapping = this.individualMappingsImpl.get(individualMappingID);

			/* Compares the new IndividualMapping with the existing to see if they are equal */
			Boolean comparisonResult = existingIndividualMapping.compare(newIndividualMapping);
			
			/* Creates the Response */
			response = Response.ok(gson.toJson(comparisonResult)).build();
			
		}catch(NullPointerException nullPointerException){
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			ErrorMessage requiredFieldNull = new ErrorMessage(Response.Status.BAD_REQUEST, "3", "messages.individualmappingmanager"); 
			
			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.BAD_REQUEST, requiredFieldNull);
			
		}catch(Exception exception){
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			ErrorMessage error = new ErrorMessage(Response.Status.INTERNAL_SERVER_ERROR, "1", "messages.individualmappingmanager"); 
			
			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.INTERNAL_SERVER_ERROR, error);
			
			exception.printStackTrace();
		}

		return response;
	}
}
