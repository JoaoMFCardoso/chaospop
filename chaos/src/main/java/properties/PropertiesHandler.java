package properties;

import java.io.FileInputStream;
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

	/**
	 * Gets the correct messages from the ResourceBundle.
	 *
	 * @param baseName The ResourceBundle base name
	 * @param language The language
	 * @return The correct messages
	 */
	public static ResourceBundle getMessages(String baseName, String language){
		Locale currentLocale = new Locale(language);
		return ResourceBundle.getBundle(baseName, currentLocale);
	}

	/* ### Properties Methods ### */
	/**
	 * Loads the properties at the startup of the ontology populator application.
	 */
	public static void propertiesLoader(){
		configProperties = new Properties();

		try {
			//load a properties file
			configProperties.load(new FileInputStream("./config.properties"));

		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Properties saver.
	 */
	public static void propertiesSaver(){
			FileOutputStream fos;
			try {
				fos = new FileOutputStream("./config.properties");
				configProperties.store(fos, "Ontology Genesis Properties");
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}


	}
}
