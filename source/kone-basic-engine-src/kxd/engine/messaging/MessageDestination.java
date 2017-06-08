package kxd.engine.messaging;

/**
 * 消息目的地
 * 
 * @author zhaom
 * 
 */
public class MessageDestination {
	/**
	 * 消息目的地地址
	 */
	private String[] to;
	/**
	 * 消息传送器名称
	 */
	private String transmitter;

	public MessageDestination() {
		super();
	}

	public MessageDestination(String[] to, String transmitter) {
		super();
		this.to = to;
		this.transmitter = transmitter;
	}

	public String[] getTo() {
		return to;
	}

	public void setTo(String[] to) {
		this.to = to;
	}

	public String getTransmitter() {
		return transmitter;
	}

	public void setTransmitter(String transmitter) {
		this.transmitter = transmitter;
	}

}
