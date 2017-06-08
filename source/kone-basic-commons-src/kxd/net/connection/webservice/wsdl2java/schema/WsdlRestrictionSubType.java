package kxd.net.connection.webservice.wsdl2java.schema;

import java.io.IOException;
import java.io.PrintStream;

import kxd.net.connection.webservice.wsdl2java.Wsdl2JavaProcessor;
import kxd.net.connection.webservice.wsdl2java.WsdlDecodeException;
import kxd.util.stream.Stream;

import org.w3c.dom.Element;

abstract public class WsdlRestrictionSubType {
	abstract public void decodeRestrictionType(Wsdl2JavaProcessor processor,
			Element el) throws WsdlDecodeException;

	abstract public void debug(String prefix, PrintStream stream);

	abstract public void toJava(Wsdl2JavaProcessor processor, String prefix,
			String className, Stream stream) throws IOException;
}
