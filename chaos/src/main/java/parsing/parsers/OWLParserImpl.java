package parsing.parsers;

import java.io.File;

import ontologies.extractor.OntologyOperations;
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
		OntologyOperations ontologyExtractionOperations = new OntologyOperations(file);

		/* Sets  */
		ontologyFile.setsGeneralOntologyFileAttributes(ontologyExtractionOperations);

		/* Saves the OntologyFile */
		this.ontologyFileImpl.save(ontologyFile);

		return;
	}

}
