package domain.to;

import java.util.ArrayList;

public class MappingTO {

	/** The mapping identifier */
	private String _id;

	/** The file name. */
	private ArrayList<String> fileNames;

	/** The specific ontology. */
	private ArrayList<String> specificOntologies;

	/** The base ontology. */
	private String baseOntology;

	/**  The individuals mappings. */
	private ArrayList<String> individualMappings;

	public MappingTO() {
		this._id = null;
		this.fileNames = null;
		this.specificOntologies = null;
		this.baseOntology = null;
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
	 * @return the specificOntologies
	 */
	public ArrayList<String> getSpecificOntologies() {
		return specificOntologies;
	}

	/**
	 * @param specificOntologies the specificOntologies to set
	 */
	public void setSpecificOntologies(ArrayList<String> specificOntologies) {
		this.specificOntologies = specificOntologies;
	}

	/**
	 * @return the baseOntology
	 */
	public String getBaseOntology() {
		return baseOntology;
	}

	/**
	 * @param baseOntology the baseOntology to set
	 */
	public void setBaseOntology(String baseOntology) {
		this.baseOntology = baseOntology;
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
