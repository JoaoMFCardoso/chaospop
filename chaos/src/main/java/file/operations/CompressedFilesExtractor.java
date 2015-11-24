package file.operations;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import utils.FileOperationsUtils;

import com.github.junrar.Archive;
import com.github.junrar.exception.RarException;
import com.github.junrar.rarfile.FileHeader;

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
	public ArrayList<File> unZipFile(File zipFile) throws Exception{
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

		}catch(IOException ex){
			ex.printStackTrace();
			throw ex;
		}

		return extractedFiles;
	}


	public ArrayList<File> unRarFile(File rarFile) throws Exception {
		ArrayList<File> extractedFiles = new ArrayList<File>();
		Archive arch = null;

		/* Creates the Archive object */
		try {
			arch = new Archive(rarFile);
		} catch (RarException e) {
			e.printStackTrace();
			throw e;
		} catch (IOException e1) {
			e1.printStackTrace();
			throw e1;
		}

		/* Extracts the files from the Archive object */
		if (arch != null) {
			/* The rar file is encrypted and thus cannot be opened */
			if (arch.isEncrypted()) {
				arch.close();
				return null;
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
				try {
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
				} catch (IOException e) {
					e.printStackTrace();
					arch.close();
					throw e;
				} catch (RarException e) {
					e.printStackTrace();
					arch.close();
					throw e;
				}
			}

			arch.close();
		}

		return extractedFiles;
	}
}
