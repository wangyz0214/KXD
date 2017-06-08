package kxd.net.connection.webservice.types;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;

import kxd.net.connection.webservice.WebServiceConnection;

abstract public class WSClass implements Serializable {
	private static final long serialVersionUID = 1L;

	abstract public void encode(SOAPElement element) throws Exception;

	abstract public void decode(WebServiceConnection con,
			Map<String, List<SOAPElement>> map) throws Exception;

	public void decode(WebServiceConnection con, SOAPElement element)
			throws Exception {
		if (element != null) {
			if (element.hasAttribute("href")) {
				String id = element.getAttribute("href").trim();
				if (id.startsWith("#"))
					id = id.substring(1);
				element = con.getIdElementMap().get(id);
				if (element == null)
					throw new Exception("can not found element[id=#" + id + "]");
			}
			java.util.Iterator<?> it = element.getChildElements();
			HashMap<String, List<SOAPElement>> map = new HashMap<String, List<SOAPElement>>();
			while (it.hasNext()) {
				Object o = it.next();
				SOAPElement el = (SOAPElement) o;
				String name = el.getLocalName();
				List<SOAPElement> ls = map.get(name);
				if (ls == null) {
					ls = new ArrayList<SOAPElement>();
					map.put(name, ls);
				}
				ls.add(el);
			}
			try {
				decode(con, map);
			} finally {
				map.clear();
				map = null;
			}
		}
	}

	public <T> void encodeTextElement(SOAPElement parent, String localName,
			T value) throws SOAPException {
		if (localName != null)
			parent.addChildElement(localName).addTextNode(
					WSConverter.toString(value));
		else
			parent.addTextNode(WSConverter.toString(value));
	}

	public <T> void encodeTextElements(SOAPElement parent, String localName,
			T[] value) throws SOAPException {
		if (localName != null) {
			for (T o : value)
				parent.addChildElement(localName).addTextNode(
						WSConverter.toString(o));
		} else {
			for (T o : value)
				parent.addTextNode(WSConverter.toString(o));
		}
	}

	public <T extends WSClass> void encodeElements(SOAPElement parent,
			String localName, T[] value) throws Exception {
		for (T o : value)
			o.encode(localName != null ? parent.addChildElement(localName)
					: parent);
	}

	public <T extends WSClass> void encodeElement(SOAPElement parent,
			String localName, T value) throws Exception {
		value.encode(localName != null ? parent.addChildElement(localName)
				: parent);
	}

	public <T> T decodeTextElement(WebServiceConnection con, String localName,
			Map<String, List<SOAPElement>> map, Class<T> clazz)
			throws Exception {
		if (localName == null) {
			Collection<List<SOAPElement>> c = map.values();
			for (List<SOAPElement> ls : c) {
				if (ls.size() > 0) {
					T ret = WSConverter.fromString(ls.get(0).getTextContent(),
							clazz);
					return ret;
				}
			}
			return null;
		} else {
			List<SOAPElement> ls = map.get(localName);
			if (ls != null && ls.size() > 0) {
				T ret = WSConverter.fromString(ls.get(0).getTextContent(),
						clazz);
				return ret;
			} else
				return null;
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T[] decodeTextElements(WebServiceConnection con,
			String localName, Map<String, List<SOAPElement>> map, Class<T> clazz)
			throws Exception {
		if (localName == null) {
			Collection<List<SOAPElement>> c = map.values();
			int size = 0;
			for (List<SOAPElement> ls : c) {
				size += ls.size();
			}
			T[] ret = (T[]) Array.newInstance(clazz, size);
			int i = 0;
			for (List<SOAPElement> ls : c) {
				for (SOAPElement e : ls) {
					ret[i] = WSConverter.fromString(e.getTextContent(), clazz);
					i++;
				}
			}
			return ret;
		} else {
			List<SOAPElement> ls = map.get(localName);
			if (ls != null && ls.size() > 0) {
				T[] ret = (T[]) Array.newInstance(clazz, ls.size());
				for (int i = 0; i < ret.length; i++) {
					ret[i] = WSConverter.fromString(ls.get(i).getTextContent(),
							clazz);
				}
				return ret;
			} else
				return null;
		}
	}

	public <T extends WSClass> T decodeElement(WebServiceConnection con,
			String localName, Map<String, List<SOAPElement>> map, Class<T> clazz)
			throws Exception {
		if (localName == null) {
			Collection<List<SOAPElement>> c = map.values();
			for (List<SOAPElement> ls : c) {
				if (ls.size() > 0) {
					T ret = clazz.newInstance();
					ret.decode(con, ls.get(0));
					return ret;
				}
			}
			return null;
		} else {
			List<SOAPElement> ls = map.get(localName);
			if (ls != null && ls.size() > 0) {
				T ret = clazz.newInstance();
				ret.decode(con, ls.get(0));
				return ret;
			} else
				return null;
		}
	}

	@SuppressWarnings("unchecked")
	public <T extends WSClass> T[] decodeElements(WebServiceConnection con,
			String localName, Map<String, List<SOAPElement>> map, Class<T> clazz)
			throws Exception {
		if (localName == null) {
			Collection<List<SOAPElement>> c = map.values();
			int size = 0;
			for (List<SOAPElement> ls : c) {
				size += ls.size();
			}
			T[] ret = (T[]) Array.newInstance(clazz, size);
			int i = 0;
			for (List<SOAPElement> ls : c) {
				for (SOAPElement e : ls) {
					ret[i] = clazz.newInstance();
					ret[i].decode(con, e);
					i++;
				}
			}
			return ret;
		} else {
			List<SOAPElement> ls = map.get(localName);
			if (ls != null && ls.size() > 0) {
				T[] ret = (T[]) Array.newInstance(clazz, ls.size());
				for (int i = 0; i < ret.length; i++) {
					ret[i] = clazz.newInstance();
					ret[i].decode(con, ls.get(i));
				}
				return ret;
			} else
				return null;
		}
	}
}
