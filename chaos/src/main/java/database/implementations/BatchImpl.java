package database.implementations;

import java.util.ArrayList;
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
import domain.bo.population.Batch;

public class BatchImpl implements MongoService<Batch> {

	/** The collection name for this implementation of the mongo service */
	public static final String collectionName = "Batches";

	/** The collection that is associated with this implementation */
	protected DBCollection collection;

	/** The database that is retrieved from the database connector */
	protected DB database;

	/**
	 * The class constructor
	 * Connects to the database
	 */
	public BatchImpl() {
		DatabaseConnector databaseConnector = new DatabaseConnector();
		this.database = databaseConnector.getDatabase();
		this.collection = database.getCollection(collectionName);
	}

	@Override
	public BasicDBObject buildDBObject(Batch batch) {
		/* Creates the database object */
		BasicDBObject batchDBObj = new BasicDBObject();

		/* Appends the various attributes to the database object */
		batchDBObj.append("_id", batch.getID());

		if(null != batch.getDataFiles()){
			List<Object> dataFilesDBList = new BasicDBList();

			/* Runs all DataFile objects id's */
			for(ObjectId dataFileId : batch.getDataFiles()){
				DBObject dataFileIdDBObject = new BasicDBObject();

				/* stores the file name */
				dataFileIdDBObject.put("dataFile", dataFileId);
				dataFilesDBList.add(dataFileIdDBObject);
			}

			batchDBObj.append("dataFiles", dataFilesDBList);
		}


		return batchDBObj;
	}

	@Override
	public String save(Batch batch) {
		/* Creates the database object */
		BasicDBObject batchDBObj = buildDBObject(batch);

		this.collection.insert(batchDBObj);
		return batchDBObj.get("_id").toString();
	}

	@Override
	public String replace(ObjectId id, Batch newBatch) {
		/* Creates the new database object */
		BasicDBObject newBatchDBObj = buildDBObject(newBatch);

		/* Create the query */
		BasicDBObject query = new BasicDBObject().append("_id", id);

		this.collection.update(query, newBatchDBObj);
		return newBatchDBObj.get("_id").toString();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Batch get(ObjectId id) {
		/* Gets the basic database object */
		BasicDBObject persistent = (BasicDBObject) this.collection.findOne(id);

		Batch batch = new Batch();
		Set<String> keyset = persistent.keySet();
		for(String key: keyset){
			/* Creates the Batch based on the keys */
			switch (key) {
			case "_id":
				batch.setID(id);
				break;
			case "dataFiles":
				ArrayList<ObjectId> dataFilesDBArray = (ArrayList<ObjectId>) persistent.get(key);
				batch.setDataFiles(dataFilesDBArray);
				break;
			default:
				break;
			}
		}

		return batch;
	}

	@Override
	public List<Batch> getBy(String field, String value) {
		/* Creates the Batch list */
		List<Batch> batchList = new ArrayList<Batch>();

		/* A query is created with the given field */
		BasicDBObject query = new BasicDBObject(field, value);

		/* Creates the cursor and gets all basic db objects in the collection that match the query*/
		DBCursor cursor = this.collection.find(query);

		Batch batch = null;
		try {
		   while(cursor.hasNext()) {
			   /* Gets the object id and converts it to a Batch
				 * The batch is then returned */
				BasicDBObject basicDBObject = (BasicDBObject) cursor.next();
				batch = get((ObjectId) basicDBObject.get("_id"));
				batchList.add(batch);
		   }
		} finally {
		   cursor.close();
		}

		return batchList;
	}

	@Override
	public List<Batch> getAll() {
		/* Creates the Batch list */
		List<Batch> batchList = new ArrayList<Batch>();

		/* Creates the cursor and gets all basic db objects in the collection */
		DBCursor cursor = this.collection.find();

		Batch batch = null;
		try {
		   while(cursor.hasNext()) {
			   /* Gets the object id and converts it to a Batch
				 * The batch is then returned */
				BasicDBObject basicDBObject = (BasicDBObject) cursor.next();
				batch = get((ObjectId) basicDBObject.get("_id"));
				batchList.add(batch);
		   }
		} finally {
		   cursor.close();
		}

		return batchList;
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
