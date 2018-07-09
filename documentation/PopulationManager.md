**Process Batch**
----
Executes a population operation on a given batch.

* **URL**

  /populationManager/processBatch

* **Method:**
  
 `POST`

* **Data Params**

  * @FormParam("id") String batchID

* **Success Response:**
  
 * **Code:** 200 OK<br />
    **Content:** `"5b1268c214b49bd38a84e49e"`
 
* **Error Response:**

  * **Code:** 500 INTERNAL SERVER ERROR<br />
    **Content:** `{ "status": 500, message : "An internal error has taken place. Please contact the ChaosPop administrator." }`

  * **Code:** 400 BAD REQUEST<br />
    **Content:** `{ "status": 404, message : "A Batch id must be provided." }`
   
  * **Code:** 400 BAD REQUEST<br />
    **Content:** `{ "status": 400, message : "The provided Batch id has an illegal argument." }`
    
  * **Code:** 400 BAD REQUEST<br />
    **Content:** `{ "status": 404, message : "An error has occurred when creating the populated ontology. Check your input." }`
   
  * **Code:** 400 BAD REQUEST<br />
    **Content:** `{ "status": 400, message : "An error has occurred when storing the populated ontology. Check your input." }`
  
  * **Code:** 400 BAD REQUEST<br />
    **Content:** `{ "status": 404, message : "An error has occurred when storing the populated ontology local file. Check your input." }`
   
  * **Code:** 400 BAD REQUEST<br />
    **Content:** `{ "status": 400, message : "No Nodes were found to be associated with the provided Individual Mapping. Check your Individual Mapping input." }`
