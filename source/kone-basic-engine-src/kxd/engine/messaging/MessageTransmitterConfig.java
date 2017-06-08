package kxd.engine.messaging;

import java.util.concurrent.ConcurrentHashMap;

public class MessageTransmitterConfig {
	ConcurrentHashMap<String, String> params = new ConcurrentHashMap<String, String>();
	String name;
	String transmitterClassName;
	String type;

	public MessageTransmitterConfig() {
		super();
	}

	public MessageTransmitterConfig(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTransmitterClassName() {
		return transmitterClassName;
	}

	public void setTransmitterClassName(String transmitterClassName) {
		this.transmitterClassName = transmitterClassName;
	}

	public ConcurrentHashMap<String, String> getParams() {
		return params;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
