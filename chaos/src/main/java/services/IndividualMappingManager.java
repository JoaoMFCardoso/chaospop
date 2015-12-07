package services;

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

import database.implementations.IndividualMappingsImpl;
import domain.bo.mappings.IndividualMapping;
import domain.to.IndividualMappingTO;

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
	 * @return 200 if everything went well 500 if not
	 */
	@POST
	@Path("/createIndividualMapping")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createIndividualMapping(IndividualMappingTO individualMappingTO){
		Response response;
		try{
			/* Creates the individual mapping from the individual mapping transfer object */
			IndividualMapping individualMapping = new IndividualMapping(individualMappingTO);

			/* Stores the new individual mapping in the database */
			this.individualMappingsImpl.save(individualMapping);

			/* Creates the Response */
			response = Response.status(200).build();
		}catch(Exception exception){
			exception.printStackTrace();
			/* Sends a response that is not ok */
			response = Response.status(500).build();
		}

		return response;
	}

	/**
	 * This method removes a list of IndividualMapping objects from the database
	 * @param individualMappingIds The IndividualMapping id list. All ids are sepparated by ",".
	 * @return 200 if everything went well, 500 if not.
	 */
	@POST
	@Path("/removeIndividualMapping")
	public Response removeFile(@FormParam("ids") String individualMappingIds){
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
			response = Response.status(200).build();
		}catch(Exception exception){
			exception.printStackTrace();
			/* Sends a response that is not ok */
			response = Response.status(500).build();
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
	public ArrayList<IndividualMappingTO> getAllIndividualMappings(){
		ArrayList<IndividualMappingTO> individualMappingTOs = new ArrayList<IndividualMappingTO>();

		/* Get all IndividualMapping objects from the database */
		List<IndividualMapping> individualMappings = this.individualMappingsImpl.getAll();

		/* Runs the IndividualMapping objects and fills the IndividualMappingTO array  */
		for(IndividualMapping individualMapping : individualMappings){
			IndividualMappingTO individualMappingTO = individualMapping.createTransferObject();
			individualMappingTOs.add(individualMappingTO);
		}

		return individualMappingTOs;
	}
}
