package kxd.engine.scs.report.beans;

import java.io.IOException;
import java.io.Serializable;

import kxd.util.Streamable;
import kxd.util.stream.Stream;

/**
 * 故障统计项
 * 
 * @author zhaom
 * 
 */
public class FaultStatField implements Serializable, Streamable {
	private static final long serialVersionUID = 1L;
	private int faultType;
	private long count = 0;

	public FaultStatField() {
		super();
	}

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

	public int getFaultType() {
		return faultType;
	}

	public void setFaultType(int faultType) {
		this.faultType = faultType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + faultType;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		FaultStatField other = (FaultStatField) obj;
		if (faultType != other.faultType)
			return false;
		return true;
	}

	@Override
	public void readData(Stream stream) throws IOException {
		faultType = stream.readInt(false, 3000);
		count = stream.readLong(3000);
	}

	@Override
	public void writeData(Stream stream) throws IOException {
		stream.writeInt(faultType, false, 3000);
		stream.writeLong(count, 3000);
	}

}
