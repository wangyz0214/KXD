package kxd.net.adapters.tuxedo.atmi;

import org.apache.log4j.Logger;

public class Tuxedo {
	Logger logger = Logger.getLogger(Tuxedo.class);
	protected InitBuffer initBuffer = null;
	protected volatile boolean disposed = true;

	public Tuxedo() {
	}

	public Tuxedo(TuxedoConfig config) throws TuxedoException {
		connect(config);
	}

	public Tuxedo(String wsnAddr, int flags, String usrname, String cltname,
			String passwd, String grpname, byte[] data) throws TuxedoException {
		connect(wsnAddr, flags, usrname, cltname, passwd, grpname, data);
	}

	public Tuxedo(String wsnAddr, boolean Multi) throws TuxedoException {
		connect(wsnAddr, Multi);
	}

	public void connect(TuxedoConfig config) throws TuxedoException {
		connect(config.getWsnAddr(), config.getFlags(), config.getUsrname(),
				config.getCltname(), config.getPasswd(), config.getGrpname(),
				config.getData());
	}

	public void connect(String wsnAddr, boolean Multi) throws TuxedoException {
		if (Multi) {
			connect(wsnAddr, TuxedoAtmi.TPMULTICONTEXTS, null, null, null, null, null);
		} else
			connect(wsnAddr, 0, null, null, null, null, null);
	}

	public void connect(String wsnAddr, int flags, String usrname,
			String cltname, String passwd, String grpname, byte[] data)
			throws TuxedoException {
		dispose();
		if (flags != 0 || usrname != null || cltname != null || passwd != null
				|| grpname != null || data != null) {
			initBuffer = new InitBuffer();
			initBuffer
					.setParams(flags, usrname, cltname, passwd, grpname, data);
		}
		logger.debug("connect[" + wsnAddr + "," + flags + "," + usrname + ","
				+ cltname + "," + passwd + "," + grpname + "]");
		TuxedoAtmi.tuxputenv(("WSNADDR=" + wsnAddr).getBytes());
		long handle = initBuffer != null ? initBuffer.getHandle() : 0;
		if (TuxedoAtmi.tpinit(handle) == -1) {
			try {
				procError();
				String msg = TuxedoAtmi.getErrorString();
				throw new TuxedoException(0, msg);
			} finally {
				dispose();
			}
		}
		disposed = false;
	}

	public void dispose() {
		if (initBuffer != null) {
			initBuffer.dispose();
			initBuffer = null;
		}
		if (!disposed) {
			TuxedoAtmi.tpterm();
			disposed = true;
		}
	}

	public void checkDisposed() throws TuxedoException {
		if (disposed)
			throw new TuxedoException(0, "Tuxedo连接已经断开");
	}

	public InitBuffer getInitBuffer() {
		return initBuffer;
	}

	public void procError() throws TuxedoException {
		int error;
		switch ((error = TuxedoAtmi.tperrno())) {
		case TuxedoAtmi.TPEINVAL:
			throw new TuxedoException(error, "调用参数不正确");
		case TuxedoAtmi.TPENOENT:
			throw new TuxedoException(error, "调用的服务不存在");
		case TuxedoAtmi.TPEITYPE:
			throw new TuxedoException(error, "不允许的数据类型");
		case TuxedoAtmi.TPEOTYPE:
			throw new TuxedoException(error, "数据类型不匹配");
		case TuxedoAtmi.TPETRAN:
			throw new TuxedoException(error, "不支持的事务或TPNOTRAN未设定");
		case TuxedoAtmi.TPETIME:
			throw new TuxedoException(error, "调用超时");
		case TuxedoAtmi.TPESVCFAIL:
			throw new TuxedoException(error, "调用失败[TPESVCFAIL]");
		case TuxedoAtmi.TPESVCERR:
			throw new TuxedoException(error, "调用失败[TPESVCERR]");
		case TuxedoAtmi.TPEBLOCK:
			throw new TuxedoException(error, "调用失败[TPEBLOCK]");
		case TuxedoAtmi.TPGOTSIG:
			throw new TuxedoException(error, "调用失败[TPGOTSIG]");
		case TuxedoAtmi.TPEPROTO:
			throw new TuxedoException(error, "调用失败[TPEPROTO]");
		case TuxedoAtmi.TPESYSTEM:
			throw new TuxedoException(error, "Tuxedo故障[TPESYSTEM]");
		case TuxedoAtmi.TPEOS:
			throw new TuxedoException(error, "操作系统故障[TPEOS]");
		}
	}

	/**
	 * 
	 * @param service
	 * @param input
	 * @param output
	 * @param flags
	 *            long 标志[TPNOTRAN,TPNOCHANGE,TPNOBLOCK,TPNOTIME,TPSIGRSTRT]
	 * @return
	 * @throws TuxedoException
	 */
	public int tpcall(String service, Buffer input, Buffer output, int flags)
			throws TuxedoException {
		logger.debug("tpcall(" + service + ")");
		int r = TuxedoAtmi.tpcall(service.getBytes(), input.getHandle(),
				input.getLength(), output.getHandle(), flags);
		if (r == -1) {
			procError();
		}
		return r;
	}

	/**
	 * 
	 * @param service
	 * @param input
	 * @param flags
	 *            long 标志[TPNOTRAN,TPNOCHANGE,TPNOBLOCK,TPNOTIME,TPSIGRSTRT]
	 * @return
	 * @throws TuxedoException
	 */
	public TuxedoAsynCall tpacall(String service, Buffer input, int flags)
			throws TuxedoException {
		return new TuxedoAsynCall(this, service, input, flags);
	}

	/**
	 * 
	 * @param service
	 * @param input
	 * @param flags
	 *            long
	 *            [TPNOTRAN,TPSENDONLY,TPRECVONLY,TPNOBLOCK,TPNOTIME,TPSIGRSTRT]
	 * @return
	 * @throws TuxedoException
	 */
	public TuxedoSession tpconnect(String service, Buffer input, int flags)
			throws TuxedoException {
		return new TuxedoSession(this, service, input, flags);
	}

	public boolean isDisposed() {
		return disposed;
	}
}
