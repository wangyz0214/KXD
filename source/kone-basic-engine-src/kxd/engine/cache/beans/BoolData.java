package kxd.engine.cache.beans;

import java.io.IOException;

import kxd.util.stream.Stream;

public class BoolData extends CachedIdObject<Boolean> {
	private static final long serialVersionUID = 1L;

	public BoolData() {
		super();
	}

	public BoolData(boolean data) {
		super(data);
	}

	@Override
	public void readData(Stream stream) throws IOException {
		setId(stream.readBoolean(3000));
	}

	@Override
	public void writeData(Stream stream) throws IOException {
		stream.writeBoolean(getId(), 3000);
	}

}
