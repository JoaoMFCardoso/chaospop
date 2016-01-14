package ontologies.extractor;

import static org.semanticweb.owlapi.search.Searcher.annotationObjects;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.AddImport;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLImportsDeclaration;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.parameters.Imports;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;

import utils.PopulationUtils;
import database.implementations.OntologyFileImpl;
import domain.bo.ontologies.OntologyFile;

/**
 * This class implements Ontology data Extraction methods
 * @author João Cardoso
 *
 */
public class OntologyOperations {

	/** The OWL Ontologies Manager */
	private OWLOntologyManager manager;

	/** The OWL Data Factory */
	private OWLDataFactory factory;

	/** The OWL Ontology */
	private OWLOntology ontology;

	/** The OWL Reasoner Factory */
	private OWLReasonerFactory reasonerFactory;

	/** The OWL Reasoner */
	private OWLReasoner reasoner;

	/******************************************************************************************************************************
	 * CONSTRUCTORS
	 ******************************************************************************************************************************/

	/**
	 * This constructor initializes the manager, data factory, reasoner factory, reasoner and ontology based on a file
	 * @param file The owl file
	 * @throws OWLOntologyCreationException
	 */
	public OntologyOperations(File file) throws OWLOntologyCreationException{
		this.reasonerFactory = new StructuralReasonerFactory();
		this.manager = OWLManager.createOWLOntologyManager();
		this.factory = this.manager.getOWLDataFactory();
		this.ontology = this.manager.loadOntologyFromOntologyDocument(file);
		this.reasoner = reasonerFactory.createNonBufferingReasoner(this.ontology);
	}

	/**
	 * This constructor initializes the manager, data factory, reasoner factory, reasoner and ontology based on a IRI namespace that is published online
	 * @param ontologyNamespace A IRI that is published online
	 * @throws OWLOntologyCreationException
	 */
	public OntologyOperations(IRI ontologyNamespace) throws OWLOntologyCreationException{
		this.reasonerFactory = new StructuralReasonerFactory();
		this.manager = OWLManager.createOWLOntologyManager();
		this.factory = this.manager.getOWLDataFactory();
		this.ontology = this.manager.loadOntology(ontologyNamespace);
		this.reasoner = reasonerFactory.createNonBufferingReasoner(this.ontology);
	}

	/**
	 * This constructor initializes the manager, data factory, reasoner factory, reasoner and ontology based on an object stored in the database
	 * @param ontologyFileId The ontology id in the database
	 * @throws OWLOntologyCreationException
	 */
	public OntologyOperations(String ontologyFileId) throws OWLOntologyCreationException{
		/* Gets the OntologyFile from the database */
		OntologyFileImpl ontologyFileImpl = new OntologyFileImpl();
		OntologyFile ontologyFile = ontologyFileImpl.get(ontologyFileId);

		/* Initializes the reasoner factory, the manager and the data factory */
		this.reasonerFactory = new StructuralReasonerFactory();
		this.manager = OWLManager.createOWLOntologyManager();
		this.factory = this.manager.getOWLDataFactory();

		/* Calls the correct constructor */
		if(null == ontologyFile.getPath()){
			this.ontology = this.manager.loadOntology(ontologyFile.getNamespace());
		}else{
			File file = new File(ontologyFile.getPath());
			this.ontology = this.manager.loadOntologyFromOntologyDocument(file);
		}

		/* Initializes the reasoner */
		this.reasoner = reasonerFactory.createNonBufferingReasoner(this.ontology);
	}

	/**
	 * This constructor is used for the PopulationOperations class so as to reuse all the Objects that have been used to create the new ontology
	 * @param manager The OWL Ontologies Manager
	 * @param factory The OWL Data Factory
	 * @param reasonerFactory The OWL Reasoner Factory
	 * @param ontology The OWL Ontology
	 * @param reasoner The OWL Reasoner
	 */
	public OntologyOperations(OWLOntologyManager manager,
			OWLDataFactory factory,
			OWLReasonerFactory reasonerFactory,
			OWLOntology ontology,
			OWLReasoner reasoner){

		this.reasonerFactory = reasonerFactory;
		this.manager = manager;
		this.factory = factory;
		this.ontology = ontology;
		this.reasoner = reasoner;
	}

	/******************************************************************************************************************************
	 * ONTOLOGY CREATION METHODS
	 ******************************************************************************************************************************/
	/**
	 * Gets a set of Ontologies that imported into the original Ontology
	 * @return A Set<OWLOntology> with the imported ontologies
	 */
	public Set<OWLOntology> getImportedOntologiesList(){
		/* Gets the imported Ontologies for the loaded Ontology */
		Set<OWLOntology> importedOntologies = this.manager.getImports(this.ontology);
		return importedOntologies;
	}

	/**
	 * Imports an ontology into the existing ontology
	 * @param baseOntologyNamespace
	 * @throws OWLOntologyCreationException
	 */
	private void importOntology(IRI ontologyNamespace){
		/* Imports the ontology into the loaded ontology */
		OWLImportsDeclaration candidateOntology = this.factory.getOWLImportsDeclaration(ontologyNamespace);
		this.manager.applyChange(new AddImport(this.ontology, candidateOntology));
	}

	/**
	 * Imports a list of candidate ontologies
	 * @param ontologyList A list of candidate ontologies IRI's
	 * @throws OWLOntologyCreationException
	 */
	public void importOntologies(Set<OWLOntology> ontologyList) throws OWLOntologyCreationException{

		/* Runs the candidate ontologies and imports them as necessary */
		for(OWLOntology candidateOntology : ontologyList){

			/* Checks if the candidate ontology has been previously imported */
			if(isImported(candidateOntology)){
				continue;
			}else{
				/* Imports the candidate ontology */
				IRI candidateOntologyIRI = IRI.create(candidateOntology.getOntologyID().getOntologyIRI().get().toString() + "#");
				this.manager.loadOntology(candidateOntologyIRI);
				importOntology(candidateOntologyIRI);
			}
		}

		return;
	}

	/**
	 * Checks if a given ontology IRI exists in the loaded Ontology's imports
	 * @param ontology The candidate ontology
	 * @return true if it exists, false otherwise.
	 */
	private Boolean isImported(OWLOntology ontology){
		boolean imported = false;

		/* Gets the imported Ontologies for the loaded Ontology */
		Set<OWLOntology> importedOntologies = manager.getImports(this.ontology);

		/* Checks if the candidate Ontology has been previously imported */
		for(OWLOntology importedOntology : importedOntologies){
			if(ontology.getOntologyID().getOntologyIRI().equals(importedOntology.getOntologyID().getOntologyIRI())){
				imported = true;
				continue;
			}
		}

		return imported;
	}

	/**
	 * This method creates an annotation assertion axiom, associating an owl class with a named individual
	 * @param owlClassIRI The owl class IRI
	 * @param owlNamedIndividual The owl named individual
	 */
	private void createOwlClass(IRI owlClassIRI, OWLNamedIndividual owlNamedIndividual){
		OWLClass owlClass = this.factory.getOWLClass(owlClassIRI);

		OWLClassAssertionAxiom classAssertion = this.factory.getOWLClassAssertionAxiom(owlClass, owlNamedIndividual);

		// Add the class assertion
		this.manager.applyChange(new AddAxiom(this.ontology, classAssertion));
	}

	/******************************************************************************************************************************
	 * ONTOLOGY EXTRACTION METHODS
	 ******************************************************************************************************************************/

	/**
	 * Gets the Object's ontology
	 * @return The OWLOntology object
	 */
	public OWLOntology getOntology(){
		return this.ontology;
	}

	/**
	 * This method returns the namespace of an Ontology
	 * @return The IRI namespace of the given ontology
	 */
	public IRI getNamespace(){
		return this.ontology.getOntologyID().getOntologyIRI().get();
	}

	/**
	 * Returns the OWLClass object given its IRI in String form
	 * @param owlClassIRI Then OWLClass IRI in String form
	 * @return OWLClass object
	 */
	public OWLClass getOWLClass(String owlClassIRI){
		OWLClass owlClass = this.factory.getOWLClass(IRI.create(owlClassIRI));
		return owlClass;
	}

	/**
	 * This method gets all the classes from an ontology
	 * @return An IRI array with all the OWLClass IRI's
	 */
	public ArrayList<IRI> getClasses(){
		ArrayList<IRI> classArray = new ArrayList<IRI>();

		/* Gets all the classes in signature */
		Set<OWLClass> classes = this.ontology.getClassesInSignature();

		/* Fills the class Array */
		for(OWLClass owlClass : classes){
			classArray.add(owlClass.getIRI());
		}

		return classArray;
	}

	/**
	 * This method extracts all the individuals in signature from an Ontology as well as their label and class
	 * @return A HashMap containing the OWLNamedIndividuals as key and their labels and class in a string array
	 */
	public HashMap<IRI, String []> getIndividualsInSignatureData(){
		HashMap<IRI, String []> indlabMap = new HashMap<IRI, String []>();

		/* Gets all the individuals from this and its imported ontologies */
		Set<OWLNamedIndividual> individuals = this.ontology.getIndividualsInSignature(Imports.INCLUDED);

		/* Runs the individuals and gets their labels and classes */
		for(OWLNamedIndividual individual : individuals){
			String[] labelAndClass = new String[2];

			/* Gets all the Annotation Properties that match the RDFSLabel */
			for(OWLAnnotation annotation : annotationObjects(this.ontology.getAnnotationAssertionAxioms(individual.getIRI()),
					this.factory.getRDFSLabel())){

				if (annotation.getValue() instanceof OWLLiteral) {
					OWLLiteral value = (OWLLiteral) annotation.getValue();
					String label = value.getLiteral();

					/* Adds the label to the labelAndClass array */
					labelAndClass[0] = label;
				}
			}

			/* Gets all the Annotation Properties that match the RDFType */
			Set<OWLClassAssertionAxiom> ca = this.ontology.getClassAssertionAxioms(individual);
			for(OWLClassAssertionAxiom a : ca){
				String indOwlClass = a.getClassExpression().asOWLClass().getIRI().toString();

				/* Adds the OWL Class to the labelAndClass array */
				labelAndClass[1] = indOwlClass;
			}

			/* Adds a new entry to the HashMap */
			indlabMap.put(individual.getIRI(), labelAndClass);
		}

		return indlabMap;
	}

	/**
	 *
	 * @param individualFragment
	 * @param individualLabel
	 * @param individualIRI
	 * @param individualClassIRI
	 * @return
	 */
	public OWLNamedIndividual getOWLNamedIndividual(String individualFragment, String individualLabel, IRI individualIRI,
			IRI individualClassIRI){

		OWLNamedIndividual owlNamedIndividual = null;

		/*****************************************************************************************************************
		 * Existing Individual Handling Procedures
		 *****************************************************************************************************************/

		/* Checks any existing individuals in the current ontology for matches with the data of the new individual */
		for(OWLNamedIndividual candidateIndividual : this.ontology.getIndividualsInSignature(Imports.INCLUDED)){
			String candidateIndividualName = PopulationUtils.getCandidateIndividualName(candidateIndividual);

			/* If there is a match, then the individual already exists, and it's either being requested
			 * or needs to be updated */
			if(candidateIndividualName.equals(individualFragment)){
				/* The owlNamedIndividual that is to be returned is the candidateIndividual */
				owlNamedIndividual = candidateIndividual;

				/* Now there is the need to check that this individual isn't a proto individual with no class
				 * i.e., it was created as the second individual in an object property. This would mean that it
				 * has no class and no label, just its name.
				 *
				 * This verification is made through the individual's axioms. */
				//TODO handle this situation

			}
		}

		/*****************************************************************************************************************
		 * New Individual Handling Procedures
		 *****************************************************************************************************************/
		/* If no matches were found with any existing individuals, a new individual must be created with the input data */

		/* A new OWLNamedIndividual Object must be associated with the DataFactory */
		owlNamedIndividual = this.factory.getOWLNamedIndividual(individualIRI);

		/* Assigns the individual with a label. If the individualLabel provided in this methods input is an empty string, then the
		 * individualFragment should be adopted as label */

		/* Uses the individualFragment if the individualLabelindividualLabel is empty */
		if(individualLabel.isEmpty()){
			individualLabel = individualFragment;
		}

		OWLAnnotation annotation = this.factory.getOWLAnnotation(this.factory.getRDFSLabel(), this.factory.getOWLLiteral(individualLabel));
		OWLAnnotationAssertionAxiom annotationAssertionAxiom = this.factory.getOWLAnnotationAssertionAxiom(individualIRI, annotation);
		manager.applyChange(new AddAxiom(this.ontology, annotationAssertionAxiom));

		/* Assings the individual with a class. */
		if(null != individualClassIRI){
			createOwlClass(individualClassIRI, owlNamedIndividual);
		}

		return owlNamedIndividual;
	}

	/**
	 * Gets all the object properties that instances of a given owl class must have
	 * @param owlClass The owl class
	 * @return An ArrayList containing the IRIs of all the necessary object properties for the given class
	 */
	public ArrayList<IRI> getObjectPropertiesFromClass(OWLClass owlClass){
		ArrayList<IRI> objectPropertiesArray = new ArrayList<IRI>();

		/* Runs all Object Properties in the signature */
		for(OWLObjectPropertyExpression objectPropertyExpression : this.ontology.getObjectPropertiesInSignature(Imports.INCLUDED)){
			/* Get the class restrictions for this object property expression */
			OWLClassExpression restriction = this.factory.getOWLObjectSomeValuesFrom(objectPropertyExpression,
					this.factory.getOWLThing());
			OWLClassExpression intersection = this.factory.getOWLObjectIntersectionOf(owlClass,
					this.factory.getOWLObjectComplementOf(
							restriction));

			/* If this object property is needed in this owl class then it is added to the array */
			if(!this.reasoner.isSatisfiable(intersection)){
				objectPropertiesArray.add(objectPropertyExpression.asOWLObjectProperty().getIRI());
			}
		}

		return objectPropertiesArray;
	}

	/**
	 * Gets all the data properties that instances of a given owl class must have
	 * @param owlClass The owl class
	 * @return An ArrayList containing the IRIs of all the necessary data properties for the given class
	 */
	public ArrayList<IRI> getDataPropertiesFromClass(OWLClass owlClass){
		ArrayList<IRI> dataPropertiesArray = new ArrayList<IRI>();

		/* Runs all Data Properties in the signature */
		for(OWLDataPropertyExpression dataPropertyExpression : this.ontology.getDataPropertiesInSignature(Imports.INCLUDED)){
			/* If a data property has the given owl class in its signature then it is added to the array */
			if(dataPropertyExpression.getClassesInSignature().contains(owlClass)){
				dataPropertiesArray.add(dataPropertyExpression.asOWLDataProperty().getIRI());
			}
		}

		return dataPropertiesArray;
	}

	/**
	 * This method gets all the object properties in an ontology and its imported ontologies
	 * @return An IRI array with all the object properties
	 */
	public ArrayList<IRI> getObjectProperties(){
		ArrayList<IRI> objectPropertiesArray = new ArrayList<IRI>();

		/* Gets all the object properties in signature */
		Set<OWLObjectProperty> owlObjectProperties = this.ontology.getObjectPropertiesInSignature(Imports.INCLUDED);

		/* Fills the object properties array */
		for(OWLObjectProperty owlObjectProperty : owlObjectProperties){
			objectPropertiesArray.add(owlObjectProperty.getIRI());
		}

		return objectPropertiesArray;
	}

	/**
	 * This method gets all the data properties in an ontology and its imported ontologies
	 * @return An IRI array with all the data properties
	 */
	public ArrayList<IRI> getDataProperties(){
		ArrayList<IRI> dataPropertiesArray = new ArrayList<IRI>();

		/* Gets all the object properties in signature */
		Set<OWLDataProperty> owlDataProperties = this.ontology.getDataPropertiesInSignature(Imports.INCLUDED);

		/* Fills the object properties array */
		for(OWLDataProperty owlDataProperty : owlDataProperties){
			dataPropertiesArray.add(owlDataProperty.getIRI());
		}

		return dataPropertiesArray;
	}
}
