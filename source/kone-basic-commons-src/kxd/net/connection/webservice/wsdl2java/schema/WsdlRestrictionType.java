package kxd.net.connection.webservice.wsdl2java.schema;

import java.io.IOException;
import java.io.PrintStream;

import javax.xml.namespace.QName;

import kxd.net.connection.webservice.wsdl2java.Wsdl2JavaProcessor;
import kxd.net.connection.webservice.wsdl2java.WsdlDecodeException;
import kxd.util.stream.Stream;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class WsdlRestrictionType extends WsdlSimpleType {
	private QName baseType;
	private WsdlRestrictionSubType type;

	public QName getBaseType() {
		return baseType;
	}

	public void setBaseType(QName baseType) {
		this.baseType = baseType;
	}

	public WsdlRestrictionSubType getType() {
		return type;
	}

	@Override
	public void decode(Wsdl2JavaProcessor processor, Element el)
			throws WsdlDecodeException {
		super.decode(processor, el);
		baseType = processor.decodeSchemaTypeQName(el, "base");
		NodeList ls = el.getChildNodes();
		for (int i = 0; i < ls.getLength(); i++) {
			Node node = ls.item(i);
			if (node instanceof Element) {
				if (node.getLocalName().equals("enumeration")) {
					type = new WsdlEnumType();
				} else
					throw new WsdlDecodeException("暂不支持的结点["
							+ node.getLocalName() + "]");
				type.decodeRestrictionType(processor, el);
			}
		}
	}

	@Override
	public void debug(String prefix, PrintStream stream) {
		super.debug(prefix, stream);
		stream.println(prefix + "[base=" + baseType.getLocalPart() + "("
				+ baseType.getNamespaceURI() + ")]{");
		if (type != null) {
			type.debug(prefix, stream);
		}
		stream.println(prefix + "}");
	}

	public void toJava(Wsdl2JavaProcessor processor, String prefix,
			Stream stream) throws IOException {
		if (type != null) {
			String className = processor.getClassName(baseType.getPrefix(),
					baseType.getLocalPart());
			type.toJava(processor, prefix, className, stream);
		}
	}

}
