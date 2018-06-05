package parsing;

import java.io.File;

import exceptions.ChaosPopException;

public interface ParserInterface {

	/** This method parses a file
	 * @throws Exception */
	String parseFile(File file) throws ChaosPopException;
}
