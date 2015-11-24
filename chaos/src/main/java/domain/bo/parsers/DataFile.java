package domain.bo.parsers;

import org.bson.types.ObjectId;

import domain.to.DataFileTO;

/**
 * The Data File class
 * A class that defines a raw data file according to the system's view
 * @author João Cardoso
 *
 */
public class DataFile {

	/**
	 * The database id
	 */
	private ObjectId _id;

	/**
	 * The file's name
	 */
	private String name;

	/**
	 * The file's structured data
	 */
	private ObjectId nodeID;

	/**
	 * The class constructor
	 */
	public DataFile() {
		this._id = new ObjectId();
		this.name = null;
		this.nodeID = null;
	}

	/**
	 * This constructor creates a DataFile based on the transfer object
	 * @param dataFileTO the DataFile transfer object
	 */
	public DataFile(DataFileTO dataFileTO){
		this._id = new ObjectId(dataFileTO.get_id());
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
	 * @return the _id
	 */
	public ObjectId getID() {
		return _id;
	}

	/**
	 * @param _id the _id to set
	 */
	public void setID(ObjectId _id) {
		this._id = _id;
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

	/**
	 * @return the node ID
	 */
	public ObjectId getNodeID() {
		return nodeID;
	}

	/**
	 * @param node the node to set
	 */
	public void setNodeID(ObjectId nodeID) {
		this.nodeID = nodeID;
	}
}
