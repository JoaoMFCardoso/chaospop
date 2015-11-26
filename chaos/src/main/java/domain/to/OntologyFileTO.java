package domain.to;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * The OntologyFile Transfer Object
 * See OntologyFile class for reference
 * @author João Cardoso
 *
 */
@XmlRootElement (name = "OntologyFile")
public class OntologyFileTO {

	/** The database id */
	private String _id;

	/** The ontology's IRI namespace */
	private String namespace;

	/** The ontology's file path */
	private String path;

	public OntologyFileTO() {
		this._id = null;
		this.namespace = null;
		this.path = null;
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
	 * @return the namespace
	 */
	public String getNamespace() {
		return namespace;
	}

	/**
	 * @param namespace the namespace to set
	 */
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @param path the path to set
	 */
	public void setPath(String path) {
		this.path = path;
	}
}
