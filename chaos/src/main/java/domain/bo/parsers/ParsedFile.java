package domain.bo.parsers;

import org.bson.types.ObjectId;

public class ParsedFile {

	/** The database id */
	protected ObjectId _id;

	/** The file's structured data */
	protected ObjectId nodeID;

	public ParsedFile() {
		this._id = new ObjectId();
		this.nodeID = null;
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
	 * @return the nodeID
	 */
	public ObjectId getNodeID() {
		return nodeID;
	}

	/**
	 * @param nodeID the nodeID to set
	 */
	public void setNodeID(ObjectId nodeID) {
		this.nodeID = nodeID;
	}

}
