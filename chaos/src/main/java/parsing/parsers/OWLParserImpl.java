package parsing.parsers;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import ontologies.extractor.OntologyExtractionOperations;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import parsing.ParserInterface;
import database.implementations.OntologyFileImpl;
import domain.bo.ontologies.OntologyFile;

public class OWLParserImpl implements ParserInterface {

	/** The database functions to store OntologyFile objects */
	private OntologyFileImpl ontologyFileImpl;

	/** The class constructor
	 *  It initializes the OntologyFileImpl objects
	 */
	public OWLParserImpl() {
		this.ontologyFileImpl = new OntologyFileImpl();
	}

	@Override
	public void parseFile(File file) throws Exception {
		OntologyFile ontologyFile = new OntologyFile();

		/* Sets the path */
		ontologyFile.setPath(file.getAbsolutePath());

		/* Loads the ontology from the file */
		OntologyExtractionOperations ontologyExtractionOperations = new OntologyExtractionOperations(file);


		/* Sets the namespace */
		IRI namespace = ontologyExtractionOperations.getNamespace();
		ontologyFile.setNamespace(namespace);

		/* Gets all the classes */
		ArrayList<IRI> classes = ontologyExtractionOperations.getClasses();
		ontologyFile.setClasses(classes);

		/* Gets all the individuals and labels */
		HashMap<IRI, String> individualsAndLabelsMap = ontologyExtractionOperations.getIndividualsAndLabels();
		ontologyFile.setIndividuals(individualsAndLabelsMap);

		/* Saves the OntologyFile */
		this.ontologyFileImpl.save(ontologyFile);

		return;
	}

}
