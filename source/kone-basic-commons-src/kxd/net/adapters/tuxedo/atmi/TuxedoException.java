package kxd.net.adapters.tuxedo.atmi;

import java.io.IOException;

public class TuxedoException extends IOException {
	private static final long serialVersionUID = 1L;
	int errorNo;

	public TuxedoException() {
		super();
	}

	public TuxedoException(int errorNo, String msg, Throwable cause) {
		super(msg, cause);
		this.errorNo = errorNo;
	}

	public TuxedoException(int errorNo, String msg) {
		super(msg);
		this.errorNo = errorNo;
	}

	public TuxedoException(int errorNo, Throwable cause) {
		super(cause);
		this.errorNo = errorNo;
	}

}
