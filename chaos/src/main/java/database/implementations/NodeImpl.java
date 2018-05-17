package database.implementations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.bson.types.ObjectId;

import utils.MongoUtilities;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import database.DatabaseConnector;
import database.MongoService;
import domain.bo.parsers.Node;

public class NodeImpl implements MongoService<Node> {
	/**
	 * The collection name for this implementation of the mongo service
	 */
	public static final String collectionName = "Nodes";

	/**
	 * The collection that is associated with this implementation
	 */
	protected DBCollection collection;

	/**
	 * The database that is retrieved from the database connector
	 */
	protected DB database;

	/**
	 * The class constructor
	 * Connects to the database
	 */
	public NodeImpl() {
		DatabaseConnector databaseConnector = new DatabaseConnector();
		this.database = databaseConnector.getDatabase();
		this.collection = database.getCollection(collectionName);
	}

	@Override
	public BasicDBObject buildDBObject(Node node){
		/* Creates the database object */
		BasicDBObject nodeDBObj = new BasicDBObject();

		/* Appends the various attributes to the database object */
		nodeDBObj.append("_id", node.getID());
		nodeDBObj.append("dataFileId", node.getDataFileId());
		nodeDBObj.append("parent", node.getParent());
		nodeDBObj.append("tag", node.getTag());
		nodeDBObj.append("value", node.getValue());
		nodeDBObj.append("hasAttributes", node.HasAttributes());

		/* Creates the attributes database object and appends it to the node database object*/
		if(null != node.getAttributes()){
			HashMap<String, String> attributes = node.getAttributes();
			BasicDBObject attributesDBObj = new BasicDBObject(attributes);
			nodeDBObj.append("attributes", attributesDBObj);
		}

		/* Creates the children database object and appends it to the node database object*/
		if(null != node.getChildren()){
			List<Object> children = new BasicDBList();

			/* Runs all child ids */
			for(ObjectId childId : node.getChildren()){
				DBObject childDBObject = new BasicDBObject();

				/* stores the child id */
				childDBObject.put("child", childId);
				children.add(childDBObject);
			}

			nodeDBObj.append("children", children);
		}

		return nodeDBObj;
	}

	@Override
	public String save(Node node) {
		/* Build the database object */
		BasicDBObject nodeDBObj = buildDBObject(node);

		this.collection.insert(nodeDBObj);
		return nodeDBObj.get("_id").toString();
	}

	@Override
	public String replace(String id, Node newNode){
		/* Build the database object */
		BasicDBObject newNodeDBObj = buildDBObject(newNode);

		/* Create the query */
		BasicDBObject query = new BasicDBObject().append("_id", new ObjectId(id));

		this.collection.update(query, newNodeDBObj);
		return newNodeDBObj.get("_id").toString();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Node get(String id) {
		/* Gets the basic database object */
		ObjectId dbID = new ObjectId(id);
		BasicDBObject persistent = (BasicDBObject) this.collection.findOne(dbID);

		Node node = new Node();
		Set<String> keyset = persistent.keySet();
		for(String key: keyset){
			/* Creates the Node based on the keys */
			switch (key) {
			case "_id":
				node.setID(dbID);
				break;
			case "dataFileId":
				node.setDataFileId((ObjectId) persistent.get(key));
				break;
			case "parent":
				node.setParent((ObjectId) persistent.get(key));
				break;
			case "tag":
				node.setTag((String) persistent.get(key));
				break;
			case "value":
				node.setValue((String) persistent.get(key));
				break;
			case "attributes":
				HashMap<String, String> attributesDBMap = (HashMap<String, String>) persistent.get(key);
				node.setAttributes(attributesDBMap);
				break;
			case "children":
				ArrayList<ObjectId> children = MongoUtilities.convertALOIdFromBDBL((BasicDBList) persistent.get(key), "child");
				node.setChildren(children);
				break;
			default:
				break;
			}
		}

		return node;
	}

	@Override
	public List<Node> getBy(String field, String value) {
		/* Creates the Node list */
		List<Node> nodeList = new ArrayList<Node>();

		/* A query is created with the given field */
		BasicDBObject query = new BasicDBObject(field, value);

		/* Creates the cursor and gets all basic db objects in the collection that match the query*/
		DBCursor cursor = this.collection.find(query);

		Node node = null;
		try {
		   while(cursor.hasNext()) {
			   /* Gets the object id and converts it to a Node
				 * The Node is then returned */
				BasicDBObject basicDBObject = (BasicDBObject) cursor.next();
				node = get(((ObjectId) basicDBObject.get("_id")).toString());
				nodeList.add(node);
		   }
		} finally {
		   cursor.close();
		}

		return nodeList;
	}
	
	/**
	 * This method gets a list of nodes by id
	 * @param field The field to be searched
	 * @param id The id to be searched
	 */
	public List<Node> getBy(String field, ObjectId id) {
		/* Creates the Node list */
		List<Node> nodeList = new ArrayList<Node>();

		/* A query is created with the given field */
		BasicDBObject query = new BasicDBObject(field, id);

		/* Creates the cursor and gets all basic db objects in the collection that match the query*/
		DBCursor cursor = this.collection.find(query);

		Node node = null;
		try {
		   while(cursor.hasNext()) {
			   /* Gets the object id and converts it to a Node
				 * The Node is then returned */
				BasicDBObject basicDBObject = (BasicDBObject) cursor.next();
				node = get(((ObjectId) basicDBObject.get("_id")).toString());
				nodeList.add(node);
		   }
		} finally {
		   cursor.close();
		}

		return nodeList;
	}

	/**
	 * Gets the Nodes from a specific DataFile that match a certain tag
	 * @param dataFileId The DataFile object Id
	 * @param tag The string to be matched
	 * @return A list of all matching Nodes.
	 */
	public List<Node> getMatchingTagsInDataFile(String dataFileId, String tag){
		/* Creates the Node list */
		List<Node> nodeList = new ArrayList<Node>();

		/* A query is created with the given field */
		ObjectId dfId = new ObjectId(dataFileId);
		BasicDBObject query = new BasicDBObject("dataFileId", dfId).append("tag", tag);

		/* Creates the cursor and gets all basic db objects in the collection that match the query*/
		DBCursor cursor = this.collection.find(query);

		Node node = null;
		try {
		   while(cursor.hasNext()) {
			   /* Gets the object id and converts it to a Node
				 * The Node is then returned */
				BasicDBObject basicDBObject = (BasicDBObject) cursor.next();
				node = get(((ObjectId) basicDBObject.get("_id")).toString());
				nodeList.add(node);
		   }
		} finally {
		   cursor.close();
		}

		return nodeList;
	}

	/**
	 * Gets the child Nodes of a given Node ID
	 * @param nodeId The Id of the Node to be searched
	 * @return A List with all its children nodes
	 */
	public List<Node> getChildNodes(ObjectId nodeId){
		/* Creates the Node list */
		List<Node> nodeList = new ArrayList<Node>();

		/* A query is created with the given parent id as field and its value as value */
		BasicDBObject query = new BasicDBObject("parent", nodeId);

		/* Creates the cursor and gets all basic db objects in the collection that match the query*/
		DBCursor cursor = this.collection.find(query);

		Node node = null;
		try {
		   while(cursor.hasNext()) {
			   /* Gets the object id and converts it to a Node
				 * The Node is then returned */
				BasicDBObject basicDBObject = (BasicDBObject) cursor.next();
				node = get(((ObjectId) basicDBObject.get("_id")).toString());
				nodeList.add(node);
		   }
		} finally {
		   cursor.close();
		}

		return nodeList;
	}

	@Override
	public List<Node> getAll() {
		/* Creates the Node list */
		List<Node> nodeList = new ArrayList<Node>();

		/* Creates the cursor and gets all basic db objects in the collection */
		DBCursor cursor = this.collection.find();

		Node node = null;
		try {
		   while(cursor.hasNext()) {
			   /* Gets the object id and converts it to a Node
				 * The Node is then returned */
				BasicDBObject basicDBObject = (BasicDBObject) cursor.next();
				node = get(((ObjectId) basicDBObject.get("_id")).toString());
				nodeList.add(node);
		   }
		} finally {
		   cursor.close();
		}

		return nodeList;
	}

	@Override
	public void remove(String id) {
		/* Removes the child Node objects */
		Node node = get(id);

		if(null != node.getChildren() &&
				!node.getChildren().isEmpty()){
			/* Runs the children Node and removes them */
			for(ObjectId childNode : node.getChildren()){
				remove(childNode.toString());
			}
		}

		/* A query is created with the given tag */
		BasicDBObject query = new BasicDBObject("_id", new ObjectId(id));

		/* Removes the object */
		this.collection.remove(query);

		return;
	}

	@Override
	public void dropCollection() {
		this.collection.drop();
	}

}
