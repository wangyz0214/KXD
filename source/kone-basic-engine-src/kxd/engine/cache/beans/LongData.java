package kxd.engine.cache.beans;

import java.io.IOException;

import kxd.util.stream.Stream;

public class LongData extends CachedIdObject<Long> {
	private static final long serialVersionUID = 1L;

	public LongData() {
		super();
	}

	public LongData(long data) {
		super(data);
	}

	@Override
	public void readData(Stream stream) throws IOException {
		setId(stream.readLong(3000));
		super.readData(stream);
	}

	@Override
	public void writeData(Stream stream) throws IOException {
		stream.writeLong(getId(), 3000);
		super.writeData(stream);
	}
}
