**Create Mapping**
----
Creates a new Mapping object in the database based on the transfer object created in the client application.

* **URL**

  /mappingManager/createMapping

* **Method:**
  
 `POST`

* **Data Params**

  * `{
    "outputOntologyFileName" : "family",
    "outputOntologyNamespace" : "http://chaospop.sysresearch.org/ontologies/family.owl#",
    "fileNames" : ["5b1268c214b49bd38a84e455"],
    "directOntologyImports" : ["5b0d68d914b4e72380fa1502"],
    "individualMappings" : ["5b1268c214b49bd38a84e453"]
}`

* **Success Response:**
  
 * **Code:** 200 OK<br />
    **Content:** `"5b1b07d1b94ec1f079bc6299"`
 
* **Error Response:**

  * **Code:** 500 INTERNAL SERVER ERROR<br />
    **Content:** `{ "status": 500, message : "An internal error has taken place. Please contact the ChaosPop administrator." }`

  * **Code:** 400 BAD REQUEST<br />
    **Content:** `{ "status": 400, message : "The Data File that is being removed does not exist in the Mapping." }`
    
**Get Mapping**
----
gets a Mapping object when given its id.

* **URL**

  /mappingManager/getMapping

* **Method:**
  
 `POST`

* **Data Params**

  * @FormParam("id") String mappingID

* **Success Response:**
  
 * **Code:** 200 OK<br />
    **Content:** `{
    "_id": "5b1268c214b49bd38a84e49e",
    "directOntologyImports": [
        "5b0d68d914b4e72380fa1502"
    ],
    "fileNames": [
        "5b1268c214b49bd38a84e455"
    ],
    "individualMappings": [
        "5b1268c214b49bd38a84e453"
    ],
    "outputOntologyFileName": "cardosofamily",
    "outputOntologyNamespace": "http://sysresearch.org/ontologies/cardosofamily.owl#"
}`
 
* **Error Response:**

  * **Code:** 500 INTERNAL SERVER ERROR<br />
    **Content:** `{ "status": 500, message : "An internal error has taken place. Please contact the ChaosPop administrator." }`

  * **Code:** 400 BAD REQUEST<br />
    **Content:** `{ "status": 404, message : "A Mapping id must be provided." }`
   
  * **Code:** 400 BAD REQUEST<br />
    **Content:** `{ "status": 400, message : "The Mapping id has an illegal argument." }`
    
**Get All Mappings**
----
Returns all the existing Mappings in the database

* **URL**

  /mappingManager/getAllMappings

* **Method:**
  
 `GET`


* **Success Response:**
  
 * **Code:** 200 OK<br />
    **Content:** `{
    "mappingsTO": [
        {
            "_id": "5b1268c214b49bd38a84e49e",
            "directOntologyImports": [
                "5b0d68d914b4e72380fa1502"
            ],
            "fileNames": [
                "5b1268c214b49bd38a84e455"
            ],
            "individualMappings": [
                "5b1268c214b49bd38a84e453"
            ],
            "outputOntologyFileName": "cardosofamily",
            "outputOntologyNamespace": "http://sysresearch.org/ontologies/cardosofamily.owl#"
        },
        {
            "_id": "5b1268c214b49bd38a84e49f",
            "directOntologyImports": [
                "5b0d68d914b4e72380fa1502"
            ],
            "fileNames": [
                "5b1268c214b49bd38a84e455"
            ],
            "individualMappings": [
                "5b1268c214b49bd38a84e453"
            ],
            "outputOntologyFileName": "cardosofamily",
            "outputOntologyNamespace": "http://sysresearch.org/ontologies/cardosofamily.owl#"
        }
    ]
}`
 
* **Error Response:**

  * **Code:** 500 INTERNAL SERVER ERROR<br />
    **Content:** `{ "status": 500, message : "An internal error has taken place. Please contact the ChaosPop administrator." }`

**Remove Mapping**
----
Removes a list of Mapping objects from the database

* **URL**

  /mappingManager/removeMappings

* **Method:**
  
 `POST`

* **Data Params**

  * `["5b1268c214b49bd38a84e455","5b1a958c32fa83d5ea219b11"]`

* **Success Response:**
  
 * **Code:** 200 OK <br />
    **Content:** `{
    "status": 200,
    "message": "The Mapping(s) [\"5b1268c214b49bd38a84e455\",\"5b1a958c32fa83d5ea219b11\"] has(have) been removed."
}`
 
* **Error Response:**

  * **Code:** 500 INTERNAL SERVER ERROR<br />
    **Content:** `{ "status": 500, message : "An internal error has taken place. Please contact the ChaosPop administrator." }`

  * **Code:** 400 BAD REQUEST<br />
    **Content:** `{ "status": 400, message : "A Mapping id must be provided." }`
  
  * **Code:** 400 BAD REQUEST<br />
    **Content:** `{ "status": 400, message : "The Mapping id has an illegal argument." }`

**Add Data File to Mapping**
----
Adds a Data File to a Mapping

* **URL**

  /mappingManager/addDataFileToMapping

* **Method:**
  
 `POST`

* **Data Params**

  * @FormParam("mappingId") String mappingId
  * @FormParam("dataFileId") String dataFileId

* **Success Response:**
  
 * **Code:** 200 OK <br />
    **Content:** `{"status": 200, message": "The The Data File id  5b1268c214b49bd38a84e49f has been correctly added to the Mapping."}`
 
* **Error Response:**

  * **Code:** 500 INTERNAL SERVER ERROR<br />
    **Content:** `{ "status": 500, message : "An internal error has taken place. Please contact the ChaosPop administrator." }`

  * **Code:** 400 BAD REQUEST<br />
    **Content:** `{ "status": 400, message : "Both a Mapping ID and a Data File ID must be provided." }`
    
  * **Code:** 400 BAD REQUEST<br />
    **Content:** `{ "status": 400, message : "The provided Data File has already been added to the Mapping." }`
  
  * **Code:** 400 BAD REQUEST<br />
    **Content:** `{ "status": 400, message : "Either the Mapping ID or the Data File ID have an illegal argument." }`

**Get All Data Files from Mapping**
----
Gets all the Data Files from a Mapping

* **URL**

  /mappingManager/getAllDataFilesFromMapping

* **Method:**
  
 `POST`

* **Data Params**

  * @FormParam("mappingId") String mappingId

* **Success Response:**
  
 * **Code:** 200 OK <br />
    **Content:** `{
    "dataFilesTO": [
        {
            "_id": "5b1a960f32fa86e9698b1065",
            "name": "family_pets.xml",
            "nodeId": "5b1a960f32fa86e9698b1066"
        }
    ]
}`
 
* **Error Response:**

  * **Code:** 500 INTERNAL SERVER ERROR<br />
    **Content:** `{ "status": 500, message : "An internal error has taken place. Please contact the ChaosPop administrator." }`

  * **Code:** 400 BAD REQUEST<br />
    **Content:** `{ "status": 400, message : "Either the Mapping ID or the Data File ID have an illegal argument." }`
  
  * **Code:** 400 BAD REQUEST<br />
    **Content:** `{ "status": 400, message : "Both a Mapping ID and a Data File ID must be provided." }`
    
**Remove Data File from Mapping**
----
Removes a Data File from a Mapping

* **URL**

  /mappingManager/removeDataFileFromMapping

* **Method:**
  
 `POST`

* **Data Params**

  * @FormParam("mappingId") String mappingId
  * @FormParam("dataFileId") String dataFileId

* **Success Response:**
  
 * **Code:** 200 OK <br />
    **Content:** `{
    "status": 200,
    "message": "The Data File [\"5b1268c214b49bd38a84e455\"] has(have) been removed."
}`
 
* **Error Response:**

  * **Code:** 500 INTERNAL SERVER ERROR<br />
    **Content:** `{ "status": 500, message : "An internal error has taken place. Please contact the ChaosPop administrator." }`

 * **Code:** 400 BAD REQUEST<br />
    **Content:** `{ "status": 400, message : "Either the Mapping ID or the Data File ID have an illegal argument." }`
  
  * **Code:** 400 BAD REQUEST<br />
    **Content:** `{ "status": 400, message : "Both a Mapping ID and a Data File ID must be provided." }`
