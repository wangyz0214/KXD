package kxd.engine.cache.beans;

import java.io.IOException;

import kxd.util.stream.Stream;

public class ShortData extends CachedIdObject<Short> {
	private static final long serialVersionUID = 1L;

	public ShortData() {
		super();
	}

	public ShortData(short data) {
		super(data);
	}

	@Override
	public void readData(Stream stream) throws IOException {
		setId(stream.readShort(false, 3000));
		super.readData(stream);
	}

	@Override
	public void writeData(Stream stream) throws IOException {
		stream.writeShort(getId(), false, 3000);
		super.writeData(stream);
	}

}
