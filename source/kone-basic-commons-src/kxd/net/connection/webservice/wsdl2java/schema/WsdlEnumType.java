package kxd.net.connection.webservice.wsdl2java.schema;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import kxd.net.connection.webservice.wsdl2java.Wsdl2JavaProcessor;
import kxd.net.connection.webservice.wsdl2java.WsdlDecodeException;
import kxd.util.stream.Stream;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class WsdlEnumType extends WsdlRestrictionSubType {
	private List<String> values = new ArrayList<String>();

	@Override
	public void decodeRestrictionType(Wsdl2JavaProcessor processor, Element el)
			throws WsdlDecodeException {
		NodeList ls = el.getChildNodes();
		for (int i = 0; i < ls.getLength(); i++) {
			Node node = ls.item(i);
			if (node instanceof Element) {
				if (node.getLocalName().equals("enumeration")) {
					values.add(((Element) node).getAttribute("value"));
				} else
					throw new WsdlDecodeException("暂不支持的结点["
							+ node.getLocalName() + "]");
			}
		}
	}

	@Override
	public void debug(String prefix, PrintStream stream) {
		stream.println(prefix + "enumeration: " + values.toString());
	}

	public void toJava(Wsdl2JavaProcessor processor, String prefix,
			String className, Stream stream) throws IOException {
		for (int i = 0; i < values.size(); i++) {
			String v = values.get(i);
			stream.writeln(prefix + "\tpublic final static " + className + " "
					+ v + "=new " + className + "(\"" + v + "\");", 3000);
		}
		stream.writeln(prefix + "\tprivate " + className + " value;\r\n", 3000);
		stream.writeln(prefix + "\tpublic " + className
				+ " getValue(){return value;}\r\n", 3000);
		stream.writeln(prefix + "\tpublic void setValue(" + className
				+ " value){this.value=value;}\r\n", 3000);

		stream.writeln(
				prefix
						+ "\tpublic void encode(SOAPElement element) throws Exception{",
				3000);

		stream.writeln(prefix
				+ "\t\telement.addTextNode(WSConverter.toString(value));", 3000);

		stream.writeln(
				prefix
						+ "\t}\r\n\r\n"
						+ prefix
						+ "\tpublic void decode(WebServiceConnection con,Map<String, List<SOAPElement>> map) throws Exception{",
				3000);
		stream.writeln(prefix + "\t}", 3000);
		stream.writeln(
				prefix
						+ "\tpublic void decode(WebServiceConnection con,SOAPElement element) throws Exception{",
				3000);
		stream.writeln(prefix
				+ "\t\tWSConverter.fromString(element.getTextContent(),"
				+ className + ".class);", 3000);
		stream.writeln(prefix + "\t}", 3000);
	}
}
