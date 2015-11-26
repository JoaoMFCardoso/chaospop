package domain.to;

import java.util.ArrayList;
import java.util.HashMap;

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

	/** The ontology's classes */
	private ArrayList<String> classes;

	/** The ontology's individuals and labels */
	private HashMap<String, String> individuals;

	/** The ontology's object properties */
	private ArrayList<String> objectProperties;

	/** The ontology's data properties */
	private ArrayList<String> dataProperties;

	public OntologyFileTO() {
		this._id = null;
		this.namespace = null;
		this.path = null;
		this.classes = null;
		this.individuals = null;
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

	/**
	 * @return the classes
	 */
	public ArrayList<String> getClasses() {
		return classes;
	}

	/**
	 * @param classes the classes to set
	 */
	public void setClasses(ArrayList<String> classes) {
		this.classes = classes;
	}

	/**
	 * @return the individuals
	 */
	public HashMap<String, String> getIndividuals() {
		return individuals;
	}

	/**
	 * @param individuals the individuals to set
	 */
	public void setIndividuals(HashMap<String, String> individuals) {
		this.individuals = individuals;
	}

	/**
	 * @return the objectProperties
	 */
	public ArrayList<String> getObjectProperties() {
		return objectProperties;
	}

	/**
	 * @param objectProperties the objectProperties to set
	 */
	public void setObjectProperties(ArrayList<String> objectProperties) {
		this.objectProperties = objectProperties;
	}

	/**
	 * @return the dataProperties
	 */
	public ArrayList<String> getDataProperties() {
		return dataProperties;
	}

	/**
	 * @param dataProperties the dataProperties to set
	 */
	public void setDataProperties(ArrayList<String> dataProperties) {
		this.dataProperties = dataProperties;
	}
}
