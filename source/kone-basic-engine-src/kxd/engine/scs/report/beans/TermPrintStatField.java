package kxd.engine.scs.report.beans;

import java.io.IOException;
import java.io.Serializable;

import kxd.util.Streamable;
import kxd.util.stream.Stream;

/**
 * 终端打印统计项
 * 
 * @author zhaom
 * 
 */
public class TermPrintStatField implements Serializable, Streamable {
	private static final long serialVersionUID = 1L;
	private short id;
	private long count = 0;
	private long lines = 0;

	public TermPrintStatField() {
		super();
	}

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

	public long getLines() {
		return lines;
	}

	public void setLines(long lines) {
		this.lines = lines;
	}

	public short getId() {
		return id;
	}

	public void setId(short id) {
		this.id = id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		TermPrintStatField other = (TermPrintStatField) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public void readData(Stream stream) throws IOException {
		id = stream.readShort(false, 3000);
		count = stream.readLong(3000);
		lines = stream.readLong(3000);
	}

	@Override
	public void writeData(Stream stream) throws IOException {
		stream.writeShort(id, false, 3000);
		stream.writeLong(count, 3000);
		stream.writeLong(lines, 3000);
	}

}
