package utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import ontologies.extractor.OntologyOperations;

import org.bson.types.ObjectId;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import database.implementations.IndividualMappingsImpl;
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
	 * Gets the IndividualMapping which matches the tag of a given Node, within the range set by a Mapping's DataFile list
	 * @param node The Node whose tag is going to be the query attribute
	 * @param mapping The Mapping which sets the range to search
	 * @return An individual Mapping which matches the result, null otherwise.
	 */
	public static IndividualMapping getNodeMatchingIndividualMapping(Node node, Mapping mapping){
		/* Gets the Node tag which will act as query */
		String nodeTag = node.getTag();

		/* Initializes the IndividualMapping database implementation */
		IndividualMappingsImpl individualMappingsImpl = new IndividualMappingsImpl();

		/* Gets all the IndividualMappings that match the Node's tag */
		List<IndividualMapping> matches = individualMappingsImpl.getBy("tag", nodeTag);

		/* Runs all the IndividualMapping objects that were matched and returns the one which is matched in the Mapping
		 * IndividualMapping Id's list */
		for(IndividualMapping individualMapping : matches){
			if(individualMapping.getDataFileIds().contains(node.getDataFileId())){
				return individualMapping;
			}
		}

		return null;
	}

	/**
	 * Gets the value from a specific child
	 * @param node The node to be searched
	 * @param path The path to be followed
	 * @param depth The current depth
	 * @return An array containing the values of several individuals
	 */
	public static ArrayList<IRI> getIndividualIRIsFromChildPath(Node node, String[] path, int depth,
			IRI namespace, NodeImpl nodeImpl){
		ArrayList<IRI> individualIRIs = new ArrayList<IRI>();
		Boolean inAttributes = false;

		/* Checks if the tag has a specification for attributes
		 * ex. the tag would be <tag>/<attribute>
		 * this would mean that the value of the IRI is in the attribute */
		String[] complexTag = path[depth].split("/");
		String childTag = complexTag[0];
		int nextDepth = depth + 1;

		String attribute = null;
		if(complexTag.length > 1){
			inAttributes = true;
			attribute = complexTag[1];
		}

		/* Gets the Node' children */
		List<Node> childNodes = nodeImpl.getChildNodes(node.getID());

		/* Looks in the childs for the specific tag */
		for(Node child : childNodes){
			if(child.getTag().equals(childTag)){
				/* if it's the end of the path */
				if(path.length == nextDepth){
					String individualName = null;

					/* If the IRI is in the attributes it fetches the correct attribute */
					if(inAttributes){
						individualName = child.getAttributes().get(attribute);
					}else{
						individualName = child.getValue();
					}

					/* Removes all whitespaces between words  */
					individualName = individualName.replaceAll("\\s+","");

					individualIRIs.add(IRI.create(namespace.toString(), individualName));
				}else{ /* keep going to another child */
					individualIRIs = getIndividualIRIsFromChildPath(child, path, nextDepth, namespace, nodeImpl);
					break;
				}
			}
		}

		return individualIRIs;
	}

	/**
	 * Gets the Parent Individual IRI of a given Node
	 * @param mapping The Mapping used in the population
	 * @param node The Node whose parent is to be fetched
	 * @param namespace The namespace of the ontology being populated
	 * @return The IRI of the parent individual of the given node
	 */
	public static IRI getParentIndividualIRI(Mapping mapping, Node node, IRI namespace){
		/* Gets the Individual IRI of the Parent Individual. This might later be used to fetch a second individual. */
		NodeImpl nodeImpl = new NodeImpl();
		Node parentNode = nodeImpl.get(node.getParent().toString());
		IndividualMapping parentIndividualMapping = PopulationUtils.getNodeMatchingIndividualMapping(parentNode, mapping);
		String parentIndividualName = PopulationUtils.createIndividualName(parentNode, parentIndividualMapping);
		IRI parentIndividualIRI = IRI.create(namespace.toString(), parentIndividualName);

		return parentIndividualIRI;
	}

	/**
	 * Gets a data property value when given a node and a data property mapping detail
	 * @param node The Node from whom the data property value will be extracted
	 * @param dataPropertyMappingDetail The data property mapping detail which details how the information is stored in the node
	 * @return A data property value
	 */
	public static String getDataPropertyValue(Node node, String dataPropertyMappingDetail){
		/* Extracts the data property value from the node */
		String dataPropertyValue = extractFieldFromNode(node, dataPropertyMappingDetail);

		return dataPropertyValue;
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

		/* Removes all whitespaces between words  */
		individualName = individualName.replaceAll("\\s+","");

		return individualName;
	}

	/**
	 * Creates an individual name when its name is based on an attribue
	 * @param node The Node from which data will be gathered to create the individual
	 * @param attributeName The name of the attribute
	 * @return An Individual Name
	 */
	public static String createIndividualNameFromAttribute(Node node, String attributeName){

		/* Gets the Individual's Name from the Node */
		String individualName = extractFieldFromNode(node, attributeName);

		/* Removes all whitespaces between words  */
		individualName = individualName.replaceAll("\\s+","");

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

	/**
	 * Extracts a given field from a given node
	 * @param node The Node from which the data is extracted
	 * @param field The field that is being processed
	 * @return The value of the field
	 */
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
