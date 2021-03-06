package properties;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * The Class PropertiesHandler.
 */
public class PropertiesHandler {

	/** The config properties. */
	public static Properties configProperties;
	
	private static String propertiesFile = "config.properties";

	/**
	 * Gets the correct messages from the ResourceBundle.
	 *
	 * @param baseName The ResourceBundle base name
	 * @param language The language
	 * @return The correct messages
	 */
	public static ResourceBundle getMessages(String baseName, String language){
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		Locale currentLocale = new Locale(language);
		return ResourceBundle.getBundle(baseName, currentLocale, classLoader);
	}

	/* ### Properties Methods ### */
	/**
	 * Loads the properties at the startup of the ontology populator application.
	 */
	public static void propertiesLoader(){
		configProperties = new Properties();

		try {
			//load a properties file
			configProperties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(propertiesFile));

		} catch (IOException ex) {
			ex.printStackTrace();
			System.exit(0);
		}
	}

	/**
	 * Properties saver.
	 */
	public static void propertiesSaver(){
			FileOutputStream fos;
			try {
				fos = new FileOutputStream("./" + propertiesFile);
				configProperties.store(fos, "Ontology Genesis Properties");
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}


	}
}
