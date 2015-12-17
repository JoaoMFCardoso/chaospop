package utils;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import ontologies.extractor.OntologyExtractionOperations;

public class PopulationUtils {

	/**
	 * Imports an Ontology and all it's signature imports into a destination ontology already loaded into the OntologyExtractionOperations object
	 * and avoids duplicates.
	 * @param ontologyId The ontology id of the ontology to be imported
	 * @param ontologyExtractionOperations The OntologyExtractionOperations loaded with the destination ontology
	 * @throws OWLOntologyCreationException
	 */
	public static void importOntologies(String ontologyId, OntologyExtractionOperations ontologyExtractionOperations) throws OWLOntologyCreationException{
		/* Creates the ontology to be imported and gets all its inner imports. Then it runs the destination ontology imports to see if any match
		 * If they do then the inner import is skipped.
		 * The end result is the aggregation of the imports of both ontologies but without any duplicates */
		OntologyExtractionOperations baseOntologyExtractionOperations = new OntologyExtractionOperations(ontologyId);
		ontologyExtractionOperations.importOntologies(baseOntologyExtractionOperations.getImportedOntologiesList());

		return;
	}
}
