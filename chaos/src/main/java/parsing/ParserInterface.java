package parsing;

import java.io.File;

public interface ParserInterface {

	/** This method parses a file
	 * @throws Exception */
	String parseFile(File file) throws Exception;
}
