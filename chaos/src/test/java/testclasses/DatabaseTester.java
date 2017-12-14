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
import database.implementations.NodeImpl;
import database.implementations.OntologyFileImpl;
import domain.bo.mappings.IndividualMapping;
import domain.bo.mappings.Mapping;
import domain.bo.ontologies.OntologyFile;
import domain.bo.parsers.DataFile;
import domain.bo.parsers.Node;
import domain.bo.population.Batch;

public class DatabaseTester {

	public static void populate(){
		/* INDIVIDUAL MAPPINGS */

		IndividualMapping indMap = new IndividualMapping();
		indMap.setTag("pessoa");
		indMap.setIndividualName(".inattributes-nome");
		indMap.setIndividualLabel(".inattributes-alcunha");
		indMap.setSpecification(false);
		indMap.setOwlClassIRI(IRI.create("http://sysresearch.org/ontologies/DSOs/software.owl#DLL"));

		HashMap<IRI, String> testObjProp = new HashMap<IRI, String>();
		testObjProp.put(IRI.create("http://sysresearch.org/ontologies/UO.owl#realizes"), ".inspecificchild-filho");
		testObjProp.put(IRI.create("http://sysresearch.org/ontologies/UO.owl#aggregates"), ".inspecificchild-conjuge");
		indMap.setObjectProperties(testObjProp);

		HashMap<IRI, Pair<String, String>> testDataProp = new HashMap<IRI, Pair<String, String>>();
		Pair<String, String> p1 = new Pair<String, String>(".inattributes-idade", "Integer");
		testDataProp.put(IRI.create("http://sysresearch.org/ontologies/DSOs/software.owl#hasInstallDate"), p1);
		Pair<String, String> p2 = new Pair<String, String>(".inattributes-cor_olhos", "String");
		testDataProp.put(IRI.create("http://sysresearch.org/ontologies/DSOs/software.owl#hasVersion"), p2);
		indMap.setDataProperties(testDataProp);

		IndividualMappingsImpl indMapImpl = new IndividualMappingsImpl();
		indMapImpl.save(indMap);

		List<IndividualMapping> testIndMapList = indMapImpl.getAll();

		/* DATA FILES */

		String f1 = "family.xml";

		DataFile df1 = new DataFile();

		df1.setName(f1);

		/* NODE */

		Node n0 = new Node();
		n0.setDataFileId(df1.getID());
		n0.setTag("pessoas");

		Node n1 = new Node();
		n1.setDataFileId(df1.getID());
		n1.setTag("pessoa");
		n1.setParent(n0.getID());

		HashMap<String, String> n1attrs = new HashMap<String, String>();
		n1attrs.put("nome", "JoaoAntero");
		n1attrs.put("alcunha", "JoaoPai");
		n1attrs.put("idade", "55");
		n1attrs.put("cor_olhos", "azul");
		n1.setAttributes(n1attrs);

		Node n2 = new Node();
		n2.setDataFileId(df1.getID());
		n2.setTag("pessoa");
		n2.setParent(n0.getID());

		HashMap<String, String> n2attrs = new HashMap<String, String>();
		n2attrs.put("nome", "MariaGoretti");
		n2attrs.put("alcunha", "MaeGoretti");
		n2attrs.put("idade", "55");
		n2attrs.put("cor_olhos", "castanho");
		n2.setAttributes(n2attrs);

		Node n3 = new Node();
		n3.setDataFileId(df1.getID());
		n3.setTag("pessoa");
		n3.setParent(n0.getID());

		HashMap<String, String> n3attrs = new HashMap<String, String>();
		n3attrs.put("nome", "JoaoManuel");
		n3attrs.put("alcunha", "Jonini");
		n3attrs.put("idade", "28");
		n3attrs.put("cor_olhos", "castanho");
		n3.setAttributes(n3attrs);

		Node n4 = new Node();
		n4.setDataFileId(df1.getID());
		n4.setTag("pessoa");
		n4.setParent(n0.getID());

		HashMap<String, String> n4attrs = new HashMap<String, String>();
		n4attrs.put("nome", "AnaRita");
		n4attrs.put("alcunha", "Ritinha");
		n4attrs.put("idade", "24");
		n4attrs.put("cor_olhos", "verde");
		n4.setAttributes(n4attrs);

		Node n5 = new Node();
		n5.setDataFileId(df1.getID());
		n5.setTag("filho");
		n5.setValue("JoaoManuel");
		n5.setParent(n1.getID());

		Node n6 = new Node();
		n6.setDataFileId(df1.getID());
		n6.setTag("filho");
		n6.setValue("AnaRita");
		n6.setParent(n1.getID());

		Node n7 = new Node();
		n7.setDataFileId(df1.getID());
		n7.setTag("filho");
		n7.setValue("JoaoManuel");
		n7.setParent(n2.getID());

		Node n8 = new Node();
		n8.setDataFileId(df1.getID());
		n8.setTag("filho");
		n8.setValue("AnaRita");
		n8.setParent(n2.getID());

		Node n9 = new Node();
		n9.setDataFileId(df1.getID());
		n9.setTag("conjuge");
		n9.setValue("MariaGoretti");
		n9.setParent(n1.getID());

		Node n10 = new Node();
		n10.setDataFileId(df1.getID());
		n10.setTag("conjuge");
		n10.setValue("JoaoAntero");
		n10.setParent(n2.getID());

		ArrayList<ObjectId> n0Children = new ArrayList<ObjectId>();
		n0.setChildren(n0Children);
		n0.addChild(n1.getID());
		n0.addChild(n2.getID());
		n0.addChild(n3.getID());
		n0.addChild(n4.getID());

		ArrayList<ObjectId> n1Children = new ArrayList<ObjectId>();
		n1.setChildren(n1Children);
		n1.addChild(n5.getID());
		n1.addChild(n6.getID());
		n1.addChild(n7.getID());

		ArrayList<ObjectId> n2Children = new ArrayList<ObjectId>();
		n2.setChildren(n2Children);
		n2.addChild(n8.getID());
		n2.addChild(n9.getID());
		n2.addChild(n10.getID());


		NodeImpl nimpl = new NodeImpl();
		nimpl.save(n0);
		nimpl.save(n1);
		nimpl.save(n2);
		nimpl.save(n3);
		nimpl.save(n4);
		nimpl.save(n5);
		nimpl.save(n6);
		nimpl.save(n7);
		nimpl.save(n8);
		nimpl.save(n9);
		nimpl.save(n10);

		/* DATAFILE CONT. */

		df1.setNodeID(n0.getID());

		DataFileImpl dfimpl = new DataFileImpl();
		dfimpl.save(df1);

		/* ONTOLOGY FILE */

		OntologyFile of1 = new OntologyFile();
		OntologyFile of2 = new OntologyFile();
		OntologyFile of3 = new OntologyFile();
		OntologyFile of4 = new OntologyFile();

		IRI o1 = IRI.create("http://sysresearch.org/ontologies/UO.owl#");
		IRI o2 = IRI.create("http://sysresearch.org/ontologies/scenarios/lnec_dll.owl#");
		IRI o3 = IRI.create("http://sysresearch.org/ontologies/scenarios/lnec.owl#");
		IRI o4 = IRI.create("http://sysresearch.org/ontologies/DSOs/software.owl#");

		of1.setNamespace(o1);
		of2.setNamespace(o2);
		of3.setNamespace(o3);
		of4.setNamespace(o4);

		OntologyFileImpl ofimpl = new OntologyFileImpl();
		ofimpl.save(of1);
		ofimpl.save(of2);
		ofimpl.save(of3);
		ofimpl.save(of4);

		/* MAPPINGS */

		Mapping m = new Mapping();

		ArrayList<ObjectId> files = new ArrayList<ObjectId>();

		files.add(df1.getID());

		m.setFileList(files);

		m.setOutputOntologyFileName("family");
		m.setOutputOntologyNamespace(IRI.create("http://sysresearch.org/ontologies/scenarios/family.owl#"));

		/* Imported Ontologies */
		
		ArrayList<ObjectId> ios = new ArrayList<ObjectId>();
		ios.add(of1.getID());
		ios.add(of4.getID());
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

		DataFileImpl dfi = new DataFileImpl();
		dfi.dropCollection();

		IndividualMappingsImpl imi = new IndividualMappingsImpl();
		imi.dropCollection();

		MappingImpl mi = new MappingImpl();
		mi.dropCollection();

		NodeImpl ni = new NodeImpl();
		ni.dropCollection();

		OntologyFileImpl ofi = new OntologyFileImpl();
		ofi.dropCollection();
	}

	public static void main(String[] args) {
		dropCollections();
		populate();
	}

}
