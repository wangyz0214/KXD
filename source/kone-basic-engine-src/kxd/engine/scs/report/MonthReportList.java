package kxd.engine.scs.report;

import java.util.Date;

import kxd.engine.dao.Dao;
import kxd.util.DateTime;
import kxd.util.ObjectCreator;
import kxd.util.Streamable;

/**
 * 基于月份的统计报表列表
 * 
 * @author zhaom
 * 
 * @param <N>
 *            报表名称项
 * @param <E>
 *            报表项
 */
abstract public class MonthReportList<N extends Streamable, E extends ReportItem<N>>
		extends ReportList<N, E> {
	int month;

	/**
	 * 构造器
	 * 
	 * @param dao
	 *            数据库访问对象
	 * @param afterSecondsExpire
	 *            缓存在多少秒后失效，失效后，将重新从数据库中装入缓存
	 * @param clazz
	 *            报表项生成器
	 * @param orgId
	 *            机构ID
	 * @param month
	 *            月份：yyyyMM
	 */
	public MonthReportList(Dao dao, Date expireTime, ObjectCreator<E> creator,
			int orgId, int month) {
		super(dao, expireTime, creator, orgId);
		this.month = month;
		if (month < new DateTime().getFullMonth()) // 前面月分的，设置缓存失效为1天
			this.expireTime = new DateTime().addHours(1).getTime();
	}

	/**
	 * 获取月份：yyyyMMdd
	 * 
	 * @return 月份
	 */
	public int getMonth() {
		return month;
	}

}
