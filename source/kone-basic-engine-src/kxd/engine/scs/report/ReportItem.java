package kxd.engine.scs.report;

import kxd.util.Streamable;

/**
 * 报表数据项目
 * 
 * @author zhaom
 * @param <N>
 *            表示当前报表的名称项对象
 */
public interface ReportItem<N extends Streamable> extends Streamable {
	/**
	 * 获取报表项的前面的名称项，可以由多项组成
	 * 
	 */
	public N getNameItem();

	/**
	 * 设置报表项的前面的名称项，可以由多项组成
	 * 
	 */
	public void setNameItem(N item);

	/**
	 * 向报表项目中增加一项数据
	 * 
	 * @param list
	 *            当前报表对象
	 * @param data
	 *            数据
	 */
	public void addData(ReportList<?, ?> list, Object[] data);

	/**
	 * 将item的数据，添加到当前数据项中
	 * 
	 * @param list
	 *            当前报表对象
	 * @param item
	 *            要增加的项目
	 */
	public void addData(ReportList<?, ?> list, ReportItem<N> item);

	/**
	 * 完成本条数据的统计
	 * 
	 * @param list
	 *            当前报表对象
	 */
	public void complele(ReportList<?, ?> list);
}
