package kxd.engine.scs.report;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import kxd.engine.dao.Dao;
import kxd.engine.helper.AdminHelper;
import kxd.engine.helper.beans.IdentOrg;
import kxd.remote.scs.util.QueryResult;
import kxd.util.ObjectCreator;

/**
 * 机构统计报表列表
 * 
 * @author zhaom
 * 
 * @param <E>
 *            报表项
 */
public class OrgStatReportList<E extends ReportItem<IdentOrg>> extends
		MonthDayReportList<IdentOrg, E> {
	private E total;
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
	 */
	public OrgStatReportList(Dao dao, Date expireTime,
			final ObjectCreator<E> creator, int orgId, int month, int day) {
		super(dao, expireTime, creator, orgId, month, day);
		cachedObjectCreator = new ObjectCreator<E>() {
			@Override
			public E newInstance() {
				E e = creator.newInstance();
				e.setNameItem(new IdentOrg());
				return e;
			}
		};
	}

	/**
	 * 从按ident排序的机构列表中，找出ident所在的机构
	 * 
	 * @param ls
	 *            机构列表
	 * @param ident
	 *            目标序号
	 * @return 找到的机构
	 */
	protected E getOrgIndexIdInIdents(int ident) {
		for (int i = result.getResultList().size() - 1; i >= 0; i--) {
			E o = result.getResultList().get(i);
			if (ident >= o.getNameItem().getIdent())
				return o;
		}
		return null;
	}

	/**
	 * @param sql
	 *            严格按此格式：select b.ident,a.month,...,from 统计表名 a,org b,...
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
		if (whereString != null && !whereString.isEmpty())
			whereString += " and ";
		else
			whereString = " where ";
		whereString += " b.ident>=" + org.getStartIdent() + " and b.ident<="
				+ org.getEndIdent();
		return super.queryMultiMonthReportList(tablePrefix, months, sql,
				whereString, orderGroupString, params);
	}

	@Override
	protected void processResults(List<?> results) {
		Iterator<?> it = results.iterator();
		while (it.hasNext()) {
			Object[] o = (Object[]) it.next();
			int ident = Integer.valueOf(o[0].toString());
			E id = getOrgIndexIdInIdents(ident);
			if (id != null)
				id.addData(this, o);
			else
				total.addData(this, o);
		}
	}

	@Override
	protected void queryAfter() {
		for (E o : result.getResultList()) {
			total.addData(this, o);
			o.complele(this);
		}
		total.complele(this);
		result.getResultList().add(0, total);
		result.setCount(result.getResultList().size());
	}

	@Override
	protected void queryBefore() {
		total = newReportItem();
		total.setNameItem(new IdentOrg());
		total.getNameItem().setOrgId(org.getId());
		total.getNameItem().setOrgDesp(org.getOrgDesp());
		List<IdentOrg> ls = AdminHelper.getOrgChildIdents(orgVersionPrefix,
				dao, org);
		for (IdentOrg o : ls) {
			E item = newReportItem();
			item.setNameItem(o);
			result.getResultList().add(item);
		}
	}

	@Override
	ObjectCreator<E> getCacheCreator() {
		return cachedObjectCreator;
	}

}
