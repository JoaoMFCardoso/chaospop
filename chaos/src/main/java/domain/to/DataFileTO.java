package domain.to;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement (name = "DataFile")
/**
 * The DataFile Transfer Object
 * See DataFile class for reference
 * @author João Cardoso
 *
 */
public class DataFileTO {

	/** The DataFile _id */
	private String _id;

	/** The DataFile name */
	private String name;

	/** The DataFile associated Node _id */
	private String nodeId;

	public DataFileTO() {
		this._id = null;
		this.name = null;
		this.nodeId = null;
	}

	/**
	 * @return the _id
	 */
	public String get_id() {
		return _id;
	}

	/**
	 * @param _id the _id to set
	 */
	public void set_id(String _id) {
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
	 * @return the nodeId
	 */
	public String getNodeId() {
		return nodeId;
	}

	/**
	 * @param nodeId the nodeId to set
	 */
	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

}
