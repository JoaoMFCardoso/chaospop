package utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.bson.types.ObjectId;
import org.semanticweb.owlapi.model.IRI;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;

public class MongoUtilities {

	/**
	 * This method converts an IRI to the mongoDB database
	 * @param iri The iri, possibly with illegal characters
	 * @return The db iri (a string)
	 */
	public static String convertIRItoDB(IRI iri){
		String dbIRI;
		if(iri.toString().contains(".")){
			dbIRI = iri.toString().replace(".", "«");
		}else{
			dbIRI = iri.toString();
		}

		return dbIRI;
	}

	/**
	 * This method converts a db IRI to a proper IRI
	 * @param iri The db iri
	 * @return A proper iri
	 */
	public static IRI convertIRIfromDB(String dbIri){
		IRI iri;
		if(dbIri.contains("«")){
			iri = IRI.create(dbIri.replace("«", "."));
		}else{
			iri = IRI.create(dbIri);
		}

		return iri;
	}

	/**
	 *This method converts an individuals map into a suitable map to be stored in mongodb
	 *It converts from a HashMap with IRI as key and a String Array as value, to a String as Key and BasicDBObject as value
	 * @param individualsMap The individuals Map
	 * @return The appropriated individuals Map after processing
	 */
	public static HashMap<String, Object> convertHM_IRISA_SBDBOToDB(HashMap<IRI, String[]> individualsMap){
		HashMap<String, Object> returnMap = new HashMap<String, Object>();

		for(IRI key : individualsMap.keySet()){
			BasicDBObject labelAndClass = new BasicDBObject();
			labelAndClass.append("label", individualsMap.get(key)[0]);
			labelAndClass.append("class", individualsMap.get(key)[1]);

			String newKey = convertIRItoDB(key);

			returnMap.put(newKey, labelAndClass);
		}

		return returnMap;
	}

	public static HashMap<IRI, String[]> convertHM_SBDBO_IRISAFromDB(HashMap<String, Object> individualsDBMap){
		HashMap<IRI, String[]> returnMap = new HashMap<IRI, String[]>();

		for(String key : individualsDBMap.keySet()){
			IRI newKey = convertIRIfromDB(key);

			BasicDBObject persistent = (BasicDBObject) individualsDBMap.get(key);
			Set<String> keyset = persistent.keySet();
			String[] labelAndClass = new String[2];
			for(String value : keyset){
				/* Creates the Label And Class based on the values */
				switch (value) {
				case "label":
					labelAndClass[0] = (String) persistent.get(value);
					break;
				case "class":
					labelAndClass[1] = (String) persistent.get(value);
					break;
				default:
					break;
				}
			}

			returnMap.put(newKey, labelAndClass);
		}
		return returnMap;
	}

	/**
	 * This method converts a properties map into a suitable map that can be stored in mongodb
	 * This is due to the fact that IRI's can't be serialized, besides they can contain illegal characters
	 * such as ".".
	 *  @param propertiesMap The properties map to be converted
	 *  @return An appropriated properties map
	 */
	public static HashMap<String, String> convertPropertyMapToDB(HashMap<IRI, String> propertiesMap){
		HashMap<String, String> cleanKeysMap = new HashMap<String, String>();
		for(IRI key : propertiesMap.keySet()){
			/* Adapts keys to clear any illegal characters */
			String newKey = convertIRItoDB(key);

			/* Adds the pair to the new hashmap */
			cleanKeysMap.put(newKey, propertiesMap.get(key));
		}

		return cleanKeysMap;
	}

	/**
	 * This method converts a properties map that has been stored in mongodb to a properties map that
	 * is suitable for use within the back-end of the project, i.e., converts string keys into iri keys.
	 *  @param propertiesMap The properties map to be converted
	 *  @return A properties map with IRI keys
	 */
	public static HashMap<IRI, String> convertPropertyMapFromDB(HashMap<String, String> propertiesMap){
		HashMap<IRI, String> cleanKeysMap = new HashMap<IRI, String>();
		for(String key : propertiesMap.keySet()){
			/* Cleans the keys that have been tweeked in the creation process */
			IRI newKey = convertIRIfromDB(key);

			/* Adds the pair to the new hashmap */
			cleanKeysMap.put(newKey, propertiesMap.get(key));
		}

		return cleanKeysMap;
	}

	/**
	 * This method converts an IRI array into a String array
	 * @param list the string array
	 * @return an IRI array
	 */
	public static ArrayList<String> convertALIRIToDB(ArrayList<IRI> list){
		ArrayList<String> returnList = new ArrayList<String>();

		for(IRI element : list){
			/* Cleans the keys that have been tweeked in the creation process */
			String newElement = convertIRItoDB(element);

			/* Adds the pair to the new hashmap */
			returnList.add(newElement);
		}

		return returnList;
	}

	/**
	 * This method converts a string array into an IRI array
	 * @param list the string array
	 * @return an IRI array
	 */
	public static ArrayList<IRI> convertArrayListIRIFromDB(ArrayList<String> list){
		ArrayList<IRI> returnList = new ArrayList<IRI>();

		for(String element : list){
			/* Cleans the keys that have been tweeked in the creation process */
			IRI newElement = convertIRIfromDB(element);

			/* Adds the pair to the new hashmap */
			returnList.add(newElement);
		}

		return returnList;
	}

	/**
	 * This method converts a string array into an String array with illegal characters for the db
	 * @param list the string array
	 * @return an string array with illegal characters for the db
	 */
	public static ArrayList<String> convertArrayListStringFromDB(ArrayList<String> list){
		ArrayList<String> returnList = new ArrayList<String>();

		for(String element : list){
			/* Cleans the keys that have been tweeked in the creation process */
			String newElement = element.replace("«", ".");

			/* Adds the pair to the new hashmap */
			returnList.add(newElement);
		}

		return returnList;
	}

	/**
	 * This method converts a BasicDBList into an ObjectId ArrayList.
	 * @param basicDBList The BasicDBList object
	 * @param key The key for the BasicDBObject objects within the BasicDBList
	 * @return An ObjectId ArrayList
	 */
	public static ArrayList<ObjectId> convertALOIdFromBDBL(BasicDBList basicDBList, String key){
		ArrayList<ObjectId> returnList = new ArrayList<ObjectId>();

		/* Runs the BasicDBList and performs the necessary casts */
		for(Object element : basicDBList){
			BasicDBObject bdbo = (BasicDBObject) element;
			ObjectId id = (ObjectId) bdbo.get(key);
			returnList.add(id);
		}

		return returnList;
	}

	/**
	 * This method converts a BasicDBList into a String ArrayList.
	 * @param basicDBList The BasicDBList object
	 * @param key The key for the BasicDBObject objects within the BasicDBList
	 * @return An ObjectId ArrayList
	 */
	public static ArrayList<String> convertALStringFromBDBL(BasicDBList basicDBList, String key){
		ArrayList<String> returnList = new ArrayList<String>();

		/* Runs the BasicDBList and performs the necessary casts */
		for(Object element : basicDBList){
			BasicDBObject bdbo = (BasicDBObject) element;
			String id = (String) bdbo.get(key);
			returnList.add(id);
		}

		return returnList;
	}
}
