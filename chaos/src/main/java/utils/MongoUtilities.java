package utils;

import java.util.ArrayList;
import java.util.HashMap;

import org.semanticweb.owlapi.model.IRI;

public class MongoUtilities {

	/**
	 * This method converts an IRI to the mongoDB database
	 * @param iri The iri, possibly with illegal characters
	 * @return The db iri (a string)
	 */
	public static String convertIRItoDB(IRI iri){
		String dbIRI;
		if(iri.toString().contains(".")){
			dbIRI = iri.toString().replace(".", "_");
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
		if(dbIri.contains("_")){
			iri = IRI.create(dbIri.replace("_", "."));
		}else{
			iri = IRI.create(dbIri);
		}

		return iri;
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
			String newElement = element.replace("_", ".");

			/* Adds the pair to the new hashmap */
			returnList.add(newElement);
		}

		return returnList;
	}
}
