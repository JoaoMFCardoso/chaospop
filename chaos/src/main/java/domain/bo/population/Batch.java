package domain.bo.population;

import java.util.ArrayList;

import org.bson.types.ObjectId;

import utils.TransferObjectUtils;
import domain.to.BatchTO;

public class Batch {

	/** The Batch database id*/
	private ObjectId _id;

	/** The Mapping objects that are associated with this Batch object */
	private ArrayList<ObjectId> mappings;

	/** Builds a new Batch object with a unique ObjecId */
	public Batch() {
		this._id = new ObjectId();
		this.mappings = null;
	}

	/**
	 * Builds a new Batch object based on a BatchTO transfer object
	 * @param batchTO
	 */
	public Batch(BatchTO batchTO){
		/* This if clause is here in case this is an update to an existing object */
		if(null == batchTO.get_id()){
			this._id = new ObjectId();
		}else{
			this._id = new ObjectId(batchTO.get_id());
		}
		this.mappings = TransferObjectUtils.convertALStringToObjectId(batchTO.getDataFiles());
	}

	public BatchTO createTransferObject(){
		BatchTO bto = new BatchTO();

		/* Set the BatchTO attributes */
		bto.set_id(this._id.toString());
		bto.setDataFiles(TransferObjectUtils.convertALObjectIdToString(this.mappings));

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
	 * @return the mappings
	 */
	public ArrayList<ObjectId> getMappings() {
		return mappings;
	}

	/**
	 * @param mappings the mappings to set
	 */
	public void setMappings(ArrayList<ObjectId> dataFiles) {
		this.mappings = dataFiles;
	}
}
