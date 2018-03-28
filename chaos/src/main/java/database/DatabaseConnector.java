package database;

import java.net.UnknownHostException;
import java.util.Arrays;

import org.apache.log4j.Logger;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

import properties.PropertiesHandler;

public class DatabaseConnector {

	/**
	 * The connection to the logger
	 */
	static Logger log = Logger.getLogger(DatabaseConnector.class.getName());

	/**
	 * The mongo client that is used to create the connection to the database
	 */
	private MongoClient mongoClient;
	
	/**
	 * The mongo credential that is used to store the credentials for the connection to the database
	 */
	private MongoCredential credential;

	/**
	 * The database
	 */
	private DB database;

	/**
	 * The class constructor, that allows for the connection to a database according to the properties file
	 */
	public DatabaseConnector() {
		PropertiesHandler.propertiesLoader();

		String host = PropertiesHandler.configProperties.getProperty("db.host");
		Integer port = Integer.valueOf(PropertiesHandler.configProperties.getProperty("db.port"));
		String databaseName = PropertiesHandler.configProperties.getProperty("db.name");
		String user = PropertiesHandler.configProperties.getProperty("db.user");
		char[] passwd = PropertiesHandler.configProperties.getProperty("db.passwd").toCharArray();
		
		try {
			this.credential = MongoCredential.createCredential(user, databaseName, passwd);
			this.mongoClient = new MongoClient(new ServerAddress(host, port), Arrays.asList(credential));
			//this.mongoClient = new MongoClient(host, port);
			this.database = this.mongoClient.getDB(databaseName);
		} catch (UnknownHostException e) {
			log.error("Mongo Client throwed an exception", e);
		}
	}

	/**
	 * Gets the connected database
	 * @return The connected database
	 */
	public DB getDatabase(){
		return this.database;
	}
}
