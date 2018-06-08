**Add File**
----
This method uploads a file from the client. Processes it and stores it in the database

* **URL**

  /fileManager/addFile

* **Method:**
  
 `POST`

* **Supported Files**

  The currently supported file types you can add using POST are:

  * XML BPMN EPML JSON CSV ZIP RAR OWL

* **Data Params**

  * @FormDataParam("file") InputStream uploadedInputStream
  * @FormDataParam("file") FormDataContentDisposition fileDetail

* **Success Response:**
  
 * **Code:** 200 OK<br />
    **Content:** `{ id : "5b16987c8d9a117e67abee81" }`
 
* **Error Response:**

  * **Code:** 500 INTERNAL SERVER ERROR<br />
    **Content:** `{ message : "An internal error has taken place. Please contact the ChaosPop administrator." }`

  * **Code:** 413 REQUEST ENTITY TOO LARGE<br />
    **Content:** `{ message : "The uploaded file is over the maximum size allowed (500 MB)." }`
    
  * **Code:** 415 UNSUPPORTED MEDIA TYPE<br />
    **Content:** `{ message : "The uploaded file extension is not supported." }`
    
  * **Code:** 500 INTERNAL SERVER ERROR<br />
    **Content:** `{ message : "The uploaded file already exists in the ChaosPop database." }`

**Upload File SFTP**
----
Uploads an ontology file to the SFTP Server.

* **URL**

  /fileManager/uploadFileSFTP

* **Method:**
  
 `POST`

* **Data Params**

  * `{"ontologyFileId" : "5b16987c8d9a117e67abee81}`

* **Success Response:**
  
 * **Code:** 204 NO CONTENT <br />
    **Content:** The server successfully uploaded the ontology file to the SFTP server.
 
* **Error Response:**

  * **Code:** 500 INTERNAL SERVER ERROR<br />
    **Content:** `{ message : "An internal error has taken place. Please contact the ChaosPop administrator." }`

  * **Code:** 404 NOT FOUND<br />
    **Content:** `{ message : "There are no Ontology files associated with the provided ID." }`
    
  * **Code:** 404 NOT FOUND<br />
    **Content:** `{ message : "The ontology file is not localy stored. And thus cannot be uploaded onto the SFTP server." }`
  
  * **Code:** 404 NOT FOUND<br />
    **Content:** FileNotFoundException message.
    
  * **Code:** 400 BAD REQUEST<br />
    **Content:** `{ message : "The provided Ontology Namespace is not compatible with the ChaosPop SFTP server." }`
    
  * **Code:** 400 BAD REQUEST<br />
    **Content:** `{ message : "The Ontology file has already been uploaded to the SFTP server with this namespace." }`
    
  * **Code:** 400 BAD REQUEST<br />
    **Content:** SFTP connection establishment errors
    
  * **Code:** 400 BAD REQUEST<br />
    **Content:** `{ message : "The Ontology file has already been uploaded to the SFTP server with this namespace." }`
  
  * **Code:** 400 BAD REQUEST<br />
    **Content:** `{ message : "The provided Ontology File ID has an illegal argument." }`
    
