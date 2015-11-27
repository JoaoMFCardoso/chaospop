package services;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import ontologies.extractor.OntologyExtractionOperations;

import org.semanticweb.owlapi.model.IRI;

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

}
