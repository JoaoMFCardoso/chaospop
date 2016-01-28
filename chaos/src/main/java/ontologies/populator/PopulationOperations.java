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

			/* Imports Ontologies.
			 * It gather all the indirectly imported ontologies by each directly imported ontology
			 * and then it creates a final list of directly imported ontologies, only importing those ontologies that
			 * are not indirectly imported by others.
			 */
			PopulationUtils.importOntologies(mapping.getDirectOntologyImports(), this.ontologyOperations);

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
			processNodes(nodeList, mapping, individualMapping);
		}
	}

	/**
	 * Runs the Node list and creates an individual for each Node instance, according to the rules
	 * specified in the IndividualMapping object.
	 * @param nodeList An ArrayList of Nodes that will be processed to create OWLIndividuals
	 * @param mapping The population operation mapping
	 * @param individualMapping The IndividualMapping object that specifies the rules of the population
	 */
	private void processNodes(ArrayList<Node> nodeList, Mapping mapping, IndividualMapping individualMapping){
		/* Runs the Node List */
		for(Node node : nodeList){
			createIndividual(node, mapping, individualMapping);
		}
	}

	/*********************************************************************************************************************
	 * Individual Creation Methods
	 *********************************************************************************************************************/

	/**
	 * Creates a new Individual and its object and data properties
	 * @param node The Node that is used to generate the new individual
	 * @param mapping The Mapping in the population procedure
	 * @param individualMapping The individual mapping for the creation of this individual
	 */
	private void createIndividual(Node node, Mapping mapping, IndividualMapping individualMapping){

		/*****************************************************************************************************************
		 * INDIVIDUAL DATA CREATION
		 *****************************************************************************************************************/

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

		/* Checks if an OWLNamedIndividual is in need of an update. There are two reasons for an OWLNamedIndividual to need an update:
		 *  	1. The individual is a proto individual created as the second individual of an object property. As such it only has
		 *  basic information such as its IRI and label. No OWLClass has been asigned to it.
		 *  	2. The IndividualMapping requires that this individual is a specification of an existing individual */
		Boolean individualNeedsUpdate = this.ontologyOperations.isIndividualInNeedOfUpdate(individual);
		if(individualMapping.getSpecification() || individualNeedsUpdate){
			this.ontologyOperations.updateOWLNamedIndividual(individual, individualLabel, individualClassIRI);
		}

		/*****************************************************************************************************************
		 * OBJECT PROPERTIES CREATION
		 *****************************************************************************************************************/

		/* Checks if the Individual Mapping implies the creation of Object Properties for this individual */
		if(!individualMapping.getObjectProperties().isEmpty()){
			this.ontologyOperations.handleObjectPropertyCreation(node, mapping, individualMapping, individual);
		}

		/*****************************************************************************************************************
		 * DATA PROPERTIES CREATION
		 *****************************************************************************************************************/

		/* Checks if the Individual Mapping implies the creation of Data Properties for this individual */
		if(!individualMapping.getDataProperties().isEmpty()){
			this.ontologyOperations.handleDataPropertyCreation(node, mapping, individualMapping, individual);
		}
	}


}
