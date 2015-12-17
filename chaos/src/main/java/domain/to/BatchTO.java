package domain.to;

import java.util.ArrayList;

public class BatchTO {

	/** The Batch database id*/
	private String _id;

	/** The Mapping objects that are associated with this Batch object */
	private ArrayList<String> mappingIds;

	public BatchTO() {
		this._id = null;
		this.mappingIds = null;
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
	 * @return the mappingIds
	 */
	public ArrayList<String> getMappingIds() {
		return mappingIds;
	}

	/**
	 * @param mappingIds the mappingIds to set
	 */
	public void setMappingIds(ArrayList<String> mappingIds) {
		this.mappingIds = mappingIds;
	}
}
