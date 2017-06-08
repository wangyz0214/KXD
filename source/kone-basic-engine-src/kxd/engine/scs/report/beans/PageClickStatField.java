package kxd.engine.scs.report.beans;

import java.io.IOException;
import java.io.Serializable;

import kxd.util.Streamable;
import kxd.util.stream.Stream;

/**
 * 业务量统计项
 * 
 * @author zhaom
 * 
 */
public class PageClickStatField implements Serializable, Streamable {
	private static final long serialVersionUID = 1L;
	private int id;
	private long count = 0;

	public PageClickStatField() {
		super();
	}

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
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
		PageClickStatField other = (PageClickStatField) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public void readData(Stream stream) throws IOException {
		id = stream.readInt(false, 3000);
		count = stream.readLong(3000);
	}

	@Override
	public void writeData(Stream stream) throws IOException {
		stream.writeInt(id, false, 3000);
		stream.writeLong(count, 3000);
	}

}
