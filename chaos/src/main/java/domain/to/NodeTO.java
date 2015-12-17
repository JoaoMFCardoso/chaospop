package domain.to;

import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement (name = "Node")
/**
 * The Node class transfer object
 * See Node class for reference
 * @author João Cardoso
 *
 */
public class NodeTO {

	/** The node id */
	private String _id;

	/** The dataFile id */
	private String dataFileId;

	/** The children. */
	@XmlElementWrapper(name = "childrenIDs")
	@XmlElement(name = "childID")
	private ArrayList<String> children;

	/** The parent. */
	private String parent;

	/** The tag. */
	private String tag;

	/** The value. */
	private String value;

	/** The has attributes. */
	private Boolean hasAttributes;

	/** The attributes. */
	@XmlElementWrapper(name = "attrs")
	@XmlElement(name = "attr")
	private HashMap<String, String> attributes;

	/** The constructor. */
	public NodeTO() {
		this._id = null;
		this.dataFileId = null;
		this.children = null;
		this.parent = null;
		this.tag = null;
		this.value = null;
		this.hasAttributes = null;
		this.attributes = null;
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
	 * @return the dataFileId
	 */
	public String getDataFileId() {
		return dataFileId;
	}

	/**
	 * @param dataFileId the dataFileId to set
	 */
	public void setDataFileId(String dataFileId) {
		this.dataFileId = dataFileId;
	}

	/**
	 * @return the children
	 */
	public ArrayList<String> getChildren() {
		return children;
	}

	/**
	 * @param children the children to set
	 */
	public void setChildren(ArrayList<String> children) {
		this.children = children;
	}

	/**
	 * @return the parent
	 */
	public String getParent() {
		return parent;
	}

	/**
	 * @param parent the parent to set
	 */
	public void setParent(String parent) {
		this.parent = parent;
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
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * @return the hasAttributes
	 */
	public Boolean getHasAttributes() {
		return hasAttributes;
	}

	/**
	 * @param hasAttributes the hasAttributes to set
	 */
	public void setHasAttributes(Boolean hasAttributes) {
		this.hasAttributes = hasAttributes;
	}

	/**
	 * @return the attributes
	 */
	public HashMap<String, String> getAttributes() {
		return attributes;
	}

	/**
	 * @param attributes the attributes to set
	 */
	public void setAttributes(HashMap<String, String> attributes) {
		this.attributes = attributes;
	}
}
