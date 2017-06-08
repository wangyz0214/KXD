/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package kxd.remote.scs.interfaces;

import java.util.List;

import javax.ejb.Remote;

import kxd.remote.scs.beans.BaseTermAd;
import kxd.util.KeyValue;

/**
 * 
 * @author 赵明
 */
@Remote
public interface TermAdBeanRemote {

	/**
	 * 获取终端的广告列表
	 * 
	 * @param termId
	 *            终端ID
	 * @return 指定终端的广告列表
	 */
	public List<BaseTermAd> getAdList(long loginUserId, int termId);

	/**
	 * 获取终端的广告列表
	 * 
	 * @param termId
	 *            终端ID
	 * @param adCategoryId
	 *            广告分类
	 * @return 指定终端的广告列表
	 */
	public List<BaseTermAd> getAdList(long loginUserId, int termId,
			Integer adCategoryId,String adDesp);

	public BaseTermAd updateUpload(long loginUserId, BaseTermAd item);

	/**
	 * 添加机构广告
	 */
	public BaseTermAd add(long loginUserId, BaseTermAd orgAd);

	/**
	 * 编辑机构广告
	 */
	public KeyValue<String, BaseTermAd> edit(long loginUserId, BaseTermAd orgAd);

	/**
	 * 删除机构广告
	 */
	public BaseTermAd delete(long loginUserId, int orgAd);

	/**
	 * 查找机构广告
	 * 
	 * @param manufId
	 */
	public BaseTermAd find(int orgAd);

	/**
	 * 获取指定终端的广告列表
	 * 
	 * @param termId
	 *            终端ID
	 * @param adCategoryId
	 *            广告分类，如果为null，则返回终端下全部广告
	 * @return
	 */
	public List<BaseTermAd> getTermAdUsingList(int termId, Integer adCategoryId);

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
