package kxd.engine.scs.trade;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class ServiceConfig {
	public ConcurrentHashMap<String, String> params = new ConcurrentHashMap<String, String>();
	public ConcurrentHashMap<Integer, Class<?>> appClasses = new ConcurrentHashMap<Integer, Class<?>>();
	public CopyOnWriteArrayList<ServiceBean> beanList = new CopyOnWriteArrayList<ServiceBean>();
	public ServiceBean defaultBean = null;
	public Class<?> clazz;
}
