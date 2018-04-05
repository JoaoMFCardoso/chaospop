package services;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;

import com.google.gson.Gson;

import database.implementations.OntologyFileImpl;
import domain.bo.ontologies.OntologyFile;
import domain.bo.parsers.DataFile;
import domain.to.DataFileTO;
import domain.to.OntologyFileTO;
import file.sftp.SFTPServerConnectionManager;
import ontologies.extractor.OntologyOperations;
import properties.PropertiesHandler;
import utils.FileOperationsUtils;
import utils.TransferObjectUtils;

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
			OntologyOperations ontologyExtractionOperations = new OntologyOperations(namespaceIRI);
			ontologyFile.setsGeneralOntologyFileAttributes(ontologyExtractionOperations);

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
	 * This method returns all the OntologyFiles stored in the database
	 * @return An ArrayList with all DataFiles
	 */
	@GET
	@Path("/listOntologyFiles")
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<OntologyFileTO> listOntologyFiles(){
		ArrayList<OntologyFileTO> ontologyFilesTO = new ArrayList<OntologyFileTO>();

		/* Get all OntologyFile objects from the database */
		List<OntologyFile> dataFiles = this.ontologyFileImpl.getAll();

		/* Runs the DataFile objects and fills the DataFileTO array  */
		for(OntologyFile ontologyFile : dataFiles){
			OntologyFileTO ontologyFileTO = ontologyFile.createTransferObject();
			ontologyFilesTO.add(ontologyFileTO);
		}

		return ontologyFilesTO;
	}
	
	/**
	 * This method gets a OntologyFile object when given its id
	 * @param ontologyFileId The OntologyFile id
	 * @return A OntologyFile transfer object
	 */
	@POST
	@Path("/getOntologyFile")
	@Produces(MediaType.APPLICATION_JSON)
	public OntologyFileTO getOntologyFile(@FormParam("id") String ontologyFileId){
		/* Gets the OntologyFile object from the database and builds the transfer object */
		OntologyFile ontologyFile = this.ontologyFileImpl.get(ontologyFileId);
		OntologyFileTO ontologyFileTO = ontologyFile.createTransferObject();

		return ontologyFileTO;
	}
	
	/**
	 * This method removes a list of OntologyFile objects from the database
	 * @param ontologyIds The OntologyFile id list. All ids are separated by ",".
	 * @return 200 if everything went well, 500 if not.
	 */
	@POST
	@Path("/removeOntologyFiles")
	public Response removeOntologyFiles(@FormParam("ontologyIds") String ontologyIds){
		Response response;
		try{
			/* Gets the OntologyFile id's from the ontologyIds string
			 * The id's are sepparated by ","
			 * e.g. 123,2344,455 */
			String[] ids = ontologyIds.split(",");

			/* Runs all ids and fetches the OntologyFile object  */
			for(String ontologyId : ids){

				/* Gets the OntologyFile */
				OntologyFile ontologyFile = this.ontologyFileImpl.get(ontologyId);

				/* If the DataFile is in the SFTP Server, it removes it from the server */
				String namespace = ontologyFile.getNamespace().toString();
				if(FileOperationsUtils.isSFTPServerCompliant(namespace)){
					SFTPServerConnectionManager sftpCManager = new SFTPServerConnectionManager();
					sftpCManager.removeSFTPFile(namespace);
				}

				/* Removes the OntologyFile */
				this.ontologyFileImpl.remove(ontologyId);
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
	 * Gets the SFTP Default Namespace
	 * @return The SFTP Default Namespace
	 */
	@GET
	@Path("/getSFTPDefaultNamespace")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSFTPDefaultNamespace(){
		Response response;
		try{
			/* Gets the Namespace from the properties */
			PropertiesHandler.propertiesLoader();

			String defaultNamespace = PropertiesHandler.configProperties.getProperty("sftp.namespace");

			/* Gets the Response */
			response = Response.ok(defaultNamespace, MediaType.APPLICATION_JSON).build();
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
			OntologyOperations ontologyExtractionOperations = new OntologyOperations(ontologyFileId);
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

	/**
	 * Gets the Ontology's Object Properties
	 * @param ontologyFileId The id of the Ontology
	 * @return An array with all the Object properties in the Ontology's signature
	 */
	@POST
	@Path("/getObjectProperties")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getObjectProperties(@FormParam("ontologyId") String ontologyFileId){
		Response response;
		try{
			/* Get the Object properties */
			OntologyOperations ontologyExtractionOperations = new OntologyOperations(ontologyFileId);
			ArrayList<IRI> objectPropertiesIRIArray = ontologyExtractionOperations.getObjectProperties();

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
			OntologyOperations ontologyExtractionOperations = new OntologyOperations(ontologyFileId);
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
	 * Gets the Ontology's Data Properties
	 * @param ontologyFileId The id of the Ontology
	 * @return An array with all the Data properties in the Ontology's signature
	 */
	@POST
	@Path("/getDataProperties")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getDataProperties(@FormParam("ontologyId") String ontologyFileId){
		Response response;
		try{
			/* Get the Data properties */
			OntologyOperations ontologyExtractionOperations = new OntologyOperations(ontologyFileId);
			ArrayList<IRI> dataPropertiesIRIArray = ontologyExtractionOperations.getDataProperties();

			/* Converts the Data Properties IRI Array into a String Array */
			ArrayList<String> dataPropertiesStringArray = TransferObjectUtils.convertALIRIToString(dataPropertiesIRIArray);

			/* Converts the String Classes Array to a JSON String */
			Gson gson = new Gson();
			String jsonResponse = gson.toJson(dataPropertiesStringArray);

			/* Gets the Response */
			response = Response.ok(jsonResponse, MediaType.APPLICATION_JSON).build();
		}catch(Exception exception){
			/* Sends a response that is not ok */
			response = Response.status(500).build();
		}

		return response;
	}

	/**
	 * Gets the data properties necessary to a given owl class
	 * @param ontologyFileId the ontology id in the database
	 * @param owlClass The owl class IRI in String form
	 * @return An array containing the all the Data Properties IRI
	 */
	@POST
	@Path("/getDataPropertiesForClass")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getDataPropertiesForClass(@FormParam("ontologyId") String ontologyFileId, @FormParam("owlClass") String owlClass){
		Response response;
		try{
			/* Get the Data properties for the given class */
			OntologyOperations ontologyExtractionOperations = new OntologyOperations(ontologyFileId);
			OWLClass owlClassObject = ontologyExtractionOperations.getOWLClass(owlClass);
			ArrayList<IRI> dataPropertiesIRIArray = ontologyExtractionOperations.getDataPropertiesFromClass(owlClassObject);

			/* Converts the Data Properties IRI Array into a String Array */
			ArrayList<String> dataPropertiesStringArray = TransferObjectUtils.convertALIRIToString(dataPropertiesIRIArray);

			/* Converts the String Classes Array to a JSON String */
			Gson gson = new Gson();
			String jsonResponse = gson.toJson(dataPropertiesStringArray);

			/* Gets the Response */
			response = Response.ok(jsonResponse, MediaType.APPLICATION_JSON).build();
		}catch(Exception exception){
			/* Sends a response that is not ok */
			response = Response.status(500).build();
		}

		return response;
	}
}
