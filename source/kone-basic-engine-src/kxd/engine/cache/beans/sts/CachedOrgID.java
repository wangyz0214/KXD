package kxd.engine.cache.beans.sts;

import java.io.IOException;

import kxd.engine.cache.beans.IntegerData;
import kxd.util.stream.Stream;

/**
 * 缓存终端数据
 * 
 * @author zhaom
 * 
 */
public class CachedOrgID extends IntegerData {
	private static final long serialVersionUID = 1L;
	private int depth;

	public CachedOrgID() {
		super();
	}

	public CachedOrgID(int orgId, int depth) {
		super(orgId);
		this.depth = depth;
	}

	@Override
	public void readData(Stream stream) throws IOException {
		super.readData(stream);
		setDepth(stream.readInt(false, 3000));
	}

	@Override
	public void writeData(Stream stream) throws IOException {
		super.writeData(stream);
		stream.writeInt(getDepth(), false, 3000);
	}

	public int getOrgId() {
		return getId();
	}

	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

}
