package kxd.engine.cache;

import java.io.Serializable;

import kxd.util.memcached.MemCachedClient;

public class CacheConfigParams implements Serializable {
	private static final long serialVersionUID = 9053614931227998445L;
	final private String jndiConfigName;
	final private String jndiBeanName;
	final private MemCachedClient memCachedClient;

	public CacheConfigParams(String jndiConfigName, String jndiBeanName,
			MemCachedClient memCachedClient) {
		super();
		this.jndiConfigName = jndiConfigName;
		this.jndiBeanName = jndiBeanName;
		this.memCachedClient = memCachedClient;
	}

	public String getJndiConfigName() {
		return jndiConfigName;
	}

	public String getJndiBeanName() {
		return jndiBeanName;
	}

	public MemCachedClient getMemCachedClient() {
		return memCachedClient;
	}

}
