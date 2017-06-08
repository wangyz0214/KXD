package kxd.engine.fileservice;

import kxd.util.KoneException;

public class FileException extends KoneException {
	private static final long serialVersionUID = 3069624637125239256L;

	public FileException() {
		super();
	}

	public FileException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public FileException(String msg) {
		super(msg);
	}

	public FileException(Throwable cause) {
		super(cause);
	}

}
