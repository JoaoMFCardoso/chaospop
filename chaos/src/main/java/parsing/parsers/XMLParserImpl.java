package parsing.parsers;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.bson.types.ObjectId;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import database.implementations.DataFileImpl;
import database.implementations.NodeImpl;
import domain.bo.parsers.DataFile;
import domain.bo.parsers.Node;
import parsing.ParserInterface;
import utils.FileOperationsUtils;

public class XMLParserImpl implements ParserInterface {

	/** The log4j log. */
//	private static final Logger parserLog = Logger.getLogger(XMLParserImpl.class);

	/** The database functions to store DataFile objects */
	private DataFileImpl dataFileImpl;

	/** The database functions to store Node objects */
	private NodeImpl nodeImpl;

	/** The class constructor
	 *  It initializes the DataFileImpl and NodeImpl objects
	 */
	public XMLParserImpl() {
		this.dataFileImpl = new DataFileImpl();
		this.nodeImpl = new NodeImpl();
	}

	@SuppressWarnings("unchecked")
	@Override
	public String parseFile(File file) throws Exception{
		SAXBuilder builder = new SAXBuilder();
		DataFile dataFile = new DataFile();
		ObjectId dataFileId = dataFile.getID();

		try {
			Document document = (Document) builder.build(file);

			/* Gets the root element
			 * And creates the Node*/
			Element xmlRoot = document.getRootElement();

			Node rootNode = new Node();

			/* Set the DataFile id */
			rootNode.setDataFileId(dataFileId);

			rootNode.setTag(xmlRoot.getName());

			/* Gets the attributes if they exist */
			if(!xmlRoot.getAttributes().isEmpty()){
				HashMap<String, String> attributeHashMap = new HashMap<String, String>();
				List<Attribute> attributeList = xmlRoot.getAttributes();

				for(Attribute attribute : attributeList){
					attributeHashMap.put(attribute.getName(), attribute.getValue());
				}

				/* Adds the attributeHashMap to the Node */
				rootNode.setAttributes(attributeHashMap);
			}

			if(xmlRoot.getChildren().isEmpty()){
				/* Gets the value of the root, because it has no children */
				String value = xmlRoot.getText().replaceAll("\\n+", "");
				value = value.replaceAll("\\t+", "");
				rootNode.setValue(value);

				/* Stores the Node */
				this.nodeImpl.save(rootNode);
			}else{
				/* Creates the children array */
				rootNode.initializeChildren();

				/* Recursively handles the rest of the elements */
				XMLNodeFiller(dataFileId, xmlRoot, rootNode);
			}

			/* Stores the DataFile in the database */
			String dataFileID = FileOperationsUtils.storeDataFile(this.dataFileImpl, dataFile, file, rootNode);
			
			return dataFileID;

		} catch (IOException io) {
			System.out.println(io.getMessage());
			throw io;
		} catch (JDOMException jdomex) {
			System.out.println(jdomex.getMessage());
			throw jdomex;
		}
	}

	/**
	 * XML node filler.
	 * @param dataFileId The id of the DataFile
	 * @param xmlParent the xml parent
	 * @param parentNode the parent node
	 */
	@SuppressWarnings("unchecked")
	private void XMLNodeFiller(ObjectId dataFileId, Element xmlParent, Node parentNode){
		/* Gets the parent's children */
		List<Element> childList = xmlParent.getChildren();

		/* Runs the children and creates an XMLNode for each child */
		for(Element child : childList){
			/* Creates the Node and fills it */
			Node node = new Node();

			/* Set the DataFile id */
			node.setDataFileId(dataFileId);

			/* Sets the Node's parent Node */
			node.setParent(parentNode.getID());

			/* Gets the tag */
			node.setTag(child.getName());

			/* Gets the attributes if they exist */
			if(!child.getAttributes().isEmpty()){
				HashMap<String, String> attributeHashMap = new HashMap<String, String>();
				List<Attribute> attributeList = child.getAttributes();

				for(Attribute attribute : attributeList){
					attributeHashMap.put(attribute.getName(), attribute.getValue());
				}

				/* Adds the attributeHashMap to the node */
				node.setAttributes(attributeHashMap);
			}

			if(child.getChildren().isEmpty()){
				/* Gets the value if the child has no children
				 * For otherwise it would have no value */
				String value = child.getText().replaceAll("\\n+", "");
				value = value.replaceAll("\\t+", "");
				node.setValue(value);

				/* Adds the childNode to the parentNode */
				parentNode.addChild(node.getID());

				/* Stores the Node */
				this.nodeImpl.save(node);
			}else{
				/* Adds the childNode to the parentNode */
				parentNode.addChild(node.getID());

				/* Creates the child's  children array */
				node.initializeChildren();

				/* If the child has children recursively call the XMLNodeFiller */
				XMLNodeFiller(dataFileId, child, node);
			}
		}

		/* Stores the parent Node */
		this.nodeImpl.save(parentNode);
	}
}
