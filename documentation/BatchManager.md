**Create Batch**
----
Creates a new Batch object in the database based on the transfer object created in the client application.

* **URL**

  /batchManager/createBatch

* **Method:**
  
 `POST`

* **Data Params**

  * `{"mappingIds" : ["5b1268c214b49bd38a84e49e", "5b1268c214b49bd38a84e49f"]}`

* **Success Response:**
  
 * **Code:** 200 OK<br />
    **Content:** `{"_id": "5b1b07d1b94ec1f079bc6299","mappingIds": ["5b1268c214b49bd38a84e49e"]}`
 
* **Error Response:**

  * **Code:** 500 INTERNAL SERVER ERROR<br />
    **Content:** `{ "status": 500, message : "An internal error has taken place. Please contact the ChaosPop administrator." }`

  * **Code:** 400 BAD REQUEST<br />
    **Content:** `{ "status": 400, message : "It is necessary to input a Batch json object in order for it to be created in the database." }`
    
   * **Code:** 400 BAD REQUEST<br />
    **Content:** `{ "status": 400, message : "The inputed json Batch has an illegal argument. Verify your input." }`

**Get Batch**
----
gets a Batch object when given its id.

* **URL**

  /batchManager/getBatch

* **Method:**
  
 `POST`

* **Data Params**

  * `{"id" : "5b16987c8d9a117e67abee81"}`

* **Success Response:**
  
 * **Code:** 204 NO CONTENT <br />
    **Content:** The server successfully uploaded the ontology file to the SFTP server.
 
* **Error Response:**

  * **Code:** 500 INTERNAL SERVER ERROR<br />
    **Content:** `{ "status": 500, message : "An internal error has taken place. Please contact the ChaosPop administrator." }`

  * **Code:** 404 NOT FOUND<br />
    **Content:** `{ "status": 404, message : "There is no Batch associated with the provided id." }`
    
  * **Code:** 400 BAD REQUEST<br />
    **Content:** `{ "status": 404, message : "A Batch id must be provided." }`
   
  * **Code:** 400 BAD REQUEST<br />
    **Content:** `{ "status": 400, message : "The Batch id has an illegal argument." }`
    
**Get All Batches**
----
Returns all the existing Batches in the database

* **URL**

  /batchManager/getAllBatches

* **Method:**
  
 `GET`

* **Data Params**

  * 

* **Success Response:**
  
 * **Code:** 200 OK<br />
    **Content:** `{
    "batchesTO": [
        {
            "_id": "5b1268c214b49bd38a84e49f",
            "mappingIds": [
                "5b1268c214b49bd38a84e49e"
            ]
        },
        {
            "_id": "5b1b02e5b94edfd6fd93a62e",
            "mappingIds": [
                "5b1268c214b49bd38a84e49e"
            ]
        }
    ]
}`
 
* **Error Response:**

  * **Code:** 500 INTERNAL SERVER ERROR<br />
    **Content:** `{ "status": 500, message : "An internal error has taken place. Please contact the ChaosPop administrator." }`

**Remove Batch**
----
Removes a list of Batch objects from the database

* **URL**

  /batchManager/removeBatch

* **Method:**
  
 `POST`

* **Data Params**

  * `["5b1268c214b49bd38a84e455","5b1a958c32fa83d5ea219b11"]`

* **Success Response:**
  
 * **Code:** 200 OK <br />
    **Content:** `{
    "status": 200,
    "message": "The Batch(es) [\"5b1268c214b49bd38a84e455\",\"5b1a958c32fa83d5ea219b11\"] has(have) been removed."
}`
 
* **Error Response:**

  * **Code:** 500 INTERNAL SERVER ERROR<br />
    **Content:** `{ "status": 500, message : "An internal error has taken place. Please contact the ChaosPop administrator." }`

  * **Code:** 404 NOT FOUND<br />
    **Content:** `{ "status": 404, message : "There are no Batch files associated with the provided id." }`
    
  * **Code:** 400 BAD REQUEST<br />
    **Content:** `{ "status": 400, message : "You need to provide a Batch id." }`
  
  * **Code:** 400 BAD REQUEST<br />
    **Content:** `{ "status": 400, message : "The provided Batch id has an illegal argument." }`

**Add Mapping to Batch**
----
Adds a Mapping to a Batch

* **URL**

  /batchManager/addMappingToBatch

* **Method:**
  
 `POST`

* **Data Params**

  * @FormParam("mappingId") String mappingId
  * @FormParam("batchId") String batchId

* **Success Response:**
  
 * **Code:** 200 OK <br />
    **Content:** `{"status": 200, message": "The Mapping id 5b1268c214b49bd38a84e49f has been correctly added to the provided Batch."}`
 
* **Error Response:**

  * **Code:** 500 INTERNAL SERVER ERROR<br />
    **Content:** `{ "status": 500, message : "An internal error has taken place. Please contact the ChaosPop administrator." }`

  * **Code:** 404 NOT FOUND<br />
    **Content:** `{ "status": 404, message : "Both a Batch id and a Mapping id must be provided." }`
    
  * **Code:** 400 BAD REQUEST<br />
    **Content:** `{ "status": 400, message : "The provided Mapping has already been added to the Batch." }`
  
  * **Code:** 400 BAD REQUEST<br />
    **Content:** `{ "status": 400, message : "Either the Batch id or the Mapping id have an illegal argument." }`

**Get All Mappings from Batch**
----
Gets all the Mappings from a Batch

* **URL**

  /batchManager/getAllMappingsFromBatch

* **Method:**
  
 `POST`

* **Data Params**

  * `["5b1268c214b49bd38a84e455","5b1a958c32fa83d5ea219b11"]`

* **Success Response:**
  
 * **Code:** 200 OK <br />
    **Content:** `{
    "status": 200,
    "message": "The Data File(s) [\"5b1268c214b49bd38a84e455\",\"5b1a958c32fa83d5ea219b11\"] has(have) been removed."
}`
 
* **Error Response:**

  * **Code:** 500 INTERNAL SERVER ERROR<br />
    **Content:** `{ "status": 500, message : "An internal error has taken place. Please contact the ChaosPop administrator." }`

  * **Code:** 404 NOT FOUND<br />
    **Content:** `{ "status": 404, message : "There are no Data files associated with the provided ID." }`
    
  * **Code:** 400 BAD REQUEST<br />
    **Content:** `{ "status": 400, message : "You need to provide a Data File ID." }`
  
  * **Code:** 400 BAD REQUEST<br />
    **Content:** `{ "status": 400, message : "The provided Data File ID has an illegal argument." }`
    
**Remove Mapping from Batch**
----
Removes a Mapping from a Batch

* **URL**

  /batchManager/removeMappingFromBatch

* **Method:**
  
 `POST`

* **Data Params**

  * `["5b1268c214b49bd38a84e455","5b1a958c32fa83d5ea219b11"]`

* **Success Response:**
  
 * **Code:** 200 OK <br />
    **Content:** `{
    "status": 200,
    "message": "The Batch(es) [\"5b1268c214b49bd38a84e455\",\"5b1a958c32fa83d5ea219b11\"] has(have) been removed."
}`
 
* **Error Response:**

  * **Code:** 500 INTERNAL SERVER ERROR<br />
    **Content:** `{ "status": 500, message : "An internal error has taken place. Please contact the ChaosPop administrator." }`

  * **Code:** 404 NOT FOUND<br />
    **Content:** `{ "status": 404, message : "There are no Batch files associated with the provided id." }`
    
  * **Code:** 400 BAD REQUEST<br />
    **Content:** `{ "status": 400, message : "You need to provide a Batch id." }`
  
  * **Code:** 400 BAD REQUEST<br />
    **Content:** `{ "status": 400, message : "The provided Batch id has an illegal argument." }`
