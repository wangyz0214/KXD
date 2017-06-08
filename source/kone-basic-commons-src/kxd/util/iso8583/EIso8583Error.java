package kxd.util.iso8583;

public class EIso8583Error extends Exception {
	private static final long serialVersionUID = 1L;

	public EIso8583Error(String msg) {
        super(msg);
    }

	public EIso8583Error() {

		super();
	}

	public EIso8583Error(String message, Throwable cause) {

		super(message, cause);
	}

	public EIso8583Error(Throwable cause) {

		super(cause);
	}
}