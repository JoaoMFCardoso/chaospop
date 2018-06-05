package parsing.parsers;

import java.io.File;

import javax.ws.rs.core.Response;

import database.implementations.OntologyFileImpl;
import domain.bo.ontologies.OntologyFile;
import exceptions.ChaosPopException;
import exceptions.ErrorMessage;
import ontologies.extractor.OntologyOperations;
import parsing.ParserInterface;

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
	public String parseFile(File file) throws ChaosPopException {
		OntologyFile ontologyFile = new OntologyFile();
		String ontologyFileID = null;
		
		try {
		
		/* Sets the path */
		ontologyFile.setPath(file.getAbsolutePath());

		/* Loads the ontology from the file */
		OntologyOperations ontologyExtractionOperations = new OntologyOperations(file);

		/* Sets  */
		ontologyFile.setsGeneralOntologyFileAttributes(ontologyExtractionOperations);

		/* Saves the OntologyFile */
		ontologyFileID = this.ontologyFileImpl.save(ontologyFile);

		}catch(Exception exception) {
			ErrorMessage owlOntologyCreatioError = new ErrorMessage();
			owlOntologyCreatioError.setMessage(exception.getMessage());
			owlOntologyCreatioError.setStatus(Response.Status.BAD_REQUEST.getStatusCode());
			
			ChaosPopException chaosPopException = new ChaosPopException(exception.getMessage());
			chaosPopException.setErrormessage(owlOntologyCreatioError);
			
			throw chaosPopException;
		}
		
		return ontologyFileID;
	}

}
