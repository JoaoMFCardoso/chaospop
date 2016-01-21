package domain.to;

import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The IndividualMapping class transfer object
 * See IndividualMapping for more info
 * @author João Cardoso
 *
 */
@XmlRootElement(name = "IndividualMapping")
public class IndividualMappingTO {

	/**
	 * The mongodb id for the object
	 */
	private String _id;

	/** The mongodb id for the DataFile object that this IndividualMapping maps */
	private ArrayList<String> dataFileIds;

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
	 * The individual owl class.
	 */
	private String owlClassIRI;

	/**
	 * The specification indicates that the created individual will modify the class of a previously created individual,
	 * as this is a subclass of the previously assigned class.
	 */
	private Boolean specification;

	/**
	 * The object properties [PropertyIRI, IndividualIRI].
	 * If individualIRI is .parent then the individual String is the xml parent for this tag.
	 */
	@XmlElementWrapper(name = "objectProps")
	@XmlElement(name = "objectProp")
	private HashMap<String, String> objectProperties;

	/**
	 * The data properties [PropertyIRI, value].
	 * If the value is .invalue: The value is the tag value. It must first be searched within the tag attributes
	 * and if not found, it must be searched within the tag's children.
	 */
	@XmlElementWrapper(name = "dataProps")
	@XmlElement(name = "dataProp")
	private HashMap<String, String> dataProperties;

	public IndividualMappingTO() {
		this._id = null;
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
	 * @return the dataFileIds
	 */
	public ArrayList<String> getDataFileIds() {
		return dataFileIds;
	}

	/**
	 * @param dataFileIds the dataFileIds to set
	 */
	public void setDataFileIds(ArrayList<String> dataFileIds) {
		this.dataFileIds = dataFileIds;
	}

	/**
	 * @return the tag
	 */
	public String getTag() {
		return tag;
	}

	/**
	 * @param tag the tag to set
	 */
	public void setTag(String tag) {
		this.tag = tag;
	}

	/**
	 * @return the individualName
	 */
	public String getIndividualName() {
		return individualName;
	}

	/**
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
	 * @return the owlClassIRI
	 */
	public String getOwlClassIRI() {
		return owlClassIRI;
	}

	/**
	 * @param owlClassIRI the owlClassIRI to set
	 */
	public void setOwlClassIRI(String owlClassIRI) {
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
	 * @return the objectProperties
	 */
	public HashMap<String, String> getObjectProperties() {
		return objectProperties;
	}

	/**
	 * @param objectProperties the objectProperties to set
	 */
	public void setObjectProperties(HashMap<String, String> objectProperties) {
		this.objectProperties = objectProperties;
	}

	/**
	 * @return the dataProperties
	 */
	public HashMap<String, String> getDataProperties() {
		return dataProperties;
	}

	/**
	 * @param dataProperties the dataProperties to set
	 */
	public void setDataProperties(HashMap<String, String> dataProperties) {
		this.dataProperties = dataProperties;
	}
}
