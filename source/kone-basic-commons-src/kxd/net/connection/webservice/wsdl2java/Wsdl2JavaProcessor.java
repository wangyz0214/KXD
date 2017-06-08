package kxd.net.connection.webservice.wsdl2java;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.wsdl.BindingOperation;
import javax.wsdl.Definition;
import javax.wsdl.Operation;
import javax.wsdl.Part;
import javax.wsdl.Port;
import javax.wsdl.Service;
import javax.wsdl.Types;
import javax.wsdl.extensions.schema.Schema;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.wsdl.extensions.soap.SOAPOperation;
import javax.xml.namespace.QName;

import kxd.net.connection.webservice.types.WSConverter;
import kxd.net.connection.webservice.wsdl2java.schema.WsdlSchema;
import kxd.util.StringUnit;
import kxd.util.stream.Stream;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.ibm.wsdl.extensions.soap.SOAPBindingImpl;

public class Wsdl2JavaProcessor {
	Definition define;
	String packageName;
	HashMap<String, WsdlSchema> schemas = new HashMap<String, WsdlSchema>();
	HashMap<String, String> classNames = new HashMap<String, String>();

	public Wsdl2JavaProcessor(Definition define) throws Exception {
		super();
		this.define = define;
		packageName = convertNamespaceToPackageName(define.getTargetNamespace());
		processSchemas();
	}

	private static String convertNamespaceToPackageName(String namespace) {
		namespace = namespace.toLowerCase().trim();
		int i = namespace.indexOf("//");
		if (i > -1)
			namespace = namespace.substring(i + 2);
		i = namespace.indexOf("/");
		if (i > -1)
			namespace = namespace.substring(0, i);
		String n[] = StringUnit.split(namespace, ".");
		namespace = "";
		for (i = n.length - 1; i >= 0; i--) {
			if (i < n.length - 1)
				namespace += ".";
			int index = n[i].lastIndexOf(":");
			if (index > -1)
				namespace += n[i].substring(index + 1);
			else
				namespace += n[i];
		}
		return namespace;
	}

	private void processSchemas() throws Exception {
		Types types = define.getTypes();
		for (Object o : types.getExtensibilityElements()) {
			Schema s = (Schema) o;
			Element el = s.getElement();
			WsdlSchema schema = new WsdlSchema(this, el);
			schemas.put(el.getAttribute("targetNamespace"), schema);
			schema.setPrefix(el.lookupPrefix(el.getAttribute("targetNamespace")));
			System.out.println("schema{[" + schema.getPrefix() + "]"
					+ el.getAttribute("targetNamespace") + "}{");
			schema.debug("\t", System.out);
			System.out.println("}");
		}
	}

	public QName decodeSchemaTypeQName(Element el, String qname) {
		if (!el.hasAttribute(qname)) {
			NamedNodeMap map = el.getAttributes();
			for (int i = 0; i < map.getLength(); i++) {
				Node node = map.item(i);
				String n = node.getLocalName();
				if (n.equals(qname)) {
					qname = node.getNodeValue().trim();
					break;
				}
			}
		} else
			qname = el.getAttribute(qname).trim();
		int index = qname.indexOf(":");
		if (index > -1) {
			String localPart = qname.substring(index + 1);
			String prefix = qname.substring(0, index);
			String namespaceURI = el.lookupNamespaceURI(prefix);
			if (namespaceURI.toLowerCase().startsWith("http://www.w3.org/")) {
				namespaceURI = "";
				prefix = "";
			}
			return new QName(namespaceURI, localPart, prefix);
		} else
			return new QName(qname);
	}

	public void toJava(String root) throws IOException {
		String path = root + "/src/" + packageName + "/";
		path = path.replace(".", "/");
		path = path.replace("\\", "/");
		path = path.replace("//", "/");
		for (Object name : define.getServices().keySet()) {
			Service s = (Service) define.getServices().get(name);
			String className = StringUnit.firstWordCap(s.getQName()
					.getLocalPart());
			File file = new File(path + className + ".java");
			file.getParentFile().mkdirs();
			Stream stream = new Stream(null, new FileOutputStream(file));
			try {
				stream.writeln("package " + packageName + ";", 3000);
				stream.writeln(
						"import kxd.net.connection.webservice.types.*;\r\n"
								+ "import kxd.net.connection.webservice.WebServiceConnection;\r\n",
						3000);
				stream.writeln("import java.util.List;\r\n", 3000);
				stream.writeln("import org.apache.log4j.Logger;\r\n", 3000);
				stream.writeln("import java.util.Map;\r\n", 3000);

				stream.writeln("import javax.xml.soap.SOAPElement;\r\n", 3000);
				stream.writeln("import javax.xml.namespace.QName;\r\n", 3000);
				stream.writeln("import javax.xml.soap.SOAPMessage;\r\n", 3000);
				stream.writeln(
						"import kxd.net.adapters.webservice.WebServiceAdapter;\r\n",
						3000);
				stream.writeln(
						"import kxd.net.adapters.webservice.WebServiceRequest;\r\n",
						3000);
				stream.writeln("public class " + className + "{", 3000);
				for (WsdlSchema schema : schemas.values()) {
					schema.toJava(this, stream);
				}
				for (Object p : s.getPorts().keySet()) {
					Port port = (Port) s.getPorts().get(p);
					Object addr = port.getExtensibilityElements().get(0);
					if (addr instanceof SOAPAddress) {
						stream.writeln(
								"\tpublic static class " + port.getName()
										+ "Adapter extends WebServiceAdapter{",
								3000);
						stream.writeln(
								"\t\tprivate final static Logger logger = Logger.getLogger("
										+ port.getName() + "Adapter.class);",
								3000);
						stream.writeln(
								"\t\tpublic Logger getLogger(){return logger;}",
								3000);
						stream.writeln("\t\tpublic " + port.getName()
								+ "Adapter() throws Exception{", 3000);
						SOAPAddress address = (SOAPAddress) addr;
						stream.writeln(
								"\t\t\tsuper(\"" + address.getLocationURI()
										+ "\");", 3000);

						stream.writeln("\t\t}", 3000);
						boolean isRpcMode = false;
						List<?> ls = port.getBinding()
								.getExtensibilityElements();
						for (Object o : ls) {
							if (o instanceof SOAPBindingImpl) {
								isRpcMode = ((SOAPBindingImpl) o).getStyle()
										.toLowerCase().equals("rpc");
							}
						}
						if (isRpcMode)
							portToJavaRpc(port, "\t", stream);
						else
							portToJavaDocument(port, "\t", stream);
						stream.writeln("\t}", 3000);
					}
				}
				stream.writeln("}", 3000);
			} finally {
				stream.close();
			}
		}
	}

	private void portToJavaRpc(Port port, String prefix, Stream stream)
			throws IOException {
		for (Object o : port.getBinding().getBindingOperations()) {
			BindingOperation oper = (BindingOperation) o;
			Operation op = oper.getOperation();
			String outClassName = "void";
			Part outPart = null;
			if (!op.getOutput().getMessage().getParts().isEmpty()) {
				outPart = (Part) op.getOutput().getMessage().getParts()
						.values().iterator().next();
				outClassName = getClassNameByNamespaceURI(
						outPart.getDocumentationElement(), outPart
								.getTypeName().getNamespaceURI(), outPart
								.getTypeName().getLocalPart());
			}
			stream.writeString(
					prefix + "\tpublic " + outClassName + " " + op.getName()
							+ "(", 3000);
			int i = 0;
			Iterator<?> it = null;
			if (op.getParameterOrdering() != null)
				it = op.getParameterOrdering().iterator();
			else
				it = op.getInput().getMessage().getParts().keySet().iterator();
			while (it.hasNext()) {
				Part part = op.getInput().getMessage()
						.getPart((String) it.next());
				if (i > 0)
					stream.writeString(",", 3000);
				i++;
				String className = getClassNameByNamespaceURI(
						part.getDocumentationElement(), part.getTypeName()
								.getNamespaceURI(), part.getTypeName()
								.getLocalPart());
				stream.writeString(className + " " + part.getName(), 3000);
			}
			stream.writeln(") throws Exception {", 3000);
			stream.writeln(prefix
					+ "\t\tSOAPMessage msg = createSOAPMessage();", 3000);
			stream.writeln(
					prefix
							+ "\t\tSOAPElement el=msg.getSOAPBody().addChildElement(new QName(\""
							+ define.getTargetNamespace() + "\",\""
							+ op.getName() + "\",\""
							+ define.getPrefix(define.getTargetNamespace())
							+ "\"));", 3000);
			if (op.getParameterOrdering() != null)
				it = op.getParameterOrdering().iterator();
			else
				it = op.getInput().getMessage().getParts().keySet().iterator();
			while (it.hasNext()) {
				Part part = op.getInput().getMessage()
						.getPart((String) it.next());
				partToJava(true, part, prefix + "\t", stream);
			}
			String action = "";
			if (oper.getExtensibilityElements().size() > 0)
				action = ((SOAPOperation) oper.getExtensibilityElements()
						.get(0)).getSoapActionURI();
			if (outClassName.equals("void")) {
				stream.writeln(prefix + "\t\treturn call(msg,\"" + action
						+ "\");", 3000);
			} else {
				stream.writeln("\t\t\tSOAPMessage rmsg=call(msg,\"" + action
						+ "\");", 3000);
				stream.writeln("\t\t\tel=getFirstElement(rmsg.getSOAPBody(),\""
						+ outPart.getName() + "\");", 3000);
				if (outPart.getTypeName().getNamespaceURI().toLowerCase()
						.startsWith("http://www.w3.org/")) {
					stream.writeln(prefix + "\t\t" + outClassName
							+ " ret = WSConverter.fromString"
							+ "(el.getTextContent()," + outClassName
							+ ".class);", 3000);
				} else {
					stream.writeln(prefix + "\t\t" + outClassName
							+ " ret = new " + outClassName + "();", 3000);
					stream.writeln("\t\t\tret.decode(this,el);", 3000);
				}
				stream.writeln(prefix + "\t\treturn ret;", 3000);
			}
			stream.writeln(prefix + "\t}", 3000);
		}
		stream.writeln(
				prefix
						+ "\tprotected Object doExecute(WebServiceRequest data) throws "
						+ "Exception{", 3000);
		stream.writeln(prefix + "\t\tObject r=null;", 3000);
		int j = 0;
		for (Object o : port.getBinding().getBindingOperations()) {
			BindingOperation oper = (BindingOperation) o;
			Operation op = oper.getOperation();
			if (j == 0) {
				stream.writeln(prefix + "\t\tif(data.getOperation().equals(\""
						+ op.getName() + "\")){", 3000);
			} else
				stream.writeln(
						prefix + "\t\t}else if(data.getOperation().equals(\""
								+ op.getName() + "\")){", 3000);
			j++;
			if (!op.getOutput().getMessage().getParts().isEmpty()) {
				stream.writeString(prefix + "\t\t\tr=" + op.getName() + "(",
						3000);
			} else
				stream.writeString(prefix + "\t\t\t" + op.getName() + "(", 3000);
			int i = 0;
			Iterator<?> it = null;
			if (op.getParameterOrdering() != null)
				it = op.getParameterOrdering().iterator();
			else
				it = op.getInput().getMessage().getParts().keySet().iterator();
			while (it.hasNext()) {
				Part part = op.getInput().getMessage()
						.getPart((String) it.next());
				if (i > 0)
					stream.writeString(",", 3000);
				String className = getClassNameByNamespaceURI(
						part.getDocumentationElement(), part.getTypeName()
								.getNamespaceURI(), part.getTypeName()
								.getLocalPart());
				stream.writeString("(" + className + ")data.getParams()[" + i
						+ "]", 3000);
				i++;
			}
			stream.writeln(");", 3000);
			if (op.getOutput().getMessage().getParts().isEmpty())
				stream.writeString(prefix
						+ "\t\t\tr=new kxd.net.VoidNetResponse();", 3000);
		}
		if (port.getBinding().getBindingOperations().size() > 0)
			stream.writeln(prefix + "\t\t}", 3000);
		stream.writeln(prefix + "\t\treturn r;", 3000);
		stream.writeln(prefix + "\t}", 3000);
	}

	private void portToJavaDocument(Port port, String prefix, Stream stream)
			throws IOException {
		for (Object o : port.getBinding().getBindingOperations()) {
			BindingOperation oper = (BindingOperation) o;
			Operation op = oper.getOperation();
			String outClassName = "void";
			Part outPart = null;
			if (!op.getOutput().getMessage().getParts().isEmpty()) {
				outPart = (Part) op.getOutput().getMessage().getParts()
						.values().iterator().next();
				outClassName = getClassNameByNamespaceURI(
						outPart.getDocumentationElement(), outPart
								.getElementName().getNamespaceURI(), outPart
								.getElementName().getLocalPart());
			}
			stream.writeString(
					prefix + "\tpublic " + outClassName + " " + op.getName()
							+ "(", 3000);
			int i = 0;
			Iterator<?> it = null;
			if (op.getParameterOrdering() != null)
				it = op.getParameterOrdering().iterator();
			else
				it = op.getInput().getMessage().getParts().keySet().iterator();
			while (it.hasNext()) {
				Part part = op.getInput().getMessage()
						.getPart((String) it.next());
				if (i > 0)
					stream.writeString(",", 3000);
				i++;
				String className = getClassNameByNamespaceURI(
						part.getDocumentationElement(), part.getElementName()
								.getNamespaceURI(), part.getElementName()
								.getLocalPart());
				stream.writeString(className + " " + part.getName(), 3000);
			}
			stream.writeln(") throws Exception {", 3000);
			stream.writeln(prefix
					+ "\t\tSOAPMessage msg = createSOAPMessage();", 3000);
			stream.writeln(
					prefix
							+ "\t\tSOAPElement el=msg.getSOAPBody().addChildElement(new QName(\""
							+ define.getTargetNamespace() + "\",\""
							+ op.getName() + "\",\""
							+ define.getPrefix(define.getTargetNamespace())
							+ "\"));", 3000);
			if (op.getParameterOrdering() != null)
				it = op.getParameterOrdering().iterator();
			else
				it = op.getInput().getMessage().getParts().keySet().iterator();
			while (it.hasNext()) {
				Part part = op.getInput().getMessage()
						.getPart((String) it.next());
				partToJava(false, part, prefix + "\t", stream);
			}
			String action = "";
			if (oper.getExtensibilityElements().size() > 0)
				action = ((SOAPOperation) oper.getExtensibilityElements()
						.get(0)).getSoapActionURI();
			if (outClassName.equals("void")) {
				stream.writeln(prefix + "\t\tcall(msg,\"" + action + "\");",
						3000);
			} else {
				stream.writeln("\t\t\tSOAPMessage rmsg=call(msg,\"" + action
						+ "\");", 3000);
				stream.writeln("\t\t\tel=getFirstElement(rmsg.getSOAPBody(),\""
						+ outPart.getElementName().getLocalPart() + "\");",
						3000);
				if (outPart.getElementName().getNamespaceURI().toLowerCase()
						.startsWith("http://www.w3.org/")) {
					stream.writeln(prefix + "\t\t" + outClassName
							+ " ret = WSConverter.fromString"
							+ "(el.getTextContent()," + outClassName
							+ ".class);", 3000);
				} else {
					stream.writeln(prefix + "\t\t" + outClassName
							+ " ret = new " + outClassName + "();", 3000);
					stream.writeln("\t\t\tret.decode(this,el);", 3000);
				}
				stream.writeln(prefix + "\t\treturn ret;", 3000);
			}
			stream.writeln(prefix + "\t}", 3000);
		}
		stream.writeln(
				prefix
						+ "\tprotected Object doExecute(WebServiceRequest data) throws "
						+ "Exception{", 3000);
		stream.writeln(prefix + "\t\tObject r=null;", 3000);
		int j = 0;
		for (Object o : port.getBinding().getBindingOperations()) {
			BindingOperation oper = (BindingOperation) o;
			Operation op = oper.getOperation();
			if (j == 0) {
				stream.writeln(prefix + "\t\tif(data.getOperation().equals(\""
						+ op.getName() + "\")){", 3000);
			} else
				stream.writeln(
						prefix + "\t\t}else if(data.getOperation().equals(\""
								+ op.getName() + "\")){", 3000);
			j++;
			if (!op.getOutput().getMessage().getParts().isEmpty()) {
				stream.writeString(prefix + "\t\t\tr=" + op.getName() + "(",
						3000);
			} else
				stream.writeString(prefix + "\t\t\t" + op.getName() + "(", 3000);
			int i = 0;
			Iterator<?> it = null;
			if (op.getParameterOrdering() != null)
				it = op.getParameterOrdering().iterator();
			else
				it = op.getInput().getMessage().getParts().keySet().iterator();
			while (it.hasNext()) {
				Part part = op.getInput().getMessage()
						.getPart((String) it.next());
				if (i > 0)
					stream.writeString(",", 3000);
				String className = getClassNameByNamespaceURI(
						part.getDocumentationElement(), part.getElementName()
								.getNamespaceURI(), part.getElementName()
								.getLocalPart());
				stream.writeString("(" + className + ")data.getParams()[" + i
						+ "]", 3000);
				i++;
			}
			stream.writeln(");", 3000);
			if (op.getOutput().getMessage().getParts().isEmpty())
				stream.writeString(prefix
						+ "\t\t\tr=new kxd.net.VoidNetResponse();", 3000);
		}
		if (port.getBinding().getBindingOperations().size() > 0)
			stream.writeln(prefix + "\t\t}", 3000);
		stream.writeln(prefix + "\t\treturn r;", 3000);
		stream.writeln(prefix + "\t}", 3000);
	}

	private void partToJava(boolean isRpc, Part part, String prefix,
			Stream stream) throws IOException {
		QName name = isRpc ? part.getTypeName() : part.getElementName();
		if (isRpc) {
			if (name.getNamespaceURI().toLowerCase()
					.startsWith("http://www.w3.org/")) {
				stream.writeln(
						prefix + "\t" + "el.addChildElement(\""
								+ part.getName() + "\").addTextNode("
								+ part.getName() + ".toString());", 3000);
			} else {
				stream.writeln(prefix + "\t" + part.getName()
						+ ".encode(el.addChildElement(\"" + part.getName()
						+ "\"));", 3000);
			}
		} else {
			if (name.getNamespaceURI().toLowerCase()
					.startsWith("http://www.w3.org/")) {
				stream.writeln(
						prefix + "\t" + "el.addTextNode(" + part.getName()
								+ ".toString());", 3000);
			} else {
				stream.writeln(prefix + "\t" + part.getName() + ".encode(el);",
						3000);
			}
		}
	}

	public String getClassName(String prefix, String type) throws IOException {
		if (prefix.length() > 0) {
			// String v = this.define.getNamespace(prefix);
			String tt = type;// v + "_" + type;
			String name = classNames.get(tt);
			if (name == null) {
				name = type;
				if (type.equals("Exception"))
					name = "FaultException";
				Collection<String> c = classNames.values();
				if (c.contains(name))
					for (int i = 1; true; i++) {
						name = type + i;
						if (!c.contains(name))
							break;
					}
				name = StringUnit.firstWordCap(name);
				classNames.put(tt, name);
			}
			return name;
		} else {
			return WSConverter.getStandardTypeName(type);
		}
	}

	public String getClassNameByNamespaceURI(Element element,
			String namespaceURI, String type) throws IOException {
		String prefix = "";
		if (!namespaceURI.toLowerCase().startsWith("http://www.w3.org/"))
			prefix = define.getPrefix(namespaceURI);
		return getClassName(prefix, type);
	}
}
