package kxd.net.connection.webservice.wsdl2java;

import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;

public class Wsdl2Java {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length > 1)
			wsdl2Java(args[0], args[1]);
		else
			// wsdl2Java(
			// "http://10.143.131.217:6001/selfterminal/services/SelfTerminalInterface?wsdl",
			// "d:\\temp");
			// wsdl2Java("http://localhost:8080/webservices/RpcHelloWorld?wsdl",
			// "d:\\temp");
			wsdl2Java(
					"http://vastest.10010.com:8001/vas/services/vasService?wsdl",
					"d:\\temp");
	}

	public static void wsdl2Java(String wsdlUri, String rootPath) {
		try {
			WSDLFactory factory = WSDLFactory.newInstance();
			WSDLReader reader = factory.newWSDLReader();
			reader.setFeature("javax.wsdl.verbose", true);
			reader.setFeature("javax.wsdl.importDocuments", true);
			new Wsdl2JavaProcessor(reader.readWSDL(null, wsdlUri))
					.toJava(rootPath);
			// Part a;

			// Map<?, ?> map = def.getAllPortTypes();
			// for (Object o : map.keySet()) {
			// PortType v = (PortType)map.get(o);
			// v.getOperations()
			// System.out.println(o + "=" + v);
			// }
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
