package kxd.engine.scs.report;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;

import kxd.engine.dao.Dao;
import kxd.engine.helper.AdminHelper;
import kxd.remote.scs.util.AppException;
import kxd.remote.scs.util.QueryResult;
import kxd.util.DateTime;
import kxd.util.ObjectCreator;

/**
 * 终端统计报表列表，按终端统计，不会出现终端重复的情况
 * 
 * @author zhaom
 * 
 * @param <E>
 */
public class TermStatReportList<E extends ReportItem<TermNameItem>> extends
		MonthDayReportList<TermNameItem, E> {
	final protected boolean firstQuery;
	final protected int firstResult;
	final protected int maxResults;
	final protected ArrayList<Integer> termIdList = new ArrayList<Integer>(),
			orgIdList = new ArrayList<Integer>();
	final protected Integer manufId;
	final ObjectCreator<E> cachedObjectCreator;

	/**
	 * 构造器
	 * 
	 * @param dao
	 *            数据库访问对象
	 * @param keyPrefix
	 *            缓存前缀，为null，则不缓存报表数据
	 * @param afterSecondsExpire
	 *            缓存在多少秒后失效，失效后，将重新从数据库中装入缓存
	 * @param creator
	 *            报表项生成器
	 * @param orgId
	 *            机构ID
	 * @param month
	 *            月份：yyyyMM
	 * @param day
	 *            日期：1-31
	 * @param firstQuery
	 *            是否初次查询
	 * @param firstResult
	 *            起始记录
	 * @param maxResults
	 *            返回记录数
	 */
	public TermStatReportList(Dao dao, Date expireTime,
			final ObjectCreator<E> creator, int orgId, int month, int day,
			boolean firstQuery, int firstResult, int maxResults) {
		this(dao, expireTime, creator, orgId, month, day, firstQuery,
				firstResult, maxResults, null);
	}

	/**
	 * 构造器
	 * 
	 * @param dao
	 *            数据库访问对象
	 * @param keyPrefix
	 *            缓存前缀，为null，则不缓存报表数据
	 * @param afterSecondsExpire
	 *            缓存在多少秒后失效，失效后，将重新从数据库中装入缓存
	 * @param creator
	 *            报表项生成器
	 * @param orgId
	 *            机构ID
	 * @param month
	 *            月份：yyyyMM
	 * @param day
	 *            日期：1-31
	 * @param firstQuery
	 *            是否初次查询
	 * @param firstResult
	 *            起始记录
	 * @param maxResults
	 *            返回记录数
	 * @param manufId
	 *            要过滤的厂商ID，为null，查询全部
	 */
	public TermStatReportList(Dao dao, Date expireTime,
			final ObjectCreator<E> creator, int orgId, int month, int day,
			boolean firstQuery, int firstResult, int maxResults, Integer manufId) {
		super(dao, expireTime, creator, orgId, month, day);
		this.firstQuery = firstQuery;
		this.firstResult = firstResult;
		this.maxResults = maxResults;
		this.manufId = manufId;
		cachedObjectCreator = new ObjectCreator<E>() {
			@Override
			public E newInstance() {
				E e = creator.newInstance();
				e.setNameItem(new TermNameItem());
				return e;
			}
		};
	}

	@Override
	protected void processResults(List<?> results) {
		Iterator<?> it = results.iterator();
		List<E> list = result.getResultList();
		try {
			Map<?, ?> map = AdminHelper.getOrgProvinceCity(orgIdList);
			while (it.hasNext()) {
				Object[] o = (Object[]) it.next();
				int termId = Integer.valueOf(o[0].toString());
				int index = termIdList.indexOf(termId);
				E id = list.get(index);
				id.addData(this, o);
			}
			for (E id : result.getResultList()) {
				TermNameItem nameItem = id.getNameItem();
				Object[] names = (Object[]) map.get(nameItem.getOrgId());
				nameItem.setProvinceName((String) names[0]);
				nameItem.setCityName((String) names[1]);
				nameItem.setHallName((String) names[2]);
			}
		} catch (NamingException e) {
			throw new AppException(e);
		}
	}

	@Override
	protected void queryAfter() {
		for (E o : result.getResultList()) {
			o.complele(this);
		}
	}

	@Override
	protected QueryResult<E> doGetReportFromCache(Date expireTime, String key)
			throws Throwable {
		if (firstQuery) {
			Integer c = (Integer) mc.get(key + ".count");
			QueryResult<E> r = super.doGetReportFromCache(expireTime, key);
			if (c != null && r != null) {
				r.setCount(c);
				return r;
			} else
				return null;
		} else
			return super.doGetReportFromCache(expireTime, key);
	}

	@Override
	protected void doSetReportToCache(Date expireTime, String key)
			throws Throwable {
		if (firstQuery) {
			mc.set(key + ".count", result.getCount(), expireTime);
		}
		super.doSetReportToCache(expireTime, key);
	}

	@Override
	protected void queryBefore() {
		Date expire = new DateTime().addSeconds(600).getTime();
		if (firstQuery) {
			if (manufId != null) {
				String sql = "select count(*) from term a,org b,termtype c where "
						+ "a.orgid=b.orgid and b.ident>=?1 and b.ident<=?2 and"
						+ " a.typeid=c.typeid and c.manufid=" + manufId;
				result.setCount(Integer.valueOf(dao
						.queryFromCache(orgVersionPrefix, mc, expire, sql,
								org.getStartIdent(), org.getEndIdent()).get(0)
						.toString()));
			} else {
				String sql = "select count(*) from term a,org b where "
						+ "a.orgid=b.orgid and b.ident>=?1 and b.ident<=?2";
				result.setCount(Integer.valueOf(dao
						.queryFromCache(orgVersionPrefix, mc, expire, sql,
								org.getStartIdent(), org.getEndIdent()).get(0)
						.toString()));
			}
		}
		String sql;
		if (manufId != null)
			sql = "select termid,termcode,b.orgid from term a,"
					+ "org b,termtype c where a.orgid=b.orgid and b.ident>=?1 and b.ident<=?2"
					+ " and a.typeid=c.typeid and c.manufid=" + manufId
					+ " order by orgid";
		else
			sql = "select termid,termcode,b.orgid from term a,"
					+ "org b where a.orgid=b.orgid and b.ident>=?1 and b.ident<=?2 order by orgid";
		List<?> ls = dao
				.queryPageFromCache(orgVersionPrefix, mc, expire, sql,
						firstResult, maxResults, org.getStartIdent(),
						org.getEndIdent());
		for (Object o : ls) {
			Object a[] = (Object[]) o;
			E item = newReportItem();
			TermNameItem nameItem = new TermNameItem();
			nameItem.setOrgId(Integer.valueOf(a[2].toString()));
			nameItem.setTermCode((String) a[1]);
			item.setNameItem(nameItem);
			result.getResultList().add(item);
			if (!orgIdList.contains(nameItem.getOrgId()))
				orgIdList.add(nameItem.getOrgId());
			termIdList.add(Integer.valueOf(a[0].toString()));
		}
	}

	@Override
	protected List<?> query(String sql, String whereString,
			String orderGroupString, Object... params) {
		if (termIdList.size() == 0)
			return new ArrayList<Object>();
		StringBuffer str = new StringBuffer("(");
		for (int i = 0; i < termIdList.size(); i++) {
			if (i > 0)
				str.append(" or ");
			str.append(" a.termid=" + termIdList.get(i));
		}
		str.append(")");
		if (whereString == null || whereString.isEmpty())
			whereString = str.toString();
		else
			whereString = str.toString() + " and " + whereString;
		return super.query(sql, whereString, orderGroupString, params);
	}

	/**
	 * @param sql
	 *            查询语句，严格按此格式：select a.termid,... from 统计表名 a
	 */
	@Override
	public QueryResult<E> queryReportList(String tableName, String sql,
			String whereString, String orderGroupString, Object... params) {
		return super.queryReportList(tableName, sql, whereString,
				orderGroupString, params);
	}

	@Override
	ObjectCreator<E> getCacheCreator() {
		return cachedObjectCreator;
	}

	@Override
	protected String buildMd5Key(String str) {
		return super.buildMd5Key(str + "." + firstResult + "." + maxResults
				+ "." + manufId);
	}

}
