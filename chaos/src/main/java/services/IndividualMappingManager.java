package services;

import java.util.List;

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
			response = Response.ok(individualMappingID).build();
			
		}catch(NullPointerException nullPointerException){
			/* If any of the required fields in the IndividualMappingTO is null, it will trigger a NullPointerException. */
			response = Response.status(Response.Status.BAD_REQUEST).build();
			
		}catch(Exception exception){
			exception.printStackTrace();
			/* Sends an Internal Server Error */
			response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
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
		individualMappingTO = individualMapping.createTransferObject();

		Gson gson = new Gson();
		String jsonResponse = gson.toJson(individualMappingTO);
		
		response = Response.ok(jsonResponse).build();
		
		/* Any exception leads to an error */
		}catch(NullPointerException nullPointerException) {
			response = Response.status(Response.Status.BAD_REQUEST).build();

		}catch(IllegalArgumentException illegalArgumentException) {
			response = Response.status(Response.Status.BAD_REQUEST).build();

		}catch(Exception exception) {
			response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
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
			response = Response.ok().build();
			
		}catch(NullPointerException nullPointerException) {
			response = Response.status(Response.Status.BAD_REQUEST).build();

		}catch(IllegalArgumentException illegalArgumentException) {
			response = Response.status(Response.Status.BAD_REQUEST).build();

		}catch(Exception exception){
			/* Sends a response that is not ok */
			response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
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
	public Response removeIndividualMapping(@FormParam("ids") String individualMappingIds){
		Response response;
		try{
			/* Gets the IndividualMapping id's from the individualMappingIds string
			 * The id's are sepparated by ","
			 * e.g. 123,2344,455 */
			String[] ids = individualMappingIds.split(",");

			/* Runs all ids and fetches the IndividualMapping object  */
			for(String individualMappingId : ids){
				/* Removes the IndividualMapping from the database */
				this.individualMappingsImpl.remove(individualMappingId);
			}

			/* Gets the Response */
			response = Response.ok().build();
			
		}catch(NullPointerException nullPointerException) {
			response = Response.status(Response.Status.BAD_REQUEST).build();

		}catch(IllegalArgumentException illegalArgumentException) {
			response = Response.status(Response.Status.BAD_REQUEST).build();

		}catch(Exception exception){
			/* Sends a response that is not ok */
			response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
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
			response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
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
	public Response createIndividualMapping(@HeaderParam("id") String individualMappingID, String jsonResponse){
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
			/* If any of the required fields in the IndividualMappingTO is null, it will trigger a NullPointerException. */
			response = Response.status(Response.Status.BAD_REQUEST).build();
			
		}catch(Exception exception){
			exception.printStackTrace();
			/* Sends an Internal Server Error */
			response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}

		return response;
	}
}
