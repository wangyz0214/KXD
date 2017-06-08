package kxd.engine.dao;

/**
 * 将数据转换为SQL
 * 
 * @author zhaom
 * 
 */
public interface SqlConverter<E> {

	/**
	 * 获取对象的插入SQL，对于ID为null，不应该写入Sql中
	 */
	public SqlParams getInsertSql(E o);

	/**
	 * 获取序列生成器字串，用于插入时，自动获取下一个ID值
	 */
	public String getSequenceString(E o);

	/**
	 * 获取对象的更新Sql
	 */
	public SqlParams getUpdateSql(E o);

	/**
	 * 获取对象的删除Sql
	 * 
	 */
	public SqlParams getDeleteSql(E o);

	public Dao getDao();

	public void setDao(Dao dao);
}
