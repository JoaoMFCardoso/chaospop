package ontologies.extractor;

import static org.semanticweb.owlapi.search.Searcher.annotationObjects;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import org.javatuples.Pair;
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
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLImportsDeclaration;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.parameters.Imports;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;

import utils.PopulationUtils;
import database.implementations.NodeImpl;
import database.implementations.OntologyFileImpl;
import domain.bo.mappings.IndividualMapping;
import domain.bo.mappings.Mapping;
import domain.bo.ontologies.OntologyFile;
import domain.bo.parsers.Node;

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
	 * ONTOLOGY IMPORTATION METHODS
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

	/******************************************************************************************************************************
	 * ONTOLOGY CREATION METHODS
	 ******************************************************************************************************************************/

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

	/**
	 * Creates a new OWLAnnotationAssertion Axiom, associating a Label with an Individual
	 * @param individualIRI The Individual IRI
	 * @param individualLabel The label to be associated
	 */
	private void createLabel(IRI individualIRI, String individualLabel){
		/* Creates the OWLAnnotation and OWLAnnotationAssertionAxiom */
		OWLAnnotation annotation = this.factory.getOWLAnnotation(this.factory.getRDFSLabel(), this.factory.getOWLLiteral(individualLabel));
		OWLAnnotationAssertionAxiom annotationAssertionAxiom = this.factory.getOWLAnnotationAssertionAxiom(individualIRI, annotation);

		/* Adds the new Axiom to the Manager */
		this.manager.applyChange(new AddAxiom(this.ontology, annotationAssertionAxiom));
	}

	/**
	 * Creates a new OWLNamedIndividual based on the input data
	 * @param individualName The Individual Name
	 * @param individualLabel The Individual Label
	 * @param individualIRI The Individual IRI
	 * @param individualClassIRI The Individual OWLClass IRI. Might be null if a proto individual is being created
	 * @return A valid OWLNamedIndividual
	 */
	private OWLNamedIndividual createOWLNamedIndividual(String individualName, String individualLabel, IRI individualIRI,
			IRI individualClassIRI){

		/* A new OWLNamedIndividual Object must be associated with the DataFactory */
		OWLNamedIndividual owlNamedIndividual = this.factory.getOWLNamedIndividual(individualIRI);

		/* Assigns the individual with a label. If the individualLabel provided in this methods input is an empty string, then the
		 * individualFragment should be adopted as label */

		/* Uses the individualFragment if the individualLabelindividualLabel is empty or null */
		if(null == individualLabel || individualLabel.isEmpty()){
			individualLabel = individualName;
		}

		/* Creates the new Label OWLAnnotationAxiom */
		createLabel(individualIRI, individualLabel);

		/* Assigns the individual with a class. */
		if(null != individualClassIRI){
			createOwlClass(individualClassIRI, owlNamedIndividual);
		}

		return owlNamedIndividual;
	}

	/**
	 * Handles the creation of a HashMap of Object Properties for a given OWLNamedIndividual
	 * @param node The Node object that is used to gather data for the object property creation
	 * @param mapping The Mapping associated with this population
	 * @param individualMapping The IndividualMapping object associated with this node
	 * @param firstIndividual The OWLNamedIndividual that will act as first individual
	 */
	public void handleObjectPropertyCreation(Node node, Mapping mapping, IndividualMapping individualMapping, OWLNamedIndividual firstIndividual){
		/* Runs each Object Property and creates each one */
		HashMap<IRI, String> objectProperties = individualMapping.getObjectProperties();
		for(IRI objectPropertyIRI : objectProperties.keySet()){
			/* Gets the second individuals data */
			Pair<ArrayList<IRI>, Boolean> secondIndividualsData = getSecondIndividuals(node, mapping, objectPropertyIRI,
					objectProperties.get(objectPropertyIRI));

			/* Now that the second individuals have been gathered the object properties can be created */
			createObjectProperties(objectPropertyIRI, firstIndividual, secondIndividualsData);
		}
	}

	public void handleDataPropertyCreation(Node node, Mapping mapping, IndividualMapping individualMapping, OWLNamedIndividual individual){
		/* Runs each Data Property and creates each one */
		HashMap<IRI, String> dataProperties = individualMapping.getDataProperties();
		for(IRI dataPropertyIRI : dataProperties.keySet()){

			/* Gets the data property value */
			String dataPropertyValue = PopulationUtils.getDataPropertyValue(node, dataProperties.get(dataPropertyIRI));

			/* Creates the object property if allowed */
			if(isAllowedToCreateDataProperty(dataPropertyIRI, individual, dataPropertyValue)){
				createDataProperty(dataPropertyIRI, individual, dataPropertyValue);
			}
		}
	}

	/**
	 * Creates object properties of a given type between a first individual and a list of second individuals
	 * @param objectPropertyIRI The object property type IRI
	 * @param firstIndividual The first OWLNamedIndividual.
	 * @param secondIndividualsData A Pair containing all the second individuals IRI's and a boolean indicating if the property is
	 * inverted (i.e. the second individuals will act as first individuals whilst the first will act as second)
	 */
	private void createObjectProperties(IRI objectPropertyIRI, OWLNamedIndividual firstIndividual, Pair<ArrayList<IRI>,
			Boolean> secondIndividualsData){

		/* Extracts the Second Individuals Data  */
		Boolean isInverted = secondIndividualsData.getValue1();
		ArrayList<IRI> secondIndividualsIRIs = secondIndividualsData.getValue0();

		/* Runs each second Individual and creates the object property */
		for(IRI secondIndividualIRI : secondIndividualsIRIs){
			/* Gets the second individual name */
			String secondIndividualName = secondIndividualIRI.getRemainder().get();

			/* Gets the second individual as an OWLNamedIndividual Object
			 * If the individual has not been created yet, the getOWLNamedIndividual method will create a proto individual
			 * thus the label and OWLClass must be null in this call */
			OWLNamedIndividual secondIndividual = getOWLNamedIndividual(secondIndividualName, null, secondIndividualIRI, null);

			/* Runs the Object Property Creation Pre Checks and creates the object property if allowed */
			if(isAllowedToCreateObjectProperty(objectPropertyIRI, firstIndividual, secondIndividual, isInverted)){
				createObjectProperty(objectPropertyIRI, firstIndividual, secondIndividual, isInverted);
			}
		}
	}

	/**
	 * Creates an object property between a first and second individuals
	 * @param objectPropertyIRI The Object Property to be created
	 * @param firstIndividual The first individual
	 * @param secondIndividual The second individual
	 * @param isInverted A boolean indicating if the property is to be inverted
	 */
	private void createObjectProperty(IRI objectPropertyIRI, OWLNamedIndividual firstIndividual, OWLNamedIndividual secondIndividual,
			Boolean isInverted){

		/* Create the relation */
		OWLObjectProperty objectProperty = this.factory.getOWLObjectProperty(objectPropertyIRI);

		/* Switches the First Individual for the Second in the event that this property is to be inverted */
		OWLObjectPropertyAssertionAxiom assertion = null;
		if(isInverted){
			assertion = this.factory.getOWLObjectPropertyAssertionAxiom(objectProperty, secondIndividual, firstIndividual);
		}else{
			assertion = this.factory.getOWLObjectPropertyAssertionAxiom(objectProperty, firstIndividual, secondIndividual);
		}

		/* Add the axiom to the ontology and save */
		AddAxiom addAxiomChange = new AddAxiom(this.ontology, assertion);
		this.manager.applyChange(addAxiomChange);
	}

	/**
	 * Creates a given data property for a given individual with a given value
	 * @param dataPropertyIRI The data property to be created
	 * @param individual The individual
	 * @param dataPropertyValue The data property value
	 */
	private void createDataProperty(IRI dataPropertyIRI, OWLNamedIndividual individual, String dataPropertyValue){
		/* Create the relation */
		OWLDataProperty dataProperty = this.factory.getOWLDataProperty(dataPropertyIRI);
		OWLDataPropertyAssertionAxiom assertion = this.factory.getOWLDataPropertyAssertionAxiom(dataProperty, individual, dataPropertyValue);
		/* Add the axiom to the ontology and save */
		AddAxiom addAxiomChange = new AddAxiom(this.ontology, assertion);
		this.manager.applyChange(addAxiomChange);
	}

	/******************************************************************************************************************************
	 * ONTOLOGY UPDATE METHODS
	 ******************************************************************************************************************************/

	/**
	 * Updates an existing OWLNamedIndividual. This method is called in two instances. When an existing individual is being updated as
	 * a specification. Or if a proto individual that was created in the context of an object property creation needs to be updated.
	 * @param owlNamedIndividual The OWLNamedIndividual object
	 * @param individualLabel The individual label
	 * @param individualClassIRI The individual OWLClass IRI
	 */
	public void updateOWLNamedIndividual(OWLNamedIndividual owlNamedIndividual, String individualLabel, IRI individualClassIRI){
		/* Gets the Individual IRI */
		IRI individualIRI = owlNamedIndividual.getIRI();

		/* Checks if the label is updated, and updates it if necessary */
		if(isLabelInNeedOfUpdate(individualIRI, individualLabel)){
			updateIndividualLabel(individualIRI, individualLabel);
		}

		/* Checks if the class needs to be updated, and updates it if necessary */
		if(isClassInNeedOfUpdate(owlNamedIndividual, individualClassIRI)){
			updateIndividualClass(owlNamedIndividual, individualClassIRI);
		}

		return;
	}

	/**
	 * Updates an Individual Label by first removing any existing OWLAnnotationAxioms and then adding a new one
	 * containing the new label
	 * @param individualIRI The individual IRI
	 * @param individualLabel The new label
	 */
	private void updateIndividualLabel(IRI individualIRI, String individualLabel){
		/* Removes all previously existing AnnotationAssertionAxioms */
		for(OWLAnnotationAssertionAxiom axiom : this.ontology.getAnnotationAssertionAxioms(individualIRI)){
			this.manager.removeAxiom(this.ontology, axiom);
		}

		/* Creates a new OWLAnnotationAssertionAxiom with the new individual label */
		createLabel(individualIRI, individualLabel);
	}

	/**
	 * Uldates an Individual OWLClass by first removing any existing OWLClassAssertionAxioms and then adding a new one
	 * containing the new OWLClass
	 * @param owlNamedIndividual The OWLNamedIndividual
	 * @param owlClassIRI The new OWLClass IRI
	 */
	private void updateIndividualClass(OWLNamedIndividual owlNamedIndividual, IRI owlClassIRI){
		/* Removes all the OWLClassAssertionAxioms for this individual */
		Set<OWLClassAssertionAxiom> owlClassAssertionAxioms = this.ontology.getClassAssertionAxioms(owlNamedIndividual);
		for(OWLClassAssertionAxiom owlClassAssertionAxiom : owlClassAssertionAxioms){
			this.manager.removeAxiom(this.ontology, owlClassAssertionAxiom);
		}

		/* Creates a new OWLClassAssertionAxiom for this individual */
		createOwlClass(owlClassIRI, owlNamedIndividual);
	}

	/******************************************************************************************************************************
	 * ONTOLOGY BOOLEAN METHODS
	 ******************************************************************************************************************************/

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
	 * Checks if an OWLNamedIndividual is a proto individual that needs to be updated.
	 *  The individual will need an update if it has OWLClass assigned as an OWLClassAssertionAxiom.
	 * @param owlNamedIndividual The OWLNamedIndividual that is to be analysed
	 * @return True if the OWLNamedIndividual needs to be updated, False otherwise
	 */
	public Boolean isIndividualInNeedOfUpdate(OWLNamedIndividual owlNamedIndividual){
		Boolean update = true;

		Set<OWLClassAssertionAxiom> owlClassAssertionAxioms = this.ontology.getClassAssertionAxioms(owlNamedIndividual);
		if(owlClassAssertionAxioms.isEmpty()){
			return update;
		}else{
			update = false;
			return update;
		}
	}

	/**
	 * Checks if a label is need of an update, by comparing a given new label with the existing OWLAnnotationAxiom labels
	 * associated with a given Individual IRI
	 * @param individualIRI The Individual IRI
	 * @param newIndividualLabel The new Label that will serve as comparison
	 * @return True if an update is necessary (i.e. no label was found that matches the new Label), False otherwise
	 */
	private Boolean isLabelInNeedOfUpdate(IRI individualIRI, String newIndividualLabel){
		Boolean update = true;

		/* Gathers the Individual's OWLAnnotations that are Labels */
		Collection<OWLAnnotation> owlAnnotations = annotationObjects(this.ontology.getAnnotationAssertionAxioms(individualIRI),
				this.factory.getRDFSLabel());

		/* If the collection is empty or there are more than one, then the label needs to be updated */
		if(owlAnnotations.isEmpty() || owlAnnotations.size() > 1){
			return update;
		}

		/* Runs Annotation Properties that match the RDFSLabel to see if the new individualLabel matches the existing one */
		for(OWLAnnotation annotation : owlAnnotations){

			if(annotation.getValue() instanceof OWLLiteral) {
				OWLLiteral value = (OWLLiteral) annotation.getValue();
				String label = value.getLiteral();

				/* Adds the label to the labelAndClass array */
				if(label.equals(newIndividualLabel)){
					update = false;
					return update;
				}
			}
		}

		return update;
	}

	/**
	 * Checks if a given OWLNamedIndividual is in need for its OWLClass to be updated
	 * @param owlNamedIndividual The OWLNamedIndividual Object
	 * @param owlClassIRI The new OWLClass IRI
	 * @return True if an update is necessary, False otherwise
	 */
	private Boolean isClassInNeedOfUpdate(OWLNamedIndividual owlNamedIndividual, IRI owlClassIRI){
		Boolean update = true;

		/* Gathers the OWLClassAssertionAxiom objects */
		Set<OWLClassAssertionAxiom> owlClassAssertionAxioms = this.ontology.getClassAssertionAxioms(owlNamedIndividual);

		/* If the Set of OWLClassAssertioAxiom objects is empty or there are more than one, then the OWLClass needs to be updated */
		if(owlClassAssertionAxioms.isEmpty() || owlClassAssertionAxioms.size() > 1){
			return update;
		}

		/* Runs the OWLClassAssertionAxiom Objects to see if any matches the OWLClassIRI */
		for(OWLClassAssertionAxiom owlClassAssertionAxiom : owlClassAssertionAxioms){
			String candidateOWLClass = owlClassAssertionAxiom.getClassExpression().asOWLClass().getIRI().toString();

			/* Checks if the candidateOWLClass matches the new OWLClass */
			if(owlClassIRI.toString().equals(candidateOWLClass)){
				update = false;
				return update;
			}
		}

		return update;
	}

	/**
	 * Checks if an Object Property is allowed to be created between two individuals
	 * @param objectPropertyIRI The Object property IRI
	 * @param firstIndividual The first OWLNamedIndividual
	 * @param secondIndividual The second OWLNamedIndividual
	 * @param isInverted A boolean indicating if the Object Property is inverted
	 * @return True if the Object Property is allowed to be created, false otherwise
	 */
	private Boolean isAllowedToCreateObjectProperty(IRI objectPropertyIRI, OWLNamedIndividual firstIndividual, OWLNamedIndividual secondIndividual,
			Boolean isInverted){
		Boolean isAllowed = true;

		/* Checks if the first and second individuals have the same IRI */
		if(firstIndividual.toString().equals(secondIndividual.toString())){
			isAllowed = false;
			return isAllowed;
		}

		/* Checks if the property already exists between this two individuals,
		 * considering the possibility of it being an inverted object property */
		if(isInverted){
			isAllowed = !isExistingObjectProperty(objectPropertyIRI, secondIndividual, firstIndividual);
		}else{
			isAllowed = !isExistingObjectProperty(objectPropertyIRI, firstIndividual, secondIndividual);
		}

		return isAllowed;
	}

	/**
	 * Checks if a data property is allowed to be created
	 * @param dataPropertyIRI The data property that is being tested
	 * @param individual The individual which is the object of the data property
	 * @param dataPropertyValue The data property value
	 * @return True if it is allowed to be created, false otherwise
	 */
	private Boolean isAllowedToCreateDataProperty(IRI dataPropertyIRI, OWLNamedIndividual individual, String dataPropertyValue){
		Boolean isAllowed = true;

		/* Checks if the data property has already been created */
		isAllowed = !isExistingDataProperty(dataPropertyIRI, individual, dataPropertyValue);

		return isAllowed;
	}

	/**
	 * Checks if a given object property exists
	 * @param objectPropertyIRI the object property IRI
	 * @param firstIndividual The first individual
	 * @param secondIndividual The second individual
	 * @return True if it exists, false otherwise
	 */
	private Boolean isExistingObjectProperty(IRI objectPropertyIRI, OWLNamedIndividual firstIndividual, OWLNamedIndividual secondIndividual){
		Boolean exists = false;

		/* Gets all the Object Properties that have the first individual involved */
		for(OWLObjectPropertyAssertionAxiom axiom : this.ontology.getObjectPropertyAssertionAxioms(firstIndividual)){
			/* If the Object Property matches the one in the axiom and the second individual is the same
			 * then this Object Property already exists.*/
			if(axiom.getProperty().asOWLObjectProperty().getIRI().equals(objectPropertyIRI)
					&& axiom.getObject().asOWLNamedIndividual().getIRI().equals(secondIndividual.getIRI())){
				exists = true;
				break;
			}
		}

		return exists;
	}

	/**
	 * Checks if a data property exists
	 * @param dataPropertyIRI The data property that is being tested
	 * @param individual The individual which is the object of the data property
	 * @param dataPropertyValue The data property value
	 * @return True if it exists, false otherwise
	 */
	private Boolean isExistingDataProperty(IRI dataPropertyIRI, OWLNamedIndividual individual, String dataPropertyValue){
		Boolean exists = false;

		/* Gets all the data Properties that have the first individual involved */
		for(OWLDataPropertyAssertionAxiom axiom : this.ontology.getDataPropertyAssertionAxioms(individual)){
			/* If the data Property matches the one in the axiom and the value is the same
			 * then this data Property already exists.*/
			if(axiom.getProperty().asOWLDataProperty().getIRI().equals(dataPropertyIRI)
					&& axiom.getObject().getLiteral().equals(dataPropertyValue)){
				exists = true;
				break;
			}
		}

		return exists;
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

			/* Gets all the OWLClassAssertionAxioms for this individual and fetches the first */
			Set<OWLClassAssertionAxiom> owlClassAssertionAxioms = this.ontology.getClassAssertionAxioms(individual);
			for(OWLClassAssertionAxiom owlClassAssertionAxiom : owlClassAssertionAxioms){
				String indOwlClass = owlClassAssertionAxiom.getClassExpression().asOWLClass().getIRI().toString();

				/* Adds the OWL Class to the labelAndClass array */
				labelAndClass[1] = indOwlClass;
			}

			/* Adds a new entry to the HashMap */
			indlabMap.put(individual.getIRI(), labelAndClass);
		}

		return indlabMap;
	}

	/**
	 * Gets an OWLNamedIndividual from the Ontology. It either gets an existing one or creates a new one it no match exists
	 * in the Ontology Signature.
	 * @param individualName The Individual Name
	 * @param individualLabel The Individual Label
	 * @param individualIRI The Individual IRI
	 * @param individualClassIRI The Individual OWL Class IRI
	 * @return A valid OWLNamedIndividual
	 */
	public OWLNamedIndividual getOWLNamedIndividual(String individualName, String individualLabel, IRI individualIRI,
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
			if(candidateIndividualName.equals(individualName)){
				/* The owlNamedIndividual that is to be returned is the candidateIndividual */
				owlNamedIndividual = candidateIndividual;
				return owlNamedIndividual;
			}
		}

		/*****************************************************************************************************************
		 * New Individual Handling Procedures
		 *****************************************************************************************************************/
		/* If no matches were found with any existing individuals, a new individual must be created with the input data */

		owlNamedIndividual = createOWLNamedIndividual(individualName, individualLabel, individualIRI, individualClassIRI);

		return owlNamedIndividual;
	}

	/**
	 * Gets all the second individuals necessary for the creation of a given object property.
	 *The mapping detail is as follows:
	 * 1. .parent, the second individual IRI should be the parent of the first individual IRI
	 * 2. .inspecificchild-<childnode.tag>-<grandchildnode.tag>-<...>, then the IRI of the final child should be used as second individual IRI
	 * 3. value, the second individual IRI is in the value
	 * @param node The Node Object that is used to gather data for the creation of Individuals
	 * @param mapping The Mapping associated with this population
	 * @param individualMapping The IndividualMapping Object that is associated with this Node
	 * @param ObjectPropertyIRI The Object Property IRI
	 * @param mappingDetail The Mapping detail wich indicates how the second individual should be fetched
	 * @return A Pair Object containing the Second Individuals IRI array, and a Boolean which indicates if the object property
	 * relation is inverted or not.
	 */
	private Pair<ArrayList<IRI>, Boolean> getSecondIndividuals(Node node, Mapping mapping, IRI ObjectPropertyIRI, String mappingDetail){
		/* Initialization of the second individuals array */
		ArrayList<IRI> secondIndividuals = new ArrayList<IRI>();

		/* Processes the mapping detail */
		String[] processedMappingDetail = mappingDetail.split("-");
		Boolean isInverted = false;

		/* Initializes the NodeImpl Object */
		NodeImpl nodeImpl = null;

		/* Initializes the Ontology Namespace */
		IRI namespace = this.ontology.getOntologyID().getOntologyIRI().get();

		/* Initializes the parentIndividual IRI */
		IRI parentIndividualIRI = null;

		/* Adds second individuals to the second individuals array */
		switch (processedMappingDetail[0]) {
		case ".parent":
			/* Gets the ParentIndividual IRI */
			parentIndividualIRI = PopulationUtils.getParentIndividualIRI(mapping, node, namespace);
			secondIndividuals.add(parentIndividualIRI);
			break;
		case ".parentinverted":
			/* The second individual is in the parent but the relation is inverted */
			/* Gets the ParentIndividual IRI */
			parentIndividualIRI = PopulationUtils.getParentIndividualIRI(mapping, node, namespace);
			secondIndividuals.add(parentIndividualIRI);

			/* Signals that this is an inverted relation */
			isInverted = true;
			break;
		case ".inspecificchild":
			/* Gets the individual name */
			nodeImpl = new NodeImpl();
			secondIndividuals = PopulationUtils.getIndividualIRIsFromChildPath(node, processedMappingDetail, 1, namespace, nodeImpl);
			break;
		case ".forspecificchild": /* The first and second individuals must be switched,
		 because this is an inverted relation*/
			/* Gets the individual name */
			nodeImpl = new NodeImpl();
			secondIndividuals = PopulationUtils.getIndividualIRIsFromChildPath(node, processedMappingDetail, 1, namespace, nodeImpl);

			/* Signals that this is an inverted relation */
			isInverted = true;
			break;
		case ".inattributes":
			/* The second individual IRI is in the attributes of the invidiual */
			String individualName = PopulationUtils.createIndividualNameFromAttribute(node, processedMappingDetail[1]);
			secondIndividuals.add(IRI.create(namespace.toString() , individualName));
			break;
		default:
			secondIndividuals.add(IRI.create(mappingDetail));
			break;
		}

		/* Builds and returns the return Pair */
		Pair<ArrayList<IRI>, Boolean> returnPair = new Pair<ArrayList<IRI>, Boolean>(secondIndividuals, isInverted);
		return returnPair;
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
