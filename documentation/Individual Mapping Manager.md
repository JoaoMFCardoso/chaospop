**Create Individual Mapping**
----
Creates a new Individual Mapping object in the database based on the transfer object created in the client application.

* **URL**

  /individualMappingManager/createIndividualMapping

* **Method:**
  
 `POST`

* **Data Params**

  * `{
    "tag" : "member",
    "dataFileIds" : ["5af093d445193e040736e20f"],
    "individualName" : ".inspecificchild-name-given;.inspecificchild-name-surname",
    "owlClassIRI" : "http://chaospop.sysresearch.org/ontologies/family#Person",
    "specification" : false,
    "annotationProperties" : {
        "label" : ".inspecificchild-name-nickname"
    },
    "objectProperties" : {
        "http://chaospop.sysresearch.org/ontologies/family.owl#hasBrother" : ".inspecificchild-siblings-brother"
    },
    "dataProperties" : {
        "http://chaospop.sysresearch.org/ontologies/family.owl#hasDeathYear" : [".inspecificchild-marriage-marriage_year", "Integer"]
    }
}`

* **Success Response:**
  
 * **Code:** 200 OK<br />
    **Content:** `{"id": "5b1b07d1b94ec1f079bc6299"}`
 
* **Error Response:**

  * **Code:** 500 INTERNAL SERVER ERROR<br />
    **Content:** `{ "status": 500, message : "An internal error has taken place. Please contact the ChaosPop administrator." }`

  * **Code:** 400 BAD REQUEST<br />
    **Content:** `{ "status": 400, message : "One of the Individual Mapping required fields is null. All required fields must be filled." }`

**Get Individual Mapping**
----
gets a Individual Mapping object when given its id.

* **URL**

  /individualMappingManager/getIndividualMapping

* **Method:**
  
 `POST`

* **Data Params**

  * @FormParam("id") individualMappingID

* **Success Response:**
  
 * **Code:** 204 NO CONTENT <br />
    **Content:** `{
    "_id": "5b1268c214b49bd38a84e453",
    "tag": "member",
    "individualName": ".inspecificchild-name-given;.inspecificchild-name-surname",
    "owlClassIRI": "http://chaospop.sysresearch.org/ontologies/family#Person",
    "annotationProperties": {
        "comment": ".inspecificchild-name-given;.inspecificchild-name-surname;.inspecificchild-name-nickname",
        "label": ".inspecificchild-name-nickname",
        "seeAlso": ".inspecificchild-siblings-sister"
    },
    "objectProperties": {
        "http://chaospop.sysresearch.org/ontologies/family.owl#hasBrother": ".inspecificchild-siblings-brother"
    },
    "dataProperties": {
        "http://chaospop.sysresearch.org/ontologies/family.owl#hasFamilyName": [
            ".inspecificchild-name-surname",
            "String"
        ]
    }
}`
 
* **Error Response:**

  * **Code:** 500 INTERNAL SERVER ERROR<br />
    **Content:** `{ "status": 500, message : "An internal error has taken place. Please contact the ChaosPop administrator." }`

  * **Code:** 404 NOT FOUND<br />
    **Content:** `{ "status": 404, message : "The Individual Mapping id provided is not associated with any Individual Mapping in the database." }`
    
  * **Code:** 400 BAD REQUEST<br />
    **Content:** `{ "status": 404, message : "An Individual Mapping id must be provided." }`
   
  * **Code:** 400 BAD REQUEST<br />
    **Content:** `{ "status": 400, message : "The Individual Mapping id has an illegal argument." }`
    
**Replace Individual Mapping**
----
Replaces as existing IndividualMapping in the Database

* **URL**

  /individualMappingManager/replaceIndividualMapping

* **Method:**
  
 `POST`

* **Data Params**

  * `{
    "_id" : "5b1fb541b94ee0828849c4a1",
    "tag" : "replacedTag",
    "dataFileIds" : ["5af093d445193e040736e20f"],
    "individualName" : ".inspecificchild-name-given;.inspecificchild-name-surname",
    "owlClassIRI" : "http://chaospop.sysresearch.org/ontologies/family#ReplacedClass",
    "specification" : false,
    "annotationProperties" : {
        "label" : ".inspecificchild-name-nickname"
    },
    "objectProperties" : {
        "http://sysresearch.org/ontologies/family.owl#hasBrother" : ".inspecificchild-siblings-brother"
    },
    "dataProperties" : {
        "http://sysresearch.org/ontologies/family.owl#hasAge" : [".inspecificchild-age", "Integer"]
    }
}`

* **Success Response:**
  
 * **Code:** 200 OK<br />
    **Content:** `{"status": 200, "message": "The Individual Mapping(s) 5b1fb541b94ee0828849c4a1 has been successfully replaced."}`
 
* **Error Response:**

  * **Code:** 500 INTERNAL SERVER ERROR<br />
    **Content:** `{ "status": 500, message : "An internal error has taken place. Please contact the ChaosPop administrator." }`

  * **Code:** 400 BAD REQUEST<br />
    **Content:** `{ "status": 404, message : "An Individual Mapping id must be provided." }`
   
  * **Code:** 400 BAD REQUEST<br />
    **Content:** `{ "status": 400, message : "The Individual Mapping id has an illegal argument." }`

**Remove Individual Mapping**
----
Removes a list of Individual Mapping objects from the database

* **URL**

  /individualMappingManager/removeIndividualMapping

* **Method:**
  
 `POST`

* **Data Params**

  * `["5b1268c214b49bd38a84e455","5b1a958c32fa83d5ea219b11"]`

* **Success Response:**
  
 * **Code:** 200 OK <br />
    **Content:** `{
    "status": 200,
    "message": "The Individual Mapping(s) [\"5b1268c214b49bd38a84e455\",\"5b1a958c32fa83d5ea219b11\"] has(have) been removed."
}`
 
* **Error Response:**

  * **Code:** 500 INTERNAL SERVER ERROR<br />
    **Content:** `{ "status": 500, message : "An internal error has taken place. Please contact the ChaosPop administrator." }`
    
  * **Code:** 400 BAD REQUEST<br />
    **Content:** `{ "status": 400, message : "An Individual Mapping id must be provided." }`
  
  * **Code:** 400 BAD REQUEST<br />
    **Content:** `{ "status": 400, message : "The Individual Mapping id has an illegal argument." }`

**Get All Individual Mappings**
----
Adds a Mapping to a Batch

* **URL**

  /individualMappingManager/getAllIndividualMappings

* **Method:**
  
 `GET`

* **Success Response:**
  
 * **Code:** 200 OK <br />
    **Content:** `{
    "individualMappingsTO": [
        {
            "_id": "5b1268c214b49bd38a84e453",
            "tag": "member",
            "individualName": ".inspecificchild-name-given;.inspecificchild-name-surname",
            "owlClassIRI": "http://chaospop.sysresearch.org/ontologies/family#Person",
            "annotationProperties": {
                "comment": ".inspecificchild-name-given;.inspecificchild-name-surname;.inspecificchild-name-nickname",
                "label": ".inspecificchild-name-nickname",
                "seeAlso": ".inspecificchild-siblings-sister"
            },
            "objectProperties": {
                "http://chaospop.sysresearch.org/ontologies/family.owl#hasBrother": ".inspecificchild-siblings-brother"
            },
            "dataProperties": {
                "http://chaospop.sysresearch.org/ontologies/family.owl#hasFamilyName": [
                    ".inspecificchild-name-surname",
                    "String"
                ]
            }
        }
    ]
}`
 
* **Error Response:**

  * **Code:** 500 INTERNAL SERVER ERROR<br />
    **Content:** `{ "status": 500, message : "An internal error has taken place. Please contact the ChaosPop administrator." }`

**Compare Individual Mappings**
----
Gets all the Mappings from a Batch

* **URL**

  /individualMappingManager/compareIndividualMappings

* **Method:**
  
 `POST`

* **Data Params**

  * @HeaderParam("id") individualMappingID
  * `{
    "tag" : "member",
    "dataFileIds" : ["5af093d445193e040736e20f"],
    "individualName" : ".inspecificchild-name-given;.inspecificchild-name-surname",
    "owlClassIRI" : "http://chaospop.sysresearch.org/ontologies/family#Person",
    "specification" : false,
    "annotationProperties" : {
        "label" : ".inspecificchild-name-nickname"
    },
    "objectProperties" : {
        "http://chaospop.sysresearch.org/ontologies/family.owl#hasBrother" : ".inspecificchild-siblings-brother"
    },
    "dataProperties" : {
        "http://chaospop.sysresearch.org/ontologies/family.owl#hasDeathYear" : [".inspecificchild-marriage-marriage_year", "Integer"]
    }
}`

* **Success Response:**
  
 * **Code:** 200 OK <br />
    **Content:** `{
    "status": 200,
    "message": "True/False"
}`
 
* **Error Response:**

  * **Code:** 500 INTERNAL SERVER ERROR<br />
    **Content:** `{ "status": 500, message : "An internal error has taken place. Please contact the ChaosPop administrator." }`

  * **Code:** 400 BAD REQUEST<br />
    **Content:** `{ "status": 400, message : "An Individual Mapping id must be provided." }`
    
  * **Code:** 400 BAD REQUEST<br />
    **Content:** `{ "status": 400, message : "The Individual Mapping id has an illegal argument." }`
