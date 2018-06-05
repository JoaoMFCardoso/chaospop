package parsing.parsers;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.bson.types.ObjectId;

import database.implementations.DataFileImpl;
import database.implementations.NodeImpl;
import domain.bo.parsers.DataFile;
import domain.bo.parsers.Node;
import exceptions.ChaosPopException;
import exceptions.ErrorMessage;
import parsing.ParserInterface;
import utils.FileOperationsUtils;

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
	public String parseFile(File file) throws ChaosPopException {
		/* Creates the Data File */
		DataFile dataFile = new DataFile();
		Node rootNode = new Node();
		String dataFileId;
		
		try {
		
			/* Reads the CSV File */
			Reader csvReader = new FileReader(file);
			CSVParser csvParser = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(csvReader);
			
			/* Gets the headers and records */
			Map<String, Integer> csvHeadersMap = csvParser.getHeaderMap();
			List<CSVRecord> csvRecords = csvParser.getRecords();
			
			/* Fill root Node */
			rootNode.setDataFileId(dataFile.getID());
			rootNode.setTag("root");
			ArrayList<ObjectId> rootNodeChildren = new ArrayList<ObjectId>();
			
			/* Runs the records, creating an element Node for each. And adding each one to the root node as children */
			for (CSVRecord csvRecord : csvRecords) {
				
				/* Creates and fills element Node */
				Node elementNode = new Node();
				
				elementNode.setTag("element");
				elementNode.setDataFileId(dataFile.getID());
				elementNode.setParent(rootNode.getID());
				rootNodeChildren.add(elementNode.getID());

				/* Stores the record value for each header as attributes of the element Node */
				HashMap<String, String> attributesMap = new HashMap<String,String>();
				for(String csvHeader : csvHeadersMap.keySet()) {
					/* Sets the csvHeader as the attribute, and the matching record value as its value */
					String value = csvRecord.get(csvHeadersMap.get(csvHeader));
					attributesMap.put(csvHeader, value);
				}
				
				elementNode.setAttributes(attributesMap);
				
				/* Stores the element Node */
				this.nodeImpl.save(elementNode);
			}
			
			/* Stores the root node */
			this.nodeImpl.save(rootNode);
			
			/* Stores the DataFile in the database */
			dataFileId = FileOperationsUtils.storeDataFile(this.dataFileImpl,dataFile, file, rootNode);
			
		}catch(IOException ioException) {
			ErrorMessage ioError = new ErrorMessage();
			ioError.setMessage(ioException.getMessage());
			ioError.setStatus(Response.Status.BAD_REQUEST.getStatusCode());
			
			ChaosPopException chaosPopException = new ChaosPopException(ioException.getMessage());
			chaosPopException.setErrormessage(ioError);
			
			throw chaosPopException;
		}
		
		return dataFileId;
	}

}
