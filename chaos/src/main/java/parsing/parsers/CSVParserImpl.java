package parsing.parsers;

import java.io.File;

import org.bson.types.ObjectId;

import database.implementations.DataFileImpl;
import database.implementations.NodeImpl;
import domain.bo.parsers.DataFile;
import parsing.ParserInterface;

/**
 * Parser for CSV files
 * @author João M. F. Cardoso
 *
 */
public class CSVParserImpl implements ParserInterface {

	/** The database functions to store DataFile objects */
	private DataFileImpl dataFileImpl;

	/** The database functions to store Node objects */
	private NodeImpl nodeImpl;

	/** The class constructor
	 *  It initializes the DataFileImpl and NodeImpl objects
	 */
	public CSVParserImpl() {
		this.dataFileImpl = new DataFileImpl();
		this.nodeImpl = new NodeImpl();
	}
	
	@Override
	public String parseFile(File file) throws Exception {
		/* Creates the Data File */
		DataFile dataFile = new DataFile();
		ObjectId dataFileId = dataFile.getID();
		
		// TODO Auto-generated method stub
		return null;
	}

}
