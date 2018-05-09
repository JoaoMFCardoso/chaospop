package file.sftp;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Vector;

import org.apache.commons.io.FileUtils;
import org.javatuples.Pair;

import properties.PropertiesHandler;
import utils.FileOperationsUtils;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class SFTPServerConnectionManager {

	/** The Jsch session */
	Session session = null;

	/** The Jsch channel */
	Channel channel = null;

	/** The Jsch channel SFTP */
	ChannelSftp channelSftp = null;

	/** The SFTP server ontologies directory */
	String sftpDirectory;

	/** The SFTP server base namespace */
	String sftpNamespace;

	/**
	 * The SFTP connection Manager constructor
	 * @throws Exception
	 */
	public SFTPServerConnectionManager() throws Exception {
		PropertiesHandler.propertiesLoader();

		/* Gets the SFTP properties */
		String host = PropertiesHandler.configProperties.getProperty("sftp.host");
		int port = Integer.parseInt(PropertiesHandler.configProperties.getProperty("sftp.port"));
		String user = PropertiesHandler.configProperties.getProperty("sftp.user");
		String sshPrivateKey = PropertiesHandler.configProperties.getProperty("sftp.ssh.private.key");
		this.sftpDirectory = PropertiesHandler.configProperties.getProperty("sftp.ontologies");
		this.sftpNamespace = PropertiesHandler.configProperties.getProperty("sftp.namespace");

		/* Connects to the SFTP server */
		JSch jsch = new JSch();
		jsch.addIdentity(sshPrivateKey);
        this.session = jsch.getSession(user, host, port);
        this.session.setConfig("PreferredAuthentications", "publickey");
        java.util.Properties config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no");
        this.session.setConfig(config);
        this.session.connect();
        this.channel = this.session.openChannel("sftp");
        this.channel.connect();
        this.channelSftp = (ChannelSftp) this.channel;
	}

	/**
	 * This method uploads a given file into the SFTP server that has been initialised
	 * @param filePath The path to the file that is being uploaded
	 * @return boolean indicating whether the operation was successful
	 * @throws Exception
	 */
	public boolean uploadSFTPFile(String filePath, String namespace) throws Exception{
		/* Initializes the channelSFTP with the base namespace for the SFTP Server */
		this.channelSftp.cd(this.sftpDirectory);

		/* Checks if there is a necessity to create other directories in the channelSFTP
		 * This is only necessary when the namespace differs from the sftpDirectory */
		if(FileOperationsUtils.isDirectoryCreationNeeded(this.sftpNamespace, namespace)){

			/* Gets the directories path for the given namespace */
			String directoriesPath = FileOperationsUtils.getNamespaceDirectories(this.sftpNamespace, namespace).getValue0();
			this.channelSftp.mkdir(this.sftpDirectory + directoriesPath);
			this.channelSftp.cd(this.sftpDirectory + directoriesPath);
		}

		/* Uploads the file */
        File f = new File(filePath);
        this.channelSftp.put(new FileInputStream(f), f.getName());
        
        return true;
	}

	/**
	 * Downloads a file from the SFTP server and stores it in a local temporary directory
	 * @param fileName The File name of the file that is to be downloaded
	 * @return The file path to the downloaded file
	 * @throws Exception
	 */
	public String downloadSFTPFile(String fileName) throws Exception{
		/* Gets the file from the SFTP server */
		this.channelSftp.cd(this.sftpDirectory);
		InputStream inputStream = new BufferedInputStream(this.channelSftp.get(fileName));

		/* Creates the correct directory to place the downloaded file */
		File directory = FileOperationsUtils.getCorrectDirectoryForFile(fileName);

		/* Builds the downloaded file and places it in a local temporary directory */
		File downloadedFile = FileOperationsUtils.writeToFile(inputStream, fileName);
		FileUtils.moveFileToDirectory(downloadedFile, directory, false);

		String filePath = directory + File.separator + downloadedFile.getName();
		return filePath;
	}

	/**
	 * Removes a file from the SFTP server
	 * @param namespace The namespace
	 * @throws SftpException
	 */
	public void removeSFTPFile(String namespace) throws SftpException{
		/* Gets the path to the file from the sftp standard directory, and the file name */
		Pair<String, String> fileData = FileOperationsUtils.getNamespaceDirectories(this.sftpNamespace, namespace);

		/* Sets the correct directory and removes the file */
		String filePath = this.sftpDirectory + fileData.getValue0();
		this.channelSftp.cd(filePath);
		this.channelSftp.rm(fileData.getValue1());

		/* Checks if the directory is empty and removes the directory if so */
		if(listSFTPFiles(filePath).isEmpty()){
			this.channelSftp.rmdir(filePath);
		}

		return;
	}

	/**
	 * Lists the files within the working directory of the SFTP server
	 * @return A list with all the file names
	 * @throws SftpException
	 */
	@SuppressWarnings({ "unchecked" })
	public ArrayList<String> listSFTPFiles(String directory) throws SftpException{
		ArrayList<String> fileNames = new ArrayList<String>();

		/* Sets the working directory and gets the file list */
		Vector<LsEntry> filelist = this.channelSftp.ls(directory);
        for(LsEntry file : filelist){
        	String fileName = file.getFilename();

        	if(fileName.matches("(\\w*)(\\.\\w+)")){
        		fileNames.add(file.getFilename());
        	}
        }

        return fileNames;
	}

	/**
	 * Disconnects an existing connection with an SFTP server
	 */
	public void disconnect(){
		this.channel.disconnect();
		this.session.disconnect();
	}
}
