package database.implementations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.bson.types.ObjectId;

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
	public String replace(ObjectId id, Node newNode){
		/* Build the database object */
		BasicDBObject newNodeDBObj = buildDBObject(newNode);

		/* Create the query */
		BasicDBObject query = new BasicDBObject().append("_id", id);

		this.collection.update(query, newNodeDBObj);
		return newNodeDBObj.get("_id").toString();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Node get(ObjectId id) {
		/* Gets the basic database object */
		BasicDBObject persistent = (BasicDBObject) this.collection.findOne(id);

		Node node = new Node();
		Set<String> keyset = persistent.keySet();
		for(String key: keyset){
			/* Creates the Node based on the keys */
			switch (key) {
			case "_id":
				node.setID(id);
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
				ArrayList<ObjectId> children = (ArrayList<ObjectId>) persistent.get(key);
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
				node = get((ObjectId) basicDBObject.get("_id"));
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
				node = get((ObjectId) basicDBObject.get("_id"));
				nodeList.add(node);
		   }
		} finally {
		   cursor.close();
		}

		return nodeList;
	}

	@Override
	public void remove(ObjectId id) {
		/* A query is created with the given tag */
		BasicDBObject query = new BasicDBObject("_id", id);

		/* Removes the object */
		this.collection.remove(query);

		return;
	}

	@Override
	public void dropCollection() {
		this.collection.drop();
	}

}
