**Get Node**
----
gets a Node object when given its id.

* **URL**

  /nodeManager/getNode

* **Method:**
  
 `POST`

* **Data Params**

  * @FormParam("id") String nodeId

* **Success Response:**
  
 * **Code:** 204 NO CONTENT <br />
    **Content:** `{
    "childrenIDs": {
        "childID": [
            "5b1268c214b49bd38a84e458",
            "5b1268c214b49bd38a84e45c",
            "5b1268c214b49bd38a84e45d",
            "5b1268c214b49bd38a84e45e",
            "5b1268c214b49bd38a84e45f",
            "5b1268c214b49bd38a84e462",
            "5b1268c214b49bd38a84e465",
            "5b1268c214b49bd38a84e467"
        ]
    },
    "_id": "5b1268c214b49bd38a84e457",
    "dataFileId": "5b1268c214b49bd38a84e455",
    "hasAttributes": false,
    "parent": "5b1268c214b49bd38a84e456",
    "tag": "member"
}`
 
* **Error Response:**

  * **Code:** 500 INTERNAL SERVER ERROR<br />
    **Content:** `{ "status": 500, message : "An internal error has taken place. Please contact the ChaosPop administrator." }`

  * **Code:** 404 NOT FOUND<br />
    **Content:** `{ "status": 404, message : "No Node associated with the provided Id." }`
    
  * **Code:** 400 BAD REQUEST<br />
    **Content:** `{ "status": 404, message : "A Node id must be provided." }`
   
  * **Code:** 400 BAD REQUEST<br />
    **Content:** `{ "status": 400, message : "The Batch id has an illegal argument." }`
    
**Get All Nodes From Data File**
----
Returns all the Nodes associated with a given Data File

* **URL**

  /nodeManager/getAllNodesFromDataFile

* **Method:**
  
 `POST`

* **Data Params**

  * @FormParam("id") String dataFileId

* **Success Response:**
  
 * **Code:** 200 OK<br />
    **Content:** `{
    "nodesTO": [
        {
            "childrenIDs": {
                "childID": [
                    "5b1a960f32fa86e9698b1067",
                    "5b1a962432fa86e9698b1078",
                    "5b1a962432fa86e9698b108a",
                    "5b1a962532fa86e9698b109c",
                    "5b1a962532fa86e9698b10ad"
                ]
            },
            "_id": "5b1a960f32fa86e9698b1066",
            "dataFileId": "5b1a960f32fa86e9698b1065",
            "hasAttributes": false,
            "tag": "family"
        }
    ]
}`
 
* **Error Response:**

  * **Code:** 500 INTERNAL SERVER ERROR<br />
    **Content:** `{ "status": 500, message : "An internal error has taken place. Please contact the ChaosPop administrator." }`
    
  * **Code:** 400 BAD REQUEST<br />
    **Content:** `{ "status": 404, message : "You need to provide a Data File ID." }`
   
  * **Code:** 400 BAD REQUEST<br />
    **Content:** `{ "status": 400, message : "The provided Data File ID has an illegal argument." }`
    
**Get Suggested Individual Mappings**
----
Gets all Individual Mappings that match the tag of the Node whose id is given.

* **URL**

  /nodeManager/getSuggestedIndividualMappings

* **Method:**
  
 `POST`

* **Data Params**

  * @FormParam("nodeId") String nodeId

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
                "http://chaospop.sysresearch.org/ontologies/family.owl#hasPartner": ".inspecificchild-marriage-partner_name"
            },
            "dataProperties": {
                "http://chaospop.sysresearch.org/ontologies/family.owl#hasFirstGivenName": [
                    ".inspecificchild-name-given",
                    "String"
                ]
            }
        }
    ]
}`
 
* **Error Response:**

  * **Code:** 500 INTERNAL SERVER ERROR<br />
    **Content:** `{ "status": 500, message : "An internal error has taken place. Please contact the ChaosPop administrator." }`

  * **Code:** 404 NOT FOUND<br />
    **Content:** `{ "status": 404, message : "No Individual Mappings were found for the Tag of the provided Node id." }`
    
  * **Code:** 400 BAD REQUEST<br />
    **Content:** `{ "status": 400, message : "A Node id must be provided." }`
  
  * **Code:** 400 BAD REQUEST<br />
    **Content:** `{ "status": 400, message : "The provided Node id has an illegal argument." }`
