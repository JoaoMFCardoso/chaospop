package services;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

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
import domain.to.OntologyFileTO;
import domain.to.wrappers.OntologyFileTOWrapper;
import exceptions.ErrorMessage;
import exceptions.ErrorMessageHandler;
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

	/**
	 * This method creates an OntologyFile object in the database, by loading an Ontlogy through a given namespace.
	 * @param namespace A given namespace
	 * @return The OntologyFile ID
	 */
	@POST
	@Path("/addOntologyNamespace")
	@Produces(MediaType.APPLICATION_JSON)
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
			Gson gson = new Gson();
			String id = this.ontologyFileImpl.save(ontologyFile);

			/* Gets the Response */
			response = Response.ok(gson.toJson(id)).build();

		}catch(NullPointerException nullPointerException) {
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			ErrorMessage nullNamespace = new ErrorMessage(Response.Status.BAD_REQUEST, "2", "messages/ontologymanager"); 
			
			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.BAD_REQUEST, nullNamespace);
			
		}catch(IllegalArgumentException illegalArgumentException) {
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			ErrorMessage illegalArgumentNamespace = new ErrorMessage(Response.Status.BAD_REQUEST, "5", "messages/ontologymanager"); 
			
			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.BAD_REQUEST, illegalArgumentNamespace);

		}catch(Exception exception){
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			ErrorMessage error = new ErrorMessage(Response.Status.INTERNAL_SERVER_ERROR, "1", "messages/ontologymanager"); 
			
			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.INTERNAL_SERVER_ERROR, error);
			
			exception.printStackTrace();
		}

		return response;
	}

	/**
	 * This method returns all the OntologyFiles stored in the database
	 * @return An ArrayList with all OntologyFiles
	 */
	@GET
	@Path("/listOntologyFiles")
	@Produces(MediaType.APPLICATION_JSON)
	public Response listOntologyFiles(){
		OntologyFileTOWrapper ontologyFileTOWrapper = new OntologyFileTOWrapper();
		Response response;

		try {

			/* Get all OntologyFile objects from the database */
			List<OntologyFile> ontologyFiles = this.ontologyFileImpl.getAll();

			/* Runs the DataFile objects and fills the DataFileTO array  */
			for(OntologyFile ontologyFile : ontologyFiles){
				OntologyFileTO ontologyFileTO = ontologyFile.createTransferObject();
				ontologyFileTOWrapper.ontologyFilesTO.add(ontologyFileTO);
			}

			/* Builds the response */
			response = Response.ok(ontologyFileTOWrapper).build();

		}catch(Exception exception) {
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			ErrorMessage error = new ErrorMessage(Response.Status.INTERNAL_SERVER_ERROR, "1", "messages/ontologymanager"); 
			
			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.INTERNAL_SERVER_ERROR, error);
			
			exception.printStackTrace();
		}

		return response;
	}

	/**
	 * This method gets a OntologyFile object when given its id
	 * @param ontologyFileId The OntologyFile id
	 * @return A OntologyFile transfer object
	 */
	@POST
	@Path("/getOntologyFile")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getOntologyFile(@FormParam("id") String ontologyFileId){
		/* Gets the DataFile object from the database and builds the transfer object */
		OntologyFile ontologyFile;
		OntologyFileTO ontologyFileTO;
		Response response;

		try {

			/* Gets the OntologyFile object from the database and builds the transfer object */
			ontologyFile = this.ontologyFileImpl.get(ontologyFileId);
			ontologyFileTO = ontologyFile.createTransferObject();

			/* Builds the response with a filled DataFileTO */
			response = Response.ok(ontologyFileTO).build();

			/* Any exception leads to an error */
		}catch(NullPointerException nullPointerException) {
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			ErrorMessage nullOntologyID = new ErrorMessage(Response.Status.BAD_REQUEST, "3", "messages/ontologymanager"); 
			
			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.BAD_REQUEST, nullOntologyID);

		}catch(IllegalArgumentException illegalArgumentException) {
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			ErrorMessage illegalArgumentID = new ErrorMessage(Response.Status.BAD_REQUEST, "6", "messages/ontologymanager"); 
			
			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.BAD_REQUEST, illegalArgumentID);

		}catch(Exception exception) {
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			ErrorMessage error = new ErrorMessage(Response.Status.INTERNAL_SERVER_ERROR, "1", "messages/ontologymanager"); 
			
			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.INTERNAL_SERVER_ERROR, error);
			
			exception.printStackTrace();
		}

		return response;
	}

	/**
	 * This method removes a list of OntologyFile objects from the database
	 * @param ontologyIds The OntologyFile id list. All ids are separated by ",".
	 * @return 200 if everything went well, 500 if not.
	 */
	@POST
	@Path("/removeOntologyFiles")
	@Produces(MediaType.APPLICATION_JSON)
	public Response removeOntologyFiles(@FormParam("ontologyIds") String ontologyIds){
		Response response;
		
		try{
			/* Gets the OntologyFile id's from the dataFileIds string
			 * The id's are a json array [123,456,789] */
			Gson gson = new Gson();
			String[] ids = gson.fromJson(ontologyIds, String[].class);

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
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			String language = PropertiesHandler.configProperties.getProperty("language");
			ResourceBundle resourceBundle = PropertiesHandler.getMessages("messages/ontologymanager", language);
			
			String message = resourceBundle.getString("8") + " " + ontologyIds + " " + resourceBundle.getString("9");
			
			ErrorMessage corectlyRemoved = new ErrorMessage(); 
			corectlyRemoved.setStatus(Response.Status.OK.getStatusCode());
			corectlyRemoved.setMessage(message);

			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.OK, corectlyRemoved);
			
		}catch(NullPointerException nullPointerException){
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			ErrorMessage nullOntologyID = new ErrorMessage(Response.Status.BAD_REQUEST, "3", "messages/ontologymanager"); 
			
			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.BAD_REQUEST, nullOntologyID);
			
		}catch(IllegalArgumentException illegalArgumentException) {
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			ErrorMessage illegalArgumentID = new ErrorMessage(Response.Status.BAD_REQUEST, "6", "messages/ontologymanager"); 
			
			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.BAD_REQUEST, illegalArgumentID);


		}catch(Exception exception){
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			ErrorMessage error = new ErrorMessage(Response.Status.INTERNAL_SERVER_ERROR, "1", "messages/ontologymanager"); 
			
			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.INTERNAL_SERVER_ERROR, error);
			
			exception.printStackTrace();
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
			
			Gson gson = new Gson();

			/* Gets the Response */
			response = Response.ok(gson.toJson(defaultNamespace)).build();
			
		}catch(Exception exception){
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			ErrorMessage error = new ErrorMessage(Response.Status.INTERNAL_SERVER_ERROR, "1", "messages/ontologymanager"); 
			
			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.INTERNAL_SERVER_ERROR, error);
			
			exception.printStackTrace();
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
			response = Response.ok(jsonResponse).build();
			
		}catch(NullPointerException nullPointerException){
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			ErrorMessage nullOntologyID = new ErrorMessage(Response.Status.BAD_REQUEST, "3", "messages/ontologymanager"); 
			
			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.BAD_REQUEST, nullOntologyID);
			
		}catch(IllegalArgumentException illegalArgumentException) {
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			ErrorMessage illegalArgumentID = new ErrorMessage(Response.Status.BAD_REQUEST, "6", "messages/ontologymanager"); 
			
			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.BAD_REQUEST, illegalArgumentID);

		}catch(Exception exception){
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			ErrorMessage error = new ErrorMessage(Response.Status.INTERNAL_SERVER_ERROR, "1", "messages/ontologymanager"); 
			
			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.INTERNAL_SERVER_ERROR, error);
			
			exception.printStackTrace();
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
			response = Response.ok(jsonResponse).build();
			
		}catch(NullPointerException nullPointerException){
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			ErrorMessage nullOntologyID = new ErrorMessage(Response.Status.BAD_REQUEST, "3", "messages/ontologymanager"); 
			
			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.BAD_REQUEST, nullOntologyID);
			
		}catch(IllegalArgumentException illegalArgumentException) {
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			ErrorMessage illegalArgumentID = new ErrorMessage(Response.Status.BAD_REQUEST, "6", "messages/ontologymanager"); 
			
			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.BAD_REQUEST, illegalArgumentID);

		}catch(Exception exception){
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			ErrorMessage error = new ErrorMessage(Response.Status.INTERNAL_SERVER_ERROR, "1", "messages/ontologymanager"); 
			
			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.INTERNAL_SERVER_ERROR, error);
			
			exception.printStackTrace();
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
			response = Response.ok(jsonResponse).build();
			
		}catch(NullPointerException nullPointerException){
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			ErrorMessage nullOntologyID = new ErrorMessage(Response.Status.BAD_REQUEST, "4", "messages/ontologymanager"); 
			
			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.BAD_REQUEST, nullOntologyID);
			
		}catch(IllegalArgumentException illegalArgumentException) {
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			ErrorMessage illegalArgumentID = new ErrorMessage(Response.Status.BAD_REQUEST, "7", "messages/ontologymanager"); 
			
			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.BAD_REQUEST, illegalArgumentID);
			
		}catch(Exception exception){
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			ErrorMessage error = new ErrorMessage(Response.Status.INTERNAL_SERVER_ERROR, "1", "messages/ontologymanager"); 
			
			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.INTERNAL_SERVER_ERROR, error);
			
			exception.printStackTrace();
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
			response = Response.ok(jsonResponse).build();
			
		}catch(NullPointerException nullPointerException){
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			ErrorMessage nullOntologyID = new ErrorMessage(Response.Status.BAD_REQUEST, "3", "messages/ontologymanager"); 
			
			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.BAD_REQUEST, nullOntologyID);
			
		}catch(IllegalArgumentException illegalArgumentException) {
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			ErrorMessage illegalArgumentID = new ErrorMessage(Response.Status.BAD_REQUEST, "6", "messages/ontologymanager"); 
			
			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.BAD_REQUEST, illegalArgumentID);
			
		}catch(Exception exception){
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			ErrorMessage error = new ErrorMessage(Response.Status.INTERNAL_SERVER_ERROR, "1", "messages/ontologymanager"); 
			
			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.INTERNAL_SERVER_ERROR, error);
			
			exception.printStackTrace();
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
			response = Response.ok(jsonResponse).build();
			
		}catch(NullPointerException nullPointerException){
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			ErrorMessage nullOntologyID = new ErrorMessage(Response.Status.BAD_REQUEST, "4", "messages/ontologymanager"); 
			
			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.BAD_REQUEST, nullOntologyID);
			
		}catch(IllegalArgumentException illegalArgumentException) {
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			ErrorMessage illegalArgumentID = new ErrorMessage(Response.Status.BAD_REQUEST, "7", "messages/ontologymanager"); 
			
			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.BAD_REQUEST, illegalArgumentID);
			
		}catch(Exception exception){
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
			ErrorMessage error = new ErrorMessage(Response.Status.INTERNAL_SERVER_ERROR, "1", "messages/ontologymanager"); 
			
			/* Builds a Response object */
			response = ErrorMessageHandler.toResponse(Response.Status.INTERNAL_SERVER_ERROR, error);
			
			exception.printStackTrace();
		}

		return response;
	}
}
