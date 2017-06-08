package kxd.engine.scs.report;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import kxd.engine.dao.Dao;
import kxd.engine.helper.AdminHelper;
import kxd.engine.helper.beans.IdentOrgScope;
import kxd.remote.scs.util.AppException;
import kxd.remote.scs.util.QueryResult;
import kxd.util.DataSecurity;
import kxd.util.DateTime;
import kxd.util.ObjectCreator;
import kxd.util.Streamable;
import kxd.util.memcached.MemCachedClient;
import kxd.util.memcached.MemCachedException;

/**
 * 报表列表抽象类
 * 
 * @author zhaom
 * 
 * @param <N>
 *            报表名称项
 * @param <E>
 *            报表项
 */
abstract public class ReportList<N extends Streamable, E extends ReportItem<N>> {
	final protected QueryResult<E> result = new QueryResult<E>(0,
			new ArrayList<E>());
	final protected ObjectCreator<E> creator;
	final protected Dao dao;
	final protected IdentOrgScope org;
	protected Date expireTime;
	protected static MemCachedClient mc;
	final private static Logger logger = Logger.getLogger(ReportList.class);
	final public String orgVersionPrefix = AdminHelper
			.getOrgLastVersionKeyPrefix();
	static {
		try {
			mc = MemCachedClient.getInstance("report");
		} catch (MemCachedException e) {
			logger.error("memcached[report] not config:", e);
		}
	}

	/**
	 * 构造器
	 * 
	 * @param dao
	 *            数据库访问对象
	 * @param expireTime
	 *            缓存失效时间，失效后，将重新从数据库中装入缓存。为null，则不设置缓存
	 * @param creator
	 *            报表项生成器
	 * @param orgId
	 *            机构ID
	 */
	public ReportList(Dao dao, Date expireTime, ObjectCreator<E> creator,
			int orgId) {
		this.creator = creator;
		this.dao = dao;
		this.org = AdminHelper.getOrgIdentScope(orgVersionPrefix, dao, orgId);
		this.expireTime = expireTime;
	}

	/**
	 * 查询之前的预处理动作
	 */
	abstract protected void queryBefore();

	/**
	 * 处理数据库返回的报表数据结果集
	 * 
	 * @param results
	 *            基于Object[]的结果集
	 */
	abstract protected void processResults(List<?> results);

	/**
	 * 查询之后的完成动作
	 */
	abstract protected void queryAfter();

	/**
	 * 执行最底层的查询，可重载此函数<br>
	 * 执行查询，如果设置了缓存数据，则会先查缓存，再查数据库
	 * 
	 * @param sql
	 * @param params
	 * @return
	 */
	protected List<?> doQuery(String sql, Object... params) {
		return dao.query(sql, params);
	}

	/**
	 * 查询数据
	 * 
	 * @param sql
	 *            查询语句，形如：select * from ... 不包含where之后的部分
	 * @param whereString
	 *            过滤字串
	 * @param orderGroupString
	 *            分组和排序字串
	 * @param params
	 *            附加参数：params[0]->?1,params[1]->?2，依次类推
	 * @return 返回结果集
	 */
	protected List<?> query(String sql, String whereString,
			String orderGroupString, Object... params) {
		if (whereString != null && !whereString.isEmpty())
			sql += " where " + whereString;
		if (orderGroupString != null)
			sql += " " + orderGroupString;
		return doQuery(sql, params);
	}

	protected boolean tableExists(String tablename) {
		Iterator<?> it = dao.queryFromCache("", mc,
				new DateTime().addHours(1).getTime(),
				"select count(*) from user_tables where table_name=?1",
				new Object[] { tablename.toUpperCase() }).iterator();
		if (it.hasNext()) {
			String count = it.next().toString();
			return Integer.valueOf(count) > 0;
		} else
			return false;
	}

	protected String buildMd5Key(String str) {
		try {
			String ret = "$reportcache$." + orgVersionPrefix + "."
					+ org.getIdString() + "." + DataSecurity.md5(str);
			logger.debug("build report cache key: " + str + " ==> [" + ret
					+ "]");
			return ret;
		} catch (Throwable e) {
			return null;
		}
	}

	abstract ObjectCreator<E> getCacheCreator();

	protected boolean isCacheConfiged(Date expireTime) {
		return mc != null && expireTime != null && orgVersionPrefix != null;
	}

	protected QueryResult<E> doGetReportFromCache(Date expireTime, String key)
			throws Throwable {
		List<E> ls = mc.getStreamableList(key, getCacheCreator());
		if (ls != null) {
			result.setResultList(ls);
			return result;
		} else
			return null;
	}

	protected QueryResult<E> getReportFromCache(Date expireTime, String key) {
		if (expireTime == null)
			expireTime = this.expireTime;
		if (isCacheConfiged(expireTime)) {
			try {
				return doGetReportFromCache(expireTime, key);
			} catch (Throwable e) {
				logger.error("getReportFormCache error:", e);
			}
		}
		return null;
	}

	protected void doSetReportToCache(Date expireTime, String key)
			throws Throwable {
		mc.setStreamableList(key, result.getResultList(), expireTime);
	}

	protected void setReportToCache(Date expireTime, String key) {
		if (expireTime == null)
			expireTime = this.expireTime;
		if (isCacheConfiged(expireTime)) {
			try {
				doSetReportToCache(expireTime, key);
			} catch (Throwable e) {
				logger.error("setReportToCache error:", e);
			}
		}
	}

	/**
	 * 查询报表
	 * 
	 * @param tableName
	 *            表名，假如不为null,检查表是否存在
	 * @param sql
	 *            查询语句，形如：select * from ... 不包含where之后的部分
	 * @param whereString
	 *            过滤字串
	 * @param orderGroupString
	 *            分组和排序字串
	 * @param params
	 *            附加参数：params[0]->?1,params[1]->?2，依次类推
	 * @return
	 */
	public QueryResult<E> queryReportList(String tableName, String sql,
			String whereString, String orderGroupString, Object... params) {
		StringBuffer feature = new StringBuffer("common." + tableName + "[");
		feature.append("]." + sql + "." + whereString + "." + orderGroupString
				+ "[");
		for (Object o : params) {
			feature.append(o + ",");
		}
		feature.append("]");
		String key = buildMd5Key(feature.toString());
		QueryResult<E> r = getReportFromCache(null, key);
		if (r != null)
			return r;
		queryBefore();
		if (tableName != null && tableExists(tableName))
			processResults(query(sql, whereString, orderGroupString, params));
		queryAfter();
		setReportToCache(null, key);
		return result;
	}

	/**
	 * 从多月组合查询报表
	 * 
	 * @param tablePrefix
	 *            表名前缀
	 * @param months
	 *            月份数组
	 * @param sql
	 *            查询语句，${month}变量将被替换成当前的月份,${year}变量将被替换成当前的年份
	 * @param whereString
	 *            过滤字串
	 * @param orderGroupString
	 *            分组和排序字串
	 * @param params
	 *            附加参数：params[0]->?1,params[1]->?2，依次类推
	 * @return
	 */
	public QueryResult<E> queryMultiMonthReportList(String tablePrefix,
			int[] months, String sql, String whereString,
			String orderGroupString, Object... params) {
		StringBuffer feature = new StringBuffer("multimonth." + tablePrefix
				+ "[");
		for (int m : months)
			feature.append(m + ",");
		feature.append("]." + sql + "." + whereString + "." + orderGroupString
				+ "[");
		for (Object o : params) {
			feature.append(o + ",");
		}
		feature.append("]");
		String key = buildMd5Key(feature.toString());
		QueryResult<E> r = getReportFromCache(null, key);
		if (r != null)
			return r;
		queryBefore();
		for (int m : months) {
			String sm = Integer.toString(m);
			String sy = Integer.toString(m / 100);
			String table = tablePrefix.replace("${month}", sm).replace(
					"${year}", sy);
			if (tableExists(table))
				// 之前的月份，对于设置了缓存的报表，自动置数据缓存1天失败
				processResults(query(
						sql.replace("${month}", sm).replace("${year}", sy),
						whereString.replace("${month}", sm).replace("${year}",
								sy), orderGroupString, params));
		}
		queryAfter();
		setReportToCache(null, key);
		return result;
	}

	public E newReportItem() {
		try {
			return creator.newInstance();
		} catch (Throwable e) {
			throw new AppException(e);
		}
	}
}
