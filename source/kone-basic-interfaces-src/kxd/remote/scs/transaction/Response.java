package kxd.remote.scs.transaction;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Response {

	Document document;

	Element contentElement;

	Element resultElement;

	public Response(Document xmlDoc, Element content, Element result) {

		document = xmlDoc;
		contentElement = content;
		resultElement = result;
	}

	public Element createElement(String tagName) {

		return document.createElement(tagName);
	}

	public Document getDocument() {

		return document;
	}

	public void setDocument(Document document) {

		this.document = document;
	}

	public Element getContentElement() {

		return contentElement;
	}

	public void setContentElement(Element contentElement) {

		this.contentElement = contentElement;
	}

	public Element getResultElement() {

		return resultElement;
	}

	public void setResultElement(Element resultElement) {

		this.resultElement = resultElement;
	}
}
