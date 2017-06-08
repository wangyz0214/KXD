/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package kxd.remote.scs.interfaces;

import java.util.List;

import javax.ejb.Remote;

import kxd.remote.scs.beans.BasePrintAd;
import kxd.remote.scs.beans.adinfo.EditedPrintAd;
import kxd.remote.scs.util.QueryResult;

/**
 * 
 * @author 赵明
 */
@Remote
public interface PrintAdBeanRemote {

	public QueryResult<BasePrintAd> queryPrintAd(boolean queryCount,
			long loginUserId, int orgId, Integer filter, String filterContent,
			int startRecord, int maxResult);

	/**
	 * 添加打印广告
	 */
	public int add(long loginUserId, EditedPrintAd printAd);

	/**
	 * 编辑打印广告
	 */
	public void edit(long loginUserId, EditedPrintAd printAd);

	/**
	 * 删除打印广告
	 */
	public void delete(long loginUserId, int printAd);

	/**
	 * 上线审核
	 * 
	 * @param loginUserId
	 * @param printAd
	 */
	public void onlineAudit(long loginUserId, int printAd, boolean auditOk);

	/**
	 * 上线申请
	 * 
	 * @param loginUserId
	 * @param printAd
	 * @param isCancel
	 *            是否是撤消上线
	 */
	public void onlineReq(long loginUserId, int printAd, boolean isCancel);

	/**
	 * 广告下架审核
	 * 
	 * @param loginUserId
	 * @param printAd
	 */
	public void offlineAudit(long loginUserId, int printAd, boolean auditOk);

	/**
	 * 下线申请
	 * 
	 * @param loginUserId
	 * @param printAd
	 * @param isCancel
	 *            是否是撤消上线
	 */
	public void offlineReq(long loginUserId, int printAd, boolean isCancel);

	/**
	 * 查找打印广告
	 * 
	 * @param manufId
	 */
	public BasePrintAd find(int printAd);

	/**
	 * 获取正在使用的打印广告列表
	 * 
	 * @param orgId
	 *            打印ID
	 * @param adCategoryId
	 *            为null，获取全部广告，否则，获取该广告分类下的广告
	 * @return
	 */
	public List<EditedPrintAd> getUsingAdList(int orgId, Integer adCategoryId);
}
