package domain.to;

import java.util.ArrayList;

public class BatchTO {

	/** The Batch database id*/
	private String _id;

	/** The DataFile objects that are associated with this Batch object */
	private ArrayList<String> dataFiles;

	public BatchTO() {
		this._id = null;
		this.dataFiles = null;
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
	 * @return the dataFiles
	 */
	public ArrayList<String> getDataFiles() {
		return dataFiles;
	}

	/**
	 * @param dataFiles the dataFiles to set
	 */
	public void setDataFiles(ArrayList<String> dataFiles) {
		this.dataFiles = dataFiles;
	}
}
