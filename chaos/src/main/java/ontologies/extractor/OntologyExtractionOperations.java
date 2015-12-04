package ontologies.extractor;

import static org.semanticweb.owlapi.search.Searcher.annotationObjects;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;

import database.implementations.OntologyFileImpl;
import domain.bo.ontologies.OntologyFile;

/**
 * This class implements Ontology data Extraction methods
 * @author João Cardoso
 *
 */
public class OntologyExtractionOperations {

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

	/**
	 * This constructor initializes the manager, data factory, reasoner factory, reasoner and ontology based on a file
	 * @param file The owl file
	 * @throws OWLOntologyCreationException
	 */
	public OntologyExtractionOperations(File file) throws OWLOntologyCreationException{
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
	public OntologyExtractionOperations(IRI ontologyNamespace) throws OWLOntologyCreationException{
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
	public OntologyExtractionOperations(String ontologyFileId) throws OWLOntologyCreationException{
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
	 * This method sets the general OntologyFile attributes, i.e.,
	 * the namespace, the classes and individuals and labels.
	 * @param ontologyFile The OntologyFile object to be filled
	 * @return The filled OntologyFile object
	 */
	public OntologyFile setsGeneralOntologyFileAttributes(OntologyFile ontologyFile){
		OntologyFile of = ontologyFile;

		/* Sets the namespace */
		IRI namespace = getNamespace();
		of.setNamespace(namespace);

		/* Gets all the classes */
		ArrayList<IRI> classes = getClasses();
		of.setClasses(classes);

		/* Gets all the individuals and labels */
		HashMap<IRI, String> individualsAndLabelsMap = getIndividualsAndLabels();
		of.setIndividuals(individualsAndLabelsMap);

		return of;
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
	 * This method extracts all the individuals in signature from an Ontology as well as their labels
	 * @return A HashMap containing the individuals IRI as key and their labels as value
	 */
	public HashMap<IRI, String> getIndividualsAndLabels(){
		HashMap<IRI, String> indlabMap = new HashMap<IRI, String>();

		/* Gets all the individuals */
		Set<OWLNamedIndividual> individuals = this.ontology.getIndividualsInSignature();

		/* Runs the individuals and gets their labels */
		for(OWLNamedIndividual individual : individuals){
			/* Gets all the Annotation Properties that match the RDFSLabel */
			for(OWLAnnotation annotation : annotationObjects(this.ontology.getAnnotationAssertionAxioms(individual.getIRI()), this.factory.getRDFSLabel())){
				 if (annotation.getValue() instanceof OWLLiteral) {
	                    OWLLiteral value = (OWLLiteral) annotation.getValue();
	                    String label = value.toString();

	                    /* Adds a new entry to the HashMap */
	                    indlabMap.put(individual.getIRI(), label);
				 }
			}
		}

		return indlabMap;
	}

	/**
	 * This method gets all the classes from an ontology and its imported ontologies
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
	 * Gets all the object properties that instances of a given owl class must have
	 * @param owlClass The owl class
	 * @return An ArrayList containing the IRIs of all the necessary object properties for the given class
	 */
	public ArrayList<IRI> getObjectPropertiesFromClass(OWLClass owlClass){
		ArrayList<IRI> objectPropertiesArray = new ArrayList<IRI>();

		/* Runs all Object Properties in the signature */
		for(OWLObjectPropertyExpression objectPropertyExpression : this.ontology.getObjectPropertiesInSignature()){
			/* Get the class restrictions for this object property expression */
			OWLClassExpression restriction = this.factory.getOWLObjectSomeValuesFrom(objectPropertyExpression, this.factory.getOWLThing());
			OWLClassExpression intersection = this.factory.getOWLObjectIntersectionOf(owlClass, this.factory.getOWLObjectComplementOf(restriction));

			/* If this object property is needed in this owl class then it is added to the array */
			if(!this.reasoner.isSatisfiable(intersection)){
				objectPropertiesArray.add(objectPropertyExpression.asOWLObjectProperty().getIRI());
			}
		}

		return objectPropertiesArray;
	}

	/**
	 * This method gets all the object properties in an ontology and its imported ontologies
	 * @return An IRI array with all the object properties
	 */
	public ArrayList<IRI> getObjectProperties(){
		ArrayList<IRI> objectPropertiesArray = new ArrayList<IRI>();

		/* Gets all the object properties in signature */
		Set<OWLObjectProperty> owlObjectProperties = this.ontology.getObjectPropertiesInSignature();

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
		Set<OWLDataProperty> owlDataProperties = this.ontology.getDataPropertiesInSignature();

		/* Fills the object properties array */
		for(OWLDataProperty owlDataProperty : owlDataProperties){
			dataPropertiesArray.add(owlDataProperty.getIRI());
		}

		return dataPropertiesArray;
	}
}
