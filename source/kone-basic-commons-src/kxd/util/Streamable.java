package kxd.util;

import java.io.IOException;

import kxd.util.stream.Stream;

public interface Streamable {
	public void readData(Stream stream) throws IOException;

	public void writeData(Stream stream) throws IOException;
}
