package kxd.engine.dao.interceptors;

import kxd.engine.dao.Dao;

/**
 * 任务拦截器
 * 
 * @author zhaom
 * 
 */
public interface TaskInterceptor {
	/**
	 * 指示当前任务是否可以继续运行
	 * 
	 * @param dao
	 * @return
	 */
	public boolean isEnabled(Dao dao);
}
