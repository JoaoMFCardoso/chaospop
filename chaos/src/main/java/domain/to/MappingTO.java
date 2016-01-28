package domain.to;

import java.util.ArrayList;

public class MappingTO {

	/** The mapping identifier */
	private String _id;

	/** The output Ontology file name */
	private String outputOntologyFileName;

	/**  The output Ontology Namespace */
	private String outputOntologyNamespace;

	/** The file name. */
	private ArrayList<String> fileNames;

	/** The directly imported ontologies. */
	private ArrayList<String> directOntologyImports;

	/**  The individuals mappings. */
	private ArrayList<String> individualMappings;

	public MappingTO() {
		this._id = null;
		this.outputOntologyFileName = null;
		this.outputOntologyNamespace = null;
		this.fileNames = null;
		this.directOntologyImports = null;
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
	 * @return the outputOntologyFileName
	 */
	public String getOutputOntologyFileName() {
		return outputOntologyFileName;
	}

	/**
	 * @param outputOntologyFileName the outputOntologyFileName to set
	 */
	public void setOutputOntologyFileName(String outputOntologyFileName) {
		this.outputOntologyFileName = outputOntologyFileName;
	}

	/**
	 * @return the outputOntologyNamespace
	 */
	public String getOutputOntologyNamespace() {
		return outputOntologyNamespace;
	}

	/**
	 * @param outputOntologyNamespace the outputOntologyNamespace to set
	 */
	public void setOutputOntologyNamespace(String outputOntologyNamespace) {
		this.outputOntologyNamespace = outputOntologyNamespace;
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
	 * @return the directOntologyImports
	 */
	public ArrayList<String> getDirectOntologyImports() {
		return directOntologyImports;
	}

	/**
	 * @param directOntologyImports the directOntologyImports to set
	 */
	public void setDirectOntologyImports(ArrayList<String> directOntologyImports) {
		this.directOntologyImports = directOntologyImports;
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
