package kxd.engine.dao;

import java.util.List;

/**
 * 数据转换器，负责将数据库查询出来的数据，转换为可用的数据对象
 * 
 * @author zhaom
 * 
 */
public interface DaoConverter<E> {
	/**
	 * 转换一个数据对象
	 * 
	 * @param result
	 *            查询到的数据字段列表
	 * @return
	 */
	public E convert(Object result);

	/**
	 * 转换一组数据对象
	 * 
	 * @param results
	 * @return
	 */
	public List<E> convert(List<?> results);

	public Dao getDao();

	public void setDao(Dao dao);

}
