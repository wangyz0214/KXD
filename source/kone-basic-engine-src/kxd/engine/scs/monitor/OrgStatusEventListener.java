package kxd.engine.scs.monitor;

import kxd.engine.cache.ejb.beans.EjbCachedOrgNode;

public interface OrgStatusEventListener {
	/**
	 * 有新的机构加入
	 * 
	 * @param org
	 */
	public void orgAdded(EjbCachedOrgNode org);

	/**
	 * 机构信息被修改，更新缓存
	 * 
	 * @param org
	 */
	public void orgModified(EjbCachedOrgNode org);

	/**
	 * 机构信息被移除，更新缓存
	 * 
	 * @param orgId
	 */
	public void orgRemoved(EjbCachedOrgNode org);

	/**
	 * 机构迁移
	 * 
	 * @param dao
	 * @param org
	 * @param newParentOrg
	 */
	public void orgMoved(EjbCachedOrgNode org, EjbCachedOrgNode newParentOrg);

}
