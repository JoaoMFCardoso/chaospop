package file.sftp;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Vector;

import org.apache.commons.io.FileUtils;

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
		String password = PropertiesHandler.configProperties.getProperty("sftp.password");
		this.sftpDirectory = PropertiesHandler.configProperties.getProperty("sftp.ontologies");

		/* Connects to the SFTP server */
		JSch jsch = new JSch();
        this.session = jsch.getSession(user, host, port);
        this.session.setPassword(password);
        java.util.Properties config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no");
        this.session.setConfig(config);
        this.session.connect();
        this.channel = this.session.openChannel("sftp");
        this.channel.connect();
        this.channelSftp = (ChannelSftp) this.channel;
	}

	/**
	 * This method uploads a given file into the SFTP server that has been initialized
	 * @param filePath The path to the file that is being uploaded
	 * @throws Exception
	 */
	public void uploadSFTPFile(String filePath) throws Exception{
		/* Sets the destination directory and uploads the file */
		this.channelSftp.cd(this.sftpDirectory);
        File f = new File(filePath);
        this.channelSftp.put(new FileInputStream(f), f.getName());
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

		String filePath = downloadedFile.getAbsolutePath();
		return filePath;
	}

	/**
	 * Removes a file from the SFTP server
	 * @param fileName The filename
	 * @throws SftpException
	 */
	public void removeSFTPFile(String fileName) throws SftpException{
		/* Sets the correct directory and removes the file */
		this.channelSftp.cd(this.sftpDirectory);
		this.channelSftp.rm(fileName);
	}

	/**
	 * Lists the files within the working directory of the SFTP server
	 * @return A list with all the file names
	 * @throws SftpException
	 */
	@SuppressWarnings({ "unchecked" })
	public ArrayList<String> listSFTPFiles() throws SftpException{
		ArrayList<String> fileNames = new ArrayList<String>();

		/* Sets the working directory and gets the file list */
		Vector<LsEntry> filelist = this.channelSftp.ls(this.sftpDirectory);
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
