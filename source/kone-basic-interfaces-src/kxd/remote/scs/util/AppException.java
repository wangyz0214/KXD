package kxd.remote.scs.util;

import kxd.util.KoneException;

public class AppException extends KoneException {
	private static final long serialVersionUID = 1L;

	public AppException(Throwable cause) {
		super(cause);
	}

	public AppException(String message, Throwable cause) {
		super(message, cause);
	}

	public AppException(String message) {
		super(message);
	}

	public AppException() {
	}

}
