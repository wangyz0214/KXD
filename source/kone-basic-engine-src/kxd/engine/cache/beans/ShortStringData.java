package kxd.engine.cache.beans;

import java.io.IOException;

import kxd.util.stream.Stream;

public class ShortStringData extends CachedIdObject<String> {
	private static final long serialVersionUID = 1L;

	public ShortStringData() {
		super();
	}

	public ShortStringData(String data) {
		super(data);
	}

	@Override
	public void readData(Stream stream) throws IOException {
		setId(stream.readPacketByteString(3000));
		super.readData(stream);
	}

	@Override
	public void writeData(Stream stream) throws IOException {
		stream.writePacketByteString(getId(), 3000);
		super.writeData(stream);
	}
}
