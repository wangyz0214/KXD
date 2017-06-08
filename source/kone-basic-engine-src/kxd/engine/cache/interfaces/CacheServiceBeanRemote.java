/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package kxd.engine.cache.interfaces;

import java.io.Serializable;
import java.util.List;

import javax.ejb.Remote;

import kxd.util.KeyValue;

/**
 * 
 * @author 赵明
 */
@Remote
public interface CacheServiceBeanRemote {
	/**
	 * 获取类名对应的表CheckSum是否改变，用于检查本地缓存是否需要
	 * 
	 * @param className
	 * @return
	 */
	// public byte[] getKeysCheckSum(String className);

	/**
	 * 从数据库获取className对应的表的全部ID及数据摘要
	 * 
	 * @param <K>
	 * @param className
	 * @return
	 */
	public KeyValue<byte[], List<?>> getKeys(String className, Object extParam);

	/**
	 * 从数据库获取className对应表中的全部值
	 * 
	 * @param <K>
	 * @param className
	 * @param keys
	 * @return
	 */
	public List<?> getValues(String className, List<?> keys, Object extParam);

	/**
	 * 从数据库获取className对应表中的主键为key的值
	 * 
	 * @param className
	 * @param key
	 * @return
	 */
	public Serializable getKeyValue(String className, Serializable key,
			Object extParam);

	/**
	 * 装入初始缓存
	 */
	public boolean loadInitCache();

}
