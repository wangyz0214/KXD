/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package kxd.remote.scs.interfaces;

import javax.ejb.Remote;

import kxd.remote.scs.beans.right.EditedOrg;

/**
 * 
 * @author 赵明
 */
@Remote
public interface OrgBeanRemote {
	/**
	 * 添加机构
	 */
	public int add(long loginUserId, EditedOrg org);

	/**
	 * 编辑用户
	 */
	public void edit(long loginUserId, EditedOrg org);

	/**
	 * 删除机构
	 * 
	 */
	public void delete(long loginUserId, Integer[] orgId);

	public void move(long loginUserId, int orgId, int newParentOrgId);

	/**
	 * 查找机构
	 * 
	 * @param orgId
	 */
	public EditedOrg find(int orgId);
}
