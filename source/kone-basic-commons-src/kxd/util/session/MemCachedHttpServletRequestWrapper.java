package kxd.util.session;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import kxd.util.memcached.MemCachedException;

public class MemCachedHttpServletRequestWrapper extends
		javax.servlet.http.HttpServletRequestWrapper {

	String sid = "";
	HttpSession session;

	public MemCachedHttpServletRequestWrapper(String sid,
			HttpServletRequest arg0) {
		super(arg0);
		this.sid = sid;
	}

	public HttpSession getSession(boolean create) {
		try {
			if (create || session == null)
				session = new MemCachedHttpSessionWrapper(this.sid);
			return session;
		} catch (MemCachedException e) {
			e.printStackTrace();
			return null;
		}
	}

	public HttpSession getSession() {
		try {
			if (session == null)
				session = new MemCachedHttpSessionWrapper(this.sid);
			return session;
		} catch (MemCachedException e) {
			e.printStackTrace();
			return null;
		}
	}

}
