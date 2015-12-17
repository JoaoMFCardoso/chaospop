package domain.bo.mappings;

import java.util.ArrayList;

import org.bson.types.ObjectId;
import org.semanticweb.owlapi.model.IRI;

import utils.TransferObjectUtils;
import domain.to.MappingTO;

/**
 * The Class Mapping.
 *
 */
public class Mapping{

	/** The mapping identifier */
	private ObjectId _id;

	/** The output Ontology file name */
	private String outputOntologyFileName;

	/**  The output Ontology Namespace */
	private IRI outputOntologyNamespace;

	/** The file list. */
	private ArrayList<ObjectId> fileList;

	/** The specific ontology IRI. */
	private ArrayList<ObjectId> specificOntologies;

	/** The base ontology IRI. */
	private ObjectId baseOntology;

	/**  The individuals mappings. */
	private ArrayList<ObjectId> individualMappings;

	/**
	 * Instantiates a new mapping.
	 */
	public Mapping() {
		this._id = new ObjectId();
		this.outputOntologyFileName = null;
		this.outputOntologyNamespace = null;
		this.fileList = null;
		this.specificOntologies = null;
		this.baseOntology = null;
		this.individualMappings = null;
	}

	/**
	 * Instantiates a new Mapping through an Mapping transfer object
	 * @param mappingTO The Mapping transfer object
	 */
	public Mapping(MappingTO mappingTO){
		/* This if clause is here in case this is an update to an existing object */
		if(null == mappingTO.get_id()){
			this._id = new ObjectId();
		}else{
			this._id = new ObjectId(mappingTO.get_id());
		}
		this.outputOntologyFileName = mappingTO.getOutputOntologyFileName();
		this.outputOntologyNamespace = IRI.create(mappingTO.getOutputOntologyNamespace());
		this.baseOntology = new ObjectId(mappingTO.getBaseOntology());
		this.fileList = TransferObjectUtils.convertALStringToObjectId(mappingTO.getFileNames());
		this.specificOntologies = TransferObjectUtils.convertALStringToObjectId(mappingTO.getSpecificOntologies());
		this.individualMappings = TransferObjectUtils.convertALStringToObjectId(mappingTO.getIndividualMappings());
	}

	/**
	 * This method creates a MappingTO transfer object
	 * @return A MappingTO transfer object
	 */
	public MappingTO createTransferObject(){
		MappingTO mto = new MappingTO();

		/* Sets the MappingTO attributes */
		mto.set_id(this._id.toString());
		mto.setBaseOntology(this.baseOntology.toString());
		mto.setOutputOntologyFileName(this.outputOntologyFileName);
		mto.setOutputOntologyNamespace(this.outputOntologyNamespace.toString());

		if(null == this.specificOntologies){
			mto.setSpecificOntologies(null);
		}else{
			mto.setSpecificOntologies(TransferObjectUtils.convertALObjectIdToString(this.specificOntologies));
		}

		if(null == this.individualMappings){
			mto.setFileNames(null);
		}else{
			mto.setFileNames(TransferObjectUtils.convertALObjectIdToString(this.fileList));
		}

		if(null == this.individualMappings){
			mto.setIndividualMappings(null);
		}else{
			mto.setIndividualMappings(TransferObjectUtils.convertALObjectIdToString(this.individualMappings));
		}

		return mto;
	}

	/**
	 * @return the mappingID
	 */
	public ObjectId getID() {
		return _id;
	}


	/**
	 * @param mappingID the mappingID to set
	 */
	public void setID(ObjectId _id) {
		this._id = _id;
	}


	/**
	 * Gets the file list.
	 *
	 * @return the fileList
	 */
	public ArrayList<ObjectId> getFileList() {
		return fileList;
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
	public IRI getOutputOntologyNamespace() {
		return outputOntologyNamespace;
	}

	/**
	 * @param outputOntologyNamespace the outputOntologyNamespace to set
	 */
	public void setOutputOntologyNamespace(IRI outputOntologyNamespace) {
		this.outputOntologyNamespace = outputOntologyNamespace;
	}

	/**
	 * Sets the file array.
	 *
	 * @param fileList the fileList to set
	 */
	public void setFileList(ArrayList<ObjectId> fileList) {
		this.fileList = fileList;
	}

	/**
	 * @return the specificOntologies
	 */
	public ArrayList<ObjectId> getSpecificOntologies() {
		return specificOntologies;
	}


	/**
	 * @param specificOntologies the specificOntologies to set
	 */
	public void setSpecificOntologies(ArrayList<ObjectId> specificOntologies) {
		this.specificOntologies = specificOntologies;
	}


	/**
	 * Gets the base ontology.
	 *
	 * @return the baseOntology
	 */
	public ObjectId getBaseOntology() {
		return baseOntology;
	}

	/**
	 * Sets the base ontology.
	 *
	 * @param baseOntology the baseOntology to set
	 */
	public void setBaseOntology(ObjectId baseOntology) {
		this.baseOntology = baseOntology;
	}


	/**
	 * @return the individualMappings
	 */
	public ArrayList<ObjectId> getIndividualMappings() {
		return individualMappings;
	}


	/**
	 * @param individualMappings the individualMappings to set
	 */
	public void setIndividualMappings(ArrayList<ObjectId> individualMappings) {
		this.individualMappings = individualMappings;
	}
}
