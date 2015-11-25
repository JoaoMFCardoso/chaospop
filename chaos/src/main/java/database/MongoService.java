package database;

import java.util.List;

import com.mongodb.BasicDBObject;

@SuppressWarnings("hiding")
public interface MongoService<Object> {

	/**
	 * This method builds the database object when given a java object
	 * @param object the object from which to build the database object
	 * @return The database object
	 */
	public BasicDBObject buildDBObject(Object object);

	/**
	 * This method saves an object to the database
	 * @param object The object being saved
	 * @return The id of the saved object
	 */
	public String save(Object object);

	/**
	 * This method replaces an object that is in the database
	 * @param id The object id of the object to be replaced
	 * @param object The object to replace the object in the database
	 * @return The id of the replaced object
	 */
	public String replace(String id, Object object);

	/**
	 * Gets an existing object from the database given its key
	 * @param id The object id
	 * @return The existing object from the database
	 */
	public Object get(String id);

	/**
	 * This method gets an list of objects through a query by field and value
	 * @param field The field to be searched
	 * @param tag The field value
	 * @return An list with all matching objects
	 */
	public List<Object> getBy(String field, String value);

	/**
	 * Lists all objects in the database
	 * @return A list of all objects in the database
	 */
	public List<Object> getAll();

	/**
	 * This method removes an object from the database when given its key
	 * @param id The object id
	 */
	public void remove(String id);

	/**
	 * Drops a collection from the database when given its name
	 * @param collectionName The name of the collection that is to be dropped
	 */
	public void dropCollection();

}
