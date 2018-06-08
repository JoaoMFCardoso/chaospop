package file.operations;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.ws.rs.core.Response;

import com.github.junrar.Archive;
import com.github.junrar.exception.RarException;
import com.github.junrar.rarfile.FileHeader;

import exceptions.ChaosPopException;
import exceptions.ErrorMessage;
import utils.FileOperationsUtils;

public class CompressedFilesExtractor {

	public CompressedFilesExtractor() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * This method unzips a Zip file
	 * @param zipFile The Zip File
	 * @return An array with all the extracted files and dirs
	 * @throws An IOException
	 */
	public ArrayList<File> unZipFile(File zipFile) throws ChaosPopException{
		byte[] buffer = new byte[1024];
		ArrayList<File> extractedFiles = new ArrayList<File>();

		try{

			//get the zip file content
			ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
			//get the zipped file list entry
			ZipEntry zipEntry = zis.getNextEntry();

			while(zipEntry!=null){

				String fileName = zipEntry.getName();
				File newFileDirectory = FileOperationsUtils.getCorrectDirectoryForFile(fileName);

				/* Creates the file in the appropriate directory */
				File newFile = new File(newFileDirectory.getAbsolutePath() + File.separator + fileName);

				/* Skips the actual file creation if it is a directory */
				if(!fileName.endsWith("/")){

					//create all non exists folders
					//else you will hit FileNotFoundException for compressed folder
					new File(newFile.getParent()).mkdirs();

					FileOutputStream fos = new FileOutputStream(newFile);

					int len;
					while ((len = zis.read(buffer)) > 0) {
						fos.write(buffer, 0, len);
					}

					fos.close();

					extractedFiles.add(newFile);
				}

				zipEntry = zis.getNextEntry();
			}

			zis.closeEntry();
			zis.close();

		}catch(IOException ioException){
			ErrorMessage ioError = new ErrorMessage();
			ioError.setMessage(ioException.getMessage());
			ioError.setStatus(Response.Status.BAD_REQUEST.getStatusCode());

			ChaosPopException chaosPopException = new ChaosPopException(ioException.getMessage());
			chaosPopException.setErrormessage(ioError);

			throw chaosPopException;
		}

		return extractedFiles;
	}


	public ArrayList<File> unRarFile(File rarFile) throws ChaosPopException {
		ArrayList<File> extractedFiles = new ArrayList<File>();
		Archive arch = null;

		/* Creates the Archive object */
		try {
			arch = new Archive(rarFile);


			/* Extracts the files from the Archive object */
			if (arch != null) {
				/* The rar file is encrypted and thus cannot be opened */
				if (arch.isEncrypted()) {
					arch.close();

					ErrorMessage genericError = new ErrorMessage(Response.Status.BAD_REQUEST, "9", "mesages/filemanager");

					ChaosPopException chaosPopException = new ChaosPopException(genericError.getMessage());

					throw chaosPopException;
				}

			}

			FileHeader fh = null;
			while (true) {
				fh = arch.nextFileHeader();

				/* Breaks the extraction process if there are no more files to extract */
				if (fh == null) {
					break;
				}

				/* If the file is encrypted it cannot be extracted */
				if (fh.isEncrypted()) {
					continue;
				}

				/* Extracts the file */

				if (!fh.isDirectory()) {

					/* gets correct destination */
					File destination = FileOperationsUtils.getCorrectDirectoryForFile(fh.getFileNameString());

					File f = FileOperationsUtils.createFile(fh, destination);
					OutputStream stream = new FileOutputStream(f);
					arch.extractFile(fh, stream);
					stream.close();

					/* Adds the file to the extracted files list */
					extractedFiles.add(f);
				}
			}

			arch.close();
		}catch (RarException |IOException exception) {
			ErrorMessage genericError = new ErrorMessage();
			genericError.setMessage(exception.getMessage());
			genericError.setStatus(Response.Status.BAD_REQUEST.getStatusCode());

			ChaosPopException chaosPopException = new ChaosPopException(exception.getMessage());
			chaosPopException.setErrormessage(genericError);

			throw chaosPopException;
		}

		return extractedFiles;
	}
}
