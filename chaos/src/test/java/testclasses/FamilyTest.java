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
import ontologies.populator.PopulationOperations;

public class FamilyTest {

	public static void populate(){
		/* INDIVIDUAL MAPPINGS */

		IndividualMapping indMap = new IndividualMapping();
		indMap.setTag("member");
		indMap.setIndividualName(".inspecificchild-name-given;.inspecificchild-name-surname");
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
		
		//Annotation Properties
		HashMap<String, String> annotationProperties = new HashMap<String, String>();
		
		//label
		annotationProperties.put("label", ".inspecificchild-name-nickname");
		
		//Comment
		annotationProperties.put("comment", ".inspecificchild-name-given;.inspecificchild-name-surname;.inspecificchild-name-nickname");
		
		//seeAlso
		annotationProperties.put("seeAlso", ".inspecificchild-siblings-sister");
		
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
		
		
		indMap.setAnnotationProperties(annotationProperties);
		indMap.setDataProperties(dataProperties);
		indMap.setObjectProperties(objectProperties);
		
		IndividualMappingsImpl indMapImpl = new IndividualMappingsImpl();
		indMapImpl.save(indMap);

		List<IndividualMapping> testIndMapList = indMapImpl.getAll();

		/* DATA FILES */

		String f1 = "family.xml";

		DataFile df1 = new DataFile();

		df1.setName(f1);

		/* NODE */

		//Family
		Node family1 = new Node();
		family1.setDataFileId(df1.getID());
		family1.setTag("family");

		//Member1
		Node member1 = new Node();
		member1.setDataFileId(df1.getID());
		member1.setTag("member");
		member1.setParent(family1.getID());

		//Name
		Node nameMember1 = new Node();
		nameMember1.setDataFileId(df1.getID());
		nameMember1.setTag("name");
		nameMember1.setParent(member1.getID());
		
		ArrayList<ObjectId> childrenMember1 = new ArrayList<ObjectId>();
		member1.setChildren(childrenMember1);
		member1.addChild(nameMember1.getID());
		
		//given
		Node givenMember1 = new Node();
		givenMember1.setDataFileId(df1.getID());
		givenMember1.setTag("given");
		givenMember1.setParent(nameMember1.getID());
		givenMember1.setValue("João Manuel");
		
		ArrayList<ObjectId> childrenNameMember1 = new ArrayList<ObjectId>();
		nameMember1.setChildren(childrenNameMember1);
		nameMember1.addChild(givenMember1.getID());
		
		//surname
		Node surnameMember1 = new Node();
		surnameMember1.setDataFileId(df1.getID());
		surnameMember1.setTag("surname");
		surnameMember1.setParent(nameMember1.getID());
		surnameMember1.setValue("Cardoso");
		
		nameMember1.addChild(surnameMember1.getID());
		
		//nickname
		Node nicknameMember1 = new Node();
		nicknameMember1.setDataFileId(df1.getID());
		nicknameMember1.setTag("nickname");
		nicknameMember1.setParent(nameMember1.getID());
		nicknameMember1.setValue("Jonini");
		
		nameMember1.addChild(nicknameMember1.getID());
		
		//sex
		Node sexMember1 = new Node();
		sexMember1.setDataFileId(df1.getID());
		sexMember1.setTag("sex");
		sexMember1.setParent(member1.getID());
		sexMember1.setValue("male");
		
		member1.addChild(sexMember1.getID());
		
		//birth year
		Node birthYearMember1 = new Node();
		birthYearMember1.setDataFileId(df1.getID());
		birthYearMember1.setTag("birth_year");
		birthYearMember1.setParent(member1.getID());
		birthYearMember1.setValue("1987");
		
		member1.addChild(birthYearMember1.getID());
		
		//death year
		Node deathYearMember1 = new Node();
		deathYearMember1.setDataFileId(df1.getID());
		deathYearMember1.setTag("death_year");
		deathYearMember1.setParent(member1.getID());
		deathYearMember1.setValue("");
		
		member1.addChild(deathYearMember1.getID());

		//marriage
		Node marriageMember1 = new Node();
		marriageMember1.setDataFileId(df1.getID());
		marriageMember1.setTag("marriage");
		marriageMember1.setParent(member1.getID());

		member1.addChild(marriageMember1.getID());
		
		//mariage year
		Node marriageYearMember1 = new Node();
		marriageYearMember1.setDataFileId(df1.getID());
		marriageYearMember1.setTag("marriage_year");
		marriageYearMember1.setParent(marriageMember1.getID());
		marriageYearMember1.setValue("");
		
		ArrayList<ObjectId> childrenMarriageMember1 = new ArrayList<ObjectId>();
		marriageMember1.setChildren(childrenMarriageMember1);
		marriageMember1.addChild(marriageYearMember1.getID());
		
		//partner name
		Node partnerNameMember1 = new Node();
		partnerNameMember1.setDataFileId(df1.getID());
		partnerNameMember1.setTag("marriage_partner");
		partnerNameMember1.setParent(marriageMember1.getID());
		partnerNameMember1.setValue("");
		
		marriageMember1.addChild(partnerNameMember1.getID());
		
		//ancestors
		Node ancestorsMember1 = new Node();
		ancestorsMember1.setDataFileId(df1.getID());
		ancestorsMember1.setTag("ancestors");
		ancestorsMember1.setParent(member1.getID());

		member1.addChild(ancestorsMember1.getID());
		
		//father
		Node fatherMember1 = new Node();
		fatherMember1.setDataFileId(df1.getID());
		fatherMember1.setTag("father");
		fatherMember1.setParent(ancestorsMember1.getID());
		fatherMember1.setValue("João Antero");
		
		ArrayList<ObjectId> childrenAncestorsMember1 = new ArrayList<ObjectId>();
		ancestorsMember1.setChildren(childrenAncestorsMember1);
		ancestorsMember1.addChild(fatherMember1.getID());
		
		//partner name
		Node motherMember1 = new Node();
		motherMember1.setDataFileId(df1.getID());
		motherMember1.setTag("mother");
		motherMember1.setParent(ancestorsMember1.getID());
		motherMember1.setValue("Maria Goretti Fernandes");
		
		ancestorsMember1.addChild(motherMember1.getID());
		
		//siblings
		Node siblingsMember1 = new Node();
		siblingsMember1.setDataFileId(df1.getID());
		siblingsMember1.setTag("siblings");
		siblingsMember1.setParent(member1.getID());

		member1.addChild(siblingsMember1.getID());
		
		//sister
		Node sisterMember1 = new Node();
		sisterMember1.setDataFileId(df1.getID());
		sisterMember1.setTag("sister");
		sisterMember1.setParent(siblingsMember1.getID());
		sisterMember1.setValue("Ana Rita Cardoso");
		
		ArrayList<ObjectId> childrenSiblingsMember1 = new ArrayList<ObjectId>();
		siblingsMember1.setChildren(childrenSiblingsMember1);
		siblingsMember1.addChild(sisterMember1.getID());
		
		//descendants
		Node descendantsMember1 = new Node();
		descendantsMember1.setDataFileId(df1.getID());
		descendantsMember1.setTag("descendants");
		descendantsMember1.setParent(member1.getID());

		member1.addChild(descendantsMember1.getID());
		
		//Member2
		Node member2 = new Node();
		member2.setDataFileId(df1.getID());
		member2.setTag("member");
		member2.setParent(family1.getID());

		//Name
		Node nameMember2 = new Node();
		nameMember2.setDataFileId(df1.getID());
		nameMember2.setTag("name");
		nameMember2.setParent(member2.getID());
		
		ArrayList<ObjectId> childrenMember2 = new ArrayList<ObjectId>();
		member2.setChildren(childrenMember2);
		member2.addChild(nameMember2.getID());
		
		//given
		Node givenMember2 = new Node();
		givenMember2.setDataFileId(df1.getID());
		givenMember2.setTag("given");
		givenMember2.setParent(nameMember2.getID());
		givenMember2.setValue("João Antero");
		
		ArrayList<ObjectId> childrenNameMember2 = new ArrayList<ObjectId>();
		nameMember2.setChildren(childrenNameMember2);
		nameMember2.addChild(givenMember2.getID());
		
		//surname
		Node surnameMember2 = new Node();
		surnameMember2.setDataFileId(df1.getID());
		surnameMember2.setTag("surname");
		surnameMember2.setParent(nameMember2.getID());
		surnameMember2.setValue("Cardoso");
		
		nameMember2.addChild(surnameMember2.getID());
		
		//nickname
		Node nicknameMember2 = new Node();
		nicknameMember2.setDataFileId(df1.getID());
		nicknameMember2.setTag("nickname");
		nicknameMember2.setParent(nameMember2.getID());
		nicknameMember2.setValue("Pai");
		
		nameMember2.addChild(nicknameMember2.getID());
		
		//sex
		Node sexMember2 = new Node();
		sexMember2.setDataFileId(df1.getID());
		sexMember2.setTag("sex");
		sexMember2.setParent(member2.getID());
		sexMember2.setValue("male");
		
		member2.addChild(sexMember2.getID());
		
		//birth year
		Node birthYearMember2 = new Node();
		birthYearMember2.setDataFileId(df1.getID());
		birthYearMember2.setTag("birth_year");
		birthYearMember2.setParent(member2.getID());
		birthYearMember2.setValue("1960");
		
		member2.addChild(birthYearMember2.getID());
		
		//death year
		Node deathYearMember2 = new Node();
		deathYearMember2.setDataFileId(df1.getID());
		deathYearMember2.setTag("death_year");
		deathYearMember2.setParent(member2.getID());
		deathYearMember2.setValue("");
		
		member2.addChild(deathYearMember2.getID());

		//marriage
		Node marriageMember2 = new Node();
		marriageMember2.setDataFileId(df1.getID());
		marriageMember2.setTag("marriage");
		marriageMember2.setParent(member2.getID());

		member2.addChild(marriageMember2.getID());
		
		//mariage year
		Node marriageYearMember2 = new Node();
		marriageYearMember2.setDataFileId(df1.getID());
		marriageYearMember2.setTag("marriage_year");
		marriageYearMember2.setParent(marriageMember2.getID());
		marriageYearMember2.setValue("1986");
		
		ArrayList<ObjectId> childrenMarriageMember2 = new ArrayList<ObjectId>();
		marriageMember2.setChildren(childrenMarriageMember2);
		marriageMember2.addChild(marriageYearMember2.getID());
		
		//partner name
		Node partnerNameMember2 = new Node();
		partnerNameMember2.setDataFileId(df1.getID());
		partnerNameMember2.setTag("marriage_partner");
		partnerNameMember2.setParent(marriageMember2.getID());
		partnerNameMember2.setValue("Maria Goretti Fernandes");
		
		marriageMember2.addChild(partnerNameMember2.getID());
		
		//ancestors
		Node ancestorsMember2 = new Node();
		ancestorsMember2.setDataFileId(df1.getID());
		ancestorsMember2.setTag("ancestors");
		ancestorsMember2.setParent(member2.getID());

		member2.addChild(ancestorsMember2.getID());
		
		//father
		Node fatherMember2 = new Node();
		fatherMember2.setDataFileId(df1.getID());
		fatherMember2.setTag("father");
		fatherMember2.setParent(ancestorsMember2.getID());
		fatherMember2.setValue("Antero Cardoso");
		
		ArrayList<ObjectId> childrenAncestorsMember2 = new ArrayList<ObjectId>();
		ancestorsMember2.setChildren(childrenAncestorsMember2);
		ancestorsMember2.addChild(fatherMember2.getID());
		
		//partner name
		Node motherMember2 = new Node();
		motherMember2.setDataFileId(df1.getID());
		motherMember2.setTag("mother");
		motherMember2.setParent(ancestorsMember2.getID());
		motherMember2.setValue("Maria Teresa Cardoso");
		
		ancestorsMember2.addChild(motherMember2.getID());
		
		//siblings
		Node siblingsMember2 = new Node();
		siblingsMember2.setDataFileId(df1.getID());
		siblingsMember2.setTag("siblings");
		siblingsMember2.setParent(member2.getID());

		member2.addChild(siblingsMember2.getID());
		
		//descendants
		Node descendantsMember2 = new Node();
		descendantsMember2.setDataFileId(df1.getID());
		descendantsMember2.setTag("descendants");
		descendantsMember2.setParent(member2.getID());

		member2.addChild(descendantsMember2.getID());
		
		//son
		Node sonMember2 = new Node();
		sonMember2.setDataFileId(df1.getID());
		sonMember2.setTag("son");
		sonMember2.setParent(descendantsMember2.getID());
		sonMember2.setValue("João Manuel Cardoso");
		
		ArrayList<ObjectId> childrenDescendantsMember2 = new ArrayList<ObjectId>();
		descendantsMember2.setChildren(childrenDescendantsMember2);
		descendantsMember2.addChild(sonMember2.getID());
		
		//daughter
		Node daughterMember2 = new Node();
		daughterMember2.setDataFileId(df1.getID());
		daughterMember2.setTag("daughter");
		daughterMember2.setParent(descendantsMember2.getID());
		daughterMember2.setValue("Ana Rita Cardoso");
		
		descendantsMember2.addChild(daughterMember2.getID());
		
		//Member3
		Node member3 = new Node();
		member3.setDataFileId(df1.getID());
		member3.setTag("member");
		member3.setParent(family1.getID());

		//Name
		Node nameMember3 = new Node();
		nameMember3.setDataFileId(df1.getID());
		nameMember3.setTag("name");
		nameMember3.setParent(member3.getID());
		
		ArrayList<ObjectId> childrenMember3 = new ArrayList<ObjectId>();
		member3.setChildren(childrenMember3);
		member3.addChild(nameMember3.getID());
		
		//given
		Node givenMember3 = new Node();
		givenMember3.setDataFileId(df1.getID());
		givenMember3.setTag("given");
		givenMember3.setParent(nameMember3.getID());
		givenMember3.setValue("Narua Goretti");
		
		ArrayList<ObjectId> childrenNameMember3 = new ArrayList<ObjectId>();
		nameMember3.setChildren(childrenNameMember3);
		nameMember3.addChild(givenMember3.getID());
		
		//surname
		Node surnameMember3 = new Node();
		surnameMember3.setDataFileId(df1.getID());
		surnameMember3.setTag("surname");
		surnameMember3.setParent(nameMember3.getID());
		surnameMember3.setValue("Fernandes");
		
		nameMember3.addChild(surnameMember3.getID());
		
		//nickname
		Node nicknameMember3 = new Node();
		nicknameMember3.setDataFileId(df1.getID());
		nicknameMember3.setTag("nickname");
		nicknameMember3.setParent(nameMember3.getID());
		nicknameMember3.setValue("Mãe");
		
		nameMember3.addChild(nicknameMember3.getID());
		
		//sex
		Node sexMember3 = new Node();
		sexMember3.setDataFileId(df1.getID());
		sexMember3.setTag("sex");
		sexMember3.setParent(member3.getID());
		sexMember3.setValue("female");
		
		member3.addChild(sexMember3.getID());
		
		//birth year
		Node birthYearMember3 = new Node();
		birthYearMember3.setDataFileId(df1.getID());
		birthYearMember3.setTag("birth_year");
		birthYearMember3.setParent(member3.getID());
		birthYearMember3.setValue("1960");
		
		member3.addChild(birthYearMember3.getID());
		
		//death year
		Node deathYearMember3 = new Node();
		deathYearMember3.setDataFileId(df1.getID());
		deathYearMember3.setTag("death_year");
		deathYearMember3.setParent(member3.getID());
		deathYearMember3.setValue("");
		
		member3.addChild(deathYearMember3.getID());

		//marriage
		Node marriageMember3 = new Node();
		marriageMember3.setDataFileId(df1.getID());
		marriageMember3.setTag("marriage");
		marriageMember3.setParent(member3.getID());

		member3.addChild(marriageMember3.getID());
		
		//mariage year
		Node marriageYearMember3 = new Node();
		marriageYearMember3.setDataFileId(df1.getID());
		marriageYearMember3.setTag("marriage_year");
		marriageYearMember3.setParent(marriageMember3.getID());
		marriageYearMember3.setValue("1986");
		
		ArrayList<ObjectId> childrenMarriageMember3 = new ArrayList<ObjectId>();
		marriageMember3.setChildren(childrenMarriageMember3);
		marriageMember3.addChild(marriageYearMember3.getID());
		
		//partner name
		Node partnerNameMember3 = new Node();
		partnerNameMember3.setDataFileId(df1.getID());
		partnerNameMember3.setTag("marriage_partner");
		partnerNameMember3.setParent(marriageMember3.getID());
		partnerNameMember3.setValue("João Antero Cardoso");
		
		marriageMember3.addChild(partnerNameMember3.getID());
		
		//ancestors
		Node ancestorsMember3 = new Node();
		ancestorsMember3.setDataFileId(df1.getID());
		ancestorsMember3.setTag("ancestors");
		ancestorsMember3.setParent(member3.getID());

		member3.addChild(ancestorsMember3.getID());
		
		//father
		Node fatherMember3 = new Node();
		fatherMember3.setDataFileId(df1.getID());
		fatherMember3.setTag("father");
		fatherMember3.setParent(ancestorsMember3.getID());
		fatherMember3.setValue("Manuel Álvaro Fernandes");
		
		ArrayList<ObjectId> childrenAncestorsMember3 = new ArrayList<ObjectId>();
		ancestorsMember3.setChildren(childrenAncestorsMember3);
		ancestorsMember3.addChild(fatherMember3.getID());
		
		//partner name
		Node motherMember3 = new Node();
		motherMember3.setDataFileId(df1.getID());
		motherMember3.setTag("mother");
		motherMember3.setParent(ancestorsMember3.getID());
		motherMember3.setValue("Maria Emídio Fernandes");
		
		ancestorsMember3.addChild(motherMember3.getID());
		
		//siblings
		Node siblingsMember3 = new Node();
		siblingsMember3.setDataFileId(df1.getID());
		siblingsMember3.setTag("siblings");
		siblingsMember3.setParent(member3.getID());

		member3.addChild(siblingsMember3.getID());
		
		//descendants
		Node descendantsMember3 = new Node();
		descendantsMember3.setDataFileId(df1.getID());
		descendantsMember3.setTag("descendants");
		descendantsMember3.setParent(member3.getID());

		member3.addChild(descendantsMember3.getID());
		
		//son
		Node sonMember3 = new Node();
		sonMember3.setDataFileId(df1.getID());
		sonMember3.setTag("son");
		sonMember3.setParent(descendantsMember3.getID());
		sonMember3.setValue("João Manuel Cardoso");
		
		ArrayList<ObjectId> childrenDescendantsMember3 = new ArrayList<ObjectId>();
		descendantsMember3.setChildren(childrenDescendantsMember3);
		descendantsMember3.addChild(sonMember3.getID());
		
		//daughter
		Node daughterMember3 = new Node();
		daughterMember3.setDataFileId(df1.getID());
		daughterMember3.setTag("daughter");
		daughterMember3.setParent(descendantsMember3.getID());
		daughterMember3.setValue("Ana Rita Cardoso");
		
		descendantsMember3.addChild(daughterMember3.getID());
		
		//Member4
		Node member4 = new Node();
		member4.setDataFileId(df1.getID());
		member4.setTag("member");
		member4.setParent(family1.getID());

		//Name
		Node nameMember4 = new Node();
		nameMember4.setDataFileId(df1.getID());
		nameMember4.setTag("name");
		nameMember4.setParent(member4.getID());
		
		ArrayList<ObjectId> childrenMember4 = new ArrayList<ObjectId>();
		member4.setChildren(childrenMember4);
		member4.addChild(nameMember4.getID());
		
		//given
		Node givenMember4 = new Node();
		givenMember4.setDataFileId(df1.getID());
		givenMember4.setTag("given");
		givenMember4.setParent(nameMember4.getID());
		givenMember4.setValue("Ana Rita");
		
		ArrayList<ObjectId> childrenNameMember4 = new ArrayList<ObjectId>();
		nameMember4.setChildren(childrenNameMember4);
		nameMember4.addChild(givenMember4.getID());
		
		//surname
		Node surnameMember4 = new Node();
		surnameMember4.setDataFileId(df1.getID());
		surnameMember4.setTag("surname");
		surnameMember4.setParent(nameMember4.getID());
		surnameMember4.setValue("Cardoso");
		
		nameMember4.addChild(surnameMember4.getID());
		
		//nickname
		Node nicknameMember4 = new Node();
		nicknameMember4.setDataFileId(df1.getID());
		nicknameMember4.setTag("nickname");
		nicknameMember4.setParent(nameMember4.getID());
		nicknameMember4.setValue("Rita");
		
		nameMember4.addChild(nicknameMember4.getID());
		
		//sex
		Node sexMember4 = new Node();
		sexMember4.setDataFileId(df1.getID());
		sexMember4.setTag("sex");
		sexMember4.setParent(member4.getID());
		sexMember4.setValue("female");
		
		member4.addChild(sexMember4.getID());
		
		//birth year
		Node birthYearMember4 = new Node();
		birthYearMember4.setDataFileId(df1.getID());
		birthYearMember4.setTag("birth_year");
		birthYearMember4.setParent(member4.getID());
		birthYearMember4.setValue("1991");
		
		member4.addChild(birthYearMember4.getID());
		
		//death year
		Node deathYearMember4 = new Node();
		deathYearMember4.setDataFileId(df1.getID());
		deathYearMember4.setTag("death_year");
		deathYearMember4.setParent(member4.getID());
		deathYearMember4.setValue("");
		
		member4.addChild(deathYearMember4.getID());

		//marriage
		Node marriageMember4 = new Node();
		marriageMember4.setDataFileId(df1.getID());
		marriageMember4.setTag("marriage");
		marriageMember4.setParent(member4.getID());

		member4.addChild(marriageMember4.getID());
		
		//mariage year
		Node marriageYearMember4 = new Node();
		marriageYearMember4.setDataFileId(df1.getID());
		marriageYearMember4.setTag("marriage_year");
		marriageYearMember4.setParent(marriageMember4.getID());
		marriageYearMember4.setValue("");
		
		ArrayList<ObjectId> childrenMarriageMember4 = new ArrayList<ObjectId>();
		marriageMember4.setChildren(childrenMarriageMember4);
		marriageMember4.addChild(marriageYearMember4.getID());
		
		//partner name
		Node partnerNameMember4 = new Node();
		partnerNameMember4.setDataFileId(df1.getID());
		partnerNameMember4.setTag("marriage_partner");
		partnerNameMember4.setParent(marriageMember4.getID());
		partnerNameMember4.setValue("");
		
		marriageMember4.addChild(partnerNameMember4.getID());
		
		//ancestors
		Node ancestorsMember4 = new Node();
		ancestorsMember4.setDataFileId(df1.getID());
		ancestorsMember4.setTag("ancestors");
		ancestorsMember4.setParent(member4.getID());

		member4.addChild(ancestorsMember4.getID());
		
		//father
		Node fatherMember4 = new Node();
		fatherMember4.setDataFileId(df1.getID());
		fatherMember4.setTag("father");
		fatherMember4.setParent(ancestorsMember4.getID());
		fatherMember4.setValue("João Antero");
		
		ArrayList<ObjectId> childrenAncestorsMember4 = new ArrayList<ObjectId>();
		ancestorsMember4.setChildren(childrenAncestorsMember4);
		ancestorsMember4.addChild(fatherMember4.getID());
		
		//partner name
		Node motherMember4 = new Node();
		motherMember4.setDataFileId(df1.getID());
		motherMember4.setTag("mother");
		motherMember4.setParent(ancestorsMember4.getID());
		motherMember4.setValue("Maria Goretti Fernandes");
		
		ancestorsMember4.addChild(motherMember4.getID());
		
		//siblings
		Node siblingsMember4 = new Node();
		siblingsMember4.setDataFileId(df1.getID());
		siblingsMember4.setTag("siblings");
		siblingsMember4.setParent(member4.getID());

		member4.addChild(siblingsMember4.getID());
		
		//brother
		Node brotherMember4 = new Node();
		brotherMember4.setDataFileId(df1.getID());
		brotherMember4.setTag("brother");
		brotherMember4.setParent(siblingsMember4.getID());
		brotherMember4.setValue("João Manuel Cardoso");
		
		ArrayList<ObjectId> childrenSiblingsMember4 = new ArrayList<ObjectId>();
		siblingsMember4.setChildren(childrenSiblingsMember4);
		siblingsMember4.addChild(brotherMember4.getID());
		
		//descendants
		Node descendantsMember4 = new Node();
		descendantsMember4.setDataFileId(df1.getID());
		descendantsMember4.setTag("descendants");
		descendantsMember4.setParent(member4.getID());

		member4.addChild(descendantsMember4.getID());
		
		NodeImpl nimpl = new NodeImpl();
		nimpl.save(family1);
		nimpl.save(member1);
		nimpl.save(nameMember1);
		nimpl.save(givenMember1);
		nimpl.save(surnameMember1);
		nimpl.save(nicknameMember1);
		nimpl.save(sexMember1);
		nimpl.save(birthYearMember1);
		nimpl.save(deathYearMember1);
		nimpl.save(marriageMember1);
		nimpl.save(marriageYearMember1);
		nimpl.save(partnerNameMember1);
		nimpl.save(ancestorsMember1);
		nimpl.save(fatherMember1);
		nimpl.save(motherMember1);
		nimpl.save(siblingsMember1);
		nimpl.save(sisterMember1);
		nimpl.save(descendantsMember1);
		nimpl.save(member2);
		nimpl.save(nameMember2);
		nimpl.save(givenMember2);
		nimpl.save(surnameMember2);
		nimpl.save(nicknameMember2);
		nimpl.save(sexMember2);
		nimpl.save(birthYearMember2);
		nimpl.save(deathYearMember2);
		nimpl.save(marriageMember2);
		nimpl.save(marriageYearMember2);
		nimpl.save(partnerNameMember2);
		nimpl.save(ancestorsMember2);
		nimpl.save(fatherMember2);
		nimpl.save(motherMember2);
		nimpl.save(siblingsMember2);
		nimpl.save(descendantsMember2);
		nimpl.save(sonMember2);
		nimpl.save(daughterMember2);
		nimpl.save(member3);
		nimpl.save(nameMember3);
		nimpl.save(givenMember3);
		nimpl.save(surnameMember3);
		nimpl.save(nicknameMember3);
		nimpl.save(sexMember3);
		nimpl.save(birthYearMember3);
		nimpl.save(deathYearMember3);
		nimpl.save(marriageMember3);
		nimpl.save(marriageYearMember3);
		nimpl.save(partnerNameMember3);
		nimpl.save(ancestorsMember3);
		nimpl.save(fatherMember3);
		nimpl.save(motherMember3);
		nimpl.save(siblingsMember3);
		nimpl.save(descendantsMember3);
		nimpl.save(sonMember3);
		nimpl.save(daughterMember3);
		nimpl.save(member4);
		nimpl.save(nameMember4);
		nimpl.save(givenMember4);
		nimpl.save(surnameMember4);
		nimpl.save(nicknameMember4);
		nimpl.save(sexMember4);
		nimpl.save(birthYearMember4);
		nimpl.save(deathYearMember4);
		nimpl.save(marriageMember4);
		nimpl.save(marriageYearMember4);
		nimpl.save(partnerNameMember4);
		nimpl.save(ancestorsMember4);
		nimpl.save(fatherMember4);
		nimpl.save(motherMember4);
		nimpl.save(siblingsMember4);
		nimpl.save(brotherMember4);
		nimpl.save(descendantsMember4);

		/* DATAFILE CONT. */

		df1.setNodeID(family1.getID());

		DataFileImpl dfimpl = new DataFileImpl();
		dfimpl.save(df1);
		

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
		
		/* Population Operations */
		
		PopulationOperations pop = new PopulationOperations(b);
		pop.processBatch();
		
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

