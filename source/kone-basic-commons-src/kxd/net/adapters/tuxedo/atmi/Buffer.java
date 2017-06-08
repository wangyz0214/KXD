package kxd.net.adapters.tuxedo.atmi;

/**
 * 缓冲类
 * 
 * @author zhaom
 * 
 */
abstract public class Buffer {
	long handle = 0;

	abstract public void reset() throws TuxedoException;

	public int getLength() throws TuxedoException {
		checkDisposed();
		return (int) TuxedoAtmi.getBufferSize(handle);
	}

	public long getHandle() {
		return handle;
	}

	public void checkDisposed() throws TuxedoException {
		if (handle == 0)
			throw new TuxedoException(0, "Buffer 已经释放或没有分配");
	}

	public void dispose() {
		if (handle != 0) {
			TuxedoAtmi.tpfree(handle);
			handle = 0;
		}
	}
}
