package utils;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import ontologies.extractor.OntologyOperations;

import org.bson.types.ObjectId;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import database.implementations.NodeImpl;
import domain.bo.mappings.IndividualMapping;
import domain.bo.mappings.Mapping;
import domain.bo.parsers.Node;

public class PopulationUtils {

	/**
	 * Imports an Ontology and all it's signature imports into a destination ontology already loaded into the OntologyOperations object
	 * and avoids duplicates.
	 * @param ontologyId The ontology id of the ontology to be imported
	 * @param ontologyOperations The OntologyOperations loaded with the destination ontology
	 * @throws OWLOntologyCreationException
	 */
	public static void importOntologies(String ontologyId, OntologyOperations ontologyOperations) throws OWLOntologyCreationException{
		/* Creates the ontology to be imported and gets all its inner imports. Then it runs the destination ontology imports to see if any match
		 * If they do then the inner import is skipped.
		 * The end result is the aggregation of the imports of both ontologies but without any duplicates */
		OntologyOperations baseOntologyOperations = new OntologyOperations(ontologyId);

		Set<OWLOntology> importedOntologies = baseOntologyOperations.getImportedOntologiesList();
		importedOntologies.add(baseOntologyOperations.getOntology());

		ontologyOperations.importOntologies(importedOntologies);

		return;
	}

	/**
	 * Gets the name in form of String of a given OWLNamedIndividual. If the OWLNamedIndividual has no name, it returns an empty String
	 * @param candidateIndividual The OWLNamedIndividual
	 * @return The OWLNamedIndividual name or an empty String if it doesn't have one
	 */
	public static String getCandidateIndividualName(OWLNamedIndividual candidateIndividual){
		String candidateIndividualName;

		/* Gets the candidate individual name, and returns an empty name if the individual had no name, and thus
		 * triggered an IllegalStateException when trying to perform the get() method */
		try{
			candidateIndividualName = candidateIndividual.getIRI().getRemainder().get();
		}catch(IllegalStateException illegalStateException){
			candidateIndividualName = "";
		}

		return candidateIndividualName;
	}

	/**
	 * Gets the Node in the database which matches the tag of a given IndividualMapping, within the range set by a Mapping's DataFile list
	 * @param mapping The Mapping who sets the range to search
	 * @param individualMapping The IndividualMapping whose tag is going to be the query attribute
	 * @return A list of Nodes if there are any results, null otherwise
	 */
	public static ArrayList<Node> getIndividualMappingMatchingNode(Mapping mapping, IndividualMapping individualMapping){
		NodeImpl nodeImpl = new NodeImpl();
		String individualMappingTag = individualMapping.getTag();

		/* Runs the Mapping file list  */
		ArrayList<Node> nodeList = new ArrayList<Node>();
		for(ObjectId dataFileId : mapping.getFileList()){
			ArrayList<Node> queryResults =  (ArrayList<Node>) nodeImpl.getMatchingTagsInDataFile(dataFileId.toString(), individualMappingTag);

			/* Checks if there are any results for the query. If there are it returns the Node */
			if(!queryResults.isEmpty()){
				nodeList.addAll(queryResults);
			}
		}

		return nodeList;
	}

	/**
	 * Creates a new Individual Name from the Node and the IndividualMapping objects
	 * @param node The Node which holds the new individual information
	 * @param individualMapping The IndividualMapping which regulates how the new individual is to be created
	 * @return An individual name
	 */
	public static String createIndividualName(Node node, IndividualMapping individualMapping){
		/* Gets the Individual Name Mapping from the IndividualMapping*/
		String individualNameMapping = individualMapping.getIndividualName();

		/* Gets the Individual's Name from the Node */
		String individualName = extractFieldFromNode(node, individualNameMapping);

		return individualName;
	}

	/**
	 * Creates a new Label from the Node and the IndividualMapping objects
	 * @param node The Node which holds the new individual information
	 * @param individualMapping The IndividualMapping which regulates how the new individual is to be created
	 * @return An individual label
	 */
	public static String createIndividualLabel(Node node, IndividualMapping individualMapping){
		/* Gets the Individual Label Mapping from the IndividualMapping */
		String individualLabelMapping = individualMapping.getIndividualLabel();

		/* Gets the Individual's Label from the Node */
		String individualLabel = extractFieldFromNode(node, individualLabelMapping);

		return individualLabel;
	}

	private static String extractFieldFromNode(Node node, String field){
		String[] fieldParams = field.split("-");

		switch(fieldParams[0]){
		case ".invalue": /* The field is in the Node's value */
			field = node.getValue();
			break;
		case ".inspecificchild":
			/* The field is in one of the Node's children value. The Child's tag is defined in FieldParams */
			field = searchChildNodes(node, fieldParams, 1);
			break;
		case ".inattributes": /* The field is in a specific attribute value */
			field = node.getAttributes().get(fieldParams[1]);
			break;
		default: /* The field is in none of the above therefore it must be generated */
			field = UUID.randomUUID().toString();
			break;
		}

		return field;
	}

	/**
	 * gets the value of a specific child specified in the path argument
	 * @param node The Node that is beeing searched
	 * @param path The path containing the various Node tags
	 * @param depth The depth that is being searched
	 * @return The value of the specified child
	 */
	private static String searchChildNodes(Node node, String[] path, int depth){
		String value = "";

		/* Gets specific tag */
		String childTag = path[depth];
		int nextDepth = depth + 1;

		/* Gets the Children Nodes */
		NodeImpl nodeImpl = new NodeImpl();
		ArrayList<Node> children = (ArrayList<Node>) nodeImpl.getBy("parent", node.getID().toString());

		/* Looks in the childs for the specific tag */
		for(Node child : children){
			if(child.getTag().equals(childTag)){
				/* if it's the end of the path */
				if(path.length == nextDepth){
					value = child.getValue();
				}else{ /* keep going to another child */
					value = searchChildNodes(child, path, nextDepth);
					break;
				}
			}
		}

		return value;
	}
}
