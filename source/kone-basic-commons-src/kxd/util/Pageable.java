package kxd.util;

public interface Pageable {
	/**
	 * 获取总页数
	 * 
	 * @return 总页数
	 */
	public int getPages();

	/**
	 * 获取一页的记录数
	 * 
	 * @return 总页数
	 */
	public int getPageRecordCount();

	/**
	 * 获取当前页
	 * 
	 * @return 总页数
	 */
	public int getPage();

	/**
	 * 获取总记录数
	 * 
	 * @return 总记录数
	 */
	public int getRecordCount();

	/**
	 * 获取数据列表
	 * 
	 * @return
	 */
	public Object getDataList();
}
