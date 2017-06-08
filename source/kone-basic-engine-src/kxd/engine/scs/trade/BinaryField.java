package kxd.engine.scs.trade;

import java.io.IOException;

import kxd.util.DataUnit;
import kxd.util.stream.*;

public class BinaryField {
	byte[] value;

	public void printContent(String prefix) {
		String[] r = DataUnit.formatHex(value);
		for (int i = 0; i < r.length; i++) {
			System.out.println(prefix + r[i]);
		}
	}

	public BinaryField(byte[] value) {
		this.value = value;
	}

	protected BinaryField() {
	}

	public void read(AbstractStream stream, int timeout) throws IOException {
		value = stream.readPacketInt(false, timeout);
	}

	public void write(AbstractStream stream, int timeout) throws IOException {
		stream.writePacketInt(value, false, timeout);
	}

	@Override
	public String toString() {
		return new String(value);
	}

	public byte[] getValue() {
		return value;
	}

	public void setValue(byte[] value) {
		this.value = value;
	}
}
