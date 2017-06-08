package kxd.engine.scs.report;

import java.util.Date;
import java.util.List;

import kxd.engine.dao.Dao;
import kxd.engine.scs.report.beans.OpenRateStatItem;
import kxd.util.DateTime;
import kxd.util.ObjectCreator;

/**
 * 终端开机率报表列表
 * 
 * @author zhaom
 * 
 */
public class TermOpenRateStatReportList extends
		TermStatReportList<OpenRateStatItem> {
	final DateTime startTime, endTime;

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
	public TermOpenRateStatReportList(Dao dao, Date expireTime,
			ObjectCreator<OpenRateStatItem> creator, int orgId, int month,
			int day, boolean firstQuery, int firstResult, int maxResults,
			DateTime startTime, DateTime endTime, Integer manufId) {
		super(dao, expireTime, creator, orgId, month, day, firstQuery,
				firstResult, maxResults, manufId);
		this.startTime = startTime;
		this.endTime = endTime;
	}

	@Override
	protected void queryBefore() {
		Date expire = new DateTime().addSeconds(60).getTime();
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
			sql = "select termid,termcode,b.orgid,a.dayruntime,a.starttime from term a,"
					+ "org b,termtype c where a.orgid=b.orgid and b.ident>=?1 and b.ident<=?2"
					+ " and a.typeid=c.typeid and c.manufid=" + manufId;
		else
			sql = "select termid,termcode,b.orgid,a.dayruntime,a.starttime from term a,"
					+ "org b where a.orgid=b.orgid and b.ident>=?1 and b.ident<=?2";
		List<?> ls = dao
				.queryPageFromCache(orgVersionPrefix, mc, expire, sql,
						firstResult, maxResults, org.getStartIdent(),
						org.getEndIdent());
		for (Object o : ls) {
			Object a[] = (Object[]) o;
			OpenRateStatItem item = newReportItem();
			TermNameItem nameItem = new TermNameItem();
			nameItem.setOrgId(Integer.valueOf(a[2].toString()));
			nameItem.setTermCode((String) a[1]);
			item.setNameItem(nameItem);
			Date d = (Date) a[4];
			int days = 0;
			if (d != null) {
				if (this.endTime.getTime().after(d)) {
					if (d.before(this.startTime.getTime()))
						days = (int) this.startTime.daysBetween(this.endTime);
					else
						days = (int) DateTime.daysBetween(d,
								this.endTime.getTime());
					days++;
				}
			}
			item.setNeedOpenTime(Integer.valueOf(a[3].toString()) * days * 3600);
			result.getResultList().add(item);
			if (!orgIdList.contains(nameItem.getOrgId()))
				orgIdList.add(nameItem.getOrgId());
			termIdList.add(Integer.valueOf(a[0].toString()));
		}
	}
}
