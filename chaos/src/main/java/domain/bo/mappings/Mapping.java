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

	/** The file name. */
	private ArrayList<ObjectId> fileNames;

	/** The specific ontology IRI. */
	private ArrayList<IRI> specificOntologiesIRI;

	/** The base ontology IRI. */
	private IRI baseOntologyIRI;

	/**  The individuals mappings. */
	private ArrayList<ObjectId> individualMappings;

	/**
	 * Instantiates a new mapping.
	 */
	public Mapping() {
		this._id = new ObjectId();
		this.fileNames = null;
		this.specificOntologiesIRI = null;
		this.baseOntologyIRI = null;
		this.individualMappings = null;
	}

	/**
	 * Instantiates a new Mapping through an Mapping transfer object
	 * @param mappingTO The Mapping transfer object
	 */
	public Mapping(MappingTO mappingTO){
		this._id = new ObjectId(mappingTO.get_id());
		this.baseOntologyIRI = IRI.create(mappingTO.getBaseOntologyIRI());
		this.fileNames = TransferObjectUtils.convertALStringToObjectId(mappingTO.getFileNames());
		this.specificOntologiesIRI = TransferObjectUtils.convertALStringToIRI(mappingTO.getSpecificOntologiesIRI());
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
		mto.setFileNames(TransferObjectUtils.convertALObjectIdToString(this.fileNames));
		mto.setSpecificOntologiesIRI(TransferObjectUtils.convertALIRIToString(this.specificOntologiesIRI));
		mto.setBaseOntologyIRI(this.baseOntologyIRI.toString());
		mto.setIndividualMappings(TransferObjectUtils.convertALObjectIdToString(this.individualMappings));

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
	 * Gets the file name.
	 *
	 * @return the fileName
	 */
	public ArrayList<ObjectId> getFileNames() {
		return fileNames;
	}

	/**
	 * Sets the file name array.
	 *
	 * @param fileName the fileName to set
	 */
	public void setFileNames(ArrayList<ObjectId> fileNames) {
		this.fileNames = fileNames;
	}

	/**
	 * @return the specificOntologiesIRI
	 */
	public ArrayList<IRI> getSpecificOntologiesIRI() {
		return specificOntologiesIRI;
	}


	/**
	 * @param specificOntologiesIRI the specificOntologiesIRI to set
	 */
	public void setSpecificOntologiesIRI(ArrayList<IRI> specificOntologiesIRI) {
		this.specificOntologiesIRI = specificOntologiesIRI;
	}


	/**
	 * Gets the base ontology iri.
	 *
	 * @return the baseOntologyIRI
	 */
	public IRI getBaseOntologyIRI() {
		return baseOntologyIRI;
	}

	/**
	 * Sets the base ontology iri.
	 *
	 * @param baseOntologyIRI the baseOntologyIRI to set
	 */
	public void setBaseOntologyIRI(IRI baseOntologyIRI) {
		this.baseOntologyIRI = baseOntologyIRI;
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
