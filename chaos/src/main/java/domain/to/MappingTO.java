package domain.to;

import java.util.ArrayList;

public class MappingTO {

	/** The mapping identifier */
	private String _id;

	/** The file name. */
	private ArrayList<String> fileNames;

	/** The specific ontology IRI. */
	private ArrayList<String> specificOntologiesIRI;

	/** The base ontology IRI. */
	private String baseOntologyIRI;

	/**  The individuals mappings. */
	private ArrayList<String> individualMappings;

	public MappingTO() {
		this._id = null;
		this.fileNames = null;
		this.specificOntologiesIRI = null;
		this.baseOntologyIRI = null;
		this.individualMappings = null;
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
	 * @return the fileNames
	 */
	public ArrayList<String> getFileNames() {
		return fileNames;
	}

	/**
	 * @param fileNames the fileNames to set
	 */
	public void setFileNames(ArrayList<String> fileNames) {
		this.fileNames = fileNames;
	}

	/**
	 * @return the specificOntologiesIRI
	 */
	public ArrayList<String> getSpecificOntologiesIRI() {
		return specificOntologiesIRI;
	}

	/**
	 * @param specificOntologiesIRI the specificOntologiesIRI to set
	 */
	public void setSpecificOntologiesIRI(ArrayList<String> specificOntologiesIRI) {
		this.specificOntologiesIRI = specificOntologiesIRI;
	}

	/**
	 * @return the baseOntologyIRI
	 */
	public String getBaseOntologyIRI() {
		return baseOntologyIRI;
	}

	/**
	 * @param baseOntologyIRI the baseOntologyIRI to set
	 */
	public void setBaseOntologyIRI(String baseOntologyIRI) {
		this.baseOntologyIRI = baseOntologyIRI;
	}

	/**
	 * @return the individualMappings
	 */
	public ArrayList<String> getIndividualMappings() {
		return individualMappings;
	}

	/**
	 * @param individualMappings the individualMappings to set
	 */
	public void setIndividualMappings(ArrayList<String> individualMappings) {
		this.individualMappings = individualMappings;
	}
}
