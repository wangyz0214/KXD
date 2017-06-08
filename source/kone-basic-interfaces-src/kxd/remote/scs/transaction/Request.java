package kxd.remote.scs.transaction;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class Request implements Serializable {
	private static final long serialVersionUID = 3594333931010089222L;
	TradeCode tradeCode;
	Map<String, String> params;
	Serializable term;
	/**
	 * 指示是否是补发交易
	 */
	boolean isRedo = false;
	private String remoteAddr, serverAddr;

	public TradeCode getTradeCode() {
		return tradeCode;
	}

	public Request(Map<String, String> params) throws TradeError {
		this.params = params;
		tradeCode = new TradeCode(this);
	}

	public String getParameter(String name) throws NoSuchFieldException {
		String ret = params.get(name);
		if (ret == null)
			throw new NoSuchFieldException("param[" + name + "] not exists!");
		return ret;
	}

	public String getParameterDef(String name, String def) {
		try {
			return getParameter(name);
		} catch (Throwable e) {
			return def;
		}
	}

	public int getParameterInt(String name) throws NoSuchFieldException,
			IllegalArgumentException {
		String ret = params.get(name);
		if (ret == null)
			throw new NoSuchFieldException("param[name=" + name
					+ "] not exists!");
		try {
			return Integer.parseInt(ret);

		} catch (Throwable e) {
			throw new IllegalArgumentException("param[name=" + name + " value="
					+ ret + "] not a integer!");
		}
	}

	public int getParameterIntDef(String name, int def) {
		try {
			return getParameterInt(name);

		} catch (Throwable e) {
			return def;
		}
	}

	public Date getParameterDateTime(String name, String format)
			throws NoSuchFieldException, IllegalArgumentException {
		String ret = params.get(name);
		if (ret == null)
			throw new NoSuchFieldException("param[name=" + name
					+ "] not exists!");
		try {
			return new SimpleDateFormat(format).parse(ret);

		} catch (Throwable e) {
			throw new IllegalArgumentException("param[name=" + name + " value="
					+ ret + "] format error: " + format);
		}
	}

	public Date getParameterDateTimeDef(String name, String format, Date def) {
		try {
			return getParameterDateTime(name, format);

		} catch (Throwable e) {
			return def;
		}
	}

	public Map<String, String> getParams() {
		return params;
	}

	public Serializable getTerm() {
		return term;
	}

	public void setTerm(Serializable term) {
		this.term = term;
	}

	public boolean isRedo() {
		return isRedo;
	}

	public void setRedo(boolean isRedo) {
		this.isRedo = isRedo;
	}

	public String getRemoteAddr() {
		return remoteAddr;
	}

	public void setRemoteAddr(String remoteAddr) {
		this.remoteAddr = remoteAddr;
	}

	public String getServerAddr() {
		return serverAddr;
	}

	public void setServerAddr(String serverAddr) {
		this.serverAddr = serverAddr;
	}

}
