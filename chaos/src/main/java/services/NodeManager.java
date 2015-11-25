package services;

import java.util.ArrayList;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import utils.TransferObjectUtils;
import database.implementations.DataFileImpl;
import database.implementations.NodeImpl;
import domain.bo.parsers.DataFile;
import domain.bo.parsers.Node;
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
}
