package kxd.net.connection.webservice.wsdl2java.schema;

import java.io.IOException;
import java.io.PrintStream;

import kxd.net.connection.webservice.wsdl2java.Wsdl2JavaProcessor;
import kxd.net.connection.webservice.wsdl2java.WsdlDecodeException;
import kxd.util.stream.Stream;

import org.w3c.dom.Element;

public class WsdlType {
	String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void decode(Wsdl2JavaProcessor processor, Element el)
			throws WsdlDecodeException {
	}

	public void debug(String prefix, PrintStream stream) {
		stream.println(prefix + getClass().getSimpleName() + "[name=" + name
				+ "]");
	}

	public void toJava(Wsdl2JavaProcessor processor, String prefix,
			Stream stream) throws IOException {
	}

}
