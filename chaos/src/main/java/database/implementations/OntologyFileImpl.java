package database.implementations;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bson.types.ObjectId;
import org.semanticweb.owlapi.model.IRI;

import utils.MongoUtilities;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;

import database.DatabaseConnector;
import database.MongoService;
import domain.bo.ontologies.OntologyFile;

public class OntologyFileImpl implements MongoService<OntologyFile> {

	/** The collection name for this implementation of the mongo service */
	public static final String collectionName = "OntologyFiles";

	/** The collection that is associated with this implementation */
	protected DBCollection collection;

	/** The database that is retrieved from the database connector */
	protected DB database;

	/**
	 * The class constructor
	 * Connects to the database
	 */
	public OntologyFileImpl() {
		DatabaseConnector databaseConnector = new DatabaseConnector();
		this.database = databaseConnector.getDatabase();
		this.collection = database.getCollection(collectionName);
	}

	@Override
	public BasicDBObject buildDBObject(OntologyFile ontologyFile) {
		/* Creates the database object */
		BasicDBObject ontologyFileDBObj = new BasicDBObject();

		/* Appends the various attributes to the database object */
		ontologyFileDBObj.append("_id", ontologyFile.getID());

		String namespaceIRI = MongoUtilities.convertIRItoDB(ontologyFile.getNamespace());
		ontologyFileDBObj.append("namespace", namespaceIRI);
		ontologyFileDBObj.append("path", ontologyFile.getPath());

		return ontologyFileDBObj;
	}

	@Override
	public String save(OntologyFile ontologyFile) {
		/* Creates the database object */
		BasicDBObject ontologyFileDBObj = buildDBObject(ontologyFile);

		this.collection.insert(ontologyFileDBObj);
		return ontologyFileDBObj.get("_id").toString();
	}

	@Override
	public String replace(String id, OntologyFile newOntologyFile) {
		/* Creates the new database object */
		BasicDBObject newOntologyFileDBObj = buildDBObject(newOntologyFile);

		/* Create the query */
		BasicDBObject query = new BasicDBObject().append("_id", new ObjectId(id));

		this.collection.update(query, newOntologyFileDBObj);
		return newOntologyFileDBObj.get("_id").toString();
	}

	@Override
	public OntologyFile get(String id) {
		/* Gets the basic database object */
		ObjectId dbID = new ObjectId(id);
		BasicDBObject persistent = (BasicDBObject) this.collection.findOne(dbID);

		OntologyFile ontologyFile = new OntologyFile();
		Set<String> keyset = persistent.keySet();
		for(String key: keyset){
			/* Creates the OntologyFile based on the keys */
			switch (key) {
			case "_id":
				ontologyFile.setID(dbID);
				break;
			case "namespace":
				IRI namespaceIRI = MongoUtilities.convertIRIfromDB((String) persistent.get(key));
				ontologyFile.setNamespace(namespaceIRI);
				break;
			case "path":
				ontologyFile.setPath((String) persistent.get(key));
				break;
			default:
				break;
			}
		}

		return ontologyFile;
	}

	@Override
	public List<OntologyFile> getBy(String field, String value) {
		/* Creates the OntologyFile list */
		List<OntologyFile> ontologyFileList = new ArrayList<OntologyFile>();

		/* A query is created with the given field */
		BasicDBObject query = new BasicDBObject(field, value);

		/* Creates the cursor and gets all basic db objects in the collection that match the query*/
		DBCursor cursor = this.collection.find(query);

		OntologyFile ontologyFile = null;
		try {
		   while(cursor.hasNext()) {
			   /* Gets the object id and converts it to a OntologyFile
				 * The OntologyFile is then returned */
				BasicDBObject basicDBObject = (BasicDBObject) cursor.next();
				ontologyFile = get(((ObjectId) basicDBObject.get("_id")).toString());
				ontologyFileList.add(ontologyFile);
		   }
		} finally {
		   cursor.close();
		}

		return ontologyFileList;
	}

	@Override
	public List<OntologyFile> getAll() {
		/* Creates the OntologyFile list */
		List<OntologyFile> ontologyFileList = new ArrayList<OntologyFile>();

		/* Creates the cursor and gets all basic db objects in the collection */
		DBCursor cursor = this.collection.find();

		OntologyFile ontologyFile = null;
		try {
		   while(cursor.hasNext()) {
			   /* Gets the object id and converts it to a OntologyFile
				 * The OntologyFile is then returned */
				BasicDBObject basicDBObject = (BasicDBObject) cursor.next();
				ontologyFile = get(((ObjectId) basicDBObject.get("_id")).toString());
				ontologyFileList.add(ontologyFile);
		   }
		} finally {
		   cursor.close();
		}

		return ontologyFileList;
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
