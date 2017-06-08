package kxd.engine.scs.trade;

import java.util.ArrayList;
import java.io.IOException;
import kxd.util.stream.AbstractStream;

public class BinaryTable {
	ArrayList<BinaryRow> rows = new ArrayList<BinaryRow>();

	public BinaryTable() {
	}

	public BinaryRow add(int fieldNum) {
		BinaryRow row = new BinaryRow(fieldNum);
		rows.add(row);
		return row;
	}

	public BinaryRow add(int index, int fieldNum) {
		BinaryRow row = new BinaryRow(fieldNum);
		rows.add(index, row);
		return row;
	}

	public BinaryRow get(int index) {
		return rows.get(index);
	}

	public BinaryRow remove(int index) {
		return rows.remove(index);
	}

	public boolean remove(BinaryRow row) {
		return rows.remove(row);
	}

	public void clear() {
		rows.clear();
	}

	public int size() {
		return rows.size();
	}

	public void read(AbstractStream stream, int timeout) throws IOException {
		int len = stream.readShort(false, timeout);
		rows.clear();
		for (int i = 0; i < len; i++) {
			BinaryRow row = new BinaryRow();
			row.read(stream, timeout);
			rows.add(row);
		}
	}

	public void write(AbstractStream stream, int timeout) throws IOException {
		short len = (short) rows.size();
		stream.writeShort(len, false, timeout);
		for (int i = 0; i < len; i++) {
			rows.get(i).write(stream, timeout);
		}
	}

	public void printContent() {
		int c = rows.size();
		System.out.println("  data table[rows=" + c + "]:");
		for (int i = 0; i < c; i++) {
			System.out.println("    row[" + i + "]:");
			get(i).printContent("      ");
		}
	}
}
