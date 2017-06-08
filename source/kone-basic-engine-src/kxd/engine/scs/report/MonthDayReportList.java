package kxd.engine.scs.report;

import java.util.Date;

import kxd.engine.dao.Dao;
import kxd.util.DateTime;
import kxd.util.ObjectCreator;
import kxd.util.Streamable;

/**
 * 基于日期的报表列表
 * 
 * @author zhaom
 * 
 * @param <N>
 *            报表名称项
 * @param <E>
 *            报表项
 */
abstract public class MonthDayReportList<N extends Streamable, E extends ReportItem<N>>
		extends MonthReportList<N, E> {
	int day;

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
	public MonthDayReportList(Dao dao, Date expireTime,
			ObjectCreator<E> creator, int orgId, int month, int day) {
		super(dao, expireTime, creator, orgId, month);
		this.day = day;
		day = month * 100 + day;
		if (day < new DateTime().getFullDay()) // 前面日期的，设置缓存失效为1天
			this.expireTime = new DateTime().addHours(1).getTime();
	}

	/**
	 * 获取日期：1-31
	 * 
	 * @return 日期
	 */
	public int getDay() {
		return day;
	}

}
