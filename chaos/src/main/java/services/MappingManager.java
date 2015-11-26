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

import database.implementations.MappingImpl;
import domain.bo.mappings.Mapping;
import domain.to.MappingTO;

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

	/**
	 * This method returns all the Mappings stored in the database
	 * @return An ArrayList with all Mappings transfer objects
	 */
	@GET
	@Path("/getAllMappings")
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<MappingTO> getAllMappings(){
		ArrayList<MappingTO> mappingTOs = new ArrayList<MappingTO>();

		/* Get all Mapping objects from the database */
		List<Mapping> mappings = this.mappingImpl.getAll();

		/* Runs the DataFile objects and fills the DataFileTO array  */
		for(Mapping mapping : mappings){
			MappingTO mappingTO = mapping.createTransferObject();
			mappingTOs.add(mappingTO);
		}

		return mappingTOs;
	}

	/**
	 * This method creates a new Mapping object in the database based
	 * on the transfer object created in the client application.
	 * @param mappingTO The client provided MappingTO transfer object
	 * @return 200 if everything went well, 500 if not.
	 */
	@POST
	@Path("/createMapping")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createMapping(MappingTO mappingTO){
		Response response;
		try{
			/* Creates the Mapping from the MappingTO */
			Mapping mapping = new Mapping(mappingTO);

			/* Stores the new mapping in the database */
			this.mappingImpl.save(mapping);

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
	 * This method removes a list of Mapping objects from the database
	 * @param mappingIds The Mapping id list. All ids are sepparated by ",".
	 * @return 200 if everything went well, 500 if not.
	 */
	@POST
	@Path("/removeMapping")
	public Response removeFile(@FormParam("ids") String mappingsIds){
		Response response;
		try{
			/* Gets the Mapping id's from the mappingsIds string
			 * The id's are sepparated by ","
			 * e.g. 123,2344,455 */
			String[] ids = mappingsIds.split(",");

			/* Runs all ids and fetches the Mapping object  */
			for(String mappingId : ids){
				/* Removes the Mapping from the database */
				this.mappingImpl.remove(mappingId);
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
