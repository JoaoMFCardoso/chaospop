package services;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import utils.TransferObjectUtils;
import database.implementations.DataFileImpl;
import database.implementations.IndividualMappingsImpl;
import database.implementations.NodeImpl;
import domain.bo.mappings.IndividualMapping;
import domain.bo.parsers.DataFile;
import domain.bo.parsers.Node;
import domain.to.IndividualMappingTO;
import domain.to.NodeTO;

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

	private IndividualMappingsImpl individualMappingsImpl = new IndividualMappingsImpl();

	/**
	 * This method gets all the Nodes in a DataFile Node tree and returns a transfer object array to the client
	 * @param dataFileId The id of the DataFile
	 * @return An ArrayList of Node transfer objects that represent all the Nodes in the DataFile's Node tree
	 */
	@POST
	@Path("/getAllNodesFromDataFile")
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<NodeTO> getAllNodesFromDataFile(@FormParam("id") String dataFileId){
		/* Gets the DataFile */
		DataFile dataFile = this.dataFileImpl.get(dataFileId);

		/* Gets the root Node id and gets all the NodeTO objects */
		String rootNodeId = dataFile.getNodeID().toString();
		ArrayList<NodeTO> nodeTOList = TransferObjectUtils.getAllNodesTOFromNode(rootNodeId);

		return nodeTOList;
	}

	/**
	 * This method gets a Node object when given its id
	 * @param nodeId The Node id
	 * @return A Node transfer object
	 */
	@POST
	@Path("/getNode")
	@Produces(MediaType.APPLICATION_JSON)
	public NodeTO getNode(@FormParam("id") String nodeId){
		/* Gets the Node Object from the database and then builds the transfer object */
		Node node = this.nodeImpl.get(nodeId);
		NodeTO nodeTO = node.createTransferObject();

		return nodeTO;
	}

	@POST
	@Path("/getSuggestedIndividualMappings")
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<IndividualMappingTO> getSuggestedIndividualMappings(@FormParam("nodeId") String nodeId){
		ArrayList<IndividualMappingTO> individualMappingTOList = new ArrayList<IndividualMappingTO>();

		/* Gets the Node and its tag */
		Node node = this.nodeImpl.get(nodeId);
		String tag = node.getTag();

		/* Gets all IndividualMapping objects that match the Node's tag */
		List<IndividualMapping> matchingIndividualMappings = this.individualMappingsImpl.getBy("tag", tag);

		/* Builds the IndividualMappingTO List */
		for(IndividualMapping individualMapping : matchingIndividualMappings){
			IndividualMappingTO individualMappingTO = individualMapping.createTransferObject();
			individualMappingTOList.add(individualMappingTO);
		}

		return individualMappingTOList;
	}
}
