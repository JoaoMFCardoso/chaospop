package services;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import com.google.gson.Gson;

import database.implementations.BatchImpl;
import domain.bo.population.Batch;
import exceptions.ChaosPopException;
import exceptions.ErrorMessage;
import exceptions.ErrorMessageHandler;
import ontologies.populator.PopulationOperations;

/**
 * This class implements a jax rs service layer
 * The implemented services have to do with handling Mapping objects
 * @author João Cardoso
 *
 */
@Path("populationManager")
public class PopulationManager {

	/** The connection to the database for Batch objects */
	private BatchImpl batchImpl = new BatchImpl();
	
	@POST
	@Path("/processBatch")
	@Produces(MediaType.APPLICATION_JSON)
	public Response processBatch(@FormParam("id") String batchID){
		/* Initializes the objects */
		Batch batch;
		Response response;

		try {
			/* Gets the Batch Object from the database */
			batch = this.batchImpl.get(batchID);
			
			/* Builds a new Population Operations Object with the given batch */
			PopulationOperations populationOperations = new PopulationOperations(batch);
			
			/* Calls to process the batch */
			ArrayList<String> ontologyIds = populationOperations.processBatch();

			/* Creates the JSON ontology ID list object */
			Gson gson = new Gson();
			
			/* Builds the response with the created ontology file ids */
			response = Response.ok(gson.toJson(ontologyIds)).build();

			/* Any exception leads to an error */
		}catch(ChaosPopException chaosPopException) {

			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.BAD_REQUEST, chaosPopException.getErrormessage());
			
		}catch(FileNotFoundException fileNotFoundException) {
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			ErrorMessage ontologyLocalFileNotFound = new ErrorMessage(Response.Status.BAD_REQUEST, "6", "populationmanager"); 
			
			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.BAD_REQUEST, ontologyLocalFileNotFound);
			
		}catch(OWLOntologyStorageException owlOntologyStorageException) {
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			ErrorMessage ontologyStorageException = new ErrorMessage(Response.Status.BAD_REQUEST, "5", "populationmanager"); 
			
			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.BAD_REQUEST, ontologyStorageException);
			
		}catch(OWLOntologyCreationException owlOntologyCreationException) {
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			ErrorMessage ontologyCreationExeption = new ErrorMessage(Response.Status.BAD_REQUEST, "4", "populationmanager"); 
			
			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.BAD_REQUEST, ontologyCreationExeption);
			
		}catch(NullPointerException nullPointerException) {
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			ErrorMessage batchNullID = new ErrorMessage(Response.Status.BAD_REQUEST, "2", "populationmanager"); 
			
			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.BAD_REQUEST, batchNullID);

		}catch(IllegalArgumentException illegalArgumentException) {
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			ErrorMessage illegalArgumentID = new ErrorMessage(Response.Status.BAD_REQUEST, "3", "populationmanager"); 
			
			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.BAD_REQUEST, illegalArgumentID);

		}catch(Exception exception) {
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			ErrorMessage error = new ErrorMessage(Response.Status.INTERNAL_SERVER_ERROR, "1", "populationmanager"); 
			
			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.INTERNAL_SERVER_ERROR, error);
			
			exception.printStackTrace();
		}

		return response;
	}
}
