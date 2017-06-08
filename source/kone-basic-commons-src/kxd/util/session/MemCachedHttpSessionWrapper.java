package kxd.util.session;

import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import kxd.util.Enumerator;
import kxd.util.memcached.MemCachedClient;
import kxd.util.memcached.MemCachedException;

import org.apache.log4j.Logger;

/**
 * 基于MemCached的session
 * 
 * @author 赵明
 * @version 1.0
 */
@SuppressWarnings("unchecked")
public class MemCachedHttpSessionWrapper extends HttpSessionWrapper {

	final static Logger logger = Logger
			.getLogger(MemCachedHttpSessionWrapper.class);
	private String sid = "";
	private Map<String, Object> map = null;
	static private MemCachedClient mc;

	static {
		try {
			mc = MemCachedClient.getInstance("session");
		} catch (Throwable e) {
			logger.error("初始化session memcached client失败", e);
		}
	}

	public MemCachedHttpSessionWrapper(String sid) throws MemCachedException {
		super(null);
		logger.debug("session-id:" + sid);
		this.sid = "sid_" + sid;
		this.map = getSession();
	}

	final public Map<String, Object> getSession() {
		Object s = null;
		try {
			s = mc.get(sid);
			if (s == null) {
				s = new HashMap<String, Object>();
			}
		} catch (Throwable e) {
			logger.debug("getSession() error:", e);
		}
		return (Map<String, Object>) s;
	}

	void saveSession(Map<String, Object> session) {
		try {
			mc.set(sid, session);
		} catch (Throwable e) {
		}
	}

	void removeSession() {
		try {
			mc.delete(sid);
		} catch (Throwable e) {
		}
	}

	@Override
	public Object getAttribute(String arg0) {
		if (map == null)
			throw new NullPointerException("memcached connection failure");
		return this.map.get(arg0);
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		if (map == null)
			throw new NullPointerException("memcached connection failure");
		return new Enumerator<String>(this.map.keySet(), true);
	}

	@Override
	public void invalidate() {
		if (map != null)
			this.map.clear();
		removeSession();
	}

	@Override
	public void removeAttribute(String arg0) {
		if (map == null)
			throw new NullPointerException("memcached connection failure");
		this.map.remove(arg0);
		saveSession(this.map);
	}

	public void clearAttributes(Collection<?> keepAttrs) {
		if (map == null)
			throw new NullPointerException("memcached connection failure");
		Object[] s = map.keySet().toArray();
		for (int i = 0; i < s.length; i++) {
			String k = s[i].toString();
			if (!keepAttrs.contains(k)) {
				map.remove(k);
			}
		}
		saveSession(this.map);
	}

	@Override
	public void setAttribute(String arg0, Object arg1) {
		if (map == null)
			throw new NullPointerException("memcached connection failure");
		if (arg1 == null) {
			this.map.remove(arg0);
		} else {
			this.map.put(arg0, arg1);
		}
		saveSession(this.map);
	}
}
