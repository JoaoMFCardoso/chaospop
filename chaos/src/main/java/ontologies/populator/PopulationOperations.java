package ontologies.populator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;

import ontologies.extractor.OntologyOperations;

import org.bson.types.ObjectId;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;

import properties.PropertiesHandler;
import utils.PopulationUtils;
import database.implementations.IndividualMappingsImpl;
import database.implementations.MappingImpl;
import database.implementations.OntologyFileImpl;
import domain.bo.mappings.IndividualMapping;
import domain.bo.mappings.Mapping;
import domain.bo.ontologies.OntologyFile;
import domain.bo.parsers.Node;
import domain.bo.population.Batch;

public class PopulationOperations {

	/** The Mapping Database implementation */
	private MappingImpl mappingImpl;

	/** The IndividualMapping Database Implementation */
	private IndividualMappingsImpl individualMappingsImpl;

	/** The Batch to be Processed */
	private Batch batch;

	/** The OWL Ontologies Manager */
	private OWLOntologyManager manager;

	/** The OWL Data Factory */
	private OWLDataFactory factory;

	/** The OWL Reasoner Factory */
	private OWLReasonerFactory reasonerFactory;

	/** The OWL Ontology */
	private OWLOntology ontology;

	/** The Ontology Namespace */
	private IRI ontologyNamespace;

	/** The OWL Reasoner */
	private OWLReasoner reasoner;

	/** The Ontology Operations Class with the loaded ontology */
	private OntologyOperations ontologyOperations;

	public PopulationOperations(Batch batch) {
		this.batch = batch;

		this.mappingImpl = new MappingImpl();
		this.individualMappingsImpl = new IndividualMappingsImpl();

		this.reasonerFactory = new StructuralReasonerFactory();
		this.manager = OWLManager.createOWLOntologyManager();
		this.factory = this.manager.getOWLDataFactory();
	}

	public Boolean preProcessChecks(){
		Boolean allClear = true;

		//TODO A script that pre processes the batch in order to check that it will run smoothly.

		return allClear;
	}

	/**
	 * Populates the Mappings within a given Batch
	 * @param batch The batch to be populated
	 */
	public void processBatch(){
		/* Runs the Batch and populates each Mapping */
		for(ObjectId mappingId : this.batch.getMappings()){
			/* Gets the Mapping object from the database */
			Mapping mapping = this.mappingImpl.get(mappingId.toString());

			/* Creates the corresponding ontology */
			createOntology(mapping);

			/* Populates the ontology */
			populateOntology(mapping);

			/* Saves the ontology */
			saveOntology(mapping);
		}
	}

	/*******************************************************************************************************************
	 * General Ontology Creation Processor Methods
	 *******************************************************************************************************************/
	/**
	 * This method saves a created ontology both into a file and in the database
	 * @param mapping The Mapping that generates the creation of this ontology
	 */
	private void saveOntology(Mapping mapping){
		try{
			/* Creates the Ontology File */
			String localOntologiesDir = PropertiesHandler.configProperties.getProperty("local.ontologies.path");
			String ontologyFilePath = localOntologiesDir + File.separator + mapping.getOutputOntologyFileName() + ".owl";
			File ontologyFile = new File(ontologyFilePath);
			FileOutputStream fileOutputStream = new FileOutputStream(ontologyFile);

			/* Creates the OntologyFile */
			OntologyFile dbOntologyFile = new OntologyFile();
			dbOntologyFile.setNamespace(mapping.getOutputOntologyNamespace());
			dbOntologyFile.setPath(ontologyFilePath);

			/* Saves the OntologyFile in the database */
			OntologyFileImpl ontologyFileImpl = new OntologyFileImpl();
			ontologyFileImpl.save(dbOntologyFile);

			/* Deletes existing ontologies with the same name */
			if(ontologyFile.isFile()){
				ontologyFile.delete();
			}

			/* Saves the new ontology */
			this.manager.saveOntology(this.ontology, fileOutputStream);
		} catch (OWLOntologyStorageException | FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Creates an Ontology simply and imports the base ontology and specific ontologies into the created ontology
	 * @param mapping The Mapping that generates the creation of this ontology
	 */
	private void createOntology(Mapping mapping){
		/* Loads the properties */
		PropertiesHandler.propertiesLoader();

		try{
			/* Creates the ontology for this mapping */
			this.ontologyNamespace = mapping.getOutputOntologyNamespace();
			this.ontology = this.manager.createOntology(this.ontologyNamespace);

			/* Initializes an OntologyExtractionOperations Object in order to use its methods to
			 * import the base ontology and specific ontologies */
			this.reasoner = this.reasonerFactory.createNonBufferingReasoner(ontology);
			this.ontologyOperations = new OntologyOperations(this.manager,
					this.factory,
					this.reasonerFactory,
					this.ontology,
					this.reasoner);

			/* Gets the base ontology and extracts its imported ontologies list */
			PopulationUtils.importOntologies(mapping.getBaseOntology().toString(), this.ontologyOperations);

			/* Imports any existing specific ontologies */
			if(null != mapping.getSpecificOntologies()){
				for(ObjectId ontologyId : mapping.getSpecificOntologies()){
					PopulationUtils.importOntologies(ontologyId.toString(), this.ontologyOperations);
				}
			}

		} catch (OWLOntologyCreationException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Populates an Ontology given a mapping
	 * @param mapping The Mapping object that holds all necessary mapping data to populate an ontology based on DataFiles
	 */
	private void populateOntology(Mapping mapping){
		/* Runs the individual Mappings */
		for(ObjectId individualMappingId : mapping.getIndividualMappings()){
			/* Gets the IndividualMapping from the database */
			IndividualMapping individualMapping = this.individualMappingsImpl.get(individualMappingId.toString());

			/* Gets the list of Nodes that match the IndividualMapping tag */
			ArrayList<Node> nodeList = PopulationUtils.getIndividualMappingMatchingNode(mapping, individualMapping);

			/* Populates all the matching Nodes */
			processNodes(nodeList, individualMapping);
		}
	}

	/**
	 * Runs the Node list and creates an individual for each Node instance, according to the rules
	 * specified in the IndividualMapping object.
	 * @param nodeList An ArrayList of Nodes that will be processed to create OWLIndividuals
	 * @param individualMapping The IndividualMapping object that specifies the rules of the population
	 */
	private void processNodes(ArrayList<Node> nodeList, IndividualMapping individualMapping){
		/* Runs the Node List */
		for(Node node : nodeList){
			createIndividual(node, individualMapping);
		}
	}

	/*********************************************************************************************************************
	 * Individual Creation Methods
	 *********************************************************************************************************************/

	private void createIndividual(Node node, IndividualMapping individualMapping){

		/* Creates the Individual Name, Label, IRI and OWLClass IRI */
		String individualName = PopulationUtils.createIndividualName(node, individualMapping);
		String individualLabel = PopulationUtils.createIndividualLabel(node, individualMapping);
		IRI individualIRI = IRI.create(this.ontologyNamespace.toString() ,individualName);
		IRI individualClassIRI = individualMapping.getOwlClassIRI();

		/* Gets an existing OWLNamedIndividual object or creates a new one */
		OWLNamedIndividual individual = this.ontologyOperations.getOWLNamedIndividual(individualName,
																								individualLabel,
																								individualIRI,
																								individualClassIRI);
	}


}
