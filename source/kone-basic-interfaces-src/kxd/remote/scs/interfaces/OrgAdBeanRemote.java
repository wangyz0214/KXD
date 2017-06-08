/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package kxd.remote.scs.interfaces;

import java.util.List;

import javax.ejb.Remote;

import kxd.remote.scs.beans.BaseOrgAd;
import kxd.remote.scs.util.QueryResult;
import kxd.util.KeyValue;

/**
 * 
 * @author 赵明
 */
@Remote
public interface OrgAdBeanRemote {

	/**
	 * 获取广告列表
	 * 
	 * @param orgId
	 *            机构ID
	 * @return
	 */
	public QueryResult<BaseOrgAd> queryOrgAd(boolean queryCount,
			long loginUserId, int orgId, Integer filter, String filterContent,
			int startRecord, int maxResult);

	/**
	 * 获取广告列表
	 * 
	 * @param orgId
	 *            机构ID
	 * @param adCategoryId 广告分类            
	 * @return
	 */
	public QueryResult<BaseOrgAd> queryOrgAd(boolean queryCount,
			long loginUserId, int orgId, Integer filter, String filterContent,
			Integer adCategoryId, int startRecord, int maxResult);

	public BaseOrgAd updateUpload(long loginUserId, BaseOrgAd item);

	/**
	 * 添加机构广告
	 */
	public BaseOrgAd add(long loginUserId, BaseOrgAd orgAd);

	/**
	 * 编辑机构广告
	 * 
	 * @param loginUserId
	 * @param orgAd
	 * @return 当广告文件改变时,返回旧的广告文件名,以便外部删除
	 */
	public KeyValue<String, BaseOrgAd> edit(long loginUserId, BaseOrgAd orgAd);

	/**
	 * 删除广告
	 * 
	 * @param loginUserId
	 * @param orgAd
	 * @return 返回旧的广告文件名,以便外部删除
	 */
	public BaseOrgAd delete(long loginUserId, int orgAd);

	/**
	 * 查找机构广告
	 * 
	 * @param manufId
	 */
	public BaseOrgAd find(int orgAd);

	/**
	 * 获取指定机构的广告列表
	 * 
	 * @param orgId
	 * @return
	 */
	public List<BaseOrgAd> getUsingAdList(int orgId, Integer adCategoryId);

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
