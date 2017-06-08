package kxd.net.adapters.tuxedo.atmi;

public class InitBuffer extends Buffer {
	public InitBuffer() throws TuxedoException {
		handle = TuxedoAtmi.allocInit();
		if (handle <= 0)
			throw new TuxedoException(0, "分配缓冲区失败");
	}

	public InitBuffer(boolean muiti) throws TuxedoException {
		handle = TuxedoAtmi.allocInit();
		if (handle <= 0)
			throw new TuxedoException(0, "分配缓冲区失败");
		setParams(TuxedoAtmi.TPMULTICONTEXTS, null, null, null, null, null);
	}

	public void reset() throws TuxedoException {
		dispose();
		handle = TuxedoAtmi.allocInit();
		if (handle <= 0)
			throw new TuxedoException(0, "分配缓冲区失败");
	}

	public void setParams(int flags, String usrname, String cltname,
			String passwd, String grpname, byte[] data) throws TuxedoException {
		checkDisposed();
		byte[] u = null, c = null, p = null, g = null;
		if (usrname != null)
			u = usrname.getBytes();
		if (cltname != null)
			c = cltname.getBytes();
		if (passwd != null)
			p = passwd.getBytes();
		if (grpname != null)
			g = grpname.getBytes();
		TuxedoAtmi.setInitParams(handle, u, c, p, g, flags, data);
	}
}
