package services;

import java.util.ArrayList;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import ontologies.extractor.OntologyExtractionOperations;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;

import utils.TransferObjectUtils;

import com.google.gson.Gson;

import database.implementations.OntologyFileImpl;
import domain.bo.ontologies.OntologyFile;

/**
 * This class implements a jax rs service layer
 * It handles all services related to Ontology management
 * @author João Cardoso
 *
 */
@Path("ontologyManager")
public class OntologyManager {

	/** The connection to the database for OntologyFile objects */
	private OntologyFileImpl ontologyFileImpl = new OntologyFileImpl();

	@POST
	@Path("/addOntologyNamespace")
	public Response addOntologyFromNamespace(@FormParam("namespace") String namespace){
		Response response;
		try{
			/* Creates a new OntologyFile element */
			OntologyFile ontologyFile = new OntologyFile();

			/* Sets all OntologyFile attributes that are necessary for this type of ontology file creation */
			IRI namespaceIRI = IRI.create(namespace);
			OntologyExtractionOperations ontologyExtractionOperations = new OntologyExtractionOperations(namespaceIRI);

			ontologyFile = ontologyExtractionOperations.setsGeneralOntologyFileAttributes(ontologyFile);

			/* Saves the OntologyFile */
			this.ontologyFileImpl.save(ontologyFile);

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
	 * Gets the object properties necessary to a given owl class
	 * @param ontologyFileId the ontology id in the database
	 * @param owlClass The owl class IRI in String form
	 * @return An array containing the all the Object Properties IRI
	 */
	@POST
	@Path("/getObjectPropertiesForClass")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getObjectPropertiesForClass(@FormParam("ontologyId") String ontologyFileId, @FormParam("owlClass") String owlClass){
		Response response;
		try{
			/* Get the Object properties for the given class */
			OntologyExtractionOperations ontologyExtractionOperations = new OntologyExtractionOperations(ontologyFileId);
			OWLClass owlClassObject = ontologyExtractionOperations.getOWLClass(owlClass);
			ArrayList<IRI> objectPropertiesIRIArray = ontologyExtractionOperations.getObjectPropertiesFromClass(owlClassObject);

			/* Converts the Object Properties IRI Array into a String Array */
			ArrayList<String> objectPropertiesStringArray = TransferObjectUtils.convertALIRIToString(objectPropertiesIRIArray);

			/* Converts the String Classes Array to a JSON String */
			Gson gson = new Gson();
			String jsonResponse = gson.toJson(objectPropertiesStringArray);

			/* Gets the Response */
			response = Response.ok(jsonResponse, MediaType.APPLICATION_JSON).build();
		}catch(Exception exception){
			/* Sends a response that is not ok */
			response = Response.status(500).build();
		}

		return response;
	}

	/**
	 * Gets all the OWL Classes for a given Ontology
	 * @param ontologyFileId The ontology database id
	 * @return An array containing all the OWLClass IRIs
	 */
	@POST
	@Path("/getOWLClasses")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getOWLClasses(@FormParam("ontologyId") String ontologyFileId){
		Response response;
		try{
			/* Get the owl classes for the given ontology */
			OntologyExtractionOperations ontologyExtractionOperations = new OntologyExtractionOperations(ontologyFileId);
			ArrayList<IRI> owlClassesIRIArray = ontologyExtractionOperations.getClasses();

			/* Converts the OWLClasses IRI Array into a String Array */
			ArrayList<String> owlClassesStringArray = TransferObjectUtils.convertALIRIToString(owlClassesIRIArray);

			/* Converts the String Classes Array to a JSON String */
			Gson gson = new Gson();
			String jsonResponse = gson.toJson(owlClassesStringArray);

			/* Gets the Response */
			response = Response.ok(jsonResponse, MediaType.APPLICATION_JSON).build();
		}catch(Exception exception){
			/* Sends a response that is not ok */
			response = Response.status(500).build();
		}

		return response;
	}
}
