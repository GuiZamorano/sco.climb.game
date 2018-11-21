package it.smartcommunitylab.climb.domain.exception;

public class InvalidParametersException extends Exception {
	private static final long serialVersionUID = 1932134789170282070L;

	public InvalidParametersException() {
		super();
	}

	public InvalidParametersException(String message) {
		super(message);
	}
}
