package database.implementations;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;

import database.DatabaseConnector;
import database.MongoService;
import domain.bo.parsers.DataFile;

public class DataFileImpl implements MongoService<DataFile> {
	/**
	 * The collection name for this implementation of the mongo service
	 */
	public static final String collectionName = "DataFiles";

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
	public DataFileImpl() {
		DatabaseConnector databaseConnector = new DatabaseConnector();
		this.database = databaseConnector.getDatabase();
		this.collection = database.getCollection(collectionName);
	}

	@Override
	public BasicDBObject buildDBObject(DataFile dataFile){
		/* Creates the database object */
		BasicDBObject dataFileDBObj = new BasicDBObject();

		/* Appends the various attributes to the database object */
		dataFileDBObj.append("_id", dataFile.getID());
		dataFileDBObj.append("name", dataFile.getName());
		dataFileDBObj.append("node", dataFile.getNodeID());

		return dataFileDBObj;
	}

	@Override
	public String save(DataFile dataFile) {
		/* Creates the database object */
		BasicDBObject dataFileDBObj = buildDBObject(dataFile);

		this.collection.insert(dataFileDBObj);
		return dataFileDBObj.get("_id").toString();
	}

	@Override
	public String replace(ObjectId id, DataFile newDataFile){
		/* Creates the new database object */
		BasicDBObject newDataFileDBObj = buildDBObject(newDataFile);

		/* Create the query */
		BasicDBObject query = new BasicDBObject().append("_id", id);

		this.collection.update(query, newDataFileDBObj);
		return newDataFileDBObj.get("_id").toString();
	}

	@Override
	public DataFile get(ObjectId id) {
		/* Gets the basic database object */
		BasicDBObject persistent = (BasicDBObject) this.collection.findOne(id);

		DataFile dataFile = new DataFile();
		Set<String> keyset = persistent.keySet();
		for(String key: keyset){
			/* Creates the DataFile based on the keys */
			switch (key) {
			case "_id":
				dataFile.setID(id);
				break;
			case "name":
				dataFile.setName((String) persistent.get(key));
				break;
			case "node":
				dataFile.setNodeID((ObjectId) persistent.get(key));
				break;
			default:
				break;
			}
		}

		return dataFile;
	}

	@Override
	public List<DataFile> getBy(String field, String value) {
		/* Creates the DataFile list */
		List<DataFile> dataFileList = new ArrayList<DataFile>();

		/* A query is created with the given field */
		BasicDBObject query = new BasicDBObject(field, value);

		/* Creates the cursor and gets all basic db objects in the collection that match the query*/
		DBCursor cursor = this.collection.find(query);

		DataFile dataFile = null;
		try {
		   while(cursor.hasNext()) {
			   /* Gets the object id and converts it to a DataFile
				 * The DataFile is then returned */
				BasicDBObject basicDBObject = (BasicDBObject) cursor.next();
				dataFile = get((ObjectId) basicDBObject.get("_id"));
				dataFileList.add(dataFile);
		   }
		} finally {
		   cursor.close();
		}

		return dataFileList;
	}

	@Override
	public List<DataFile> getAll() {
		/* Creates the DataFile list */
		List<DataFile> dataFileList = new ArrayList<DataFile>();

		/* Creates the cursor and gets all basic db objects in the collection */
		DBCursor cursor = this.collection.find();

		DataFile dataFile = null;
		try {
		   while(cursor.hasNext()) {
			   /* Gets the object id and converts it to a DataFile
				 * The DataFile is then returned */
				BasicDBObject basicDBObject = (BasicDBObject) cursor.next();
				dataFile = get((ObjectId) basicDBObject.get("_id"));
				dataFileList.add(dataFile);
		   }
		} finally {
		   cursor.close();
		}

		return dataFileList;
	}

	@Override
	public void remove(ObjectId id) {
		/* Removes the associated Node object tree */
		/* Gets the DataFile */
		DataFile dataFile = get(id);

		ObjectId rootNodeId = dataFile.getNodeID();
		NodeImpl nodeImpl = new NodeImpl();
		nodeImpl.remove(rootNodeId);

		/* Removes the DataFile */
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
