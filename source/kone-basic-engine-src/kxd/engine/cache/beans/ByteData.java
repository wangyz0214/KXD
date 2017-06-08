package kxd.engine.cache.beans;

import java.io.IOException;

import kxd.util.stream.Stream;

public class ByteData extends CachedIdObject<Byte> {
	private static final long serialVersionUID = 1L;

	public ByteData() {
		super();
	}

	public ByteData(byte data) {
		super(data);
	}

	@Override
	public void readData(Stream stream) throws IOException {
		setId(stream.readByte(3000));
	}

	@Override
	public void writeData(Stream stream) throws IOException {
		stream.writeByte(getId(), 3000);
	}

}
