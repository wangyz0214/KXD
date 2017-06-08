package kxd.engine.scs.admin.util;

public class ParamNotExistException extends Exception {
	private static final long serialVersionUID = -1700767926913277636L;

	public ParamNotExistException() {
	}

	public ParamNotExistException(String message) {
		super(message);
	}

	public ParamNotExistException(Throwable cause) {
		super(cause);
	}

	public ParamNotExistException(String message, Throwable cause) {
		super(message, cause);
	}

}
