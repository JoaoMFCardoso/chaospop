package domain.bo.ontologies;

import java.util.ArrayList;
import java.util.HashMap;

import ontologies.extractor.OntologyOperations;

import org.bson.types.ObjectId;
import org.semanticweb.owlapi.model.IRI;

import domain.bo.parsers.ParsedFile;
import domain.to.OntologyFileTO;

/**
 * The Data File class
 * A class that defines an Ontology file according to the system's view
 * @author João Cardoso
 *
 */
public class OntologyFile extends ParsedFile{

	/** The ontology's IRI namespace */
	private IRI namespace;

	/** The ontology's file path */
	private String path;

	/** The ontology's classes */
	private ArrayList<IRI> classes;

	/** The ontology's individuals and labels */
	private HashMap<IRI, String[]> individuals;

	public OntologyFile() {
		super();
		this.namespace = null;
		this.path = null;
		this.classes = null;
		this.individuals = null;
	}

	/**
	 * This class constructor is based on the transfer object
	 * @param ontologyFileTO The OntologyFile transfer object
	 */
	public OntologyFile(OntologyFileTO ontologyFileTO){
		/* This if clause is here in case this is an update to an existing object */
		if(null == ontologyFileTO.get_id()){
			this._id = new ObjectId();
		}else{
			this._id = new ObjectId(ontologyFileTO.get_id());
		}
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
	 * This method sets the general OntologyFile attributes, i.e.,
	 * the namespace, the classes and individuals and labels.
	 * @param ontologyExtractionOperations The OntologyExtractionsOperations object
	 */
	public void setsGeneralOntologyFileAttributes(OntologyOperations ontologyOperations) throws Exception{

		/* Sets the namespace */
		IRI namespace = ontologyOperations.getNamespace();
		this.setNamespace(namespace);

		/* Gets all the classes */
		ArrayList<IRI> classes = ontologyOperations.getClasses();
		this.setClasses(classes);

		/* Gets all the individuals, labels and classes */
		HashMap<IRI, String[]> individualsAndLabelsMap = ontologyOperations.getIndividualsInSignatureData();
		this.setIndividuals(individualsAndLabelsMap);

		return;
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

	/**
	 * @return the classes
	 */
	public ArrayList<IRI> getClasses() {
		return classes;
	}

	/**
	 * @param classes the classes to set
	 */
	public void setClasses(ArrayList<IRI> classes) {
		this.classes = classes;
	}

	/**
	 * @return the individuals
	 */
	public HashMap<IRI, String[]> getIndividuals() {
		return individuals;
	}

	/**
	 * @param individuals the individuals to set
	 */
	public void setIndividuals(HashMap<IRI, String[]> individuals) {
		this.individuals = individuals;
	}
}
