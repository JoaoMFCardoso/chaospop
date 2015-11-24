package utils;

import java.io.File;

public class ParsingUtils {

	/**
	 * Traverses a given directory, and calls the parser selector if a file is found.
	 *
	 * @param directory the directory
	 */
	public static void directoryTraverser(File directory){
		/* Runs the children */
		for(File child : directory.listFiles()){

			/* Calls the directory traverser if the child is a directory */
			if(child.isDirectory()){
				directoryTraverser(child);
			}else if(child.isFile()){/* If the child is a file calls the parser */
				//parserSelector(child);
				//TODO do something
			}
		}
	}


}
