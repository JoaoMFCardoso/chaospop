package testclasses;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bson.types.ObjectId;
import org.semanticweb.owlapi.model.IRI;

import utils.PopulationUtils;
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
		indMap.setTag("testTag");
		indMap.setIndividualName("testIndName");
		indMap.setIndividualLabel("testIndLabel");
		indMap.setSpecification(true);
		indMap.setOwlClassIRI(IRI.create("http://test.owl#testIri"));

		HashMap<IRI, String> testObjProp = new HashMap<IRI, String>();
		testObjProp.put(IRI.create("http://test.owl#testIri2"), "testObjProp");
		indMap.setObjectProperties(testObjProp);

		HashMap<IRI, String> testDataProp = new HashMap<IRI, String>();
		testDataProp.put(IRI.create("http://test.owl#testIri3"), "testDataProp");
		indMap.setDataProperties(testDataProp);

		IndividualMapping indMap2 = new IndividualMapping();
		indMap2.setTag("testTag2");
		indMap2.setIndividualName("testIndName2");
		indMap2.setIndividualLabel("testIndLabel2");
		indMap2.setSpecification(true);
		indMap2.setOwlClassIRI(IRI.create("http://test.owl#testIri2"));

		HashMap<IRI, String> testObjProp2 = new HashMap<IRI, String>();
		testObjProp2.put(IRI.create("http://test.owl#testIri4"), "testObjProp");
		indMap2.setObjectProperties(testObjProp2);

		HashMap<IRI, String> testDataProp2 = new HashMap<IRI, String>();
		testDataProp2.put(IRI.create("http://test.owl#testIri5"), "testDataProp");
		indMap2.setDataProperties(testDataProp2);

		IndividualMapping indMap3 = new IndividualMapping();
		indMap3.setTag("testTag");
		indMap3.setIndividualName("testIndName3");
		indMap3.setIndividualLabel("testIndLabel3");
		indMap3.setSpecification(true);
		indMap3.setOwlClassIRI(IRI.create("http://test.owl#testIri"));

		HashMap<IRI, String> testObjProp3 = new HashMap<IRI, String>();
		testObjProp3.put(IRI.create("http://test.owl#testIri4"), "testObjProp");
		indMap3.setObjectProperties(testObjProp2);

		HashMap<IRI, String> testDataProp3 = new HashMap<IRI, String>();
		testDataProp3.put(IRI.create("http://test.owl#testIri5"), "testDataProp");
		indMap3.setDataProperties(testDataProp2);

		IndividualMappingsImpl indMapImpl = new IndividualMappingsImpl();
		indMapImpl.save(indMap);
		indMapImpl.save(indMap2);
		indMapImpl.save(indMap3);

		List<IndividualMapping> testIndMapList = indMapImpl.getAll();

		/* DATA FILES */

		String f1 = "testf1.xml";
		String f2 = "testf2.xml";

		DataFile df1 = new DataFile();
		DataFile df2 = new DataFile();

		df1.setName(f1);
		df2.setName(f2);

//		df1.setName("testf3.xml");
//		dfimpl.replace(df1.getID().toString(), df1);

		/* NODE */

		Node n1 = new Node();

		n1.setDataFileId(df1.getID());
		n1.setTag("testTag");
		n1.setValue("value");

		Node n2 = new Node();

		n2.setDataFileId(df2.getID());
		n2.setTag("testTag2");
		n2.setValue("value2");

		NodeImpl nImpl = new NodeImpl();
		nImpl.save(n1);
		nImpl.save(n2);


		/* DATAFILE CONT. */

		df1.setNodeID(n1.getID());
		df2.setNodeID(n2.getID());

		DataFileImpl dfimpl = new DataFileImpl();
		dfimpl.save(df1);
		dfimpl.save(df2);

		/* ONTOLOGY FILE */

		OntologyFile of1 = new OntologyFile();
		OntologyFile of2 = new OntologyFile();
		OntologyFile of3 = new OntologyFile();

		IRI o1 = IRI.create("http://sysresearch.org/ontologies/UO.owl#");
		IRI o2 = IRI.create("http://sysresearch.org/ontologies/scenarios/lnec_dll.owl#");
		IRI o3 = IRI.create("http://sysresearch.org/ontologies/scenarios/lnec.owl#");

		of1.setNamespace(o1);
		of2.setNamespace(o2);
		of3.setNamespace(o3);

		OntologyFileImpl ofimpl = new OntologyFileImpl();
		ofimpl.save(of1);
		ofimpl.save(of2);
		ofimpl.save(of3);

		/* MAPPINGS */

		Mapping m = new Mapping();

		ArrayList<ObjectId> files = new ArrayList<ObjectId>();

		files.add(df1.getID());
		files.add(df2.getID());
		m.setFileList(files);

		m.setOutputOntologyFileName("testPopulation");
		m.setOutputOntologyNamespace(IRI.create("http://timbus.teco.edu/public/ontologies/DSOs/testPopulation.owl#"));

		m.setBaseOntology(of3.getID());

		ArrayList<ObjectId> ios = new ArrayList<ObjectId>();
		ios.add(of1.getID());
		ios.add(of2.getID());
		m.setSpecificOntologies(ios);

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
		populate();
//		dropCollections();


	}

}
