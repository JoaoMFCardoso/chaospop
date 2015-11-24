package domain.bo.population;

import java.util.ArrayList;

import org.bson.types.ObjectId;

import utils.TransferObjectUtils;
import domain.to.BatchTO;

public class Batch {

	/** The Batch database id*/
	private ObjectId _id;

	/** The DataFile objects that are associated with this Batch object */
	private ArrayList<ObjectId> dataFiles;

	/** Builds a new Batch object with a unique ObjecId */
	public Batch() {
		this._id = new ObjectId();
		this.dataFiles = null;
	}

	/**
	 * Builds a new Batch object based on a BatchTO transfer object
	 * @param batchTO
	 */
	public Batch(BatchTO batchTO){
		this._id = new ObjectId(batchTO.get_id());
		this.dataFiles = TransferObjectUtils.convertALStringToObjectId(batchTO.getDataFiles());
	}

	public BatchTO createTransferObject(){
		BatchTO bto = new BatchTO();

		/* Set the BatchTO attributes */
		bto.set_id(this._id.toString());
		bto.setDataFiles(TransferObjectUtils.convertALObjectIdToString(this.dataFiles));

		return bto;
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
	 * @return the dataFiles
	 */
	public ArrayList<ObjectId> getDataFiles() {
		return dataFiles;
	}

	/**
	 * @param dataFiles the dataFiles to set
	 */
	public void setDataFiles(ArrayList<ObjectId> dataFiles) {
		this.dataFiles = dataFiles;
	}
}
