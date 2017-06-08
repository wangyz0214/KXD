package kxd.engine.cache.beans;

import java.io.IOException;

import kxd.util.stream.Stream;

public class IntegerData extends CachedIdObject<Integer> {
	private static final long serialVersionUID = 1L;

	public IntegerData() {
		super();
	}

	public IntegerData(int data) {
		super(data);
	}

	@Override
	public void readData(Stream stream) throws IOException {
		setId(stream.readInt(false, 3000));
	}

	@Override
	public void writeData(Stream stream) throws IOException {
		stream.writeInt(getId(), false, 3000);
	}

}
