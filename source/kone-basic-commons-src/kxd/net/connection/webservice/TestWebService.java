package kxd.net.connection.webservice;

import java.io.ByteArrayOutputStream;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPMessage;

import kxd.util.DateTime;

public class TestWebService {
	static void testHelloWorld() {
		try {
			WebServiceConnection con = new WebServiceConnection(
					"http://localhost:8080/webservices/HelloWorld");
			SOAPMessage msg = con.createSOAPMessage();
			msg.getSOAPBody()
					.addChildElement(
							new QName("http://webservices.kxd/",
									"testStringArray", "ns1"))
					.addChildElement("name").addTextNode("赵明");
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			msg.writeTo(stream);
			System.out.println(stream.toString("utf-8"));
			SOAPMessage res = con.call(msg, "");
			stream = new ByteArrayOutputStream();
			res.writeTo(stream);
			System.out.println(stream.toString("utf-8"));
		} catch (Throwable e1) {
			e1.printStackTrace();
		}
	}

	static void testWeather() {
		try {
			WebServiceConnection con = new WebServiceConnection(
					"http://webservice.webxml.com.cn/WebServices/WeatherWS.asmx");
			SOAPMessage msg = con.createSOAPMessage();
			msg.getSOAPBody().addChildElement(
					new QName("http://WebXml.com.cn/", "getRegionDataset",
							"ns1"));
			SOAPMessage res = con.call(msg,
					"http://WebXml.com.cn/getRegionDataset");
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			res.writeTo(stream);
			System.out.println(stream.toString("utf-8"));
		} catch (Throwable e1) {
			e1.printStackTrace();
		}
	}

	static void testLinkage() {
		try {
			WebServiceConnection con = new WebServiceConnection(
					"http://10.143.131.217:6001/selfterminal/services/SelfTerminalInterface");
			SOAPMessage msg = con.createSOAPMessage();
			SOAPElement el = msg
					.getSOAPBody()
					.addChildElement(
							new QName("http://req.selfterminal.linkage.com",
									"FixFeeQuery", "ns1"))
					.addChildElement("request");
			el.addChildElement("customiD").addTextNode("6970228");
			el.addChildElement("rigionID").addTextNode("0391");
			el.addChildElement("terminalID").addTextNode("test");
			el.addChildElement("timeStamp").addTextNode(
					new DateTime().format("yyyyMMddHHmmss"));
			el.addChildElement("netType").addTextNode("03");
			el.addChildElement("transactionID").addTextNode(
					"20999"
							+ new DateTime().format("yyyyMMddHHmmss")
									.substring(2) + "001");
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			msg.writeTo(stream);
			System.out.println(stream.toString("utf-8"));
			SOAPMessage res = con.call(msg, "");
			stream = new ByteArrayOutputStream();
			res.writeTo(stream);
			System.out.println(stream.toString());
		} catch (Throwable e1) {
			e1.printStackTrace();
		}
	}

	static void testAsiainfo() {
		try {
			WebServiceConnection con = new WebServiceConnection(
					"http://10.143.131.204:9003/interface/xservices/eCardAction");
			SOAPMessage msg = con.createSOAPMessage();
			SOAPElement el = msg.getSOAPBody().addChildElement(
					new QName("http://service.asiainfo.com",
							"getServiceByPlatForm", "ns1"));
			el.addChildElement("in0")
					.addTextNode(
							"<?xml version=\"1.0\" encoding=\"utf-8\"?>"
									+ "<UniBSS><OrigDomain>ECIP</OrigDomain><HomeDomain>UCRM</HomeDomain><BIPCode>"
									+ "BIP2S015</BIPCode><BIPVer>0100</BIPVer><ActivityCode>T2000015</ActivityCode>"
									+ "<ActionCode>0</ActionCode><ActionRelation>0</ActionRelation><Routing><RouteType>"
									+ "01</RouteType><RouteValue>18676663134</RouteValue></Routing><ProcID>J9810122822362703768"
									+ "</ProcID><TransIDO>kxd01342010122822362740226</TransIDO><ProcessTime>20101228223627"
									+ "</ProcessTime><TestFlag>0</TestFlag><MsgSender>0000</MsgSender><MsgReceiver>1111"
									+ "</MsgReceiver><SvcContVer>0100</SvcContVer><SvcCont><![CDATA[<?xml version=\"1.0\" encoding=\"utf-8\"?><Record><UserNumber>18676663134</UserNumber><begin_date>20101001</begin_date><end_date>20101031</end_date></Record>]]></SvcCont></UniBSS>");
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			msg.writeTo(stream);
			System.out.println(stream.toString("utf-8"));
			SOAPMessage res = con.call(msg, "");
			stream = new ByteArrayOutputStream();
			res.writeTo(stream);
			System.out.println(stream.toString());
		} catch (Throwable e1) {
			e1.printStackTrace();
		}
	}

	public static void main(String[] args) {
		testHelloWorld();
	}
}
