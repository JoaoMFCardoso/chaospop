package exceptions;

public class ChaosPopException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3258009087515404588L;

	public ErrorMessage errormessage;
	
	public ChaosPopException(String message) {
		super(message);
		this.errormessage = null;
	}

	public ErrorMessage getErrormessage() {
		return errormessage;
	}

	public void setErrormessage(ErrorMessage errormessage) {
		this.errormessage = errormessage;
	}
}
