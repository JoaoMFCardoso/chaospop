**Add Ontology From Namespace**
----
Creates an OntologyFile object in the database, by loading an Ontlogy through a given namespace.

* **URL**

  /ontologyManager/addOntologyNamespace

* **Method:**
  
 `POST`

* **Data Params**

  * @FormParam("namespace") String namespace

* **Success Response:**
  
 * **Code:** 200 OK<br />
    **Content:** `"5b1b07d1b94ec1f079bc6299"`
 
* **Error Response:**

  * **Code:** 500 INTERNAL SERVER ERROR<br />
    **Content:** `{ "status": 500, message : "An internal error has taken place. Please contact the ChaosPop administrator." }`

  * **Code:** 400 BAD REQUEST<br />
    **Content:** `{ "status": 400, message : "The Ontology namespace has an illegal argument." }`
    
   * **Code:** 400 BAD REQUEST<br />
    **Content:** `{ "status": 400, message : "An Ontology namespace must be provided." }`

**List Ontology Files**
----
Gets all the OntologyFiles stored in the database

* **URL**

  /ontologyManager/listOntologyFiles

* **Method:**
  
 `GET`

* **Success Response:**
  
 * **Code:** 200 OK<br />
    **Content:** `{
    "ontologyFilesTO": [
        {
            "_id": "5b0d68d914b4e72380fa1502",
            "namespace": "http://sysresearch.org/ontologies/family.owl",
            "path": "D:\\GitRepo\\chaospop\\chaos\\src\\main\\resources\\localOnts\\family.owl"
        },
        {
            "_id": "5b0d6a2c14b4dfaa252fbb2c",
            "namespace": "http://sysresearch.org/ontologies/cardosofamily.owl#",
            "path": "D:\\GitRepo\\chaospop\\chaos\\src\\main\\resources\\localOnts\\cardosofamily.owl"
        }
    ]
}`
 
* **Error Response:**

  * **Code:** 500 INTERNAL SERVER ERROR<br />
    **Content:** `{ "status": 500, message : "An internal error has taken place. Please contact the ChaosPop administrator." }`
    
**Get Ontology File**
----
Gets a OntologyFile object when given its id

* **URL**

  /ontologyManager/getOntologyFile

* **Method:**
  
 `POST`

* **Data Params**

  * @FormParam("id") String ontologyFileId

* **Success Response:**
  
 * **Code:** 200 OK<br />
    **Content:** `{
    "_id": "5b0d68d914b4e72380fa1502",
    "namespace": "http://sysresearch.org/ontologies/family.owl",
    "path": "D:\\GitRepo\\chaospop\\chaos\\src\\main\\resources\\localOnts\\family.owl"
}`
 
* **Error Response:**

  * **Code:** 500 INTERNAL SERVER ERROR<br />
    **Content:** `{ "status": 500, message : "An internal error has taken place. Please contact the ChaosPop administrator." }`
  
  * **Code:** 400 BAD REQUEST<br />
    **Content:** `{ "status": 400, message : "An Ontology File id must be provided." }`
    
  * **Code:** 400 BAD REQUEST<br />
    **Content:** `{ "status": 400, message : "The Ontology File id has an illegal argument." }`

**Remove Ontology Files**
----
Removes a list of OntologyFile objects from the database

* **URL**

  /ontologyManager/removeOntologyFiles

* **Method:**
  
 `POST`

* **Data Params**

  * `["5b1268c214b49bd38a84e455","5b1a958c32fa83d5ea219b11"]`

* **Success Response:**
  
 * **Code:** 200 OK <br />
    **Content:** `{
    "status": 200,
    "message": "The OntologyFile(s) [\"5b1268c214b49bd38a84e455\",\"5b1a958c32fa83d5ea219b11\"] has(have) been removed."
}`
 
* **Error Response:**

  * **Code:** 500 INTERNAL SERVER ERROR<br />
    **Content:** `{ "status": 500, message : "An internal error has taken place. Please contact the ChaosPop administrator." }`
    
  * **Code:** 400 BAD REQUEST<br />
    **Content:** `{ "status": 400, message : "An Ontology File id must be provided." }`
  
  * **Code:** 400 BAD REQUEST<br />
    **Content:** `{ "status": 400, message : "The Ontology File id has an illegal argument." }`

**Get SFTP Default Namespace**
----
Gets the SFTP Default Namespace.

* **URL**

  /ontologyManager/getSFTPDefaultNamespace

* **Method:**
  
 `GET`

* **Data Params**

  * @FormParam("mappingId") String mappingId
  * @FormParam("batchId") String batchId

* **Success Response:**
  
 * **Code:** 200 OK <br />
    **Content:** `"http://chaospop.sysresearch.org/ontologies/"`
 
* **Error Response:**

  * **Code:** 500 INTERNAL SERVER ERROR<br />
    **Content:** `{ "status": 500, message : "An internal error has taken place. Please contact the ChaosPop administrator." }`

**Get OWL Classes**
----
Gets all the OWL Classes for a given Ontology

* **URL**

  /ontologyManager/getOWLClasses

* **Method:**
  
 `POST`

* **Data Params**

  * @FormParam("ontologyId") String ontologyFileId

* **Success Response:**
  
 * **Code:** 200 OK <br />
    **Content:** `[
    "http://chaospop.sysresearch.org/ontologies/family.owl#Son",
    "http://chaospop.sysresearch.org/ontologies/family.owl#Descendant",
    "http://chaospop.sysresearch.org/ontologies/family.owl#SonInLaw",
    "http://chaospop.sysresearch.org/ontologies/family.owl#Forefather",
    "http://chaospop.sysresearch.org/ontologies/family.owl#Uncle",
    "http://chaospop.sysresearch.org/ontologies/family.owl#Husband",
    "http://chaospop.sysresearch.org/ontologies/family.owl#GreatUncle",
    "http://chaospop.sysresearch.org/ontologies/family.owl#MaleDescendant",
    "http://chaospop.sysresearch.org/ontologies/family.owl#Sex",
    "http://chaospop.sysresearch.org/ontologies/family.owl#Brother",
    "http://chaospop.sysresearch.org/ontologies/family.owl#GreatGreatGrandparent",
    "http://chaospop.sysresearch.org/ontologies/family.owl#Cousin",
    "http://chaospop.sysresearch.org/ontologies/family.owl#Marriage",
    "http://chaospop.sysresearch.org/ontologies/family.owl#Grandparent",
    "http://chaospop.sysresearch.org/ontologies/family.owl#SisterInLaw",
    "http://chaospop.sysresearch.org/ontologies/family.owl#UncleInLaw",
    "http://chaospop.sysresearch.org/ontologies/family.owl#BrotherInLaw",
    "http://chaospop.sysresearch.org/ontologies/family.owl#Sister",
    "http://chaospop.sysresearch.org/ontologies/family.owl#ThirdCousin",
    "http://chaospop.sysresearch.org/ontologies/family.owl#SecondCousin",
    "http://chaospop.sysresearch.org/ontologies/family.owl#FirstCousin",
    "http://chaospop.sysresearch.org/ontologies/family.owl#Female",
    "http://chaospop.sysresearch.org/ontologies/family.owl#AuntInLaw",
    "http://chaospop.sysresearch.org/ontologies/family.owl#Grandfather",
    "http://chaospop.sysresearch.org/ontologies/family.owl#Foremother",
    "http://chaospop.sysresearch.org/ontologies/family.owl#GreatAunt",
    "http://chaospop.sysresearch.org/ontologies/family.owl#Male",
    "http://chaospop.sysresearch.org/ontologies/family.owl#Person",
    "http://chaospop.sysresearch.org/ontologies/family.owl#Aunt",
    "http://chaospop.sysresearch.org/ontologies/family.owl#BloodRelation",
    "http://chaospop.sysresearch.org/ontologies/family.owl#Mother",
    "http://chaospop.sysresearch.org/ontologies/family.owl#ParentInLaw",
    "http://chaospop.sysresearch.org/ontologies/family.owl#SiblingInLaw",
    "http://chaospop.sysresearch.org/ontologies/family.owl#Sibling",
    "http://chaospop.sysresearch.org/ontologies/family.owl#Wife",
    "http://chaospop.sysresearch.org/ontologies/family.owl#InLaw",
    "http://chaospop.sysresearch.org/ontologies/family.owl#Spouse",
    "http://chaospop.sysresearch.org/ontologies/family.owl#MotherInLaw",
    "http://chaospop.sysresearch.org/ontologies/family.owl#AncestorOfRobert",
    "http://chaospop.sysresearch.org/ontologies/family.owl#GreatGrandfather",
    "http://chaospop.sysresearch.org/ontologies/family.owl#DaughterInLaw",
    "http://chaospop.sysresearch.org/ontologies/family.owl#Parent",
    "http://chaospop.sysresearch.org/ontologies/family.owl#Daughter",
    "http://chaospop.sysresearch.org/ontologies/family.owl#FemaleDescendant",
    "http://chaospop.sysresearch.org/ontologies/family.owl#DomainEntity",
    "http://chaospop.sysresearch.org/ontologies/family.owl#MaleAncestor",
    "http://chaospop.sysresearch.org/ontologies/family.owl#Grandmother",
    "http://chaospop.sysresearch.org/ontologies/family.owl#GreatGrandparent",
    "http://chaospop.sysresearch.org/ontologies/family.owl#Ancestor",
    "http://chaospop.sysresearch.org/ontologies/family.owl#Father",
    "http://chaospop.sysresearch.org/ontologies/family.owl#FatherInLaw",
    "http://chaospop.sysresearch.org/ontologies/family.owl#Man",
    "http://chaospop.sysresearch.org/ontologies/family.owl#SecondCousinOfRobert",
    "http://chaospop.sysresearch.org/ontologies/family.owl#FemaleAncestor",
    "http://chaospop.sysresearch.org/ontologies/family.owl#Woman"
]`
 
* **Error Response:**

  * **Code:** 500 INTERNAL SERVER ERROR<br />
    **Content:** `{ "status": 500, message : "An internal error has taken place. Please contact the ChaosPop administrator." }`
    
  * **Code:** 400 BAD REQUEST<br />
    **Content:** `{ "status": 400, message : "An Ontology File id must be provided." }`
  
  * **Code:** 400 BAD REQUEST<br />
    **Content:** `{ "status": 400, message : "The Ontology File id has an illegal argument." }`
    
**Get Object Properties**
----
Removes a Mapping from a Batch

* **URL**

  /ontologyManager/getObjectProperties

* **Method:**
  
 `POST`

* **Data Params**

  * @FormParam("ontologyId") String ontologyFileId

* **Success Response:**
  
 * **Code:** 200 OK <br />
    **Content:** `[
    "http://chaospop.sysresearch.org/ontologies/family.owl#hasGreatGrandmother",
    "http://chaospop.sysresearch.org/ontologies/family.owl#isInLawOf",
    "http://chaospop.sysresearch.org/ontologies/family.owl#hasMalePartner",
    "http://chaospop.sysresearch.org/ontologies/family.owl#hasFatherInLaw",
    "http://chaospop.sysresearch.org/ontologies/family.owl#isPartnerIn",
    "http://chaospop.sysresearch.org/ontologies/family.owl#isFemalePartnerIn",
    "http://chaospop.sysresearch.org/ontologies/family.owl#isWifeOf",
    "http://chaospop.sysresearch.org/ontologies/family.owl#isSisterOf",
    "http://chaospop.sysresearch.org/ontologies/family.owl#hasParent",
    "http://chaospop.sysresearch.org/ontologies/family.owl#hasUncleInLaw",
    "http://chaospop.sysresearch.org/ontologies/family.owl#hasHusband",
    "http://chaospop.sysresearch.org/ontologies/family.owl#isDaughterOf",
    "http://chaospop.sysresearch.org/ontologies/family.owl#hasSex",
    "http://chaospop.sysresearch.org/ontologies/family.owl#hasGrandParent",
    "http://chaospop.sysresearch.org/ontologies/family.owl#isFirstCousinOf",
    "http://chaospop.sysresearch.org/ontologies/family.owl#hasForeFather",
    "http://chaospop.sysresearch.org/ontologies/family.owl#isForemotherOf",
    "http://chaospop.sysresearch.org/ontologies/family.owl#isUncleOf",
    "http://chaospop.sysresearch.org/ontologies/family.owl#isGreatGrandParentOf",
    "http://chaospop.sysresearch.org/ontologies/family.owl#isSisterInLawOf",
    "http://chaospop.sysresearch.org/ontologies/family.owl#isThirdCousinOf",
    "http://chaospop.sysresearch.org/ontologies/family.owl#hasFather",
    "http://chaospop.sysresearch.org/ontologies/family.owl#hasDaughter",
    "http://chaospop.sysresearch.org/ontologies/family.owl#hasBrother",
    "http://chaospop.sysresearch.org/ontologies/family.owl#hasGreatAunt",
    "http://chaospop.sysresearch.org/ontologies/family.owl#hasFemalePartner",
    "http://chaospop.sysresearch.org/ontologies/family.owl#isGrandmotherOf",
    "http://chaospop.sysresearch.org/ontologies/family.owl#hasMotherInLaw",
    "http://chaospop.sysresearch.org/ontologies/family.owl#hasAunt",
    "http://chaospop.sysresearch.org/ontologies/family.owl#hasAuntInLaw",
    "http://chaospop.sysresearch.org/ontologies/family.owl#isBrotherInLawOf",
    "http://chaospop.sysresearch.org/ontologies/family.owl#isGreatGrandfatherOf",
    "http://chaospop.sysresearch.org/ontologies/family.owl#isUncleInLawOf",
    "http://chaospop.sysresearch.org/ontologies/family.owl#isAncestorOf",
    "http://chaospop.sysresearch.org/ontologies/family.owl#isForefatherOf",
    "http://chaospop.sysresearch.org/ontologies/family.owl#hasPartner",
    "http://chaospop.sysresearch.org/ontologies/family.owl#isGreatUncleOf",
    "http://chaospop.sysresearch.org/ontologies/family.owl#hasParentInLaw",
    "http://chaospop.sysresearch.org/ontologies/family.owl#hasForeMother",
    "http://chaospop.sysresearch.org/ontologies/family.owl#hasGrandfather",
    "http://chaospop.sysresearch.org/ontologies/family.owl#isParentOf",
    "http://chaospop.sysresearch.org/ontologies/family.owl#isFirstCousinOnceRemovedOf",
    "http://chaospop.sysresearch.org/ontologies/family.owl#hasMother",
    "http://chaospop.sysresearch.org/ontologies/family.owl#hasSister",
    "http://chaospop.sysresearch.org/ontologies/family.owl#isChildOf",
    "http://chaospop.sysresearch.org/ontologies/family.owl#isGreatAuntOf",
    "http://chaospop.sysresearch.org/ontologies/family.owl#hasGreatGrandParent",
    "http://chaospop.sysresearch.org/ontologies/family.owl#hasUncle",
    "http://chaospop.sysresearch.org/ontologies/family.owl#isFatherOf",
    "http://chaospop.sysresearch.org/ontologies/family.owl#isSiblingInLawOf",
    "http://chaospop.sysresearch.org/ontologies/family.owl#isMalePartnerIn",
    "http://chaospop.sysresearch.org/ontologies/family.owl#isMotherOf",
    "http://chaospop.sysresearch.org/ontologies/family.owl#isGrandParentOf",
    "http://chaospop.sysresearch.org/ontologies/family.owl#isSecondCousinOf",
    "http://chaospop.sysresearch.org/ontologies/family.owl#hasAncestor",
    "http://chaospop.sysresearch.org/ontologies/family.owl#isSpouseOf",
    "http://chaospop.sysresearch.org/ontologies/family.owl#isBloodRelationOf",
    "http://chaospop.sysresearch.org/ontologies/family.owl#isSonOf",
    "http://chaospop.sysresearch.org/ontologies/family.owl#hasChild",
    "http://chaospop.sysresearch.org/ontologies/family.owl#hasWife",
    "http://chaospop.sysresearch.org/ontologies/family.owl#hasGreatGrandfather",
    "http://chaospop.sysresearch.org/ontologies/family.owl#isRelationOf",
    "http://chaospop.sysresearch.org/ontologies/family.owl#isSiblingOf",
    "http://chaospop.sysresearch.org/ontologies/family.owl#isHusbandOf",
    "http://chaospop.sysresearch.org/ontologies/family.owl#hasGrandmother",
    "http://chaospop.sysresearch.org/ontologies/family.owl#isNieceOf",
    "http://chaospop.sysresearch.org/ontologies/family.owl#isMotherInLawOf",
    "http://chaospop.sysresearch.org/ontologies/family.owl#isAuntOf",
    "http://chaospop.sysresearch.org/ontologies/family.owl#hasGreatUncle",
    "http://chaospop.sysresearch.org/ontologies/family.owl#hasSisterInLaw",
    "http://chaospop.sysresearch.org/ontologies/family.owl#isBrotherOf",
    "http://chaospop.sysresearch.org/ontologies/family.owl#isFatherInLawOf",
    "http://chaospop.sysresearch.org/ontologies/family.owl#hasBrotherInLaw",
    "http://chaospop.sysresearch.org/ontologies/family.owl#isGrandfatherOf",
    "http://chaospop.sysresearch.org/ontologies/family.owl#isGreatGrandmotherOf",
    "http://chaospop.sysresearch.org/ontologies/family.owl#isParentInLawOf",
    "http://chaospop.sysresearch.org/ontologies/family.owl#isNephewOf",
    "http://chaospop.sysresearch.org/ontologies/family.owl#isDirectSiblingOf",
    "http://chaospop.sysresearch.org/ontologies/family.owl#hasSon",
    "http://chaospop.sysresearch.org/ontologies/family.owl#isAuntInLawOf"
]`
 
* **Error Response:**

  * **Code:** 500 INTERNAL SERVER ERROR<br />
    **Content:** `{ "status": 500, message : "An internal error has taken place. Please contact the ChaosPop administrator." }`
    
  * **Code:** 400 BAD REQUEST<br />
    **Content:** `{ "status": 400, message : "An Ontology File id must be provided." }`
  
  * **Code:** 400 BAD REQUEST<br />
    **Content:** `{ "status": 400, message : "The Ontology File id has an illegal argument." }`
    
 **Get Object Properties For Class**
----
Gets the object properties necessary to a given owl class

* **URL**

  /ontologyManager/getObjectPropertiesForClass

* **Method:**
  
 `POST`

* **Data Params**

  * @FormParam("ontologyId") String ontologyFileId
  * @FormParam("owlClass") String owlClass

* **Success Response:**
  
 * **Code:** 200 OK <br />
    **Content:** `[
    "http://chaospop.sysresearch.org/ontologies/family.owl#hasGreatGrandmother",
    "http://chaospop.sysresearch.org/ontologies/family.owl#isInLawOf",
    "http://chaospop.sysresearch.org/ontologies/family.owl#hasMalePartner"
]`
 
* **Error Response:**

  * **Code:** 500 INTERNAL SERVER ERROR<br />
    **Content:** `{ "status": 500, message : "An internal error has taken place. Please contact the ChaosPop administrator." }`
    
  * **Code:** 400 BAD REQUEST<br />
    **Content:** `{ "status": 400, message : "Both an Ontology File id, and an OWL Class IRI must be provided." }`
  
  * **Code:** 400 BAD REQUEST<br />
    **Content:** `{ "status": 400, message : "Either the Ontology File ID or the OWL Class IRI have an illegal argument." }`
    
**Get Data Properties**
----
Removes a Mapping from a Batch

* **URL**

  /ontologyManager/getDataProperties

* **Method:**
  
 `POST`

* **Data Params**

  * @FormParam("ontologyId") String ontologyFileId

* **Success Response:**
  
 * **Code:** 200 OK <br />
    **Content:** `[
    "http://chaospop.sysresearch.org/ontologies/family.owl#hasMarriageYear",
    "http://chaospop.sysresearch.org/ontologies/family.owl#hasFamilyName",
    "http://chaospop.sysresearch.org/ontologies/family.owl#alsoKnownAs",
    "http://chaospop.sysresearch.org/ontologies/family.owl#formerlyKnownAs",
    "http://chaospop.sysresearch.org/ontologies/family.owl#hasBirthYear",
    "http://chaospop.sysresearch.org/ontologies/family.owl#hasDeathYear",
    "http://chaospop.sysresearch.org/ontologies/family.owl#hasFirstGivenName",
    "http://chaospop.sysresearch.org/ontologies/family.owl#knownAs",
    "http://chaospop.sysresearch.org/ontologies/family.owl#hasName"
]`
 
* **Error Response:**

  * **Code:** 500 INTERNAL SERVER ERROR<br />
    **Content:** `{ "status": 500, message : "An internal error has taken place. Please contact the ChaosPop administrator." }`
    
  * **Code:** 400 BAD REQUEST<br />
    **Content:** `{ "status": 400, message : "An Ontology File id must be provided." }`
  
  * **Code:** 400 BAD REQUEST<br />
    **Content:** `{ "status": 400, message : "The Ontology File id has an illegal argument." }`
    
 **Get Data Properties For Class**
----
Gets the data properties necessary to a given owl class

* **URL**

  /ontologyManager/getDataPropertiesForClass

* **Method:**
  
 `POST`

* **Data Params**

  * @FormParam("ontologyId") String ontologyFileId
  * @FormParam("owlClass") String owlClass

* **Success Response:**
  
 * **Code:** 200 OK <br />
    **Content:** `[
    "http://chaospop.sysresearch.org/ontologies/family.owl#hasMarriageYear",
    "http://chaospop.sysresearch.org/ontologies/family.owl#hasFamilyName" 
]`
 
* **Error Response:**

  * **Code:** 500 INTERNAL SERVER ERROR<br />
    **Content:** `{ "status": 500, message : "An internal error has taken place. Please contact the ChaosPop administrator." }`
    
  * **Code:** 400 BAD REQUEST<br />
    **Content:** `{ "status": 400, message : "Both an Ontology File id, and an OWL Class IRI must be provided." }`
  
  * **Code:** 400 BAD REQUEST<br />
    **Content:** `{ "status": 400, message : "Either the Ontology File ID or the OWL Class IRI have an illegal argument." }`
