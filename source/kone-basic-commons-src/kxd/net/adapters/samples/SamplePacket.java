package kxd.net.adapters.samples;

import java.io.IOException;
import java.util.Date;

import kxd.net.NetResponse;
import kxd.net.adapters.AsynResponse;
import kxd.net.adapters.QueueRequest;
import kxd.util.stream.AbstractStream;

public class SamplePacket implements QueueRequest, NetResponse,
		AsynResponse<Integer> {
	private static final long serialVersionUID = 2597387173825542022L;
	String data;
	private Date sendTime, recvTime, connectedTime;
	int id = 0;

	public SamplePacket() {
		super();
	}

	public SamplePacket(String data) {
		super();
		this.data = data;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public void read(AbstractStream stream, int timeout) throws IOException {
		id = stream.readInt(false, timeout);
		data = stream.readPacketByteString(timeout);
	}

	public void write(AbstractStream stream, int timeout) throws IOException {
		stream.writeInt(id, false, timeout);
		stream.writePacketByteString(data, timeout);
	}

	@Override
	public String toString() {
		return "SamplePacket [data=" + data + "]";
	}

	@Override
	public boolean isTimeoutDiscarded() {
		return true;
	}

	public Date getSendTime() {
		return sendTime;
	}

	public void setSendTime(Date sendTime) {
		this.sendTime = sendTime;
	}

	public Date getRecvTime() {
		return recvTime;
	}

	public void setRecvTime(Date recvTime) {
		this.recvTime = recvTime;
	}

	public Date getConnectedTime() {
		return connectedTime;
	}

	public void setConnectedTime(Date connectedTime) {
		this.connectedTime = connectedTime;
	}

	@Override
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
}
