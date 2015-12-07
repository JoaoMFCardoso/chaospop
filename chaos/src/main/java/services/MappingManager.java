package services;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.bson.types.ObjectId;

import database.implementations.DataFileImpl;
import database.implementations.MappingImpl;
import domain.bo.mappings.Mapping;
import domain.bo.parsers.DataFile;
import domain.to.DataFileTO;
import domain.to.MappingTO;

/**
 * This class implements a jax rs service layer
 * The implemented services have to do with handling Mapping objects
 * @author João Cardoso
 *
 */
@Path("mappingManager")
public class MappingManager {

	/** The connection to the database for Mapping objects */
	private MappingImpl mappingImpl = new MappingImpl();

	/** The connection to the database for DataFile objects */
	private DataFileImpl dataFileImpl = new DataFileImpl();

	/**
	 * This method returns all the Mappings stored in the database
	 * @return An ArrayList with all Mappings transfer objects
	 */
	@GET
	@Path("/getAllMappings")
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<MappingTO> getAllMappings(){
		ArrayList<MappingTO> mappingTOs = new ArrayList<MappingTO>();

		/* Get all Mapping objects from the database */
		List<Mapping> mappings = this.mappingImpl.getAll();

		/* Runs the Mapping objects and fills the MappingTO array  */
		for(Mapping mapping : mappings){
			MappingTO mappingTO = mapping.createTransferObject();
			mappingTOs.add(mappingTO);
		}

		return mappingTOs;
	}

	/**
	 * This method creates a new Mapping object in the database based
	 * on the transfer object created in the client application.
	 * @param mappingTO The client provided MappingTO transfer object
	 * @return 200 if everything went well, 500 if not.
	 */
	@POST
	@Path("/createMapping")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createMapping(MappingTO mappingTO){
		Response response;
		try{
			/* Creates the Mapping from the MappingTO */
			Mapping mapping = new Mapping(mappingTO);

			/* Stores the new mapping in the database */
			this.mappingImpl.save(mapping);

			/* Creates the Response */
			response = Response.status(200).build();
		}catch(Exception exception){
			exception.printStackTrace();
			/* Sends a response that is not ok */
			response = Response.status(500).build();
		}

		return response;
	}

	/**
	 * This method removes a list of Mapping objects from the database
	 * @param mappingIds The Mapping id list. All ids are sepparated by ",".
	 * @return 200 if everything went well, 500 if not.
	 */
	@POST
	@Path("/removeMapping")
	public Response removeFile(@FormParam("ids") String mappingsIds){
		Response response;
		try{
			/* Gets the Mapping id's from the mappingsIds string
			 * The id's are sepparated by ","
			 * e.g. 123,2344,455 */
			String[] ids = mappingsIds.split(",");

			/* Runs all ids and fetches the Mapping object  */
			for(String mappingId : ids){
				/* Removes the Mapping from the database */
				this.mappingImpl.remove(mappingId);
			}

			/* Gets the Response */
			response = Response.status(200).build();
		}catch(Exception exception){
			exception.printStackTrace();
			/* Sends a response that is not ok */
			response = Response.status(500).build();
		}

		return response;
	}

	/**
	 * Gets all the DataFileTO transfer objects from a given Mapping
	 * @param mappingId The Mapping id
	 * @return An Array with all DataFileTO transfer objects that represent the DataFile objects assigned to the Mapping
	 */
	@POST
	@Path("/getAllDataFilesFromMapping")
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<DataFileTO> getAllDataFilesFromMapping(@FormParam("mappingId") String mappingId){
		ArrayList<DataFileTO> dataFileTOList = new ArrayList<DataFileTO>();

		/* Gets the Mapping and its DataFile List */
		Mapping mapping = this.mappingImpl.get(mappingId);
		ArrayList<ObjectId> fileList = mapping.getFileList();

		/* Runs the DataFile list and creates DataFileTO objects */
		for(ObjectId dataFileId : fileList){
			DataFile dataFile = this.dataFileImpl.get(dataFileId.toString());
			DataFileTO dataFileTO = dataFile.createTransferObject();
			dataFileTOList.add(dataFileTO);
		}

		return dataFileTOList;
	}

	/**
	 * This method adds a DataFile to an existing Mapping
	 * @param mappingId The Mapping id
	 * @param dataFileId The DataFile id
	 * @return 200 if everything went well, 500 if not.
	 */
	@POST
	@Path("/addDataFileToMapping")
	public Response addDataFileToMapping(@FormParam("mappingId") String mappingId, @FormParam("dataFileId") String dataFileId){
		Response response;
		try{
			/* Gets the Mapping from the given id */
			Mapping mapping = this.mappingImpl.get(mappingId);

			/* Gets the Mapping's file list and adds a new id to the list if it doesn't exist already */
			ArrayList<ObjectId> fileList = mapping.getFileList();
			ObjectId newFileId = new ObjectId(dataFileId);

			if(fileList.contains(newFileId)){
				throw new Exception("DataFile has already been added to the Mapping");
			}else{
				/* The Mapping is updated with the new fileList.
				 * And the Database object is updated with the new Mapping*/
				fileList.add(newFileId);
				mapping.setFileList(fileList);

				this.mappingImpl.replace(mappingId, mapping);
			}

			/* Gets the Response */
			response = Response.status(200).build();
		}catch(Exception exception){
			exception.printStackTrace();
			/* Sends a response that is not ok */
			response = Response.status(500).build();
		}

		return response;
	}

	/**
	 * This method removes a DataFile to an existing Mapping
	 * @param mappingId The Mapping id
	 * @param dataFileId The DataFile id
	 * @return 200 if everything went well, 500 if not.
	 */
	@POST
	@Path("/removeDataFileFromMapping")
	public Response removeDataFileToMapping(@FormParam("mappingId") String mappingId, @FormParam("dataFileId") String dataFileId){
		Response response;
		try{
			/* Gets the Mapping from the given id */
			Mapping mapping = this.mappingImpl.get(mappingId);

			/* Gets the Mapping's file list and removes the Datafile from the list if it exists*/
			ArrayList<ObjectId> fileList = mapping.getFileList();
			ObjectId newFileId = new ObjectId(dataFileId);

			if(fileList.contains(newFileId)){
				/* The Mapping is updated with the new fileList.
				 * And the Database object is updated with the new Mapping*/
				fileList.remove(newFileId);
				mapping.setFileList(fileList);

				this.mappingImpl.replace(mappingId, mapping);
			}else{
				throw new Exception("DataFile does not exist in the Mapping");
			}

			/* Gets the Response */
			response = Response.status(200).build();
		}catch(Exception exception){
			exception.printStackTrace();
			/* Sends a response that is not ok */
			response = Response.status(500).build();
		}

		return response;
	}
}
