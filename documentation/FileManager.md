**Add File**
----
Uploads a file from the client. Processes it and stores it in the database

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
    **Content:** `{ id : "5b16987c8d9a117e67abee81"}`
 
* **Error Response:**

  * **Code:** 500 INTERNAL SERVER ERROR<br />
    **Content:** `{ "status": 500, message : "An internal error has taken place. Please contact the ChaosPop administrator." }`

  * **Code:** 413 REQUEST ENTITY TOO LARGE<br />
    **Content:** `{ "status": 413, message : "The uploaded file is over the maximum size allowed (500 MB)." }`
    
  * **Code:** 415 UNSUPPORTED MEDIA TYPE<br />
    **Content:** `{ "status": 415, message : "The uploaded file extension is not supported." }`
    
  * **Code:** 500 INTERNAL SERVER ERROR<br />
    **Content:** `{ "status": 500, message : "The uploaded file already exists in the ChaosPop database." }`

**Upload File SFTP**
----
Uploads an ontology file to the SFTP Server.

* **URL**

  /fileManager/uploadFileSFTP

* **Method:**
  
 `POST`

* **Data Params**

  * `{"ontologyFileId" : "5b16987c8d9a117e67abee81"}`

* **Success Response:**
  
 * **Code:** 204 NO CONTENT <br />
    **Content:** The server successfully uploaded the ontology file to the SFTP server.
 
* **Error Response:**

  * **Code:** 500 INTERNAL SERVER ERROR<br />
    **Content:** `{ "status": 500, message : "An internal error has taken place. Please contact the ChaosPop administrator." }`

  * **Code:** 404 NOT FOUND<br />
    **Content:** `{ "status": 404, message : "There are no Ontology files associated with the provided ID." }`
    
  * **Code:** 404 NOT FOUND<br />
    **Content:** `{ "status": 404, message : "The ontology file is not localy stored. And thus cannot be uploaded onto the SFTP server." }`
  
  * **Code:** 404 NOT FOUND<br />
    **Content:** FileNotFoundException message.
    
  * **Code:** 400 BAD REQUEST<br />
    **Content:** `{ "status": 400, message : "The provided Ontology Namespace is not compatible with the ChaosPop SFTP server." }`
    
  * **Code:** 400 BAD REQUEST<br />
    **Content:** `{ "status": 400, message : "The Ontology file has already been uploaded to the SFTP server with this namespace." }`
    
  * **Code:** 400 BAD REQUEST<br />
    **Content:** SFTP connection establishment errors
    
  * **Code:** 400 BAD REQUEST<br />
    **Content:** `{ "status": 400, message : "The Ontology file has already been uploaded to the SFTP server with this namespace." }`
  
  * **Code:** 400 BAD REQUEST<br />
    **Content:** `{ "status": 400, message : "The provided Ontology File ID has an illegal argument." }`
    
**Donwload File SFTP**
----
Downloads an ontology file from the SFTP Server.

* **URL**

  /fileManager/downloadFile

* **Method:**
  
 `POST`

* **Data Params**

  * `{"fileName" : "ontologyFile.owl"}`

* **Success Response:**
  
 * **Code:** 200 OK<br />
    **Content:** The ontology file is downloaded.
 
* **Error Response:**

  * **Code:** 500 INTERNAL SERVER ERROR<br />
    **Content:** `{ "status": 500, message : "An internal error has taken place. Please contact the ChaosPop administrator." }`

  * **Code:** 404 NOT FOUND<br />
    **Content:** `{ "status": 404, message : "The file you're trying to download from the SFTP Server does not exist. Please check that you've inputed a correct file name. Remember that the file name should be completed with the extension, and the path past the default namespace for the SFTP server." }`

**List Data Files**
----
Lists all the DataFiles stored in the database

* **URL**

  /fileManager/listDataFile

* **Method:**
  
 `GET`

* **Success Response:**
  
 * **Code:** 204 NO CONTENT <br />
    **Content:** `{
dataFilesTO: [
{
_id: "5b1268c214b49bd38a84e455",
name: "family.xml",
nodeId: "5b1268c214b49bd38a84e456"
},
{
_id: "5b1a958c32fa83d5ea219b11",
name: "family_pets.xml",
nodeId: "5b1a958c32fa83d5ea219b12"
}
]
}`
 
* **Error Response:**

  * **Code:** 500 INTERNAL SERVER ERROR<br />
    **Content:** `{ "status": 500, message : "An internal error has taken place. Please contact the ChaosPop administrator." }`

**Get Data File**
----
Gets a DataFile from the database

* **URL**

  /fileManager/getFile

* **Method:**
  
 `POST`

* **Data Params**

  * `{"id" : "5b16987c8d9a117e67abee81"}`

* **Success Response:**
  
 * **Code:** 200 OK <br />
    **Content:** `{
_id: "5b1268c214b49bd38a84e455",
name: "family.xml",
nodeId: "5b1268c214b49bd38a84e456"
}`
 
* **Error Response:**

  * **Code:** 500 INTERNAL SERVER ERROR<br />
    **Content:** `{ "status": 500, message : "An internal error has taken place. Please contact the ChaosPop administrator." }`

  * **Code:** 404 NOT FOUND<br />
    **Content:** `{ "status": 404, message : "There are no Data files associated with the provided ID." }`
    
  * **Code:** 400 BAD REQUEST<br />
    **Content:** `{ "status": 400, message : "You need to provide a Data File ID." }`
  
  * **Code:** 400 BAD REQUEST<br />
    **Content:** `{ "status": 400,
    message : "The provided Data File ID has an illegal argument." }`

**Remove Data File**
----
Removes a list of DataFile objects from the database

* **URL**

  /fileManager/removeFile

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
  
