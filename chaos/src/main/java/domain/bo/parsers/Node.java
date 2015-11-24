package domain.bo.parsers;

import java.util.ArrayList;
import java.util.HashMap;

import org.bson.types.ObjectId;

import utils.TransferObjectUtils;
import domain.to.NodeTO;

/**
 * The Class Node.
 *
 */
public class Node{

	/** The node id */
	private ObjectId _id;

	/** The children. */
	private ArrayList<ObjectId> children;

	/** The parent. */
	private ObjectId parent;

	/** The tag. */
	private String tag;

	/** The value. */
	private String value;

	/** The has attributes. */
	private Boolean hasAttributes;

	/** The attributes. */
	private HashMap<String, String> attributes;

	/**
	 * Instantiates a new node.
	 */
	public Node(){
		this._id = new ObjectId();
		this.children = null;
		this.parent = null;
		this.tag = null;
		this.value = null;
		this.hasAttributes = false;
		this.attributes = null;
	}

	/**
	 * This constructor creates a Node based on its transfer object
	 * @param nodeTO The Node transfer object
	 */
	public Node(NodeTO nodeTO){
		this._id = new ObjectId(nodeTO.get_id());
		this.children = TransferObjectUtils.convertALStringToObjectId(nodeTO.getChildren());
		this.parent = new ObjectId(nodeTO.getParent());
		this.tag = nodeTO.getTag();
		this.value = nodeTO.getValue();
		this.hasAttributes = nodeTO.getHasAttributes();
		this.attributes = nodeTO.getAttributes();
	}

	/**
	 * This method creates a NodeTO transfer object based on the Node object
	 * @return A NodeTO object
	 */
	public NodeTO createTransferObject(){
		NodeTO nodeTO = new NodeTO();

		/* Sets the NodeTO class attributes */
		nodeTO.set_id(this._id.toString());
		nodeTO.setChildren(TransferObjectUtils.convertALObjectIdToString(this.children));
		nodeTO.setParent(this.parent.toString());
		nodeTO.setTag(this.tag);
		nodeTO.setValue(this.value);
		nodeTO.setHasAttributes(this.hasAttributes);
		nodeTO.setAttributes(this.attributes);

		return nodeTO;
	}

	/**
	 * @return the id
	 */
	public ObjectId getID() {
		return _id;
	}

	/**
	 * @param id the id to set
	 */
	public void setID(ObjectId id) {
		this._id = id;
	}

	/**
	 * Gets the children.
	 *
	 * @return the children
	 */
	public ArrayList<ObjectId> getChildren(){
		return children;
	}

	/**
	 * Sets the children
	 * @param children The children array
	 */
	public void setChildren(ArrayList<ObjectId> children){
		this.children = children;
	}

	/**
	 * This method initializes the children array
	 */
	public void initializeChildren(){
		if(this.children == null){
			this.children = new ArrayList<ObjectId>();
		}

		return;
	}

	/**
	 * Sets the parent.
	 *
	 * @param parent the new parent
	 */
	public void setParent(ObjectId parent){
		this.parent = parent;
	}

	/**
	 * Gets the parent.
	 *
	 * @return the parent
	 */
	public ObjectId getParent(){
		return this.parent;
	}

	/**
	 * Adds the child.
	 *
	 * @param child the child
	 */
	public void addChild(ObjectId child){
		this.children.add(child);
	}

	/**
	 * Checks if is root.
	 *
	 * @return true, if is root
	 */
	public boolean isRoot(){
		return (this.parent == null);
	}

	/**
	 * Checks if is leaf.
	 *
	 * @return true, if is leaf
	 */
	public boolean isLeaf(){
		if(this.children.size() == 0)
			return true;
		else
			return false;
	}

	/**
	 * Removes the parent.
	 */
	public void removeParent(){
		this.parent = null;
	}

	/**
	 * Checks for attributes.
	 *
	 * @return true, if successful
	 */
	public boolean HasAttributes(){
		if(hasAttributes){
			return true;
		}else{
			return false;
		}
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
	 * Gets the value.
	 *
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Sets the value.
	 *
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * Gets the attributes.
	 *
	 * @return the attributes
	 */
	public HashMap<String, String> getAttributes() {
		return attributes;
	}

	/**
	 * Sets the attributes.
	 *
	 * @param attributes the attributes to set
	 */
	public void setAttributes(HashMap<String, String> attributes) {
		this.hasAttributes = true;
		this.attributes = attributes;
	}
}
