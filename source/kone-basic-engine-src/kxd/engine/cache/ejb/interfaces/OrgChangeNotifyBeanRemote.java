package kxd.engine.cache.ejb.interfaces;

import javax.ejb.Remote;

import kxd.remote.scs.beans.right.EditedOrg;

@Remote
public interface OrgChangeNotifyBeanRemote {
	/**
	 * 有新的机构加入，更新缓存
	 * 
	 * @param org
	 */
	public void orgAdded(EditedOrg org);

	/**
	 * 机构信息被修改，更新缓存
	 * 
	 * @param org
	 */
	public void orgModified(EditedOrg org);

	/**
	 * 机构信息被移除，更新缓存
	 * 
	 * @param orgId
	 */
	public void orgRemoved(int orgId);

	/**
	 * 机构迁移
	 * 
	 * @param orgId
	 * @param newParentOrgId
	 */
	public void orgMoved(int orgId, int newParentOrgId);

}
