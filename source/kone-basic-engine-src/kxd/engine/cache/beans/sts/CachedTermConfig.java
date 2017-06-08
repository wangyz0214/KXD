package kxd.engine.cache.beans.sts;

import java.io.IOException;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

import kxd.engine.cache.beans.CachedIntegerObject;
import kxd.engine.cache.beans.CachedObject;
import kxd.util.IdableObject;
import kxd.util.stream.Stream;

/**
 * 缓存终端数据
 * 
 * @author zhaom
 * 
 */
public class CachedTermConfig extends CachedIntegerObject {
	private static final long serialVersionUID = 1L;
	public final static String KEY_PREFIX = "$cache.term";
	// 运维配置个性化类配置
	public final static ConcurrentHashMap<String, Class<?>> maintConfigClassMap = new ConcurrentHashMap<String, Class<?>>();
	CachedTerm term;
	// 运维配置
	private final ConcurrentHashMap<String, Object> maintConfigMap = new ConcurrentHashMap<String, Object>();
	static {
		// 添加业务开停类
		CachedTermConfig.maintConfigClassMap.put("businessopenclose",
				CachedBusinessOpenCloseList.class);
	}

	public CachedTermConfig() {
		super();
	}

	public CachedTermConfig(int id) {
		super(id);
		term = new CachedTerm(id);
	}

	public CachedTermConfig(Integer id, boolean isNullValue) {
		super(id, isNullValue);
		term = new CachedTerm(id);
	}

	@Override
	public void doReadData(Stream stream) throws IOException {
		if (term == null)
			term = new CachedTerm();
		term.readData(stream);
		maintConfigMap.clear();
		int c = stream.readInt(false, 3000);
		for (int i = 0; i < c; i++) {
			String key = stream.readPacketByteString(3000);
			try {
				@SuppressWarnings("unchecked")
				CachedObject<Serializable> o = (CachedObject<Serializable>) maintConfigClassMap
						.get(key).newInstance();
				o.readData(stream);
				maintConfigMap.put(key, o);
			} catch (InstantiationException e) {
				throw new IOException("read(key=" + key + ") error:", e);
			} catch (IllegalAccessException e) {
				throw new IOException("read(key=" + key + ") error:", e);
			}
		}
	}

	@Override
	public void doWriteData(Stream stream) throws IOException {
		term.writeData(stream);
		stream.writeInt(maintConfigMap.size(), false, 3000);
		Enumeration<String> keys = maintConfigMap.keys();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			stream.writePacketByteString(key, 3000);
			((CachedObject<?>) maintConfigMap.get(key)).writeData(stream);
		}
	}

	@Override
	public void copyData(Object src) {
		if (src instanceof CachedTermConfig) {
			CachedTermConfig o = (CachedTermConfig) src;
			term = o.term;
			maintConfigMap.clear();
			Enumeration<String> en = o.maintConfigMap.keys();
			while (en.hasMoreElements()) {
				String e = en.nextElement();
				maintConfigMap.put(e, o.maintConfigMap.get(e));
			}
		}
	}

	@Override
	public String getText() {
		return term.getTermDesp();
	}

	@Override
	public void setText(String text) {
	}

	@Override
	public IdableObject<Integer> createObject() {
		return new CachedTermConfig();
	}

	public ConcurrentHashMap<String, Object> getMaintConfig() {
		return maintConfigMap;
	}

	public CachedTerm getTerm() {
		if (term.getId() == null)
			term.setId(getId());
		return term;
	}

	public void setTerm(CachedTerm term) {
		this.term = term;
	}
}
