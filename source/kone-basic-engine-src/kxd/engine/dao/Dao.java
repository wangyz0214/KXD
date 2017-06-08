package kxd.engine.dao;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.sql.DataSource;

import kxd.engine.cache.CachedMap;
import kxd.net.connection.jndi.Lookuper;
import kxd.net.naming.LocalNamingContext;
import kxd.net.naming.NamingContext;
import kxd.remote.scs.util.AppException;
import kxd.util.DataSecurity;
import kxd.util.DateTime;
import kxd.util.memcached.Cacheable;
import kxd.util.memcached.MemCachedClient;

import org.apache.log4j.Logger;

public class Dao {
	static final Logger logger = Logger.getLogger(Dao.class);
	protected Connection connection = null;
	protected NamingContext context = null;
	private String jndiName = null;
	private DataSource ds = null;
	static AtomicInteger connections = new AtomicInteger(0);
	DaoDelegation delegation = null;

	public Dao() {
	}

	/**
	 * 从本地的JNDI中获取数据源
	 * 
	 * @param jndiName
	 *            jndi名称
	 */
	public Dao(String jndiName) {
		this.jndiName = jndiName;
	}

	/**
	 * 构造器，并从数据源中获取连接
	 * 
	 * @param DataSource
	 *            JDBC数据源
	 */
	public Dao(DataSource ds) {
		this.ds = ds;
	}

	/**
	 * 构造器，并指定数据库连接对象
	 * 
	 * @param connection
	 *            JDBC连接
	 */
	public Dao(Connection connection) {
		if (logger.isDebugEnabled())
			logger.debug("dao(" + connection + ")[" + this
					+ "] created. [connections: " + connections.addAndGet(1)
					+ "]");
		this.connection = connection;
	}

	/**
	 * 关闭数据访问对象资源及数据库连接
	 */
	public void close() {
		delegation = null;
		if (connection != null) {
			logger.debug("dao[" + this + "] closed [connections: "
					+ connections.decrementAndGet() + "]");
			try {
				connection.close();
			} catch (Throwable e) {
				logger.warn("close connection failed:", e);
			}
			connection = null;
		}
		if (context != null) {
			try {
				context.close();
			} catch (Throwable e) {
				logger.warn("close context failed:", e);
			}
			context = null;
		}
	}

	/**
	 * 获取JDBC连接，某些特殊的应用可以通过Connection直接操作数据库
	 */
	public Connection getConnection() {
		if (connection == null) {
			if (ds != null) {
				try {
					this.connection = ds.getConnection();
					logger.debug("dao(" + ds + ")[" + this
							+ "] created. [connections: "
							+ connections.addAndGet(1) + "]");
				} catch (Throwable e) {
					throw new AppException(e);
				}
			} else {
				try {
					context = new LocalNamingContext();
					DataSource ds = context.lookup(
							Lookuper.JNDI_TYPE_DATASOURCE, jndiName,
							DataSource.class);
					connection = ds.getConnection();
					logger.debug("dao(" + jndiName + ")[" + this
							+ "] created. [connections: "
							+ connections.addAndGet(1) + "]");
				} catch (Throwable e) {
					close();
					throw new AppException(e);
				}
			}
		}
		return connection;
	}

	/**
	 * 设置JDBC连接
	 * 
	 * @param con
	 *            连接
	 */
	public void setConnection(Connection con) {
		this.connection = con;
		delegation = null;
	}

	public DaoDelegation getDelegation() {
		if (delegation == null) {
			try {
				String name = getConnection().getMetaData()
						.getDatabaseProductName().toLowerCase();
				if (name.contains("oracle"))
					delegation = new OracleDelegation(connection);
				else
					throw new AppException("不支持的数据库");
			} catch (SQLException e) {
				throw new AppException(e);
			}
		}
		return delegation;
	}

	/**
	 * 自动生成ID
	 * 
	 * @param <E>
	 *            要插入的对象类
	 * @param connection
	 *            数据库连接
	 * @param o
	 *            要插入的对象
	 * @param converter
	 *            转换器
	 * @throws SQLException
	 */
	protected <E> void autoGenerateId(E o, SqlConverter<E> converter)
			throws SQLException {
		getDelegation().autoGenerateId(o, converter);
	}

	/**
	 * 检查表名是否存在
	 * 
	 * @param tableName
	 *            表名
	 * @return true/false
	 */
	public boolean tableExists(String tableName) {
		return getDelegation().tableExists(tableName);
	}

	/**
	 * 检查表名是否存在
	 * 
	 * @param tableName
	 *            表名
	 * @return true/false
	 */
	public boolean tablePartitionExists(String tableName, String partitionName) {
		return getDelegation().tablePartitionExists(tableName, partitionName);
	}

	/**
	 * 查询数据库
	 * 
	 * @param sql
	 *            查询sql
	 * @param firstResult
	 *            初始记录索引
	 * @param maxResults
	 *            返回最大的记录条数，如果为0，则不启用分页查询
	 * @param params
	 *            参数列表，如果参数为QueryParam，则按指定的数据类型设置参数
	 * @return 结果列表
	 * @throws SQLException
	 */
	public List<?> queryPage(String sql, int firstResult, int maxResults,
			Object... params) {
		return getDelegation().queryPage(sql, firstResult, maxResults, params);
	}

	/**
	 * 查询数据库
	 * 
	 * @param sql
	 *            查询sql
	 * @param firstResult
	 *            初始记录索引
	 * @param maxResults
	 *            返回最大的记录条数，如果为0，则不启用分页查询
	 * @param params
	 *            参数列表，如果参数为QueryParam，则按指定的数据类型设置参数
	 * @return 结果列表
	 * @throws SQLException
	 */
	public List<?> queryPage(String sql, int firstResult, int maxResults,
			List<?> params) {
		return getDelegation().queryPage(sql, firstResult, maxResults, params);
	}

	/**
	 * 更新或插入数据
	 * 
	 * @param sql
	 *            查询sql
	 * @param params
	 *            参数列表，如果参数为QueryParam，则按指定的数据类型设置参数
	 * @return 返回受影响的记录条数
	 */
	public int execute(String sql, Object... params) {
		return getDelegation().execute(sql, params);
	}

	/**
	 * 更新或插入数据
	 * 
	 * @param sql
	 *            查询sql
	 * @param params
	 *            参数列表，如果参数为QueryParam，则按指定的数据类型设置参数
	 * @return 返回受影响的记录条数
	 */
	public int execute(String sql, List<?> params) {
		return getDelegation().execute(sql, params);
	}

	/**
	 * 调用存储过程
	 * 
	 * @param sql
	 *            存储过程调用SQL
	 * @param params
	 *            参数
	 * @return 输出参数列表
	 */
	public void execProcedure(String sql, Object... params) {
		getDelegation().execProcedure(sql, params);
	}

	public String getCacheKey(String keyPrefix, String sql, int firstResult,
			int maxResults, List<?> params) {
		try {
			StringBuffer sb = new StringBuffer(keyPrefix + ":" + sql + "|"
					+ firstResult + "|" + maxResults);
			for (Object o : params) {
				sb.append("|" + o);
			}
			return DataSecurity.md5(sb.toString());
		} catch (Throwable e) {
			throw new AppException(e);
		}
	}

	public List<?> queryFromCache(String keyPrefix, MemCachedClient mc,
			Date expireTime, String sql, Object... params) {
		ArrayList<Object> ls = new ArrayList<Object>();
		for (Object o : params)
			ls.add(o);
		return queryPageFromCache(keyPrefix, mc, expireTime, sql, 0, 0, ls);
	}

	public List<?> queryFromCache(String keyPrefix, MemCachedClient mc,
			Date expireTime, String sql, List<?> params) {
		return queryPageFromCache(keyPrefix, mc, expireTime, sql, 0, 0, params);
	}

	public List<?> queryPageFromCache(String keyPrefix, MemCachedClient mc,
			Date expireTime, String sql, int firstResult, int maxResults,
			Object... params) {
		ArrayList<Object> ls = new ArrayList<Object>();
		for (Object o : params)
			ls.add(o);
		return queryPageFromCache(keyPrefix, mc, expireTime, sql, firstResult,
				maxResults, ls);
	}

	public List<?> queryPageFromCache(String keyPrefix, MemCachedClient mc,
			Date expireTime, String sql, int firstResult, int maxResults,
			List<?> params) {
		try {
			String key = null;
			if (mc != null && keyPrefix != null) {
				key = getCacheKey(keyPrefix, sql, firstResult, maxResults,
						params);
				List<?> ret = (List<?>) mc.get(key);
				if (ret != null) {
					logger.debug("get from cache: " + sql);
					return ret;
				}
			}
			List<?> ret = queryPage(sql, firstResult, maxResults, params);
			if (key != null) {
				mc.set(key, ret, expireTime);
			}
			return ret;
		} catch (IOException e) {
			throw new AppException(e);
		} catch (InterruptedException e) {
			throw new AppException(e);
		}
	}

	/**
	 * 插入一个对象，如果E是从Idable继承，且getId()为null，应该自动获取下一个ID值。
	 * 
	 * @param <E>
	 *            要插入的对象类
	 * @param o
	 * @param sql
	 * @param configParams
	 * @return
	 */
	public <E> int insert(E o, SqlConverter<E> converter) {
		converter.setDao(this);
		try {
			autoGenerateId(o, converter);
		} catch (Throwable e) {
			throw new AppException(e);
		}
		SqlParams params = converter.getInsertSql(o);
		return execute(params.getSql(), params.getParams());
	}

	/**
	 * 更新一个对象至数据库中
	 * 
	 * @param <E>
	 * @param o
	 * @param convert
	 */
	public <E> int update(E o, SqlConverter<E> converter) {
		converter.setDao(this);
		SqlParams params = converter.getUpdateSql(o);
		return execute(params.getSql(), params.getParams());
	}

	/**
	 * 从数据库删除一个对象
	 * 
	 * @param <E>
	 * @param o
	 * @param convert
	 */
	public <E> int delete(E o, SqlConverter<E> converter) {
		converter.setDao(this);
		SqlParams params = converter.getDeleteSql(o);
		return execute(params.getSql(), params.getParams());
	}

	/**
	 * 查询本地SQL
	 * 
	 * @param converter
	 *            结果集转换器
	 * @param sql
	 *            SQL
	 * @return 查询结果列表
	 */
	public <E> List<E> query(DaoConverter<E> converter, String sql) {
		converter.setDao(this);
		return converter.convert(query(sql));
	}

	/**
	 * 查询本地SQL，SQL中参数用?1,?2,?3,...表示，在params中代表params[0],params[1],params[2]
	 * 
	 * @param converter
	 *            结果集转换器
	 * @param sql
	 *            SQL
	 * @param params
	 *            参数数组
	 * @return 查询结果列表
	 */
	public <E> List<E> query(DaoConverter<E> converter, String sql,
			Object... params) {
		converter.setDao(this);
		return converter.convert(query(sql, params));
	}

	/**
	 * 查询本地SQL，SQL中参数用:name,name与params的key一致
	 * 
	 * @param converter
	 *            结果集转换器
	 * @param sql
	 *            SQL
	 * @param params
	 *            参数数组
	 * @return 查询结果列表
	 */
	public <E> List<E> query(DaoConverter<E> converter, String sql,
			List<?> params) {
		converter.setDao(this);
		return converter.convert(query(sql, params));
	}

	/**
	 * 查询本地SQL，SQL中参数用?1,?2,?3,...表示，在params中代表params[0],params[1],params[2]
	 * 
	 * @param converter
	 *            结果集转换器
	 * @param sql
	 *            SQL
	 * @param firstResult
	 *            超始索引
	 * @param maxResults
	 *            最大的返回结果数
	 * @param params
	 *            参数数组
	 * @return 查询结果列表
	 */
	public <E> List<E> queryPage(DaoConverter<E> converter, String sql,
			int firstResult, int maxResults, List<?> params) {
		converter.setDao(this);
		return converter
				.convert(queryPage(sql, firstResult, maxResults, params));
	}

	/**
	 * 查询本地SQL，SQL中参数用?1,?2,?3,...表示，在params中代表params[0],params[1],params[2]
	 * 
	 * @param converter
	 *            结果集转换器
	 * @param sql
	 *            SQL
	 * @param firstResult
	 *            超始索引
	 * @param maxResults
	 *            最大的返回结果数
	 * @param params
	 *            参数数组
	 * @return 查询结果列表
	 */
	public <E> List<E> queryPage(DaoConverter<E> converter, String sql,
			int firstResult, int maxResults, Object... params) {
		converter.setDao(this);
		return converter
				.convert(queryPage(sql, firstResult, maxResults, params));
	}

	/**
	 * 查询本地SQL
	 * 
	 * @param sql
	 *            SQL
	 * @return 查询结果列表
	 */
	public List<?> query(String sql) {
		return queryPage(sql, 0, 0);
	}

	/**
	 * 查询本地SQL，SQL中参数用?1,?2,?3,...表示，在params中代表params[0],params[1],params[2]
	 * 
	 * @param sql
	 *            SQL
	 * @param params
	 *            参数数组
	 * @return 查询结果列表
	 */
	public List<?> query(String sql, Object... params) {
		return queryPage(sql, 0, 0, params);
	}

	/**
	 * 查询本地SQL，SQL中参数用:name,name与params的key一致
	 * 
	 * @param sql
	 *            SQL
	 * @param params
	 *            参数数组
	 * @return 查询结果列表
	 */
	public List<?> query(String sql, List<?> params) {
		return queryPage(sql, 0, 0, params);
	}

	/**
	 * 查询单个对象
	 * 
	 * @param sql
	 *            SQL
	 * @param params
	 *            参数
	 * @return 找到的对象，为null，表示没有查到
	 */
	public Object querySingal(String sql, Object... params) {
		List<?> ls = query(sql, params);
		if (ls.size() > 0)
			return ls.get(0);
		else
			return null;
	}

	/**
	 * 查询单个对象
	 * 
	 * @param sql
	 *            SQL
	 * @param params
	 *            参数
	 * @return 找到的对象，为null，表示没有查到
	 */
	public Object querySingal(String sql, List<?> params) {
		List<?> ls = query(sql, params);
		if (ls.size() > 0)
			return ls.get(0);
		else
			return null;
	}

	/**
	 * 查询单个对象
	 * 
	 * @param sql
	 *            SQL
	 * @param params
	 *            参数
	 * @return 找到的对象，为null，表示没有查到
	 */
	public <E> E querySingal(DaoConverter<E> converter, String sql,
			Object... params) {
		List<E> ls = query(converter, sql, params);
		if (ls.size() > 0)
			return ls.get(0);
		else
			return null;
	}

	/**
	 * 查询单个对象
	 * 
	 * @param sql
	 *            SQL
	 * @param params
	 *            参数
	 * @return 找到的对象，为null，表示没有查到
	 */
	public <E> E querySingal(DaoConverter<E> converter, String sql,
			List<?> params) {
		List<E> ls = query(converter, sql, params);
		if (ls.size() > 0)
			return ls.get(0);
		else
			return null;
	}

	/**
	 * 查询单个对象，并自动设置缓存
	 * 
	 * @param <E>
	 * @param cacheKeyPrefix
	 *            缓存前缀
	 * @param hashCode
	 *            指定服务器hashCode
	 * @param mc
	 *            memcached客户端
	 * @param nullObject
	 *            当未找到时，需要保存至缓存的对象
	 * @param nullTimeout
	 *            未长到时，保存的缓存的空对象的有效期，以毫秒为单位
	 * @param converter
	 *            数据转换器
	 * @param sql
	 *            查询字串
	 * @param params
	 *            查询参数
	 * @return
	 */
	public <K extends Serializable, E extends Cacheable<K>> E querySingalAndSetCache(
			CachedMap<K, E> map, E nullObject, int nullTimeout,
			DaoConverter<E> converter, String sql, List<?> params) {
		E e = querySingal(converter, sql, params);
		try {
			if (e != null) {
				map.putCache(e.getId(), e, null);
			} else
				map.putCache(nullObject.getId(), nullObject, new DateTime()
						.addSeconds(nullTimeout).getTime());
		} catch (Throwable ex) {
			if (logger.isDebugEnabled())
				logger.error("set cache failure：", ex);
		}
		return e;
	}

	/**
	 * 查询单个对象，并自动设置缓存
	 * 
	 * @param <E>
	 * @param map
	 *            缓存Map
	 * @param nullObject
	 *            当未找到时，需要保存至缓存的对象
	 * @param nullTimeout
	 *            未长到时，保存的缓存的空对象的有效期，以秒为单位
	 * @param converter
	 *            数据转换器
	 * @param sql
	 *            查询字串
	 * @param params
	 *            查询参数
	 * @return
	 */
	public <K extends Serializable, E extends Cacheable<K>> E querySingalAndSetCache(
			CachedMap<K, E> map, E nullObject, int nullTimeout,
			DaoConverter<E> converter, String sql, Object... params) {
		E e = querySingal(converter, sql, params);
		try {
			if (e != null) {
				map.putCache(e.getId(), e, null);
			} else
				map.putCache(nullObject.getId(), nullObject, new DateTime()
						.addSeconds(nullTimeout).getTime());
		} catch (Throwable ex) {
			if (logger.isDebugEnabled())
				logger.error("set cache failure：", ex);
		}
		return e;
	}

	/**
	 * 查询并设置缓存
	 * 
	 * @param <E>
	 * @param mc
	 *            memcached客户端
	 * @param cacheKeyPrefix
	 *            缓存前缀
	 * @param hashCode
	 *            指定服务器的hashCode
	 * @param converter
	 *            数据转换器
	 * @param sql
	 *            查询字串
	 * @param params
	 *            查询参数
	 * @return
	 */
	public <K extends Serializable, E extends Cacheable<K>> List<E> queryAndSetCache(
			CachedMap<K, E> map, DaoConverter<E> converter, String sql,
			List<?> params) {
		List<E> ls = query(converter, sql, params);
		try {
			map.putCacheAll(ls);
		} catch (Throwable ex) {
			if (logger.isDebugEnabled())
				logger.error("set cache failure：", ex);
		}
		return ls;
	}

	/**
	 * 查询并设置缓存
	 * 
	 * @param <E>
	 * @param mc
	 *            memcached客户端
	 * @param cacheKeyPrefix
	 *            缓存前缀
	 * @param hashCode
	 *            指定服务器的hashCode
	 * @param converter
	 *            数据转换器
	 * @param sql
	 *            查询字串
	 * @param params
	 *            查询参数
	 * @return
	 */
	public <K extends Serializable, E extends Cacheable<K>> List<E> queryAndSetCache(
			CachedMap<K, E> map, DaoConverter<E> converter, String sql,
			Object... params) {
		List<E> ls = query(converter, sql, params);
		try {
			map.putCacheAll(ls);
		} catch (Throwable ex) {
			if (logger.isDebugEnabled())
				logger.error("set cache failure：", ex);
		}
		return ls;
	}

}
