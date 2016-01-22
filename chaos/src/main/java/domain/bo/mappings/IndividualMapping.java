package domain.bo.mappings;

import java.util.ArrayList;
import java.util.HashMap;

import org.bson.types.ObjectId;
import org.javatuples.Pair;
import org.semanticweb.owlapi.model.IRI;

import utils.TransferObjectUtils;
import domain.to.IndividualMappingTO;

/**
 * The Class XMLIndividualMapping.
 */
public class IndividualMapping {

	/**
	 * The mongodb id for the object
	 */
	private ObjectId _id;

	/** The mongodb ids for the DataFile objects that this IndividualMapping map */
	private ArrayList<ObjectId> dataFileIds;

	/**
	 * The tag.
	 */
	private String tag;

	/**
	 * The individual name. If individualName is .invalue then the individual name is the tag value.
	 * However the value must first be searched within the tag attributes, and if not found, it must be searched within
	 * the tag's children.
	 * If no value is given to individualName, an individual name must be created.
	 */
	private String individualName;

	/**
	 * The individual label. If individualLabel is .invalue then the individual label is the tag value.
	 * However the value must first be searched within the tag attributes, and if not found, it must be searched within
	 * the tag's children.
	 * If no value is given to individualLabel, an individual label must be created.
	 */
	private String individualLabel;

	/**
	 * The individual owl class String.
	 */
	private IRI owlClassIRI;

	/**
	 * The specification indicates that the created individual will modify the class of a previously created individual,
	 * as this is a subclass of the previously assigned class.
	 */
	private Boolean specification;

	/**
	 * The object properties [PropertyIRI, IndividualIRI].
	 * If individualIRI is .parent then the individual String is the xml parent for this tag.
	 */
	private HashMap<IRI, String> objectProperties;

	/**
	 * The data properties [PropertyIRI, value].
	 * If the value is .invalue: The value is the tag value. It must first be searched within the tag attributes
	 * and if not found, it must be searched within the tag's children.
	 */
	private HashMap<IRI, Pair<String, String>> dataProperties;


	/**
	 * Instantiates a new XML individual mapping.
	 */
	public IndividualMapping() {
		this._id = new ObjectId();
		this.dataFileIds = null;
		this.tag = null;
		this.individualName = null;
		this.individualLabel = null;
		this.owlClassIRI = null;
		this.specification = null;
		this.objectProperties = null;
		this.dataProperties = null;
	}

	/**
	 * Instantiates a new XML individual mapping through an IndividualMapping transfer object
	 */
	public IndividualMapping(IndividualMappingTO individualMappingTO) {
		/* This if clause is here in case this is an update to an existing object */
		if(null == individualMappingTO.get_id()){
			this._id = new ObjectId();
		}else{
			this._id = new ObjectId(individualMappingTO.get_id());
		}

		this.dataFileIds = TransferObjectUtils.convertALStringToObjectId(individualMappingTO.getDataFileIds());
		this.tag = individualMappingTO.getTag();
		this.individualName = individualMappingTO.getIndividualName();
		this.individualLabel = individualMappingTO.getIndividualLabel();
		this.owlClassIRI = IRI.create(individualMappingTO.getOwlClassIRI());
		this.specification = individualMappingTO.getSpecification();

		if(null == individualMappingTO.getObjectProperties()){
			this.objectProperties = null;
		}else{
			this.objectProperties = TransferObjectUtils.convertHMSSToIRIS(individualMappingTO.getObjectProperties());
		}

		if(null == individualMappingTO.getDataProperties()){
			this.dataProperties = null;
		}else{
		this.dataProperties = TransferObjectUtils.convertHMSPToIRIP(individualMappingTO.getDataProperties());
		}
	}

	/**
	 * This method creates a transfer object
	 * @return A IndividualMappingTO transfer object
	 */
	public IndividualMappingTO createTransferObject(){
		IndividualMappingTO imto = new IndividualMappingTO();

		/* Sets the object's attributes */
		imto.set_id(this._id.toString());

		if(null == this.dataFileIds){
			imto.setDataFileIds(null);
		}else{
			imto.setDataFileIds(TransferObjectUtils.convertALObjectIdToString(this.dataFileIds));
		}

		imto.setTag(this.tag);
		imto.setIndividualName(this.individualName);
		imto.setIndividualLabel(this.individualLabel);
		imto.setOwlClassIRI(this.owlClassIRI.toString());
		imto.setObjectProperties(TransferObjectUtils.convertHMIRISToSS(this.objectProperties));
		imto.setDataProperties(TransferObjectUtils.convertHMIRIPToSP(this.dataProperties));

		return imto;
	}

	/**
	 * Gets the id
	 * @return the _id
	 */
	public ObjectId getID() {
		return _id;
	}

	/**
	 * Set the id
	 * @param _id the _id to set
	 */
	public void setID(ObjectId _id) {
		this._id = _id;
	}

	/**
	 * @return the dataFileIds
	 */
	public ArrayList<ObjectId> getDataFileIds() {
		return dataFileIds;
	}

	/**
	 * @param dataFileIds the dataFileIds to set
	 */
	public void setDataFileIds(ArrayList<ObjectId> dataFileIds) {
		this.dataFileIds = dataFileIds;
	}

	/**
	 * Gets the tag.
	 *
	 * @return the tag
	 */
	public String getTag() {
		return tag;
	}


	/**
	 * Sets the tag.
	 *
	 * @param tag the tag to set
	 */
	public void setTag(String tag) {
		this.tag = tag;
	}


	/**
	 * Gets the individual name.
	 *
	 * @return the individualName
	 */
	public String getIndividualName() {
		return individualName;
	}


	/**
	 * Sets the individual name.
	 *
	 * @param individualName the individualName to set
	 */
	public void setIndividualName(String individualName) {
		this.individualName = individualName;
	}

	/**
	 * @return the individualLabel
	 */
	public String getIndividualLabel() {
		return individualLabel;
	}


	/**
	 * @param individualLabel the individualLabel to set
	 */
	public void setIndividualLabel(String individualLabel) {
		this.individualLabel = individualLabel;
	}


	/**
	 * Gets the owl class String.
	 *
	 * @return the owlClassIRI
	 */
	public IRI getOwlClassIRI() {
		return owlClassIRI;
	}


	/**
	 * Sets the owl class String.
	 *
	 * @param owlClassIRI the owlClassIRI to set
	 */
	public void setOwlClassIRI(IRI owlClassIRI) {
		this.owlClassIRI = owlClassIRI;
	}


	/**
	 * @return the specification
	 */
	public Boolean getSpecification() {
		return specification;
	}


	/**
	 * @param specification the specification to set
	 */
	public void setSpecification(Boolean specification) {
		this.specification = specification;
	}


	/**
	 * Gets the object properties.
	 *
	 * @return the objectProperties
	 */
	public HashMap<IRI, String> getObjectProperties() {
		return objectProperties;
	}


	/**
	 * Sets the object properties.
	 *
	 * @param objectProperties the objectProperties to set
	 */
	public void setObjectProperties(HashMap<IRI, String> objectProperties) {
		this.objectProperties = objectProperties;
	}

	/**
	 * Gets the data properties.
	 *
	 * @return the dataProperties
	 */
	public HashMap<IRI,  Pair<String, String>> getDataProperties() {
		return dataProperties;
	}


	/**
	 * Sets the data properties.
	 *
	 * @param dataProperties the dataProperties to set
	 */
	public void setDataProperties(HashMap<IRI, Pair<String, String>> dataProperties) {
		this.dataProperties = dataProperties;
	}
}
