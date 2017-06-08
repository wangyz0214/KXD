/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package kxd.remote.scs.interfaces;

import java.util.List;

import javax.ejb.Remote;

import kxd.remote.scs.beans.BaseInfoFile;
import kxd.remote.scs.beans.adinfo.EditedInfo;
import kxd.remote.scs.util.QueryResult;

/**
 * 
 * @author 赵明
 */
@Remote
public interface InfoBeanRemote {

	/**
	 * 查询信息文件
	 * 
	 * @return
	 */
	public List<BaseInfoFile> queryInfoFile(long infoId, Integer fileType,
			Integer filter, String filterContent);

	/**
	 * 添加信息文件
	 */
	public String addFile(long loginUserId, BaseInfoFile infoFile);

	public long getNextInfoId();

	/**
	 * 删除信息文件
	 */
	public String deleteFile(long loginUserId, long infoFileId);

	/**
	 * 查询信息
	 * 
	 * @param queryCount
	 *            是否返回记录总条数
	 * @param loginUserId
	 *            登录用户
	 * @param filter
	 *            过滤操作
	 * @param filterContent
	 *            过滤内容
	 * @param startRecord
	 *            起始记录
	 * @param maxResult
	 *            最大的返回记录条数
	 * @return
	 */
	public QueryResult<EditedInfo> queryInfo(boolean queryCount,
			long loginUserId, int orgId, Integer filter, String filterContent,
			int startRecord, int maxResult);

	/**
	 * 查询信息
	 * 
	 * @param queryCount
	 *            是否返回记录总条数
	 * @param loginUserId
	 *            登录用户
	 * @param filter
	 *            过滤操作
	 * @param filterContent
	 *            过滤内容
	 * @param infoCategoryId
	 *            信息分类
	 * @param startRecord
	 *            起始记录
	 * @param maxResult
	 *            最大的返回记录条数
	 * @return
	 */
	public QueryResult<EditedInfo> queryInfo(boolean queryCount,
			long loginUserId, int orgId, Integer filter, String filterContent,
			Integer infoCategoryId, int startRecord, int maxResult);

	/**
	 * 添加信息
	 */
	public void add(long loginUserId, EditedInfo info);

	/**
	 * 编辑信息
	 */
	public void edit(long loginUserId, EditedInfo info);

	/**
	 * 删除信息
	 */
	public String[] delete(long loginUserId, long info);

	/**
	 * 查找信息
	 * 
	 * @param info
	 */
	public EditedInfo find(long info);

	/**
	 * 获取信息列表
	 * 
	 * @param loginUserId
	 * @param keyword
	 */
	public List<EditedInfo> getUsingInfoList(int orgId, Integer infoCategoryId);

	/**
	 * 上线审核
	 * 
	 * @param loginUserId
	 * @param id
	 */
	public void onlineAudit(long loginUserId, int id, boolean auditOk);

	/**
	 * 上线申请
	 * 
	 * @param loginUserId
	 * @param id
	 * @param isCancel
	 *            是否是撤消上线
	 */
	public void onlineReq(long loginUserId, int id, boolean isCancel);

	/**
	 * 广告下架审核
	 * 
	 * @param loginUserId
	 * @param id
	 */
	public void offlineAudit(long loginUserId, int id, boolean auditOk);

	/**
	 * 下线申请
	 * 
	 * @param loginUserId
	 * @param id
	 * @param isCancel
	 *            是否是撤消上线
	 */
	public void offlineReq(long loginUserId, int id, boolean isCancel);

}
