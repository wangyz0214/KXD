package kxd.net.connection.webservice;

import java.io.IOException;

import javax.xml.soap.SOAPMessage;

public class WebServiceException extends IOException {
	private static final long serialVersionUID = 1L;
	SOAPMessage errorMessage;

	public WebServiceException(String message, SOAPMessage msg) {
		super(message);
		errorMessage = msg;
	}

	public WebServiceException() {
	}

	public WebServiceException(String message) {
		super(message);
	}

	public WebServiceException(Throwable cause) {
		super(cause);
	}

	public WebServiceException(String message, Throwable cause) {
		super(message, cause);
	}

	public SOAPMessage getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(SOAPMessage errorMessage) {
		this.errorMessage = errorMessage;
	}

}
