package kxd.net.adapters.tuxedo.atmi;

public class StringBuffer extends Buffer {
	String dataType, subType;
	int bufSize;

	public void reset() throws TuxedoException {
		dispose();
		handle = TuxedoAtmi.tpalloc(dataType.getBytes(),
				subType == null ? new byte[0] : subType.getBytes(), bufSize);
		if (handle <= 0)
			throw new TuxedoException(0, "分配缓冲区失败");
	}

	public StringBuffer(String dataType, String subType, int bufSize)
			throws TuxedoException {
		this.dataType = dataType;
		this.subType = subType;
		this.bufSize = bufSize;
		reset();
	}

	public StringBuffer(String dataType, int bufSize) throws TuxedoException {
		this.dataType = dataType;
		this.subType = null;
		this.bufSize = bufSize;
		reset();
	}

	public StringBuffer(int bufSize) throws TuxedoException {
		this.dataType = "STRING";
		this.subType = null;
		this.bufSize = bufSize;
		reset();
	}

	public int read(int srcoffset, byte[] data, int dstoffset, int length)
			throws TuxedoException {
		checkDisposed();
		int ret = (int) TuxedoAtmi.Sget(handle, srcoffset, length, data, dstoffset);
		if (ret < 0)
			throw new TuxedoException(0, "读数据失败");
		else
			return ret;
	}

	public String read() throws TuxedoException {
		checkDisposed();
		int len = getLength();
		if (len < 1)
			return "";
		byte[] r = new byte[len - 1];
		len = (int) TuxedoAtmi.Sget(handle, 0, r.length, r, 0);
		if (len < 0)
			throw new TuxedoException(0, "读数据失败");
		else
			return new String(r, 0, len);
	}

	public int write(int srcoffset, byte[] data, int dstoffset, int length)
			throws TuxedoException {
		checkDisposed();
		int ret = (int) TuxedoAtmi.Sset(handle, dstoffset, data, srcoffset,
				data.length);
		if (ret < 0)
			throw new TuxedoException(0, "写数据失败");
		else
			return ret;
	}

	public int write(int srcoffset, String data) throws TuxedoException {
		checkDisposed();
		int ret = (int) TuxedoAtmi.Sset(handle, 0, data.getBytes(), srcoffset,
				data.length());
		if (ret < 0)
			throw new TuxedoException(0, "写数据失败");
		else
			return ret;
	}
}
