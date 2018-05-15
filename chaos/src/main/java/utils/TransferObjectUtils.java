package utils;

import java.util.ArrayList;
import java.util.HashMap;

import org.bson.types.ObjectId;
import org.javatuples.Pair;
import org.semanticweb.owlapi.model.IRI;

import database.implementations.NodeImpl;
import domain.bo.parsers.Node;
import domain.to.NodeTO;

/**
 * This class implements utilities methods that are to be used to handle Transfer Object vs Base Object conversions
 * @author João Cardoso
 *
 */
public class TransferObjectUtils {

	/**
	 * This method converts a String ArrayList to an ObjectId ArrayList
	 * @param stringArray The String ArrayList
	 * @return An ObjectId ArrayList
	 */
	public static ArrayList<ObjectId> convertALStringToObjectId(ArrayList<String> stringArray){
		ArrayList<ObjectId> objectIdArray = new ArrayList<ObjectId>();

		for(String element : stringArray){
			objectIdArray.add(new ObjectId(element));
		}

		return objectIdArray;
	}

	/**
	 * This method converts an ObjectId ArrayList to a String ArrayList
	 * @param objectIdArray The ObjectId ArrayList
	 * @return A String ArrayList
	 */
	public static ArrayList<String> convertALObjectIdToString(ArrayList<ObjectId> objectIdArray){
		ArrayList<String> stringArray = new ArrayList<String>();

		for(ObjectId element : objectIdArray){
			stringArray.add(element.toString());
		}

		return stringArray;
	}

	/**
	 * This method converts a String ArrayList to an IRI ArrayList
	 * @param stringArray The String ArrayList
	 * @return An IRI ArrayList
	 */
	public static ArrayList<IRI> convertALStringToIRI(ArrayList<String> stringArray){
		ArrayList<IRI> iriArray = new ArrayList<IRI>();

		for(String element : stringArray){
			iriArray.add(IRI.create(element));
		}

		return iriArray;
	}

	/**
	 * This method converts an IRI ArrayList to a String ArrayList
	 * @param iriArray The IRI ArrayList
	 * @return A String ArrayList
	 */
	public static ArrayList<String> convertALIRIToString(ArrayList<IRI> iriArray){
		ArrayList<String> stringArray = new ArrayList<String>();

		for(IRI element : iriArray){
			stringArray.add(element.toString());
		}

		return stringArray;
	}

	/**
	 * Converts a HashMap with IRI keys and Pair<String, String> values into a HashMap with String keys and String[] values
	 * @param map A HashMap with IRI keys and Pair<String, String> values
	 * @return A HashMap with String keys and String[] values
	 */
	public static HashMap<String, String[]> convertHMIRIPToSP(HashMap<IRI, Pair<String, String>> map){
		HashMap<String, String[]> returnMap = new HashMap<String, String[]>();

		/* Runs the HashMap and converts the keys */
		for(IRI key : map.keySet()){
			String[] valueArray = {map.get(key).getValue0(), map.get(key).getValue1()};
			returnMap.put(key.toString(), valueArray);
		}

		return returnMap;
	}

	/**
	 * This method converts a HashMap with IRI keys and String values to both String key and values
	 * @param map A HashMap with IRI keys and String values
	 * @return A HashMap with String keys and values
	 */
	public static HashMap<String, String> convertHMIRISToSS(HashMap<IRI, String> map){
		HashMap<String, String> returnMap = new HashMap<String, String>();

		/* Runs the HashMap and converts the keys */
		for(IRI key : map.keySet()){
			returnMap.put(key.toString(), map.get(key));
		}

		return returnMap;
	}

	/**
	 * This method converts a HashMap with String keys and Pair<String, String> values to a HashMap with IRI keys and String[] values
	 * @param map A HashMap with String keys and String[] values
	 * @return A HashMap with IRI keys and Pair<String, String> values
	 */
	public static HashMap<IRI, Pair<String, String>> convertHMSPToIRIP(HashMap<String, String[]> map){
		HashMap<IRI, Pair<String, String>> returnMap = new HashMap<IRI, Pair<String, String>>();

		/* Runs the HashMap and converts the keys */
		for(String key : map.keySet()){
			Pair<String, String> valuePair = new Pair<String, String>(map.get(key)[0], map.get(key)[1]);
			returnMap.put(IRI.create(key), valuePair);
		}

		return returnMap;
	}

	/**
	 * This method converts a HashMap with String keys and values to a HashMap with IRI keys and String values
	 * @param map A HashMap with String keys and values
	 * @return A HashMap with IRI keys and String values
	 */
	public static HashMap<IRI, String> convertHMSSToIRIS(HashMap<String, String> map){
		HashMap<IRI, String> returnMap = new HashMap<IRI, String>();

		/* Runs the HashMap and converts the keys */
		for(String key : map.keySet()){
			returnMap.put(IRI.create(key), map.get(key));
		}

		return returnMap;
	}

	/**
	 * This method creates an ArrayList of Node transfer objects
	 * @param nodeId The id of the Node object
	 * @return An ArrayList of NodeTO objects
	 */
	public static ArrayList<NodeTO> getAllNodesTOFromNode(String nodeId){
		ArrayList<NodeTO> nodeTOList = new ArrayList<NodeTO>();

		/* Gets the Node object */
		NodeImpl nodeImpl = new NodeImpl();
		Node node = nodeImpl.get(nodeId);

		/* Creates the NodeTO object and adds it to the nodeTOList */
		NodeTO nodeTO = node.createTransferObject();
		nodeTOList.add(nodeTO);

		/* If the node has children then it runs the child Node objects */
		if(null != node.getChildren() &&
				!node.getChildren().isEmpty()){

			for(ObjectId childId : node.getChildren()){
				ArrayList<NodeTO> childList = getAllNodesTOFromNode(childId.toString());
				nodeTOList.addAll(childList);
			}
		}

		return nodeTOList;
	}
}
