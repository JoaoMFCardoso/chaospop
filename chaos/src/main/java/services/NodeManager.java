package services;

import java.util.List;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;

import database.implementations.DataFileImpl;
import database.implementations.IndividualMappingsImpl;
import database.implementations.NodeImpl;
import domain.bo.mappings.IndividualMapping;
import domain.bo.parsers.DataFile;
import domain.bo.parsers.Node;
import domain.to.IndividualMappingTO;
import domain.to.NodeTO;
import domain.to.wrappers.IndividualMappingTOWrapper;
import domain.to.wrappers.NodeTOWrapper;
import utils.TransferObjectUtils;

/**
 * This class implements a jax rs service layer
 * The implemented services have to do with handling Node objects
 * @author João Cardoso
 *
 */
@Path("nodeManager")
public class NodeManager {

	/** The connection to the database for DataFile objects */
	private DataFileImpl dataFileImpl = new DataFileImpl();

	/** The connection to the database for Node objects */
	private NodeImpl nodeImpl = new NodeImpl();

	/** The connection to the database for IndividualMapping objects */
	private IndividualMappingsImpl individualMappingsImpl = new IndividualMappingsImpl();

	/**
	 * This method gets a Node object when given its id
	 * @param nodeId The Node id
	 * @return A Node transfer object
	 */
	@POST
	@Path("/getNode")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getNode(@FormParam("id") String nodeId){
		/* Initializes the objects */
		Node node;
		NodeTO nodeTO;
		Response response;

		try {

			/* Gets the Node Object from the database and then builds the transfer object */
			node = this.nodeImpl.get(nodeId);
			
			/* Checks if the Node was found based on the given ID */
			if(node == null) {
				/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
//				ErrorMessage nodeNotFound = new ErrorMessage(Response.Status.NOT_FOUND, "4", "messages.nodemanager"); 

				/* Builds a Response object */
//				response = ErrorMessageHandler.toResponse(Response.Status.NOT_FOUND, nodeNotFound);

				response = Response.status(Response.Status.NOT_FOUND).build();
				
				return response;
			}
			
			nodeTO = node.createTransferObject();

			/* Builds the response with a filled DataFileTO */
			response = Response.ok(nodeTO).build();

			/* Any exception leads to an error */
		}catch(NullPointerException nullPointerException) {
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
//			ErrorMessage nullID = new ErrorMessage(Response.Status.BAD_REQUEST, "2", "messages.nodemanager"); 
			
			/* Builds a Response object */
//			response = ErrorMessageHandler.toResponse(Response.Status.BAD_REQUEST, nullID);

			response = Response.status(Response.Status.BAD_REQUEST).build();
			
		}catch(IllegalArgumentException illegalArgumentException) {
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
//			ErrorMessage illegalArgumentError = new ErrorMessage(Response.Status.BAD_REQUEST, "3", "messages.nodemanager"); 
			
			/* Builds a Response object */
//			response = ErrorMessageHandler.toResponse(Response.Status.BAD_REQUEST, illegalArgumentError);
			
			response = Response.status(Response.Status.BAD_REQUEST).build();
			
		}catch(Exception exception) {
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
//			ErrorMessage error = new ErrorMessage(Response.Status.INTERNAL_SERVER_ERROR, "1", "messages.nodemanager"); 
			
			/* Builds a Response object */
//			response = ErrorMessageHandler.toResponse(Response.Status.INTERNAL_SERVER_ERROR, error);
			
			response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
			
			exception.printStackTrace();
		}

		return response;
	}
	
	/**
	 * This method gets all the Nodes in a DataFile Node tree and returns a transfer object array to the client
	 * @param dataFileId The id of the DataFile
	 * @return An ArrayList of Node transfer objects that represent all the Nodes in the DataFile's Node tree
	 */
	@POST
	@Path("/getAllNodesFromDataFile")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllNodesFromDataFile(@FormParam("id") String dataFileId){
		/* Initializes the objects */
		NodeTOWrapper nodeTOWrapper = new NodeTOWrapper();
		DataFile dataFile;
		Response response;

		try {

			/* Gets the DataFile */
			dataFile = this.dataFileImpl.get(dataFileId);

			/* Gets the root Node id and gets all the NodeTO objects */
			String rootNodeId = dataFile.getNodeID().toString();
			nodeTOWrapper.nodesTO = TransferObjectUtils.getAllNodesTOFromNode(rootNodeId);

			/* Builds the response */
			response = Response.ok(nodeTOWrapper).build();

		}catch(NullPointerException nullPointerException) {
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
//			ErrorMessage nullID = new ErrorMessage(Response.Status.BAD_REQUEST, "5", "messages.nodemanager"); 
			
			/* Builds a Response object */
//			response = ErrorMessageHandler.toResponse(Response.Status.BAD_REQUEST, nullID);
			
			response = Response.status(Response.Status.BAD_REQUEST).build();

		}catch(IllegalArgumentException illegalArgumentException) {
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
//			ErrorMessage illegalArgumentID = new ErrorMessage(Response.Status.BAD_REQUEST, "6", "messages.nodemanager"); 
			
			/* Builds a Response object */
//			response = ErrorMessageHandler.toResponse(Response.Status.BAD_REQUEST, illegalArgumentID);
			
			response = Response.status(Response.Status.BAD_REQUEST).build();

		}catch(Exception exception){
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
//			ErrorMessage error = new ErrorMessage(Response.Status.INTERNAL_SERVER_ERROR, "1", "messages.nodemanager"); 
			
			/* Builds a Response object */
//			response = ErrorMessageHandler.toResponse(Response.Status.INTERNAL_SERVER_ERROR, error);
			
			response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
			
			exception.printStackTrace();
		}

		return response;
	}

	/**
	 * This method gets all Individual Mappings that match the tag of the Node whose id is given
	 * @param nodeId The id of an existing Node
	 * @return An Array with all the IndividualMappingTO tranfer objects that match the tag
	 */
	@POST
	@Path("/getSuggestedIndividualMappings")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSuggestedIndividualMappings(@FormParam("nodeId") String nodeId){
		/* Initializes the variables */
		Node node;
		String tag;
		IndividualMappingTOWrapper individualMappingTOWrapper = new IndividualMappingTOWrapper();
		Response response;

		try {

			/* Gets the Node and its tag */
			node = this.nodeImpl.get(nodeId);
			tag = node.getTag();

			/* Gets all IndividualMapping objects that match the Node's tag */
			List<IndividualMapping> matchingIndividualMappings = this.individualMappingsImpl.getBy("tag", tag);

			/* Checks if the Batch was found based on the given ID */
			if(matchingIndividualMappings.isEmpty()) {
				/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
//				ErrorMessage noMatches = new ErrorMessage(Response.Status.NOT_FOUND, "7", "messages.nodemanager"); 

				/* Builds a Response object */
//				response = ErrorMessageHandler.toResponse(Response.Status.NOT_FOUND, noMatches);
				
				response = Response.status(Response.Status.NOT_FOUND).build();

				return response;
			}
			
			/* Builds the IndividualMappingTO List */
			for(IndividualMapping individualMapping : matchingIndividualMappings){
				IndividualMappingTO individualMappingTO = individualMapping.createTransferObject();
				individualMappingTOWrapper.individualMappingsTO.add(individualMappingTO);
			}


			/* Builds the response */
			Gson gson = new Gson();
			String jsonResponse = gson.toJson(individualMappingTOWrapper);
			
			response = Response.ok(jsonResponse).build();

			/* Any exception leads to an error */
		}catch(NullPointerException nullPointerException) {
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
//			ErrorMessage nullID = new ErrorMessage(Response.Status.BAD_REQUEST, "2", "messages.nodemanager"); 
			
			/* Builds a Response object */
//			response = ErrorMessageHandler.toResponse(Response.Staus.BAD_REQUEST, nullID);
			
			response = Response.status(Response.Status.BAD_REQUEST).build();
			
		}catch(IllegalArgumentException illegalArgumentException) {
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
//			ErrorMessage illegalArgumentID = new ErrorMessage(Response.Status.BAD_REQUEST, "3", "messages.nodemanager"); 
			
			/* Builds a Response object */
//			response = ErrorMessageHandler.toResponse(Response.Status.BAD_REQUEST, illegalArgumentID);
			
			response = Response.status(Response.Status.BAD_REQUEST).build();

		}catch(Exception exception) {
			/* Builds an ErrorMessage object that fetches the correct message from the ResourceBundles */
//			ErrorMessage error = new ErrorMessage(Response.Status.INTERNAL_SERVER_ERROR, "1", "messages.nodemanager"); 
			
			/* Builds a Response object */
//			response = ErrorMessageHandler.toResponse(Response.Status.INTERNAL_SERVER_ERROR, error);
			
			response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
			
			exception.printStackTrace();
		}

		return response;
	}
}
