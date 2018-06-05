package exceptions;

import java.util.ResourceBundle;

import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import properties.PropertiesHandler;


@XmlRootElement
public class ErrorMessage extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3929350087370023508L;

	/** contains the same HTTP Status code returned by the server */
	@XmlElement(name = "status")
	Integer status;
	
	/** message describing the error*/
	@XmlElement(name = "message")
	String message;

	public ErrorMessage() {
		this.status = 0;
		this.message = null;
	}
	
	public ErrorMessage(Response.Status status, String messageCode, String baseName) {
		this.status = status.getStatusCode();
		
		/* Gets the correct message based on its code */
		String language = PropertiesHandler.configProperties.getProperty("language");
		ResourceBundle resourceBundle = PropertiesHandler.getMessages(baseName, language);
		this.message = resourceBundle.getString(messageCode);
	}
	
	public int getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}