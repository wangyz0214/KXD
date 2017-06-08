package kxd.engine.messaging;

import java.util.concurrent.CopyOnWriteArrayList;

public class MessageSender {
	CopyOnWriteArrayList<MessageDestination> destinations = new CopyOnWriteArrayList<MessageDestination>();
	String name;

	public MessageSender() {
		super();
	}

	public MessageSender(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public CopyOnWriteArrayList<MessageDestination> getDestinations() {
		return destinations;
	}

}
