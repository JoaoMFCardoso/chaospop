package parsing.parsers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Iterator;

import javax.ws.rs.core.Response;

import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import database.implementations.DataFileImpl;
import database.implementations.NodeImpl;
import domain.bo.parsers.DataFile;
import domain.bo.parsers.Node;
import exceptions.ChaosPopException;
import exceptions.ErrorMessage;
import parsing.ParserInterface;

public class JSONParserImpl implements ParserInterface {

	/** The log4j log. */
//	private static final Logger parserLog = Logger.getLogger(XMLParserImpl.class);

	/** The database functions to store DataFile objects */
	private DataFileImpl dataFileImpl;

	/** The database functions to store Node objects */
	private NodeImpl nodeImpl;

	/** The class constructor
	 *  It initializes the DataFileImpl and NodeImpl objects
	 */
	public JSONParserImpl() {
		this.dataFileImpl = new DataFileImpl();
		this.nodeImpl = new NodeImpl();
	}

	@Override
	public String parseFile(File jsonFile) throws ChaosPopException {
		DataFile dataFile = new DataFile();
		ObjectId dataFileId = dataFile.getID();

		try{
			JSONTokener jsonTokener = new JSONTokener(new FileReader(jsonFile));
			JSONObject root = new JSONObject(jsonTokener);

			/* Creates the root node */
			Node rootNode = new Node();
			rootNode.setTag("root");
			rootNode.setDataFileId(dataFileId);

			/* Stores the rootNode */
			this.nodeImpl.save(rootNode);

			/* Handles the json object */
			jsonObjectHandler(dataFileId, root, rootNode);

			/* replaces the rootNode */
			this.nodeImpl.replace(rootNode.getID().toString(), rootNode);

			/* Stores the DataFile */
			String dataFileID = storeDataFile(dataFile, jsonFile, rootNode);

			return dataFileID; 
			
		}catch(JSONException jsonException) {
			ErrorMessage jsonError = new ErrorMessage();
			jsonError.setMessage(jsonException.getMessage());
			jsonError.setStatus(Response.Status.BAD_REQUEST.getStatusCode());
			
			ChaosPopException chaosPopException = new ChaosPopException(jsonException.getMessage());
			chaosPopException.setErrormessage(jsonError);
			
			throw chaosPopException;
			
		}catch(FileNotFoundException exception) {
			ErrorMessage genericError = new ErrorMessage();
			genericError.setMessage(exception.getMessage());
			genericError.setStatus(Response.Status.BAD_REQUEST.getStatusCode());
			
			ChaosPopException chaosPopException = new ChaosPopException(exception.getMessage());
			chaosPopException.setErrormessage(genericError);
			
			throw chaosPopException;
		}
	}

	/**
	 * This method gets a DataFile object and stores it in the database
	 * @param dataFile The DataFile Object
	 * @param file The file
	 * @param node The node
	 * @return The DataFile ID
	 */
	private String storeDataFile(DataFile dataFile, File file, Node node){

		/* Sets the DataFile object attributes */
		dataFile.setName(file.getName());
		dataFile.setNodeID(node.getID());

		/* Saves the DataFile object */
		String dataFileID = this.dataFileImpl.save(dataFile);
		
		return dataFileID;
	}

	/**
	 * Parses a JSON Object
	 *
	 * @param jsonObject the json object
	 * @param parent the parent
	 */
	@SuppressWarnings("unchecked")
	private void jsonObjectHandler(ObjectId dataFileId, JSONObject jsonObject, Node parent){

		Iterator<String> iterator = jsonObject.keys();
		while(iterator.hasNext()){
			Node childNode = new Node();

			/* Set the DataFile id */
			childNode.setDataFileId(dataFileId);

			/* Set parent child relations */
			parent.initializeChildren();
			parent.addChild(childNode.getID());
			childNode.setParent(parent.getID());

			/* Considering that keys are always Strings, the json value handler is called considering that premise */
			String key = (String) iterator.next();

			/* Sets the childNode tag */
			childNode.setTag(key);

			/* Saves the childNode */
			this.nodeImpl.save(childNode);

			jsonValueHandler(dataFileId, childNode, key, jsonObject.get((String) key));

			/* replaces the childNode */
			this.nodeImpl.replace(childNode.getID().toString(), childNode);
		}
	}

	/**
	 * Json array handler.
	 *
	 * @param jsonArray the json array
	 * @param parent the parent
	 */
	private void jsonArrayHandler(ObjectId dataFileId, JSONArray jsonArray, Node parent){
		/* Runs the array and looks for the key object */
		for (int i = 0; i < jsonArray.length(); i++) {

			Node arrayNode = new Node();

			/* Set the DataFile id */
			arrayNode.setDataFileId(dataFileId);

			/* Set parent child relations */
			parent.initializeChildren();
			parent.addChild(arrayNode.getID());
			arrayNode.setParent(parent.getID());

			String currentTag = setArrayNodeTag(arrayNode);
			arrayNode.setTag(currentTag);

			/* saves the arrayNode */
			this.nodeImpl.save(arrayNode);

			jsonValueHandler(dataFileId, arrayNode, jsonArray.get(i).getClass().getSimpleName(), jsonArray.get(i));

			/* replaces the arrayNode */
			this.nodeImpl.replace(arrayNode.getID().toString(), arrayNode);
		}
	}

	/**
	 * Json value handler.
	 *
	 * @param node the node
	 * @param key the key
	 * @param value the value
	 */
	private void jsonValueHandler(ObjectId dataFileId, Node node, String key, Object value){

		/* Checks the value type */
		switch (value.getClass().getSimpleName()) {
		case "String":
			node.setValue((String) value);
			break;
		case "JSONObject":
			jsonObjectHandler(dataFileId, (JSONObject) value, node);
			break;
		case "JSONArray": /* Checks each array element and calls the json Object handler */
			jsonArrayHandler(dataFileId, (JSONArray) value, node);
			break;
		case "Integer":
			node.setValue(Integer.toString((Integer) value));
			break;
		case "Boolean":
			node.setValue(Boolean.toString((Boolean) value));
			break;
		case "Long":
			node.setValue(Long.toString((Long) value));
			break;
		case "Double":
			node.setValue(Double.toString((Double) value));
			break;
		default:
			break;
		}
	}

	/**
	 * This method established in an naming order to the JSONArray nodes, in order for different levels not to carry the same tag
	 * @param node the JSONArray node
	 * @return A suitable tag
	 */
	private String setArrayNodeTag(Node node){
		Node parentNode = this.nodeImpl.get(node.getParent().toString());
		String tag = parentNode.getTag();
		int order = 0;

		/* Checks the parents tags for a jsonArray tag  */
		boolean noMatch = true;
		while(noMatch){
			String[] parentTag = tag.split("_");

			switch (parentTag[0]) {
			case "jsonArray": /* An existing order of jsonArray was found,
			 then the order must be set accordingly*/
				/* get's the order */
				order = Integer.parseInt(parentTag[1]) + 1;
				tag = "jsonArray_" + order;
				return tag;
			case "root": /* No jsonArray tag was found up to the root
			then the decision is that the order is 0 */
				tag = "jsonArray_0";
				return tag;
			default: /* Just iterate to an upper level */
				parentNode = this.nodeImpl.get(parentNode.getParent().toString());
				tag = parentNode.getTag();
				break;
			}
		}

		return tag;
	}
}
