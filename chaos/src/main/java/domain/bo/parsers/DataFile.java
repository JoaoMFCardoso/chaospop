package domain.bo.parsers;

import org.bson.types.ObjectId;

import domain.to.DataFileTO;

/**
 * The Data File class
 * A class that defines a raw data file according to the system's view
 * @author João Cardoso
 *
 */
public class DataFile extends ParsedFile{

	/** The file's name */
	private String name;

	/**
	 * The class constructor
	 */
	public DataFile() {
		super();
		this.name = null;
	}

	/**
	 * This constructor creates a DataFile based on the transfer object
	 * @param dataFileTO the DataFile transfer object
	 */
	public DataFile(DataFileTO dataFileTO){
		/* This if clause is here in case this is an update to an existing object */
		if(null == dataFileTO.get_id()){
			this._id = new ObjectId();
		}else{
			this._id = new ObjectId(dataFileTO.get_id());
		}
		this.name = dataFileTO.getName();
		this.nodeID = new ObjectId(dataFileTO.getNodeId());
	}

	/**
	 * This method creates a transfer object
	 * @return A DataFileTO transfer object
	 */
	public DataFileTO createTransferObject(){
		DataFileTO dfto = new DataFileTO();

		/* Sets the DataFileTO object attributes */
		dfto.set_id(this._id.toString());
		dfto.setName(this.name);
		dfto.setNodeId(this.nodeID.toString());

		return dfto;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
}
