package kxd.engine.scs.trade;

import java.io.IOException;
import kxd.util.stream.AbstractStream;

public class BinaryRow {
	BinaryField[] fields;

	protected BinaryRow() {

	}

	public BinaryRow(int fieldNum) {
		fields = new BinaryField[fieldNum];
		for (int i = 0; i < fieldNum; i++)
			fields[i] = new BinaryField();
	}

	/**
	 * @return fields
	 */
	public BinaryField[] getFields() {
		return fields;
	}

	public void read(AbstractStream stream, int timeout) throws IOException {
		int len = stream.readShort(false, timeout);
		fields = new BinaryField[len];
		for (int i = 0; i < len; i++) {
			fields[i] = new BinaryField();
			fields[i].read(stream, timeout);
		}
	}

	public void write(AbstractStream stream, int timeout) throws IOException {
		stream.writeShort((short) fields.length, false, timeout);
		for (int i = 0; i < fields.length; i++) {
			fields[i].write(stream, timeout);
		}
	}

	public void printContent(String prefix) {
		for (int i = 0; i < fields.length; i++) {
			System.out.println(prefix + "field[" + i + "]:");
			fields[i].printContent(prefix + "  ");
		}
	}
}
