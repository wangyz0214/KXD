package kxd.net.adapters.tuxedo.atmi;

import kxd.util.DataUnit;

public class FMLBuffer32 extends FMLBuffer {
	FMLBuffer32() {
	}

	public void reset() throws TuxedoException {
		dispose();
		handle = TuxedoAtmi.tpalloc("FML32".getBytes(), null, bufSize);
		if (handle <= 0)
			throw new TuxedoException(0, "分配缓冲区失败");
		if (TuxedoAtmi.Finit32(handle) == -1) {
			dispose();
			throw new TuxedoException(0, "初始化FML缓冲区失败");
		}
	}

	public FMLBuffer32(int bufSize) throws TuxedoException {
		this.bufSize = bufSize;
		reset();
	}

	public int read(int row, int col, byte[] data, int length)
			throws TuxedoException {
		checkDisposed();
		int ret = (int) TuxedoAtmi.Fget32(handle, row, col, data, length);
		if (ret <= 0)
			throw new TuxedoException(0, "读FML数据失败");
		else
			return ret;
	}

	public String read(int row, int col, int length) throws TuxedoException {
		checkDisposed();
		byte[] r = new byte[length];
		int len = (int) TuxedoAtmi.Fget32(handle, row, col, r, length);
		if (len <= 0)
			throw new TuxedoException(0, "读FML数据失败");
		else if (len <= 1)
			return "";
		else
			return new String(r, 0, len - 1);
	}

	public int readInt(int row, int col) throws TuxedoException {
		checkDisposed();
		byte[] r = new byte[5];
		int len = (int) TuxedoAtmi.Fget32(handle, row, col, r, 5);
		if (len < 4)
			throw new TuxedoException(0, "读FML数据失败");
		else
			return DataUnit.bytesToInt(r, 0);
	}

	public short readShort(int row, int col) throws TuxedoException {
		checkDisposed();
		byte[] r = new byte[3];
		int len = (int) TuxedoAtmi.Fget32(handle, row, col, r, 3);
		if (len < 2)
			throw new TuxedoException(0, "读FML数据失败");
		else
			return DataUnit.bytesToShort(r, 0);
	}

	public long readLong(int row, int col) throws TuxedoException {
		checkDisposed();
		byte[] r = new byte[9];
		int len = (int) TuxedoAtmi.Fget32(handle, row, col, r, 9);
		if (len < 8)
			throw new TuxedoException(0, "读FML数据失败");
		else
			return DataUnit.bytesToLong(r, 0);
	}

	public double readDouble(int row, int col) throws TuxedoException {
		checkDisposed();
		byte[] r = new byte[9];
		int len = (int) TuxedoAtmi.Fget32(handle, row, col, r, 9);
		if (len < 8)
			throw new TuxedoException(0, "读FML32数据失败");
		else
			return DataUnit.bytesToDouble(r, 0);
	}

	public float readFloat(int row, int col) throws TuxedoException {
		checkDisposed();
		byte[] r = new byte[5];
		int len = (int) TuxedoAtmi.Fget32(handle, row, col, r, 5);
		if (len < 4)
			throw new TuxedoException(0, "读FML32数据失败");
		else
			return DataUnit.bytesToFloat(r, 0);
	}

	public int next(FMLID row, FMLID col, byte[] data, int length)
			throws TuxedoException {
		checkDisposed();
		int rowcol[] = new int[2];
		rowcol[0] = row.id;
		rowcol[1] = col.id;
		int ret = (int) TuxedoAtmi.Fnext32(handle, rowcol, data, length);
		if (ret <= 0)
			return ret;
		else {
			row.id = rowcol[0];
			col.id = rowcol[1];
			return ret;
		}
	}

	public void add(int row, byte[] data) throws TuxedoException {
		checkDisposed();
		if (!TuxedoAtmi.Fadd32(handle, row, data))
			throw new TuxedoException(0, "添加FML数据失败");
	}

	public void write(int row, int col, byte[] data) throws TuxedoException {
		checkDisposed();
		if (!TuxedoAtmi.Fset32(handle, row, col, data))
			throw new TuxedoException(0, "写FML数据失败");
	}

	public void write(int row, int col, String data) throws TuxedoException {
		checkDisposed();
		if (!TuxedoAtmi.Fset32(handle, row, col, data.getBytes()))
			throw new TuxedoException(0, "写FML数据失败");
	}

	public void writeInt(int row, int col, int data) throws TuxedoException {
		checkDisposed();
		if (!TuxedoAtmi.Fset32(handle, row, col, DataUnit.intToBytes(data)))
			throw new TuxedoException(0, "写FML数据失败");
	}

	public void writeShort(int row, int col, short data) throws TuxedoException {
		checkDisposed();
		if (!TuxedoAtmi.Fset32(handle, row, col, DataUnit.shortToBytes(data)))
			throw new TuxedoException(0, "写FML数据失败");
	}

	public void writeLong(int row, int col, long data) throws TuxedoException {
		checkDisposed();
		if (!TuxedoAtmi.Fset32(handle, row, col, DataUnit.longToBytes(data)))
			throw new TuxedoException(0, "写FML数据失败");
	}

	public void writeDouble(int row, int col, double data)
			throws TuxedoException {
		checkDisposed();
		if (!TuxedoAtmi.Fset32(handle, row, col, DataUnit.doubleToBytes(data)))
			throw new TuxedoException(0, "写FML数据失败");
	}

	public void writeFloat(int row, int col, float data) throws TuxedoException {
		checkDisposed();
		if (!TuxedoAtmi.Fset32(handle, row, col, DataUnit.floatToBytes(data)))
			throw new TuxedoException(0, "写FML数据失败");
	}

	public int occur(int row) {
		return TuxedoAtmi.Foccur32(handle, row);
	}

	public void print() throws TuxedoException {
		checkDisposed();
		TuxedoAtmi.Fprint32(handle);
	}
}
