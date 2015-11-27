package database.implementations;

import java.util.ArrayList;
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
import domain.bo.mappings.Mapping;

public class MappingImpl implements MongoService<Mapping> {

	/**
	 * The collection name for this implementation of the mongo service
	 */
	public static final String collectionName = "Mappings";

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
	public MappingImpl() {
		DatabaseConnector databaseConnector = new DatabaseConnector();
		this.database = databaseConnector.getDatabase();
		this.collection = database.getCollection(collectionName);
	}

	@Override
	public BasicDBObject buildDBObject(Mapping mapping){
		/* Creates the database object */
		BasicDBObject mappingDBObj = new BasicDBObject();

		mappingDBObj.append("_id", mapping.getID());

		/* Appends the file names */
		if(null != mapping.getFileList()){
			List<Object> fileNamesDBList = new BasicDBList();

			/* Runs all file names */
			for(ObjectId fileNameId : mapping.getFileList()){
				DBObject fileNamesDBObject = new BasicDBObject();

				/* stores the file name */
				fileNamesDBObject.put("fileId", fileNameId);
				fileNamesDBList.add(fileNamesDBObject);
			}

			mappingDBObj.append("fileList", fileNamesDBList);
		}

		/* Appends the base ontology */
		mappingDBObj.append("baseOntology", mapping.getBaseOntology());

		/* Appends the specific ontologies */
		if(null != mapping.getSpecificOntologies()){
			List<Object> specificOntologiesDBList = new BasicDBList();

			/* Runs all specific ontologies */
			for(ObjectId ontologyId : mapping.getSpecificOntologies()){
				DBObject specificOntologyDBObject = new BasicDBObject();

				/* stores the specific ontology iri */
				specificOntologyDBObject.put("specificOntologyId", ontologyId);
				specificOntologiesDBList.add(specificOntologyDBObject);
			}

			mappingDBObj.append("specificOntologies", specificOntologiesDBList);
		}

		/* Appends Individual Mappings */
		if(null != mapping.getIndividualMappings()){
			List<Object> individualMappingsDBList = new BasicDBList();

			/* Runs all the individual mappings */
			for(ObjectId individualMappingID : mapping.getIndividualMappings()){
				DBObject individualMappingDBObject = new BasicDBObject();

				/* Stores the individual mappings id */
				individualMappingDBObject.put("individualMappingID", individualMappingID);
				individualMappingsDBList.add(individualMappingDBObject);
			}

			mappingDBObj.append("individualMappings", individualMappingsDBList);
		}

		return mappingDBObj;
	}

	@Override
	public String save(Mapping mapping) {
		/* Creates the database object */
		BasicDBObject mappingDBObj = buildDBObject(mapping);

		this.collection.insert(mappingDBObj);
		return mappingDBObj.get("_id").toString();
	}

	@Override
	public String replace(String id, Mapping newMapping){
		/* Creates the new database object */
		BasicDBObject newMappingDBObj = buildDBObject(newMapping);

		/* Create the query */
		BasicDBObject query = new BasicDBObject().append("_id", new ObjectId(id));

		this.collection.update(query, newMappingDBObj);
		return newMappingDBObj.get("_id").toString();
	}

	@Override
	public Mapping get(String id) {
		/* Gets the basic database object */
		ObjectId dbID = new ObjectId(id);
		BasicDBObject persistent = (BasicDBObject) this.collection.findOne(dbID);

		Mapping mapping = new Mapping();
		Set<String> keyset = persistent.keySet();
		for(String key: keyset){
			/* Creates the individual mapping based on the keys */
			switch (key) {
			case "_id":
				mapping.setID(dbID);
				break;
			case "fileList":
				ArrayList<ObjectId> filelistDBArray = MongoUtilities.convertALOIdFromBDBL((BasicDBList) persistent.get(key), "fileId");
				mapping.setFileList(filelistDBArray);
				break;
			case "baseOntology":
				mapping.setBaseOntology((ObjectId) persistent.get(key));
				break;
			case "specificOntologies":
				ArrayList<ObjectId> specificOntologiesList = MongoUtilities.convertALOIdFromBDBL((BasicDBList) persistent.get(key), "specificOntologyId");
				mapping.setSpecificOntologies(specificOntologiesList);
				break;
			case "individualMappings":
				ArrayList<ObjectId> individualMappingsIDs = MongoUtilities.convertALOIdFromBDBL((BasicDBList) persistent.get(key), "individualMappingID");
				mapping.setIndividualMappings(individualMappingsIDs);
				break;
			default:
				break;
			}
		}

		return mapping;
	}

	@Override
	public List<Mapping> getBy(String field, String value) {
		/* Creates the Mapping list */
		List<Mapping> mappingsList = new ArrayList<Mapping>();

		/* A query is created with the given field */
		BasicDBObject query = new BasicDBObject(field, value);

		/* Creates the cursor and gets all basic db objects in the collection that match the query*/
		DBCursor cursor = this.collection.find(query);

		Mapping mapping = null;
		try {
		   while(cursor.hasNext()) {
			   /* Gets the object id and converts it to a mapping
				 * The imapping is then returned */
				BasicDBObject basicDBObject = (BasicDBObject) cursor.next();
				mapping = get(((ObjectId) basicDBObject.get("_id")).toString());
				mappingsList.add(mapping);
		   }
		} finally {
		   cursor.close();
		}

		return mappingsList;
	}

	@Override
	public List<Mapping> getAll() {
		/* Creates the Mapping list */
		List<Mapping> mappingsList = new ArrayList<Mapping>();

		/* Creates the cursor and gets all basic db objects in the collection */
		DBCursor cursor = this.collection.find();
		try {
			while(cursor.hasNext()) {
				/* Gets the object id and converts it to a mapping
				 * The  mapping is then added to the list */
				BasicDBObject basicDBObject = (BasicDBObject) cursor.next();
				Mapping mapping = get(((ObjectId) basicDBObject.get("_id")).toString());
				mappingsList.add(mapping);
			}
		} finally {
			cursor.close();
		}

		return mappingsList;
	}

	@Override
	public void remove(String id) {
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
