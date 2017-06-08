package kxd.net.connection.webservice.wsdl2java.schema;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import kxd.net.connection.webservice.wsdl2java.Wsdl2JavaProcessor;
import kxd.net.connection.webservice.wsdl2java.WsdlDecodeException;
import kxd.util.StringUnit;
import kxd.util.stream.Stream;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class WsdlComplexType extends WsdlType {
	static public class Member {
		private QName type;
		private String minOccurs;
		private String maxOccurs;
		private boolean nullAble;
		private String name;

		public QName getType() {
			return type;
		}

		public void setType(QName type) {
			this.type = type;
		}

		public String getMinOccurs() {
			return minOccurs;
		}

		public void setMinOccurs(String minOccurs) {
			this.minOccurs = minOccurs;
		}

		public String getMaxOccurs() {
			return maxOccurs;
		}

		public void setMaxOccurs(String maxOccurs) {
			this.maxOccurs = maxOccurs;
		}

		public boolean isNullAble() {
			return nullAble;
		}

		public void setNullAble(boolean nullAble) {
			this.nullAble = nullAble;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	};

	private List<Member> members = new ArrayList<Member>();

	public List<Member> getMembers() {
		return members;
	}

	public void decode(Wsdl2JavaProcessor processor, Element el)
			throws WsdlDecodeException {
		super.decode(processor, el);
		if (el.getLocalName().equals("sequence")) {
			NodeList es = el.getChildNodes();
			for (int j = 0; j < es.getLength(); j++) {
				Node node = es.item(j);
				if (node instanceof Element) {
					Element e = (Element) node;
					if (e.getLocalName().equals("element")) {
						Member we = new Member();
						we.setName(e.getAttribute("name"));
						we.setType(processor.decodeSchemaTypeQName(e, "type"));
						if (e.hasAttribute("nillable"))
							we.setNullAble("true".equalsIgnoreCase(e
									.getAttribute("nillable")));
						else
							we.setNullAble(false);
						if (e.hasAttribute("minOccurs"))
							we.setMinOccurs(e.getAttribute("minOccurs"));
						if (e.hasAttribute("maxOccurs"))
							we.setMaxOccurs(e.getAttribute("maxOccurs"));
						getMembers().add(we);
					} else if (!e.getLocalName().equals("any"))
						throw new WsdlDecodeException("暂不支持的结点["
								+ e.getLocalName() + "]");
				}
			}
		} else if (el.getLocalName().equals("complexContent")) {
			NodeList ls = el.getChildNodes();
			for (int i = 0; i < ls.getLength(); i++) {
				Node node = ls.item(i);
				if (node instanceof Element) {
					Element e = (Element) node;
					if (e.getLocalName().equals("restriction")) {
						if (e.getAttribute("base").contains("Array")) {
							Element ee = (Element) e.getElementsByTagName(
									"attribute").item(0);
							Member we = new Member();
							we.setType(processor.decodeSchemaTypeQName(ee,
									"arrayType"));
							if (we.getType().getLocalPart().endsWith("[]"))
								we.setType(new QName(we.getType()
										.getNamespaceURI(), we
										.getType()
										.getLocalPart()
										.substring(
												0,
												we.getType().getLocalPart()
														.length() - 2), we
										.getType().getPrefix()));
							we.maxOccurs = "unbounded";
							we.name = null;
							getMembers().add(we);
							// getMembers().add(e);
							break;
						}
					} else
						throw new WsdlDecodeException("暂不支持的结点["
								+ e.getLocalName() + "]");
				}
			}
		}
	}

	@Override
	public void debug(String prefix, PrintStream stream) {
		super.debug(prefix, stream);
		stream.println(prefix + "{");
		for (Member m : members) {
			String n = m.getType().getNamespaceURI();
			if (n.length() > 0)
				n = "[" + n + "]";
			stream.println(prefix + "\t" + m.getName() + ": {type="
					+ m.getType().getLocalPart() + n + ",minOccurs="
					+ m.minOccurs + ",maxOccurs=" + m.maxOccurs + ",nullable="
					+ m.nullAble + "}");
		}
		stream.println(prefix + "}");
	}

	public void toJava(Wsdl2JavaProcessor processor, String prefix,
			Stream stream) throws IOException {
		for (Member m : members) {
			String className = processor.getClassName(m.getType().getPrefix(),
					m.getType().getLocalPart());

			if (m.getMaxOccurs() != null
					&& m.getMaxOccurs().equals("unbounded"))
				className += "[]";
			String n = StringUnit.firstWordCap(m.getName()), n1 = m.getName();
			if (n == null) {
				n = "Value";
				n1 = "_value";
			} else
				n1 = "_" + n1;
			stream.writeln(prefix + "\tprivate " + className + " " + n1
					+ ";\r\n", 3000);
			stream.writeln(prefix + "\tpublic " + className + " get" + n
					+ "(){return " + n1 + ";}\r\n", 3000);
			stream.writeln(prefix + "\tpublic void set" + n + "(" + className
					+ " v){this." + n1 + "=v;}\r\n", 3000);
		}
		stream.writeln(
				prefix
						+ "\tpublic void encode(SOAPElement element) throws Exception{",
				3000);

		for (Member m : members) {
			String n = m.getName(), v = m.getName();
			if (n == null) {
				n = "null";
				v = "_value";
			} else {
				n = "\"" + n + "\"";
				v = "_" + v;
			}
			if (m.getMaxOccurs() != null
					&& m.getMaxOccurs().equals("unbounded")) {
				if (m.getType().getPrefix().isEmpty()) {
					stream.writeln(prefix + "\t\tencodeTextElements(element,"
							+ n + "," + v + ");", 3000);
				} else {
					stream.writeln(prefix + "\t\tencodeElements(element," + n
							+ "," + v + ");", 3000);
				}
			} else {
				if (m.getType().getPrefix().isEmpty()) {
					stream.writeln(prefix + "\t\tencodeTextElement(element,"
							+ n + "," + v + ");", 3000);
				} else {
					stream.writeln(prefix + "\t\tencodeElement(element," + n
							+ "," + v + ");", 3000);
				}
			}
		}
		stream.writeln(
				prefix
						+ "\t}\r\n\r\n"
						+ prefix
						+ "\tpublic void decode(WebServiceConnection con,Map<String, List<SOAPElement>> map) throws Exception{",
				3000);
		for (Member m : members) {
			String className = processor.getClassName(m.getType().getPrefix(),
					m.getType().getLocalPart());
			String n = m.getName(), v = m.getName();
			if (n == null) {
				n = "null";
				v = "_value";
			} else {
				n = "\"" + n + "\"";
				v = "_" + v;
			}
			if (m.getMaxOccurs() != null
					&& m.getMaxOccurs().equals("unbounded")) {
				if (m.getType().getPrefix().isEmpty()) {
					stream.writeln(prefix + "\t\t" + v
							+ " = decodeTextElements(con," + n + ",map,"
							+ className + ".class);", 3000);
				} else {
					stream.writeln(prefix + "\t\t" + v
							+ " = decodeElements(con," + n + ",map,"
							+ className + ".class);", 3000);
				}
			} else {
				if (m.getType().getPrefix().isEmpty()) {
					stream.writeln(prefix + "\t\t" + v
							+ " = decodeTextElement(con," + n + ",map,"
							+ className + ".class);", 3000);
				} else {
					stream.writeln(prefix + "\t\t" + v
							+ " = decodeElement(con," + n + ",map," + className
							+ ".class);", 3000);
				}
			}
		}
		stream.writeln(prefix + "\t}", 3000);
	}
}
