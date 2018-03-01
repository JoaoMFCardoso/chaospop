package testclasses;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bson.types.ObjectId;
import org.javatuples.Pair;
import org.semanticweb.owlapi.model.IRI;

import database.implementations.BatchImpl;
import database.implementations.DataFileImpl;
import database.implementations.IndividualMappingsImpl;
import database.implementations.MappingImpl;
import database.implementations.OntologyFileImpl;
import domain.bo.mappings.IndividualMapping;
import domain.bo.mappings.Mapping;
import domain.bo.ontologies.OntologyFile;
import domain.bo.parsers.DataFile;
import domain.bo.population.Batch;

public class FamilyTest {

	public static void populate(){
		/* INDIVIDUAL MAPPINGS */

		IndividualMapping indMap = new IndividualMapping();
		indMap.setTag("member");
		indMap.setIndividualName(".inspecificchild-name-given;.inspecificchild-name-surname");
		indMap.setIndividualLabel(".inspecificchild-name-nickname");
		indMap.setSpecification(false);
		indMap.setOwlClassIRI(IRI.create("http://sysresearch.org/ontologies/family#Person"));

		//Data Properties
		HashMap<IRI, Pair<String, String>> dataProperties = new HashMap<IRI, Pair<String, String>>();
		
		//hasFamilyName
		Pair<String, String> p1 = new Pair<String, String>(".inspecificchild-name-surname", "String");
		dataProperties.put(IRI.create("http://sysresearch.org/ontologies/family.owl#hasFamilyName"), p1);
		
		//hasFirstGivenName
		Pair<String, String> p2 = new Pair<String, String>(".inspecificchild-name-given", "String");
		dataProperties.put(IRI.create("http://sysresearch.org/ontologies/family.owl#hasFirstGivenName"), p2);
		
		//#alsoKnownAs
		Pair<String, String> p3 = new Pair<String, String>(".inspecificchild-name-nickname", "String");
		dataProperties.put(IRI.create("http://sysresearch.org/ontologies/family.owl#alsoKnownAs"), p3);
		
		//#hasBirthYear
		Pair<String, String> p4 = new Pair<String, String>(".inspecificchild-death_year", "Integer");
		dataProperties.put(IRI.create("http://sysresearch.org/ontologies/family.owl#hasBirthYear"), p4);
		
		//#hasDeathYear
		Pair<String, String> p5 = new Pair<String, String>(".inspecificchild-birth_year", "Integer");
		dataProperties.put(IRI.create("http://sysresearch.org/ontologies/family.owl#hasDeathYear"), p5);
		
		//#hasMarriageYear
		Pair<String, String> p6 = new Pair<String, String>(".inspecificchild-marriage-marriage_year", "Integer");
		dataProperties.put(IRI.create("http://sysresearch.org/ontologies/family.owl#hasDeathYear"), p6);
		
		
		//Object Properties
		HashMap<IRI, String> objectProperties = new HashMap<IRI, String>();
		
		//#hasParent
		objectProperties.put(IRI.create("http://sysresearch.org/ontologies/family.owl#hasParent"), ".inspecificchild-ancestors-father");
		objectProperties.put(IRI.create("http://sysresearch.org/ontologies/family.owl#hasParent"), ".inspecificchild-ancestors-mother");

		//#hasBrother
		objectProperties.put(IRI.create("http://sysresearch.org/ontologies/family.owl#hasBrother"), ".inspecificchild-siblings-brother");
		
		//#hasSister
		objectProperties.put(IRI.create("http://sysresearch.org/ontologies/family.owl#hasSister"), ".inspecificchild-siblings-sister");
		
		//#hasChild
		objectProperties.put(IRI.create("http://sysresearch.org/ontologies/family.owl#hasChild"), ".inspecificchild-descendants-son");
		objectProperties.put(IRI.create("http://sysresearch.org/ontologies/family.owl#hasChild"), ".inspecificchild-descendants-daughter");

		//#hasPartner
		objectProperties.put(IRI.create("http://sysresearch.org/ontologies/family.owl#hasPartner"), ".inspecificchild-marriage-partner_name");
		
		
		indMap.setDataProperties(dataProperties);
		indMap.setObjectProperties(objectProperties);
		
		IndividualMappingsImpl indMapImpl = new IndividualMappingsImpl();
		indMapImpl.save(indMap);

		List<IndividualMapping> testIndMapList = indMapImpl.getAll();

		/* GET family.xml DATA FILE */

		DataFileImpl dfimpl = new DataFileImpl();
		
		//add correct data file id here
		DataFile df1 = dfimpl.get("5a97099b60b5f13f9d3594df");
		

		/* ONTOLOGY FILE */

		OntologyFile of1 = new OntologyFile();
		

		IRI o1 = IRI.create("http://sysresearch.org/ontologies/family.owl#");
		

		of1.setNamespace(o1);
		

		OntologyFileImpl ofimpl = new OntologyFileImpl();
		ofimpl.save(of1);
		

		/* MAPPINGS */

		Mapping m = new Mapping();

		ArrayList<ObjectId> files = new ArrayList<ObjectId>();

		files.add(df1.getID());

		m.setFileList(files);

		m.setOutputOntologyFileName("cardosofamily");
		m.setOutputOntologyNamespace(IRI.create("http://sysresearch.org/ontologies/cardosofamily.owl#"));

		/* Imported Ontologies */
		
		ArrayList<ObjectId> ios = new ArrayList<ObjectId>();
		ios.add(of1.getID());
		m.setDirectOntologyImports(ios);

		ArrayList<ObjectId> imIDList = new ArrayList<ObjectId>();
		for(IndividualMapping im : testIndMapList){
			imIDList.add(im.getID());
		}

		m.setIndividualMappings(imIDList);

		MappingImpl mimpl = new MappingImpl();
		mimpl.save(m);

		Batch b = new Batch();
		ArrayList<ObjectId> mapId = new ArrayList<ObjectId>();
		mapId.add(m.getID());
		b.setMappings(mapId);

		BatchImpl bimpl = new BatchImpl();
		bimpl.save(b);
		
	}

	public static void dropCollections(){
		BatchImpl bi = new BatchImpl();
		bi.dropCollection();

//		DataFileImpl dfi = new DataFileImpl();
//		dfi.dropCollection();

		IndividualMappingsImpl imi = new IndividualMappingsImpl();
		imi.dropCollection();

		MappingImpl mi = new MappingImpl();
		mi.dropCollection();

//		NodeImpl ni = new NodeImpl();
//		ni.dropCollection();

		OntologyFileImpl ofi = new OntologyFileImpl();
		ofi.dropCollection();
	}

	public static void main(String[] args) {
		dropCollections();
		populate();
	}
	
}

