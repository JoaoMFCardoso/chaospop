package database.implementations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.bson.types.ObjectId;
import org.javatuples.Pair;
import org.semanticweb.owlapi.model.IRI;

import utils.MongoUtilities;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import database.DatabaseConnector;
import database.MongoService;
import domain.bo.mappings.IndividualMapping;

public class IndividualMappingsImpl implements MongoService<IndividualMapping> {

	/**
	 * The collection name for this implementation of the mongo service
	 */
	public static final String collectionName = "IndividualMappings";

	/**
	 * The collection that is associated with this implementation
	 */
	protected DBCollection collection;

	/**
	 * The database that is retrieved from the database connector
	 */
	protected DB database;

	/**
	 * The class constructor.
	 * It establishes a connection to the database
	 */
	public IndividualMappingsImpl() {
		DatabaseConnector databaseConnector = new DatabaseConnector();
		this.database = databaseConnector.getDatabase();
		this.collection = database.getCollection(collectionName);

	}

	@Override
	public BasicDBObject buildDBObject(IndividualMapping individualMapping){
		/* Creates the database object */
		BasicDBObject individualMappingDBObj = new BasicDBObject();

		/* Appends the various attributes to the database object */
		individualMappingDBObj.append("_id", individualMapping.getID());

		/* Appends the DataFile Id's */
		if(null != individualMapping.getDataFileIds()){
			List<Object> DataFileIdsDBList = new BasicDBList();

			/* Runs all the DataFile Ids */
			for(ObjectId dataFileID : individualMapping.getDataFileIds()){
				DBObject dataFileIDDBObject = new BasicDBObject();

				/* Stores the individual mappings id */
				dataFileIDDBObject.put("dataFileId", dataFileID);
				DataFileIdsDBList.add(dataFileIDDBObject);
			}

			individualMappingDBObj.append("dataFileIds", DataFileIdsDBList);
		}

		individualMappingDBObj.append("tag", individualMapping.getTag());
		individualMappingDBObj.append("individualName", individualMapping.getIndividualName());
		individualMappingDBObj.append("individualLabel", individualMapping.getIndividualLabel());

		String owlClassIRI = MongoUtilities.convertIRItoDB(individualMapping.getOwlClassIRI());
		individualMappingDBObj.append("owlClassIRI", owlClassIRI);

		individualMappingDBObj.append("specification", individualMapping.getSpecification());

		/* Creates the object properties database object and appends it to the individual mappings database object*/
		if(null != individualMapping.getObjectProperties()){
			HashMap<String, String> objectProperties = MongoUtilities.convertPropertyMapToDB(individualMapping.getObjectProperties());
			BasicDBObject objectPropertiesDBObj = new BasicDBObject(objectProperties);
			individualMappingDBObj.append("objectProperties", objectPropertiesDBObj);
		}

		/* Creates the data properties database object and appends it to the individual mappings database object */
		if(null != individualMapping.getDataProperties()){
			HashMap<String, Object> dataProperties = MongoUtilities.convertHMIRIPairtoDB(individualMapping.getDataProperties());
			individualMappingDBObj.append("dataProperties", dataProperties);
		}

		return individualMappingDBObj;
	}

	@Override
	public String save(IndividualMapping individualMapping) {
		/* Creates the database object */
		BasicDBObject individualMappingDBObj = buildDBObject(individualMapping);

		this.collection.insert(individualMappingDBObj);
		return individualMappingDBObj.get("_id").toString();
	}

	@Override
	public String replace(String id, IndividualMapping newIndividualMapping){
		/* Creates the new database object */
		BasicDBObject newIndividualMappingDBObj = buildDBObject(newIndividualMapping);

		/* Create the query */
		BasicDBObject query = new BasicDBObject().append("_id", new ObjectId(id));

		this.collection.update(query, newIndividualMappingDBObj);
		return newIndividualMappingDBObj.get("_id").toString();
	}

	@SuppressWarnings("unchecked")
	@Override
	public IndividualMapping get(String id) {
		/* Gets the basic database object */
		ObjectId dbID = new ObjectId(id);
		BasicDBObject persistent = (BasicDBObject) this.collection.findOne(dbID);

		IndividualMapping individualMapping = new IndividualMapping();
		Set<String> keyset = persistent.keySet();
		for(String key: keyset){
			/* Creates the individual mapping based on the keys */
			switch (key) {
			case "_id":
				individualMapping.setID(dbID);
				break;
			case "dataFileIds":
				ArrayList<ObjectId> dataFileIDs = MongoUtilities.convertALOIdFromBDBL((BasicDBList) persistent.get(key), "dataFileId");
				individualMapping.setDataFileIds(dataFileIDs);
				break;
			case "tag":
				individualMapping.setTag((String) persistent.get(key));
				break;
			case "individualName":
				individualMapping.setIndividualName((String) persistent.get(key));
				break;
			case "individualLabel":
				individualMapping.setIndividualLabel((String) persistent.get(key));
				break;
			case "owlClassIRI":
				IRI owlClassIRI = MongoUtilities.convertIRIfromDB((String) persistent.get(key));
				individualMapping.setOwlClassIRI(owlClassIRI);
				break;
			case "specification":
				individualMapping.setSpecification((Boolean) persistent.get(key));
				break;
			case "objectProperties":
				HashMap<String, String> objectPropertiesDBMap = (HashMap<String, String>) persistent.get(key);
				HashMap<IRI, String> objectProperties = MongoUtilities.convertPropertyMapFromDB(objectPropertiesDBMap);
				individualMapping.setObjectProperties(objectProperties);
				break;
			case "dataProperties":
				HashMap<String, Object> dataPropertiesDBMap = (HashMap<String, Object>) persistent.get(key);
				HashMap<IRI, Pair<String, String>> dataProperties = MongoUtilities.convertHMSPairFromDB(dataPropertiesDBMap);
				individualMapping.setDataProperties(dataProperties);
				break;
			default:
				break;
			}
		}

		return individualMapping;
	}

	@Override
	public List<IndividualMapping> getBy(String field, String value){
		/* Creates the individualMapping list */
		List<IndividualMapping> individualMappingsList = new ArrayList<IndividualMapping>();

		/* A query is created with the given field */
		BasicDBObject query = new BasicDBObject(field, value);

		/* Creates the cursor and gets all basic db objects in the collection that match the query*/
		DBCursor cursor = this.collection.find(query);

		IndividualMapping individualMapping = null;
		try {
		   while(cursor.hasNext()) {
			   /* Gets the object id and converts it to an individual mapping
				 * The individual mapping is then returned */
				BasicDBObject basicDBObject = (BasicDBObject) cursor.next();
				individualMapping = get(((ObjectId) basicDBObject.get("_id")).toString());
				individualMappingsList.add(individualMapping);
		   }
		} finally {
		   cursor.close();
		}

		return individualMappingsList;
	}

	@Override
	public List<IndividualMapping> getAll() {
		/* Creates the individualMapping list */
		List<IndividualMapping> individualMappingsList = new ArrayList<IndividualMapping>();

		/* Creates the cursor and gets all basic db objects in the collection */
		DBCursor cursor = this.collection.find();
		try {
			while(cursor.hasNext()) {
				/* Gets the object id and converts it to an individual mapping
				 * The individual mapping is then added to the list */
				BasicDBObject basicDBObject = (BasicDBObject) cursor.next();
				IndividualMapping individualMapping = get(((ObjectId) basicDBObject.get("_id")).toString());
				individualMappingsList.add(individualMapping);
			}
		} finally {
			cursor.close();
		}

		return individualMappingsList;
	}

	@Override
	public void remove(String id){
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
