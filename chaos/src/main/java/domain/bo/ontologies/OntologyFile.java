package domain.bo.ontologies;

import org.bson.types.ObjectId;
import org.semanticweb.owlapi.model.IRI;

import domain.to.OntologyFileTO;

/**
 * The Data File class
 * A class that defines an Ontology file according to the system's view
 * @author João Cardoso
 *
 */
public class OntologyFile {

	/** The database id */
	private ObjectId _id;

	/** The ontology's IRI namespace */
	private IRI namespace;

	/** The ontology's file path */
	private String path;

	public OntologyFile() {
		this._id = new ObjectId();
		this.namespace = null;
		this.path = null;
	}

	/**
	 * This class constructor is based on the transfer object
	 * @param ontologyFileTO The OntologyFile transfer object
	 */
	public OntologyFile(OntologyFileTO ontologyFileTO){
		this._id = new ObjectId(ontologyFileTO.get_id());
		this.namespace = IRI.create(ontologyFileTO.getNamespace());
		this.path = ontologyFileTO.getPath();
	}

	/**
	 * This method creates the OntologyFile transfer object
	 * @return A OntologyFileTO transfer object
	 */
	public OntologyFileTO createTransferObject(){
		OntologyFileTO ofto = new OntologyFileTO();

		ofto.set_id(this._id.toString());
		ofto.setNamespace(this.namespace.toString());

		if(null == this.path){
			ofto.setPath(null);
		}else{
			ofto.setPath(this.path);
		}

		return ofto;
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
	 * @return the namespace
	 */
	public IRI getNamespace() {
		return namespace;
	}

	/**
	 * @param namespace the namespace to set
	 */
	public void setNamespace(IRI namespace) {
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
