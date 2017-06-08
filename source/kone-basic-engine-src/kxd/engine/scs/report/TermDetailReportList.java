package kxd.engine.scs.report;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;

import kxd.engine.dao.Dao;
import kxd.engine.helper.AdminHelper;
import kxd.remote.scs.util.AppException;
import kxd.remote.scs.util.QueryResult;
import kxd.util.ObjectCreator;

/**
 * 终端明细报表列表，一个终端可能出现在多条记录中
 * 
 * @author zhaom
 * 
 * @param <E>
 */
public class TermDetailReportList<E extends ReportItem<TermNameItem>> extends
		ReportList<TermNameItem, E> {
	final protected boolean firstQuery;
	final protected int firstResult;
	final protected int maxResults;
	final ObjectCreator<E> cachedObjectCreator;

	/**
	 * 构造器
	 * 
	 * @param dao
	 *            数据库访问对象
	 * @param creator
	 *            报表项生成器
	 * @param orgId
	 *            机构ID
	 * @param firstQuery
	 *            是否初次查询
	 * @param firstResult
	 *            起始记录
	 * @param maxResults
	 *            返回记录数
	 */
	public TermDetailReportList(Dao dao, final ObjectCreator<E> creator,
			int orgId, boolean firstQuery, int firstResult, int maxResults) {
		super(dao, null, creator, orgId);
		this.firstQuery = firstQuery;
		this.firstResult = firstResult;
		this.maxResults = maxResults;
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
		final ArrayList<Integer> orgIdList = new ArrayList<Integer>();
		try {
			while (it.hasNext()) {
				Object[] o = (Object[]) it.next();
				E id = newReportItem();
				id.setNameItem(new TermNameItem());
				id.getNameItem().setTermCode((String) o[0]);
				int orgId = Integer.valueOf(o[1].toString());
				id.getNameItem().setOrgId(orgId);
				id.addData(this, o);
				if (!orgIdList.contains(orgId))
					orgIdList.add(orgId);
				result.getResultList().add(id);
			}
			Map<?, ?> map = AdminHelper.getOrgProvinceCity(orgIdList);
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
	protected void queryBefore() {
	}

	/**
	 * 重载，分页查询
	 */
	@Override
	protected List<?> doQuery(String sql, Object... params) {
		return dao.queryPage(sql, firstResult, maxResults, params);
	}

	@Override
	protected List<?> query(String sql, String whereString,
			String orderGroupString, Object... params) {
		if (firstQuery) {
			int index = sql.toLowerCase().indexOf(" from ");
			result.setCount(Integer.valueOf(dao.querySingal(
					"select count(*) " + sql.substring(index) + " where "
							+ whereString, params).toString()));
			if (result.getCount() == 0)
				return new ArrayList<Object>();
		}
		return super.query(sql, whereString, orderGroupString, params);
	}

	/**
	 * @param sql
	 *            查询语句，严格按此格式：select a.termcode,b.orgid,... from 统计表名 a
	 */
	@Override
	public QueryResult<E> queryReportList(String tableName, String sql,
			String whereString, String orderGroupString, Object... params) {
		if (whereString != null && !whereString.isEmpty())
			whereString += " and ";
		else
			whereString = " where ";
		whereString += " b.ident>=" + org.getStartIdent() + " and b.ident<="
				+ org.getEndIdent();
		return super.queryReportList(tableName, sql, whereString,
				orderGroupString, params);
	}

	@Override
	public QueryResult<E> queryMultiMonthReportList(String tablePrefix,
			int[] months, String sql, String whereString,
			String orderGroupString, Object... params) {
		throw new AppException("queryMultiMonthReportList() not implements");
	}

	@Override
	ObjectCreator<E> getCacheCreator() {
		return cachedObjectCreator;
	}

}
