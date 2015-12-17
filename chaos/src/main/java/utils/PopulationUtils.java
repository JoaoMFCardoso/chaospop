package utils;

import java.util.ArrayList;

import ontologies.extractor.OntologyExtractionOperations;

import org.bson.types.ObjectId;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import database.implementations.NodeImpl;
import domain.bo.mappings.IndividualMapping;
import domain.bo.mappings.Mapping;
import domain.bo.parsers.Node;

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

	/**
	 * Gets the Node in the database which matches the tag of a given IndividualMapping, within the range set by a Mapping's DataFile list
	 * @param mapping The Mapping who sets the range to search
	 * @param individualMapping The IndividualMapping whose tag is going to be the query attribute
	 * @return A Node if there are any results, null otherwise
	 */
	public static Node getIndividualMappingMatchingNode(Mapping mapping, IndividualMapping individualMapping){
		NodeImpl nodeImpl = new NodeImpl();
		String individualMappingTag = individualMapping.getTag();

		/* Runs the Mapping file list  */
		Node node = null;
		for(ObjectId dataFileId : mapping.getFileList()){
			ArrayList<Node> queryResults =  (ArrayList<Node>) nodeImpl.getMatchingTagsInDataFile(dataFileId.toString(), individualMappingTag);

		/* Checks if there are any results for the query. If there are it returns the Node */
			if(!queryResults.isEmpty()){
				node = queryResults.get(0);
				break;
			}
		}

		return node;
	}
}
