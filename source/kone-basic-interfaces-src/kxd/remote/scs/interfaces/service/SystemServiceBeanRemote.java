package kxd.remote.scs.interfaces.service;

import java.util.List;

import javax.ejb.Remote;

import kxd.remote.scs.util.QueryResult;

/**
 * 
 * @author 赵明
 */
@Remote
public interface SystemServiceBeanRemote {
	/**
	 * 获取系统参数配置
	 * 
	 * @return 参数
	 */
	public List<?> getParamsConfig();

	/**
	 * 查询需要审核的信息
	 * 
	 * @param option
	 *            操作：0 - 打印广告；1 - 机构广告；2 - 终端广告；3 - 信息
	 * @return
	 */
	public QueryResult<?> queryAuditInfo(int option, boolean queryCount,
			long loginUserId, int orgId, Integer filter, String filterContent,
			int startRecord, int maxResult);

}
