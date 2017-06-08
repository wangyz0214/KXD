package kxd.engine.scs.invoice.template;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import kxd.engine.cache.beans.sts.CachedTerm;
import kxd.util.stream.Stream;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class InvoiceTemplate implements TemplateObjectable {
	private static final long serialVersionUID = 1L;
	Footer footer;
	Detail detail;
	Header header;
	int width;
	int left;

	public InvoiceTemplate() {
		super();
		footer = new Footer();
		detail = new Detail();
		header = new Header();
	}

	@Override
	public void readData(Stream stream) throws IOException {
		left = stream.readInt(false, 3000);
		width = stream.readInt(false, 3000);
		header.readData(stream);
		detail.readData(stream);
		footer.readData(stream);
	}

	@Override
	public void writeData(Stream stream) throws IOException {
		stream.writeInt(left, false, 3000);
		stream.writeInt(width, false, 3000);
		header.writeData(stream);
		detail.writeData(stream);
		footer.writeData(stream);
	}

	@Override
	public int getDataType() {
		return OBJ_TEMPLATE;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getLeft() {
		return left;
	}

	public void setLeft(int left) {
		this.left = left;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public Footer getFooter() {
		return footer;
	}

	public Detail getDetail() {
		return detail;
	}

	public Header getHeader() {
		return header;
	}

	static public void main(String[] args) throws Exception {
		FileInputStream in = new FileInputStream("d:\\template2.xml");
		byte[] b = new byte[in.available()];
		in.read(b);
		InvoiceTemplate it = new InvoiceTemplate();
		it.decode(new String(b));
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("taxno", "as;dfljsakfjsalkfd");
		params.put("userno", "13509626798");
		params.put("smallmoney", "100");
		params.put("bigmoney", "壹佰元整");
		List<String[]> details = new ArrayList<String[]>();
		details.add(new String[] { "月租费", "30元" });
		details.add(new String[] { "通讯费", "30元" });
		details.add(new String[] { "短信费", "30元" });
		details.add(new String[] { "其他其他其他其他其他费", "30元" });
		details.add(new String[] { "月租费", "30元" });
		details.add(new String[] { "通讯费", "30元" });
		details.add(new String[] { "短信费", "30元" });
		details.add(new String[] { "其他其他其他其他其他费", "30元" });
		details.add(new String[] { "月租费", "30元" });
		details.add(new String[] { "通讯费", "30元" });
		details.add(new String[] { "短信费", "30元" });
		details.add(new String[] { "其他其他其他其他其他费", "30元" });
		List<String> ls = new ArrayList<String>();
		it.format(null, params, details, ls);
		for (int i = 0; i < ls.size(); i++)
			System.out.println(i + ". " + ls.get(i));
	}

	/**
	 * 解析模板
	 * 
	 * @param template
	 * @throws Exception
	 */
	public void decode(String template) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(new ByteArrayInputStream(template
				.getBytes()));
		NodeList list = doc.getElementsByTagName("template");
		setWidth(0);
		setLeft(0);
		footer.setRows(0);
		footer.getItems().clear();
		header.setRows(0);
		header.getItems().clear();
		detail.setRows(0);
		detail.getItems().clear();
		if (list.getLength() > 0) {
			Element el = (Element) list.item(0);
			if (el.hasAttribute("width"))
				setWidth(Integer.valueOf(el.getAttribute("width")));
			if (el.hasAttribute("left"))
				setLeft(Integer.valueOf(el.getAttribute("left")));
		}
		list = doc.getElementsByTagName("header");
		for (int i = 0; i < list.getLength(); i++) {
			Node node = list.item(i);
			Element el = (Element) node;
			if (el.hasAttribute("rows"))
				header.setRows(Integer.valueOf(el.getAttribute("rows")));
			NodeList list1 = node.getChildNodes();
			for (int j = 0; j < list1.getLength(); j++) {
				node = list1.item(j);
				if ("row".equals(node.getNodeName())) {
					header.getItems().add(decodeRow((Element) node));
				} else if ("adrow".equals(node.getNodeName())) {
					header.getItems().add(decodeAdRow((Element) node));
				}
			}
		}
		list = doc.getElementsByTagName("footer");
		for (int i = 0; i < list.getLength(); i++) {
			Node node = list.item(i);
			Element el = (Element) node;
			if (el.hasAttribute("rows"))
				footer.setRows(Integer.valueOf(el.getAttribute("rows")));
			NodeList list1 = node.getChildNodes();
			for (int j = 0; j < list1.getLength(); j++) {
				node = list1.item(j);
				if ("row".equals(node.getNodeName())) {
					footer.getItems().add(decodeRow((Element) node));
				} else if ("adrow".equals(node.getNodeName())) {
					footer.getItems().add(decodeAdRow((Element) node));
				}
			}
		}
		list = doc.getElementsByTagName("list");
		for (int i = 0; i < list.getLength(); i++) {
			Node node = list.item(i);
			Element el = (Element) node;
			if (el.hasAttribute("rows"))
				detail.setRows(Integer.valueOf(el.getAttribute("rows")));
			if (el.hasAttribute("row_distances"))
				detail.setRowDistances(Integer.valueOf(el
						.getAttribute("row_distances")));
			if (el.hasAttribute("left"))
				detail.setLeft(Integer.valueOf(el.getAttribute("left")));
			NodeList list1 = node.getChildNodes();
			for (int j = 0; j < list1.getLength(); j++) {
				node = list1.item(j);
				if ("cols".equals(node.getNodeName())) {
					detail.getItems().addAll(
							decodeCols((Element) node).getItems());
				}
			}
		}
	}

	protected Columns decodeCols(Element el) {
		Columns row = new Columns();
		NodeList list = el.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			Node node = list.item(i);
			if ("col".equals(node.getNodeName())) {
				el = (Element) node;
				Column field = new Column();
				if (el.hasAttribute("left"))
					field.setLeft(Integer.valueOf(el.getAttribute("left")));
				if (el.hasAttribute("label_align"))
					field.setLabel_align(el.getAttribute("label_align"));
				if (el.hasAttribute("value_align"))
					field.setValue_align(el.getAttribute("value_align"));
				if (el.hasAttribute("label_fillchar"))
					field.setLabel_fillchar((byte) el.getAttribute(
							"label_fillchar").charAt(0));
				if (el.hasAttribute("value_fillchar"))
					field.setValue_fillchar((byte) el.getAttribute(
							"value_fillchar").charAt(0));
				if (el.hasAttribute("label_width"))
					field.setLabel_width(Integer.valueOf(el
							.getAttribute("label_width")));
				if (el.hasAttribute("value_width"))
					field.setValue_width(Integer.valueOf(el
							.getAttribute("value_width")));
				if (el.hasAttribute("regexp"))
					field.setRegexp(el.getAttribute("regexp"));
				if (el.hasAttribute("wordbreak-lines"))
					field.setWordbreak_lines(Integer.valueOf(el
							.getAttribute("wordbreak-lines")));
				row.getItems().add(field);
			}
		}
		return row;
	}

	protected Row decodeRow(Element el) {
		Row row = new Row();
		if (el.hasAttribute("left"))
			row.setLeft(Integer.valueOf(el.getAttribute("left")));
		if (el.hasAttribute("style"))
			row.setStyle(el.getAttribute("style"));
		NodeList list = el.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			Node node = list.item(i);
			if ("field".equals(node.getNodeName())) {
				el = (Element) node;
				Field field = new Field();
				if (el.hasAttribute("align"))
					field.setAlign(el.getAttribute("align"));
				if (el.hasAttribute("desp"))
					field.setDesp(el.getAttribute("desp"));
				if (el.hasAttribute("fillchar"))
					field.setFillchar((byte) el.getAttribute("fillchar")
							.charAt(0));
				if (el.hasAttribute("name"))
					field.setName(el.getAttribute("name"));
				if (el.hasAttribute("value"))
					field.setValue(el.getAttribute("value"));
				if (el.hasAttribute("regexp"))
					field.setRegexp(el.getAttribute("regexp"));
				if (el.hasAttribute("width"))
					field.setWidth(Integer.valueOf(el.getAttribute("width")));
				if (el.hasAttribute("wordbreak-lines"))
					field.setWordbreak_lines(Integer.valueOf(el
							.getAttribute("wordbreak-lines")));
				row.getItems().add(field);
			}
		}
		return row;
	}

	protected AdRow decodeAdRow(Element el) {
		AdRow row = new AdRow();
		if (el.hasAttribute("adcategory"))
			row.setAdcategoryid(Integer.valueOf(el.getAttribute("adcategory")));
		if (el.hasAttribute("align"))
			row.setAlign(el.getAttribute("align"));
		if (el.hasAttribute("width"))
			row.setWidth(Integer.valueOf(el.getAttribute("width")));
		if (el.hasAttribute("wordbreak-lines"))
			row.setWordbreak_lines(Integer.valueOf(el
					.getAttribute("wordbreak-lines")));
		if (el.hasAttribute("fillchar"))
			row.setFillchar((byte) el.getAttribute("fillchar").charAt(0));
		return row;
	}

	public void format(CachedTerm term, Map<String, String> params,
			List<String[]> details, List<String> outList) throws Exception {
		header.format(term, params, outList);
		detail.format(details, outList);
		footer.format(term, params, outList);
	}

}
