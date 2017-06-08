package kxd.net.connection.webservice.wsdl2java.schema;

import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;

import kxd.net.connection.webservice.wsdl2java.Wsdl2JavaProcessor;
import kxd.net.connection.webservice.wsdl2java.WsdlDecodeException;
import kxd.util.stream.Stream;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class WsdlSchema {
	HashMap<String, WsdlType> types = new HashMap<String, WsdlType>();
	Wsdl2JavaProcessor processor;
	String prefix;

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public WsdlSchema(Wsdl2JavaProcessor processor, Element schemaElement)
			throws Exception {
		this.processor = processor;
		NodeList ls = schemaElement.getChildNodes();
		for (int i = 0; i < ls.getLength(); i++) {
			Node node = ls.item(i);
			if (node instanceof Element) {
				schemaElement = (Element) ls.item(i);
				if (schemaElement.getLocalName().equals("complexType"))
					decodeComplexType(null, schemaElement);
				else if (schemaElement.getLocalName().equals("simpleType"))
					decodeSimpleType(null, schemaElement);
				else if (schemaElement.getLocalName().equals("element")) {
					String name = schemaElement.getAttribute("name");
					NodeList ls1 = schemaElement.getChildNodes();
					for (int j = 0; j < ls1.getLength(); j++) {
						node = ls1.item(j);
						if (node instanceof Element) {
							if (node.getLocalName().equals("complexType")) {
								decodeComplexType(name, (Element) node);
								break;
							} else if (node.getLocalName().equals("simpleType")) {
								decodeSimpleType(name, (Element) node);
								break;
							}
							throw new WsdlDecodeException("暂不支持的结点["
									+ node.getLocalName() + "]");
						}
					}
				} else if (!schemaElement.getLocalName().equals("import"))
					throw new WsdlDecodeException("暂不支持的结点["
							+ schemaElement.getLocalName() + "]");
			}
		}
	}

	public HashMap<String, WsdlType> getTypes() {
		return types;
	}

	private void decodeComplexType(String name, Element el)
			throws WsdlDecodeException {
		WsdlComplexType type = new WsdlComplexType();
		NodeList ls = el.getChildNodes();
		for (int i = 0; i < ls.getLength(); i++) {
			Node node = ls.item(i);
			if (node instanceof Element) {
				Element e = (Element) node;
				if (!(e.getLocalName().equals("sequence") || e.getLocalName()
						.equals("complexContent")))
					throw new WsdlDecodeException("暂不支持的结点[" + e.getLocalName()
							+ "]");

				type.decode(processor, e);
			}
		}
		if (name != null)
			type.setName(name);
		else
			type.setName(el.getAttribute("name"));
		types.put(type.getName(), type);
	}

	private void decodeSimpleType(String name, Element el)
			throws WsdlDecodeException {
		WsdlSimpleType type = null;
		NodeList ls = el.getChildNodes();
		for (int i = 0; i < ls.getLength(); i++) {
			Node node = ls.item(i);
			if (node instanceof Element) {
				Element e = (Element) node;
				if (e.getLocalName().equals("restriction")) {
					type = new WsdlRestrictionType();
				} else
					throw new WsdlDecodeException("暂不支持的结点[" + e.getLocalName()
							+ "]");
				if (name != null)
					type.setName(name);
				else
					type.setName(el.getAttribute("name"));
				type.decode(processor, e);
			}
		}
		if (type != null)
			types.put(type.getName(), type);
	}

	public void debug(String prefix, PrintStream stream) {
		for (WsdlType type : types.values())
			type.debug(prefix, stream);
	}

	public void toJava(Wsdl2JavaProcessor processor, Stream stream)
			throws IOException {
		for (WsdlType type : types.values()) {
			String className = processor.getClassName(prefix, type.getName());
			stream.writeln("\tpublic static class " + className
					+ " extends WSClass{", 3000);
			stream.writeln(
					"\t\tprivate static final long serialVersionUID = 1L;",
					3000);
			stream.writeln("\t\tpublic " + className + "(){}", 3000);
			stream.writeln("\t\tpublic " + className
					+ "(WebServiceConnection con,SOAPElement element) throws Exception"
					+ "{decode(con,element);}", 3000);
			type.toJava(processor, "\t", stream);
			stream.writeln("\t}", 3000);
		}
	}
}
